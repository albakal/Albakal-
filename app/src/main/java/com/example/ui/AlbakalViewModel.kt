package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AlbakalDatabase
import com.example.data.model.AppSettingsEntity
import com.example.data.model.CartItemEntity
import com.example.data.model.DebtPaymentRequestEntity
import com.example.data.model.GovernorateAreaEntity
import com.example.data.model.OrderEntity
import com.example.data.model.PasswordResetRequestEntity
import com.example.data.model.ProductCategoryEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SponsoredAdEntity
import com.example.data.model.SupervisorPermissionEntity
import com.example.data.model.SupportComplaintEntity
import com.example.data.model.UserEntity
import com.example.data.repository.AlbakalRepository
import com.example.util.OrderAlarmManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlbakalViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: AlbakalRepository
  private val alarmManager: OrderAlarmManager = OrderAlarmManager(application)

  init {
    val db = AlbakalDatabase.getDatabase(application)
    repository = AlbakalRepository(db.appDao())
  }

  // Session & Navigation State
  private val _currentUser = MutableStateFlow<UserEntity?>(null)
  val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

  private val _currentScreen = MutableStateFlow("LOGIN")
  val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

  private val _toastMessage = MutableSharedFlow<String>()
  val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

  // Dynamic Flows from Database
  val allProducts: StateFlow<List<ProductEntity>> = repository.getAllProducts()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val publishedProducts: StateFlow<List<ProductEntity>> = repository.getPublishedProducts()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val categories: StateFlow<List<ProductCategoryEntity>> = repository.getAllCategories()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val sponsoredAds: StateFlow<List<SponsoredAdEntity>> = repository.getActiveAds()
    .map { list ->
      val now = System.currentTimeMillis()
      list.filter { it.expiryDate <= 0L || it.expiryDate > now }
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allAds: StateFlow<List<SponsoredAdEntity>> = repository.getAllAds()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val governoratesWithDistricts: StateFlow<List<GovernorateAreaEntity>> = repository.getAllAreas()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val appSettings: StateFlow<AppSettingsEntity> = repository.getSettings()
    .map { it ?: AppSettingsEntity() }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettingsEntity())

  val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val pendingUsers: StateFlow<List<UserEntity>> = repository.getPendingUsers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allOrders: StateFlow<List<OrderEntity>> = repository.getAllOrders()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allDebtPayments: StateFlow<List<DebtPaymentRequestEntity>> = repository.getAllDebtPayments()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allComplaints: StateFlow<List<SupportComplaintEntity>> = repository.getAllComplaints()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val passwordResets: StateFlow<List<PasswordResetRequestEntity>> = repository.getAllPasswordResets()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Customer Cart
  private val _cart = MutableStateFlow<List<CartItemEntity>>(emptyList())
  val cart: StateFlow<List<CartItemEntity>> = _cart.asStateFlow()

  // Supervisor Permissions
  private val _supervisorPermissions = MutableStateFlow<SupervisorPermissionEntity?>(null)
  val supervisorPermissions: StateFlow<SupervisorPermissionEntity?> = _supervisorPermissions.asStateFlow()

  // Continuous Sound Alert State for Merchant
  private val _hasActiveAlarm = MutableStateFlow(false)
  val hasActiveAlarm: StateFlow<Boolean> = _hasActiveAlarm.asStateFlow()

  fun navigateTo(screen: String) {
    _currentScreen.value = screen
  }

  fun showMessage(msg: String) {
    viewModelScope.launch {
      _toastMessage.emit(msg)
    }
  }

  // Auth Operations
  fun login(emailOrPhone: String, pass: String, onSuccess: () -> Unit) {
    viewModelScope.launch {
      val res = repository.login(emailOrPhone, pass)
      res.onSuccess { user ->
        _currentUser.value = user
        // Route according to user role
        when (user.role) {
          "ADMIN" -> _currentScreen.value = "ADMIN_DASHBOARD"
          "SUPERVISOR" -> {
            loadSupervisorPermissions(user.id)
            _currentScreen.value = "ADMIN_DASHBOARD"
          }
          "MERCHANT" -> {
            _currentScreen.value = "MERCHANT_HOME"
            startListeningForMerchantOrders(user.id)
          }
          "CAPTAIN" -> _currentScreen.value = "CAPTAIN_HOME"
          else -> { // CUSTOMER
            _currentScreen.value = "CUSTOMER_HOME"
            observeCustomerCart(user.id)
          }
        }
        showMessage("مرحباً بك يا ${user.name}")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل تسجيل الدخول")
      }
    }
  }

  fun registerUser(user: UserEntity, onSuccess: () -> Unit) {
    viewModelScope.launch {
      val res = repository.registerUser(user)
      res.onSuccess {
        showMessage("تم إرسال طلب إنشاء الحساب بنجاح! سيتم مراجعته وتفعيله من قبل الإدارة.")
        _currentScreen.value = "LOGIN"
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل إنشاء الحساب")
      }
    }
  }

  fun logout() {
    alarmManager.stopAlert()
    _hasActiveAlarm.value = false
    _currentUser.value = null
    _supervisorPermissions.value = null
    _cart.value = emptyList()
    _currentScreen.value = "LOGIN"
    showMessage("تم تسجيل الخروج بنجاح")
  }

  private fun loadSupervisorPermissions(userId: String) {
    viewModelScope.launch {
      val perms = repository.getSupervisorPermissionsDirect(userId)
      _supervisorPermissions.value = perms ?: SupervisorPermissionEntity(userId = userId)
    }
  }

  private fun observeCustomerCart(customerId: String) {
    viewModelScope.launch {
      repository.getCart(customerId).collect { items ->
        _cart.value = items
      }
    }
  }

  private fun startListeningForMerchantOrders(merchantId: String) {
    viewModelScope.launch {
      repository.getNewUnacknowledgedOrders(merchantId).collect { unackOrders ->
        if (unackOrders.isNotEmpty()) {
          _hasActiveAlarm.value = true
          alarmManager.startAlert()
        } else {
          _hasActiveAlarm.value = false
          alarmManager.stopAlert()
        }
      }
    }
  }

  // Cart actions
  fun addToCart(product: ProductEntity, qty: Int = 1) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val price = product.discountPrice ?: product.price
      repository.addToCart(
        customerId = user.id,
        productId = product.id,
        productName = product.name,
        merchantId = product.merchantId,
        merchantName = product.merchantName,
        price = price,
        quantity = qty,
        imageUrl = product.imageUrl
      )
      showMessage("تمت إضافة ${product.name} إلى السلة")
    }
  }

  fun removeFromCart(cartItemId: String) {
    viewModelScope.launch {
      repository.removeCartItem(cartItemId)
    }
  }

  fun clearCart() {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      repository.clearCart(user.id)
    }
  }

  // Placing Order
  fun placeOrder(
    paymentMethod: String,
    paymentProofUrl: String?,
    onSuccess: () -> Unit
  ) {
    val user = _currentUser.value ?: return
    val items = _cart.value
    if (items.isEmpty()) {
      showMessage("السلة فارغة")
      return
    }

    viewModelScope.launch {
      val settings = appSettings.value
      val res = repository.placeOrder(
        customer = user,
        cartItems = items,
        shippingFee = settings.shippingFee,
        commissionPercent = settings.commissionRatePercent,
        paymentMethod = paymentMethod,
        paymentProofUrl = paymentProofUrl
      )
      res.onSuccess { orderNums ->
        showMessage("تم تأكيد طلبك بنجاح! رقم الطلب: $orderNums")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل إرسال الطلب")
      }
    }
  }

  // Merchant Order Actions
  fun acknowledgeOrder(orderId: String) {
    viewModelScope.launch {
      repository.acknowledgeOrder(orderId)
      showMessage("تم قبول الطلب وتحويله إلى: جاري التحضير")
    }
  }

  fun shipOrder(orderId: String, captainName: String, captainPhone: String) {
    viewModelScope.launch {
      repository.shipOrder(orderId, captainName, captainPhone)
      showMessage("تم تحديث حالة الطلب إلى: جاري الإرسال مع الكابتن $captainName")
    }
  }

  fun merchantConfirmDelivered(orderId: String) {
    viewModelScope.launch {
      repository.merchantConfirmDelivered(orderId)
      showMessage("تم تأكيد تسليم الطلب من جانب التاجر")
    }
  }

  fun customerConfirmDelivered(orderId: String) {
    viewModelScope.launch {
      repository.customerConfirmDelivered(orderId)
      showMessage("شكراً لك! تم تأكيد استلام الطلب")
    }
  }

  // Merchant Debt Repayment
  fun submitDebtPayment(amount: Double, method: String, proofUrl: String?, notes: String?, onComplete: () -> Unit) {
    val merchant = _currentUser.value ?: return
    viewModelScope.launch {
      repository.submitDebtPayment(merchant.id, merchant.name, amount, method, proofUrl, notes)
      showMessage("تم رفع طلب سداد المديونية بنجاح! سيتم مراجعته واعتماده من الإدارة.")
      onComplete()
    }
  }

  // Admin Actions
  fun approveUser(userId: String) {
    viewModelScope.launch {
      repository.approveUser(userId)
      showMessage("تمت الموافقة على تفعيل الحساب")
    }
  }

  fun rejectUser(userId: String) {
    viewModelScope.launch {
      repository.rejectUser(userId)
      showMessage("تم رفض طلب الحساب")
    }
  }

  fun banMerchant(merchantId: String, reason: String) {
    viewModelScope.launch {
      repository.banMerchant(merchantId, reason)
      showMessage("تم حظر التاجر وإخفاء منتجاته")
    }
  }

  fun unbanMerchant(merchantId: String) {
    viewModelScope.launch {
      repository.unbanMerchant(merchantId)
      showMessage("تم فك الحظر عن التاجر وإعادة منتجاته")
    }
  }

  fun approveDebtPayment(payment: DebtPaymentRequestEntity) {
    viewModelScope.launch {
      repository.approveDebtPayment(payment.id, payment)
      showMessage("تم اعتماد سداد المديونية وتحديث رصيد التاجر")
    }
  }

  fun rejectDebtPayment(payment: DebtPaymentRequestEntity) {
    viewModelScope.launch {
      repository.rejectDebtPayment(payment)
      showMessage("تم رفض طلب سداد المديونية")
    }
  }

  fun updateSettings(settings: AppSettingsEntity) {
    viewModelScope.launch {
      repository.updateSettings(settings)
      showMessage("تم حفظ إعدادات التطبيق بنجاح")
    }
  }

  fun addCategory(name: String) {
    viewModelScope.launch {
      repository.addCategory(name)
      showMessage("تمت إضافة القسم")
    }
  }

  fun addArea(gov: String, dist: String) {
    viewModelScope.launch {
      repository.addArea(gov, dist)
      showMessage("تمت إضافة المنطقة")
    }
  }

  fun addProduct(product: ProductEntity) {
    viewModelScope.launch {
      repository.addProduct(product)
      showMessage("تمت إضافة المنتج بنجاح")
    }
  }

  fun updateProduct(product: ProductEntity) {
    viewModelScope.launch {
      repository.updateProduct(product)
      showMessage("تم تحديث المنتج")
    }
  }

  fun deleteProduct(productId: String) {
    viewModelScope.launch {
      repository.deleteProduct(productId)
      showMessage("تم حذف المنتج")
    }
  }

  fun addAd(ad: SponsoredAdEntity) {
    viewModelScope.launch {
      repository.addAd(ad)
      showMessage("تمت إضافة الإعلان بنجاح")
    }
  }

  fun toggleAdActive(ad: SponsoredAdEntity) {
    viewModelScope.launch {
      repository.updateAd(ad.copy(isActive = !ad.isActive))
    }
  }

  fun deleteAd(adId: String) {
    viewModelScope.launch {
      repository.deleteAd(adId)
      showMessage("تم حذف الإعلان")
    }
  }

  fun createSupervisor(name: String, email: String, phone: String, pass: String, perms: SupervisorPermissionEntity, onSuccess: () -> Unit) {
    viewModelScope.launch {
      val res = repository.createSupervisor(name, email, phone, pass, perms)
      res.onSuccess {
        showMessage("تم إنشاء حساب المشرف وتحديد صلاحياته بنجاح")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل إنشاء المشرف")
      }
    }
  }

  fun updateSupervisorPermissions(perms: SupervisorPermissionEntity) {
    viewModelScope.launch {
      repository.saveSupervisorPermissions(perms)
      showMessage("تم تحديث صلاحيات المشرف")
    }
  }

  fun updateAdminCredentials(newEmail: String, newPass: String, onSuccess: () -> Unit) {
    val admin = _currentUser.value ?: return
    viewModelScope.launch {
      val res = repository.updateAdminCredentials(admin.id, newEmail, newPass)
      res.onSuccess {
        _currentUser.value = admin.copy(email = newEmail, password = newPass)
        showMessage("تم تحديث بيانات حساب الإدارة بنجاح")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل تحديث البيانات")
      }
    }
  }

  fun submitComplaint(subject: String, details: String, isDamaged: Boolean, merchantId: String?) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val complaint = SupportComplaintEntity(
        id = "comp_" + java.util.UUID.randomUUID().toString().take(8),
        userId = user.id,
        userName = user.name,
        userPhone = user.phone,
        userRole = user.role,
        subject = subject,
        details = details,
        isDamagedGoodsReport = isDamaged,
        merchantIdReported = merchantId
      )
      repository.submitComplaint(complaint)
      showMessage("تم إرسال بلاغك/شكواك بنجاح وسيتواصل معك الدعم الفني")
    }
  }

  fun resolveComplaint(complaint: SupportComplaintEntity, note: String) {
    viewModelScope.launch {
      repository.resolveComplaint(complaint, note)
      showMessage("تم حل الشكوى")
    }
  }

  fun requestPasswordReset(phone: String, onCodeGenerated: (String) -> Unit) {
    viewModelScope.launch {
      val res = repository.requestPasswordReset(phone)
      res.onSuccess { code ->
        showMessage("تم تقديم طلب استعادة الحساب كود التحقق المؤقت: $code")
        onCodeGenerated(code)
      }.onFailure { err ->
        showMessage(err.message ?: "فشل تقديم طلب الاستعادة")
      }
    }
  }

  fun verifyAndResetPassword(phone: String, code: String, newPass: String, onSuccess: () -> Unit) {
    viewModelScope.launch {
      val res = repository.verifyAndResetPassword(phone, code, newPass)
      res.onSuccess {
        showMessage("تم تغيير كلمة المرور بنجاح! يمكنك الآن تسجيل الدخول.")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل تعيين كلمة المرور")
      }
    }
  }

  fun acceptHonorCode() {
    val merchant = _currentUser.value ?: return
    viewModelScope.launch {
      repository.acceptHonorCode(merchant.id)
      _currentUser.value = merchant.copy(honorCodeAccepted = true)
      showMessage("تمت الموافقة على ميثاق الشرف للتجار")
    }
  }

  override fun onCleared() {
    super.onCleared()
    alarmManager.stopAlert()
  }
}
