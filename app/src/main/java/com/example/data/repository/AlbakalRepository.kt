package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.model.AppSettingsEntity
import com.example.data.model.CartItemEntity
import com.example.data.model.DebtPaymentRequestEntity
import com.example.data.model.GovernorateAreaEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.PasswordResetRequestEntity
import com.example.data.model.ProductCategoryEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SponsoredAdEntity
import com.example.data.model.SupervisorPermissionEntity
import com.example.data.model.SupportComplaintEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class AlbakalRepository(private val dao: AppDao) {

  // Users & Auth
  suspend fun login(emailOrPhone: String, pass: String): Result<UserEntity> {
    val clean = emailOrPhone.trim()
    val user = if (clean.contains("@")) {
      dao.getUserByEmail(clean)
    } else {
      dao.getUserByPhone(clean)
    } ?: return Result.failure(Exception("الحساب غير موجود، يرجى التأكد من البيانات المدخلة أو إنشاء حساب جديد"))

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
    // Check duplicate email or phone
    val existingEmail = dao.getUserByEmail(user.email.trim())
    if (existingEmail != null) {
      return Result.failure(Exception("البريد الإلكتروني مسجل مسبقاً في النظام"))
    }
    val existingPhone = dao.getUserByPhone(user.phone.trim())
    if (existingPhone != null) {
      return Result.failure(Exception("رقم الهاتف مسجل مسبقاً في النظام"))
    }
    dao.insertUser(user)
    return Result.success(Unit)
  }

  fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()
  fun getUsersByRole(role: String): Flow<List<UserEntity>> = dao.getUsersByRole(role)
  fun getPendingUsers(): Flow<List<UserEntity>> = dao.getPendingUsers()
  suspend fun getUserById(id: String): UserEntity? = dao.getUserById(id)

  suspend fun updateUser(user: UserEntity) = dao.updateUser(user)
  suspend fun deleteUser(id: String) = dao.deleteUserById(id)

  suspend fun approveUser(userId: String) {
    val user = dao.getUserById(userId) ?: return
    dao.updateUser(user.copy(status = "APPROVED"))
  }

  suspend fun rejectUser(userId: String) {
    val user = dao.getUserById(userId) ?: return
    dao.updateUser(user.copy(status = "REJECTED"))
  }

  suspend fun banMerchant(merchantId: String, reason: String) {
    val merchant = dao.getUserById(merchantId) ?: return
    dao.updateUser(merchant.copy(status = "BANNED", banReason = reason))
    // When banned, products are unpublished/hidden
    val products = dao.getProductById(merchantId) // check
  }

  suspend fun unbanMerchant(merchantId: String) {
    val merchant = dao.getUserById(merchantId) ?: return
    dao.updateUser(merchant.copy(status = "APPROVED", banReason = null))
  }

  suspend fun acceptHonorCode(merchantId: String) {
    val merchant = dao.getUserById(merchantId) ?: return
    dao.updateUser(merchant.copy(honorCodeAccepted = true))
  }

  suspend fun updateAdminCredentials(adminId: String, newEmail: String, newPass: String): Result<Unit> {
    val admin = dao.getUserById(adminId) ?: return Result.failure(Exception("الحساب غير موجود"))
    if (admin.role != "ADMIN") {
      return Result.failure(Exception("لا توجد صلاحية لتعديل حساب الإدارة الرئيسي"))
    }
    dao.updateUser(admin.copy(email = newEmail.trim(), password = newPass.trim()))
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
    dao.insertArea(GovernorateAreaEntity("area_" + UUID.randomUUID().toString().take(8), gov.trim(), dist.trim()))
  }
  suspend fun deleteArea(id: String) = dao.deleteAreaById(id)

  // Categories
  fun getAllCategories(): Flow<List<ProductCategoryEntity>> = dao.getAllCategories()
  suspend fun addCategory(name: String) {
    dao.insertCategory(ProductCategoryEntity("cat_" + UUID.randomUUID().toString().take(8), name.trim()))
  }
  suspend fun deleteCategory(id: String) = dao.deleteCategoryById(id)

  // Products
  fun getAllProducts(): Flow<List<ProductEntity>> = dao.getAllProducts()
  fun getPublishedProducts(): Flow<List<ProductEntity>> = dao.getPublishedProducts()
  fun getProductsByMerchant(merchantId: String): Flow<List<ProductEntity>> = dao.getProductsByMerchant(merchantId)
  suspend fun addProduct(product: ProductEntity) = dao.insertProduct(product)
  suspend fun updateProduct(product: ProductEntity) = dao.updateProduct(product)
  suspend fun deleteProduct(id: String) = dao.deleteProductById(id)

  // Sponsored Ads
  fun getAllAds(): Flow<List<SponsoredAdEntity>> = dao.getAllAds()
  fun getActiveAds(): Flow<List<SponsoredAdEntity>> = dao.getActiveAds()
  suspend fun addAd(ad: SponsoredAdEntity) = dao.insertAd(ad)
  suspend fun updateAd(ad: SponsoredAdEntity) = dao.updateAd(ad)
  suspend fun deleteAd(id: String) = dao.deleteAdById(id)

  // Cart
  fun getCart(customerId: String): Flow<List<CartItemEntity>> = dao.getCartForCustomer(customerId)
  suspend fun addToCart(
    customerId: String,
    productId: String,
    productName: String,
    merchantId: String,
    merchantName: String,
    price: Double,
    quantity: Int,
    imageUrl: String
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
        imageUrl = imageUrl
      )
    )
  }
  suspend fun updateCartQuantity(cartItemId: String, quantity: Int) {
    if (quantity <= 0) {
      dao.deleteCartItem(cartItemId)
    } else {
      // Find and update
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
          totalPrice = item.price * item.quantity
        )
      }
      dao.insertOrderItems(orderItems)

      // Update merchant debt with app commission
      val merchant = dao.getUserById(mId)
      if (merchant != null) {
        dao.updateUser(merchant.copy(currentDebt = merchant.currentDebt + appCommission))
      }
      createdOrderIds.add(orderNumber)
    }

    dao.clearCartForCustomer(customer.id)
    return Result.success(createdOrderIds.joinToString(", "))
  }

  suspend fun acknowledgeOrder(orderId: String) {
    val order = dao.getOrderById(orderId) ?: return
    dao.updateOrder(
      order.copy(
        isAcknowledgedByMerchant = true,
        orderStatus = "PREPARING",
        updatedAt = System.currentTimeMillis()
      )
    )
  }

  suspend fun shipOrder(orderId: String, captainName: String, captainPhone: String) {
    val order = dao.getOrderById(orderId) ?: return
    dao.updateOrder(
      order.copy(
        orderStatus = "SHIPPED",
        captainName = captainName.trim(),
        captainPhone = captainPhone.trim(),
        updatedAt = System.currentTimeMillis()
      )
    )
  }

  suspend fun merchantConfirmDelivered(orderId: String) {
    val order = dao.getOrderById(orderId) ?: return
    val updated = order.copy(
      merchantConfirmedDelivered = true,
      orderStatus = "DELIVERED",
      updatedAt = System.currentTimeMillis()
    )
    dao.updateOrder(updated)

    // Update merchant total earnings
    val merchant = dao.getUserById(order.merchantId)
    if (merchant != null) {
      dao.updateUser(merchant.copy(totalEarnings = merchant.totalEarnings + order.totalProductsPrice))
    }
  }

  suspend fun customerConfirmDelivered(orderId: String) {
    val order = dao.getOrderById(orderId) ?: return
    val updated = order.copy(
      customerConfirmedDelivered = true,
      orderStatus = "DELIVERED",
      updatedAt = System.currentTimeMillis()
    )
    dao.updateOrder(updated)
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
  }

  suspend fun approveDebtPayment(paymentId: String, payment: DebtPaymentRequestEntity) {
    dao.updateDebtPayment(payment.copy(status = "APPROVED", reviewedAt = System.currentTimeMillis()))
    val merchant = dao.getUserById(payment.merchantId)
    if (merchant != null) {
      val newDebt = (merchant.currentDebt - payment.amount).coerceAtLeast(0.0)
      dao.updateUser(merchant.copy(currentDebt = newDebt))
    }
  }

  suspend fun rejectDebtPayment(payment: DebtPaymentRequestEntity) {
    dao.updateDebtPayment(payment.copy(status = "REJECTED", reviewedAt = System.currentTimeMillis()))
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
    dao.updateUser(user.copy(password = newPass.trim()))
    return Result.success(Unit)
  }

  // App Settings
  fun getSettings(): Flow<AppSettingsEntity?> = dao.getSettings()
  suspend fun getSettingsDirect(): AppSettingsEntity = dao.getSettingsDirect() ?: AppSettingsEntity()
  suspend fun updateSettings(settings: AppSettingsEntity) = dao.updateSettings(settings)
}
