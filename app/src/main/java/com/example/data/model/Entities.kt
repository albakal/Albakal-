package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey val id: String,
  val name: String,
  val email: String,
  val phone: String,
  val password: String,
  val role: String, // ADMIN, SUPERVISOR, MERCHANT, CUSTOMER, CAPTAIN
  val status: String, // APPROVED, PENDING_APPROVAL, REJECTED, BANNED
  val banReason: String? = null,
  val governorate: String,
  val district: String,
  val street: String,
  val apartment: String,
  val floor: String,
  val monthlySubscriptionActive: Boolean = false,
  val subscriptionExpiresAt: Long = 0L,
  val currentDebt: Double = 0.0,
  val totalEarnings: Double = 0.0,
  val honorCodeAccepted: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "supervisor_permissions")
data class SupervisorPermissionEntity(
  @PrimaryKey val userId: String,
  val canManageCustomers: Boolean = true,
  val canManageMerchants: Boolean = true,
  val canReviewRegistrations: Boolean = true,
  val canManageProducts: Boolean = true,
  val canManageOffers: Boolean = true,
  val canManageOrders: Boolean = true,
  val canViewFinancials: Boolean = false,
  val canManageDebts: Boolean = false,
  val canBanMerchants: Boolean = false,
  val canManageAds: Boolean = false,
  val canManageSupport: Boolean = true,
  val canManageStoreSettings: Boolean = false
)

@Entity(tableName = "governorate_areas")
data class GovernorateAreaEntity(
  @PrimaryKey val id: String,
  val governorate: String,
  val district: String
)

@Entity(tableName = "product_categories")
data class ProductCategoryEntity(
  @PrimaryKey val id: String,
  val name: String,
  val displayOrder: Int = 0
)

@Entity(tableName = "products")
data class ProductEntity(
  @PrimaryKey val id: String,
  val merchantId: String,
  val merchantName: String,
  val name: String,
  val categoryName: String,
  val description: String,
  val price: Double,
  val discountPrice: Double? = null,
  val imageUrl: String,
  val unit: String,
  val isPublished: Boolean = true,
  val isOffer: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sponsored_ads")
data class SponsoredAdEntity(
  @PrimaryKey val id: String,
  val title: String,
  val category: String,
  val details: String,
  val imageUrl: String,
  val durationDays: Int = 30,
  val expiryDate: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
  val isActive: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
  @PrimaryKey val id: String,
  val customerId: String,
  val productId: String,
  val productName: String,
  val merchantId: String,
  val merchantName: String,
  val price: Double,
  val quantity: Int,
  val imageUrl: String
)

@Entity(tableName = "orders")
data class OrderEntity(
  @PrimaryKey val id: String,
  val orderNumber: String,
  val customerId: String,
  val customerName: String,
  val customerPhone: String,
  val customerAddress: String,
  val merchantId: String,
  val merchantName: String,
  val totalProductsPrice: Double,
  val shippingFee: Double,
  val grandTotal: Double,
  val appCommission: Double,
  val paymentMethod: String, // CASH_ON_DELIVERY, VODAFONE_CASH, INSTAPAY
  val paymentProofUrl: String? = null,
  val paymentStatus: String, // PENDING_VERIFICATION, VERIFIED, CASH_ON_DELIVERY
  val orderStatus: String, // NEW, PREPARING, SHIPPED, DELIVERED, CANCELLED
  val captainName: String? = null,
  val captainPhone: String? = null,
  val isAcknowledgedByMerchant: Boolean = false, // When true, alert sound ceases
  val customerConfirmedDelivered: Boolean = false,
  val merchantConfirmedDelivered: Boolean = false,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
  @PrimaryKey val id: String,
  val orderId: String,
  val productId: String,
  val productName: String,
  val quantity: Int,
  val unitPrice: Double,
  val totalPrice: Double
)

@Entity(tableName = "debt_payment_requests")
data class DebtPaymentRequestEntity(
  @PrimaryKey val id: String,
  val merchantId: String,
  val merchantName: String,
  val amount: Double,
  val paymentMethod: String, // VODAFONE_CASH, INSTAPAY
  val proofImageUrl: String? = null,
  val notes: String? = null,
  val status: String, // PENDING, APPROVED, REJECTED
  val createdAt: Long = System.currentTimeMillis(),
  val reviewedAt: Long? = null
)

@Entity(tableName = "support_complaints")
data class SupportComplaintEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val userName: String,
  val userPhone: String,
  val userRole: String,
  val subject: String,
  val details: String,
  val isDamagedGoodsReport: Boolean = false,
  val merchantIdReported: String? = null,
  val status: String = "OPEN", // OPEN, RESOLVED
  val adminNotes: String? = null,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "password_reset_requests")
data class PasswordResetRequestEntity(
  @PrimaryKey val id: String,
  val phone: String,
  val userEmail: String? = null,
  val userName: String? = null,
  val tempResetCode: String,
  val status: String, // PENDING, COMPLETED
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
  @PrimaryKey val id: Int = 1,
  val shippingFee: Double = 25.0,
  val commissionRatePercent: Double = 5.0,
  val customerMonthlySubscriptionFee: Double = 50.0,
  val isCustomerSubscriptionActive: Boolean = false,
  val isCaptainsFleetInterfaceActive: Boolean = false,
  val whatsappSupportNumber: String = "+201012345678",
  val vodafoneCashNumber: String = "01099887766",
  val instaPayHandle: String = "albakal@instapay",
  val cashOnDeliveryEnabled: Boolean = true,
  val vodafoneCashEnabled: Boolean = true,
  val instaPayEnabled: Boolean = true
)
