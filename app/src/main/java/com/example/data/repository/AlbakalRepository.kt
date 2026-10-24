package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.model.*
import com.example.data.remote.FirestoreSyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AlbakalRepository(
  private val dao: AppDao,
  private val syncManager: FirestoreSyncManager? = null
) {

  // Users & Auth
  suspend fun login(emailOrPhone: String, pass: String): Result<UserEntity> {
    val clean = emailOrPhone.trim()
    var user = if (clean.contains("@")) {
      dao.getUserByEmail(clean)
    } else {
      dao.getUserByPhone(clean)
    }

    // If not found in local Room yet (fresh device), try fetching directly from Firestore
    if (user == null && syncManager?.getFirestore() != null) {
      try {
        val field = if (clean.contains("@")) "email" else "phone"
        val query = syncManager.getFirestore()!!.collection("users")
          .whereEqualTo(field, clean)
          .limit(1)
          .get()
          .await()
        if (!query.isEmpty) {
          val doc = query.documents.first()
          val id = doc.getString("id") ?: doc.id
          val name = doc.getString("name") ?: ""
          val email = doc.getString("email") ?: ""
          val phone = doc.getString("phone") ?: ""
          val password = doc.getString("password") ?: ""
          val role = doc.getString("role") ?: "CUSTOMER"
          val status = doc.getString("status") ?: "PENDING_APPROVAL"
          val banReason = doc.getString("banReason")
          val governorate = doc.getString("governorate") ?: ""
          val district = doc.getString("district") ?: ""
          val street = doc.getString("street") ?: ""
          val apartment = doc.getString("apartment") ?: ""
          val floor = doc.getString("floor") ?: ""
          val monthlySubscriptionActive = doc.getBoolean("monthlySubscriptionActive") ?: false
          val subscriptionExpiresAt = doc.getLong("subscriptionExpiresAt") ?: 0L
          val currentDebt = doc.getDouble("currentDebt") ?: 0.0
          val totalEarnings = doc.getDouble("totalEarnings") ?: 0.0
          val honorCodeAccepted = doc.getBoolean("honorCodeAccepted") ?: false
          val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()

          val fetchedUser = UserEntity(
            id = id,
            name = name,
            email = email,
            phone = phone,
            password = password,
            role = role,
            status = status,
            banReason = banReason,
            governorate = governorate,
            district = district,
            street = street,
            apartment = apartment,
            floor = floor,
            monthlySubscriptionActive = monthlySubscriptionActive,
            subscriptionExpiresAt = subscriptionExpiresAt,
            currentDebt = currentDebt,
            totalEarnings = totalEarnings,
            honorCodeAccepted = honorCodeAccepted,
            createdAt = createdAt
          )
          dao.insertUser(fetchedUser)
          user = fetchedUser
        }
      } catch (e: Exception) {
        // Fall back to local check result
      }
    }

    if (user == null) {
      return Result.failure(Exception("الحساب غير موجود، يرجى التأكد من البيانات المدخلة أو إنشاء حساب جديد"))
    }

    if (user.password != pass) {
      return Result.failure(Exception("كلمة المرور غير صحيحة"))
    }

    if (user.status == "PENDING_APPROVAL") {
      return Result.failure(Exception("الحساب قيد المراجعة والموافقة من الإدارة. يرجى الانتظار حتى يتم تفعيل حسابك."))
    }

    if (user.status == "BANNED") {
      val reason = user.banReason?.let { ": $it" } ?: ""
      return Result.failure(Exception("تم حظر هذا الحساب من قبل الإدارة$reason. يرجى التواصل مع الدعم الفني."))
    }

    if (user.status == "REJECTED") {
      return Result.failure(Exception("عذراً، تم رفض طلب انضمام هذا الحساب من قبل الإدارة."))
    }

    return Result.success(user)
  }

  suspend fun registerUser(user: UserEntity): Result<Unit> {
    // Check duplicate email or phone locally
    val existingEmail = dao.getUserByEmail(user.email.trim())
    if (existingEmail != null) {
      return Result.failure(Exception("البريد الإلكتروني مسجل مسبقاً في النظام"))
    }
    val existingPhone = dao.getUserByPhone(user.phone.trim())
    if (existingPhone != null) {
      return Result.failure(Exception("رقم الهاتف مسجل مسبقاً في النظام"))
    }

    // Insert locally in Room
    dao.insertUser(user)

    // Sync to Firestore in real-time so Admin Dashboard on other devices receives registration immediately
    syncManager?.pushUser(user)

    return Result.success(Unit)
  }

  fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()
  fun getUsersByRole(role: String): Flow<List<UserEntity>> = dao.getUsersByRole(role)
  fun getPendingUsers(): Flow<List<UserEntity>> = dao.getPendingUsers()
  suspend fun getUserById(id: String): UserEntity? = dao.getUserById(id)

  suspend fun updateUser(user: UserEntity) {
    dao.updateUser(user)
    syncManager?.pushUser(user)
  }

  suspend fun deleteUser(id: String) {
    dao.deleteUserById(id)
    syncManager?.deleteUser(id)
  }

  suspend fun approveUser(userId: String) {
    val user = dao.getUserById(userId) ?: return
    val approved = user.copy(status = "APPROVED")
    dao.updateUser(approved)
    syncManager?.pushUser(approved)
  }

  suspend fun rejectUser(userId: String) {
    val user = dao.getUserById(userId) ?: return
    val rejected = user.copy(status = "REJECTED")
    dao.updateUser(rejected)
    syncManager?.pushUser(rejected)
  }

  suspend fun banMerchant(merchantId: String, reason: String) {
    val merchant = dao.getUserById(merchantId) ?: return
    val banned = merchant.copy(status = "BANNED", banReason = reason)
    dao.updateUser(banned)
    syncManager?.pushUser(banned)
  }

  suspend fun unbanMerchant(merchantId: String) {
    val merchant = dao.getUserById(merchantId) ?: return
    val unbanned = merchant.copy(status = "APPROVED", banReason = null)
    dao.updateUser(unbanned)
    syncManager?.pushUser(unbanned)
  }

  suspend fun acceptHonorCode(merchantId: String) {
    val merchant = dao.getUserById(merchantId) ?: return
    val updated = merchant.copy(honorCodeAccepted = true)
    dao.updateUser(updated)
    syncManager?.pushUser(updated)
  }

  suspend fun updateAdminCredentials(adminId: String, newEmail: String, newPass: String): Result<Unit> {
    val admin = dao.getUserById(adminId) ?: return Result.failure(Exception("الحساب غير موجود"))
    if (admin.role != "ADMIN") {
      return Result.failure(Exception("لا توجد صلاحية لتعديل حساب الإدارة الرئيسي"))
    }
    val updated = admin.copy(email = newEmail.trim(), password = newPass.trim())
    dao.updateUser(updated)
    syncManager?.pushUser(updated)
    return Result.success(Unit)
  }

  // Supervisor & Permissions
  fun getSupervisorPermissions(userId: String): Flow<SupervisorPermissionEntity?> = dao.getPermissionsForUser(userId)
  suspend fun getSupervisorPermissionsDirect(userId: String): SupervisorPermissionEntity? = dao.getPermissionsDirect(userId)
  suspend fun saveSupervisorPermissions(permissions: SupervisorPermissionEntity) = dao.insertPermission(permissions)

  suspend fun createSupervisor(
    name: String,
    email: String,
    phone: String,
    pass: String,
    permissions: SupervisorPermissionEntity
  ): Result<Unit> {
    val id = "sup_" + UUID.randomUUID().toString().take(8)
    val user = UserEntity(
      id = id,
      name = name,
      email = email,
      phone = phone,
      password = pass,
      role = "SUPERVISOR",
      status = "APPROVED",
      governorate = "الإسكندرية",
      district = "الإدارة",
      street = "المقر الإداري",
      apartment = "1",
      floor = "1"
    )
    val reg = registerUser(user)
    if (reg.isFailure) return reg
    dao.insertPermission(permissions.copy(userId = id))
    return Result.success(Unit)
  }

  // Governorates & Districts
  fun getAllAreas(): Flow<List<GovernorateAreaEntity>> = dao.getAllAreas()
  suspend fun addArea(gov: String, dist: String) {
    val area = GovernorateAreaEntity("area_" + UUID.randomUUID().toString().take(8), gov.trim(), dist.trim())
    dao.insertArea(area)
    syncManager?.pushArea(area)
  }
  suspend fun deleteArea(id: String) {
    dao.deleteAreaById(id)
    syncManager?.deleteArea(id)
  }

  // Categories
  fun getAllCategories(): Flow<List<ProductCategoryEntity>> = dao.getAllCategories()
  suspend fun addCategory(name: String) {
    val cat = ProductCategoryEntity("cat_" + UUID.randomUUID().toString().take(8), name.trim())
    dao.insertCategory(cat)
    syncManager?.pushCategory(cat)
  }
  suspend fun deleteCategory(id: String) {
    dao.deleteCategoryById(id)
    syncManager?.deleteCategory(id)
  }

  // Products
  fun getAllProducts(): Flow<List<ProductEntity>> = dao.getAllProducts()
  fun getPublishedProducts(): Flow<List<ProductEntity>> = dao.getPublishedProducts()
  fun getProductsByMerchant(merchantId: String): Flow<List<ProductEntity>> = dao.getProductsByMerchant(merchantId)
  suspend fun addProduct(product: ProductEntity) {
    dao.insertProduct(product)
    syncManager?.pushProduct(product)
  }
  suspend fun updateProduct(product: ProductEntity) {
    dao.updateProduct(product)
    syncManager?.pushProduct(product)
  }
  suspend fun deleteProduct(id: String) {
    dao.deleteProductById(id)
    syncManager?.deleteProduct(id)
  }

  // Sponsored Ads
  fun getAllAds(): Flow<List<SponsoredAdEntity>> = dao.getAllAds()
  fun getActiveAds(): Flow<List<SponsoredAdEntity>> = dao.getActiveAds()
  suspend fun addAd(ad: SponsoredAdEntity) {
    dao.insertAd(ad)
    syncManager?.pushAd(ad)
  }
  suspend fun updateAd(ad: SponsoredAdEntity) {
    dao.updateAd(ad)
    syncManager?.pushAd(ad)
  }
  suspend fun deleteAd(id: String) {
    dao.deleteAdById(id)
    syncManager?.deleteAd(id)
  }

  // Cart (Local to customer device)
  fun getCart(customerId: String): Flow<List<CartItemEntity>> = dao.getCartForCustomer(customerId)
  suspend fun addToCart(
    customerId: String,
    productId: String,
    productName: String,
    merchantId: String,
    merchantName: String,
    price: Double,
    quantity: Int,
    imageUrl: String,
    unit: String = "قطعة"
  ) {
    val id = "${customerId}_${productId}"
    dao.insertCartItem(
      CartItemEntity(
        id = id,
        customerId = customerId,
        productId = productId,
        productName = productName,
        merchantId = merchantId,
        merchantName = merchantName,
        price = price,
        quantity = quantity,
        imageUrl = imageUrl,
        unit = unit
      )
    )
  }
  suspend fun updateCartQuantity(cartItemId: String, quantity: Int) {
    if (quantity <= 0) {
      dao.deleteCartItem(cartItemId)
    }
  }
  suspend fun removeCartItem(cartItemId: String) = dao.deleteCartItem(cartItemId)
  suspend fun clearCart(customerId: String) = dao.clearCartForCustomer(customerId)

  // Orders
  fun getAllOrders(): Flow<List<OrderEntity>> = dao.getAllOrders()
  fun getOrdersForCustomer(customerId: String): Flow<List<OrderEntity>> = dao.getOrdersForCustomer(customerId)
  fun getOrdersForMerchant(merchantId: String): Flow<List<OrderEntity>> = dao.getOrdersForMerchant(merchantId)
  fun getNewUnacknowledgedOrders(merchantId: String): Flow<List<OrderEntity>> = dao.getNewUnacknowledgedOrdersForMerchant(merchantId)
  fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> = dao.getOrderItems(orderId)

  suspend fun placeOrder(
    customer: UserEntity,
    cartItems: List<CartItemEntity>,
    shippingFee: Double,
    commissionPercent: Double,
    paymentMethod: String,
    paymentProofUrl: String? = null
  ): Result<String> {
    if (cartItems.isEmpty()) return Result.failure(Exception("سلة المشتريات فارغة"))

    // Group items by merchant
    val merchantGroups = cartItems.groupBy { it.merchantId }
    val createdOrderIds = mutableListOf<String>()

    for ((mId, items) in merchantGroups) {
      val orderId = "ord_" + UUID.randomUUID().toString().take(10)
      val orderNumber = "ORD-" + (100000 + (Math.random() * 900000).toInt())
      val mName = items.first().merchantName
      val totalProductsPrice = items.sumOf { it.price * it.quantity }
      val grandTotal = totalProductsPrice + shippingFee
      val appCommission = (totalProductsPrice * commissionPercent) / 100.0

      val order = OrderEntity(
        id = orderId,
        orderNumber = orderNumber,
        customerId = customer.id,
        customerName = customer.name,
        customerPhone = customer.phone,
        customerAddress = "${customer.governorate} - ${customer.district} - ${customer.street} - عمارة/شقة ${customer.apartment} - الدور ${customer.floor}",
        merchantId = mId,
        merchantName = mName,
        totalProductsPrice = totalProductsPrice,
        shippingFee = shippingFee,
        grandTotal = grandTotal,
        appCommission = appCommission,
        paymentMethod = paymentMethod,
        paymentProofUrl = paymentProofUrl,
        paymentStatus = if (paymentMethod == "CASH_ON_DELIVERY") "CASH_ON_DELIVERY" else "PENDING_VERIFICATION",
        orderStatus = "NEW",
        isAcknowledgedByMerchant = false
      )
      dao.insertOrder(order)

      val orderItems = items.map { item ->
        OrderItemEntity(
          id = UUID.randomUUID().toString(),
          orderId = orderId,
          productId = item.productId,
          productName = item.productName,
          quantity = item.quantity,
          unitPrice = item.price,
          totalPrice = item.price * item.quantity,
          unit = item.unit
        )
      }
      dao.insertOrderItems(orderItems)

      // Sync to cloud Firestore
      syncManager?.pushOrder(order, orderItems)
      createdOrderIds.add(orderNumber)
    }

    dao.clearCartForCustomer(customer.id)
    return Result.success(createdOrderIds.joinToString(", "))
  }

  suspend fun acknowledgeOrder(orderId: String) {
    val order = dao.getOrderById(orderId) ?: return
    val updated = order.copy(
      isAcknowledgedByMerchant = true,
      orderStatus = "PREPARING",
      updatedAt = System.currentTimeMillis()
    )
    dao.updateOrder(updated)
    syncManager?.updateOrderStatus(updated)
  }

  suspend fun shipOrder(orderId: String, captainName: String, captainPhone: String) {
    val order = dao.getOrderById(orderId) ?: return
    val updated = order.copy(
      orderStatus = "SHIPPED",
      captainName = captainName.trim(),
      captainPhone = captainPhone.trim(),
      updatedAt = System.currentTimeMillis()
    )
    dao.updateOrder(updated)
    syncManager?.updateOrderStatus(updated)
  }

  suspend fun merchantConfirmDelivered(orderId: String) {
    val order = dao.getOrderById(orderId) ?: return
    val wasDelivered = order.orderStatus == "DELIVERED"
    val updated = order.copy(
      merchantConfirmedDelivered = true,
      orderStatus = "DELIVERED",
      updatedAt = System.currentTimeMillis()
    )
    dao.updateOrder(updated)
    syncManager?.updateOrderStatus(updated)

    if (!wasDelivered) {
      applyDeliveryFinancials(order)
    }
  }

  suspend fun customerConfirmDelivered(orderId: String) {
    val order = dao.getOrderById(orderId) ?: return
    val wasDelivered = order.orderStatus == "DELIVERED"
    val updated = order.copy(
      customerConfirmedDelivered = true,
      orderStatus = "DELIVERED",
      updatedAt = System.currentTimeMillis()
    )
    dao.updateOrder(updated)
    syncManager?.updateOrderStatus(updated)

    if (!wasDelivered) {
      applyDeliveryFinancials(order)
    }
  }

  private suspend fun applyDeliveryFinancials(order: OrderEntity) {
    val merchant = dao.getUserById(order.merchantId) ?: return
    // Automatically calculate sales value into monthly/total earnings,
    // and automatically calculate app commission into current debt
    val newEarnings = merchant.totalEarnings + order.totalProductsPrice
    val newDebt = merchant.currentDebt + order.appCommission
    val updatedMerchant = merchant.copy(
      totalEarnings = newEarnings,
      currentDebt = newDebt
    )
    dao.updateUser(updatedMerchant)
    syncManager?.pushUser(updatedMerchant)
  }

  // Debt Payments
  fun getAllDebtPayments(): Flow<List<DebtPaymentRequestEntity>> = dao.getAllDebtPayments()
  fun getDebtPaymentsForMerchant(merchantId: String): Flow<List<DebtPaymentRequestEntity>> = dao.getDebtPaymentsForMerchant(merchantId)

  suspend fun submitDebtPayment(
    merchantId: String,
    merchantName: String,
    amount: Double,
    method: String,
    proofUrl: String?,
    notes: String?
  ) {
    val req = DebtPaymentRequestEntity(
      id = "debt_pay_" + UUID.randomUUID().toString().take(8),
      merchantId = merchantId,
      merchantName = merchantName,
      amount = amount,
      paymentMethod = method,
      proofImageUrl = proofUrl,
      notes = notes,
      status = "PENDING"
    )
    dao.insertDebtPayment(req)
    syncManager?.pushDebtPayment(req)
  }

  suspend fun approveDebtPayment(paymentId: String, payment: DebtPaymentRequestEntity) {
    val approved = payment.copy(status = "APPROVED", reviewedAt = System.currentTimeMillis())
    dao.updateDebtPayment(approved)
    syncManager?.pushDebtPayment(approved)

    val merchant = dao.getUserById(payment.merchantId)
    if (merchant != null) {
      // Calculate remaining debt. If paid amount > debt, currentDebt becomes negative (positive credit balance)
      val newDebt = merchant.currentDebt - payment.amount
      val updatedMerchant = merchant.copy(currentDebt = newDebt)
      dao.updateUser(updatedMerchant)
      syncManager?.pushUser(updatedMerchant)
    }
  }

  suspend fun adminSettleMerchantDebt(
    merchantId: String,
    amount: Double,
    notes: String? = null
  ) {
    val merchant = dao.getUserById(merchantId) ?: return
    val newDebt = merchant.currentDebt - amount
    val updatedMerchant = merchant.copy(currentDebt = newDebt)
    dao.updateUser(updatedMerchant)
    syncManager?.pushUser(updatedMerchant)

    val req = DebtPaymentRequestEntity(
      id = "admin_settle_" + UUID.randomUUID().toString().take(8),
      merchantId = merchantId,
      merchantName = merchant.name,
      amount = amount,
      paymentMethod = "تسوية إدارة / كاش",
      proofImageUrl = null,
      notes = notes ?: "تسوية مديونية مباشرة عبر لوحة التحكم",
      status = "APPROVED",
      reviewedAt = System.currentTimeMillis()
    )
    dao.insertDebtPayment(req)
    syncManager?.pushDebtPayment(req)
  }

  suspend fun rejectDebtPayment(payment: DebtPaymentRequestEntity) {
    val rejected = payment.copy(status = "REJECTED", reviewedAt = System.currentTimeMillis())
    dao.updateDebtPayment(rejected)
    syncManager?.pushDebtPayment(rejected)
  }

  // Support & Complaints
  fun getAllComplaints(): Flow<List<SupportComplaintEntity>> = dao.getAllComplaints()
  fun getComplaintsForUser(userId: String): Flow<List<SupportComplaintEntity>> = dao.getComplaintsForUser(userId)
  suspend fun submitComplaint(complaint: SupportComplaintEntity) = dao.insertComplaint(complaint)
  suspend fun resolveComplaint(complaint: SupportComplaintEntity, adminResponse: String) {
    dao.updateComplaint(complaint.copy(status = "RESOLVED", adminNotes = adminResponse))
  }

  // Password Reset
  fun getAllPasswordResets(): Flow<List<PasswordResetRequestEntity>> = dao.getAllPasswordResets()

  suspend fun requestPasswordReset(phone: String): Result<String> {
    val user = dao.getUserByPhone(phone.trim())
      ?: return Result.failure(Exception("لم يتم العثور على حساب مرتبط برقم الهاتف المدخل"))
    val code = (100000 + (Math.random() * 900000).toInt()).toString()
    val req = PasswordResetRequestEntity(
      id = "reset_" + UUID.randomUUID().toString().take(8),
      phone = phone.trim(),
      userEmail = user.email,
      userName = user.name,
      tempResetCode = code,
      status = "PENDING"
    )
    dao.insertPasswordReset(req)
    return Result.success(code)
  }

  suspend fun verifyAndResetPassword(phone: String, code: String, newPass: String): Result<Unit> {
    val user = dao.getUserByPhone(phone.trim())
      ?: return Result.failure(Exception("رقم الهاتف غير موجود"))
    val updated = user.copy(password = newPass.trim())
    dao.updateUser(updated)
    syncManager?.pushUser(updated)
    return Result.success(Unit)
  }

  // App Settings
  fun getSettings(): Flow<AppSettingsEntity?> = dao.getSettings()
  suspend fun getSettingsDirect(): AppSettingsEntity = dao.getSettingsDirect() ?: AppSettingsEntity()
  suspend fun updateSettings(settings: AppSettingsEntity) {
    dao.updateSettings(settings)
    syncManager?.pushSettings(settings)
  }
}
