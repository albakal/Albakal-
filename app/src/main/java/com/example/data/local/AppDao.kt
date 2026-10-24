package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
  // Users
  @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
  suspend fun getUserById(id: String): UserEntity?

  @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
  suspend fun getUserByEmail(email: String): UserEntity?

  @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
  suspend fun getUserByPhone(phone: String): UserEntity?

  @Query("SELECT * FROM users ORDER BY createdAt DESC")
  fun getAllUsers(): Flow<List<UserEntity>>

  @Query("SELECT * FROM users WHERE role = :role ORDER BY createdAt DESC")
  fun getUsersByRole(role: String): Flow<List<UserEntity>>

  @Query("SELECT * FROM users WHERE status = 'PENDING_APPROVAL' ORDER BY createdAt DESC")
  fun getPendingUsers(): Flow<List<UserEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUsers(users: List<UserEntity>)

  @Update
  suspend fun updateUser(user: UserEntity)

  @Query("DELETE FROM users WHERE id = :id")
  suspend fun deleteUserById(id: String)

  // Supervisor Permissions
  @Query("SELECT * FROM supervisor_permissions WHERE userId = :userId LIMIT 1")
  fun getPermissionsForUser(userId: String): Flow<SupervisorPermissionEntity?>

  @Query("SELECT * FROM supervisor_permissions WHERE userId = :userId LIMIT 1")
  suspend fun getPermissionsDirect(userId: String): SupervisorPermissionEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPermission(permission: SupervisorPermissionEntity)

  // Governorates & Districts
  @Query("SELECT * FROM governorate_areas ORDER BY governorate ASC, district ASC")
  fun getAllAreas(): Flow<List<GovernorateAreaEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertArea(area: GovernorateAreaEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAreas(areas: List<GovernorateAreaEntity>)

  @Query("DELETE FROM governorate_areas WHERE id = :id")
  suspend fun deleteAreaById(id: String)

  // Categories
  @Query("SELECT * FROM product_categories ORDER BY displayOrder ASC")
  fun getAllCategories(): Flow<List<ProductCategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: ProductCategoryEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategories(categories: List<ProductCategoryEntity>)

  @Query("DELETE FROM product_categories WHERE id = :id")
  suspend fun deleteCategoryById(id: String)

  // Products
  @Query("SELECT * FROM products ORDER BY createdAt DESC")
  fun getAllProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE isPublished = 1 ORDER BY createdAt DESC")
  fun getPublishedProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE merchantId = :merchantId ORDER BY createdAt DESC")
  fun getProductsByMerchant(merchantId: String): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
  suspend fun getProductById(id: String): ProductEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProduct(product: ProductEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProducts(products: List<ProductEntity>)

  @Update
  suspend fun updateProduct(product: ProductEntity)

  @Query("DELETE FROM products WHERE id = :id")
  suspend fun deleteProductById(id: String)

  // Sponsored Ads
  @Query("SELECT * FROM sponsored_ads ORDER BY createdAt DESC")
  fun getAllAds(): Flow<List<SponsoredAdEntity>>

  @Query("SELECT * FROM sponsored_ads WHERE isActive = 1 ORDER BY createdAt DESC")
  fun getActiveAds(): Flow<List<SponsoredAdEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAd(ad: SponsoredAdEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAds(ads: List<SponsoredAdEntity>)

  @Update
  suspend fun updateAd(ad: SponsoredAdEntity)

  @Query("DELETE FROM sponsored_ads WHERE id = :id")
  suspend fun deleteAdById(id: String)

  // Cart
  @Query("SELECT * FROM cart_items WHERE customerId = :customerId")
  fun getCartForCustomer(customerId: String): Flow<List<CartItemEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCartItem(item: CartItemEntity)

  @Update
  suspend fun updateCartItem(item: CartItemEntity)

  @Query("DELETE FROM cart_items WHERE id = :id")
  suspend fun deleteCartItem(id: String)

  @Query("DELETE FROM cart_items WHERE customerId = :customerId")
  suspend fun clearCartForCustomer(customerId: String)

  // Orders
  @Query("SELECT * FROM orders ORDER BY createdAt DESC")
  fun getAllOrders(): Flow<List<OrderEntity>>

  @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY createdAt DESC")
  fun getOrdersForCustomer(customerId: String): Flow<List<OrderEntity>>

  @Query("SELECT * FROM orders WHERE merchantId = :merchantId ORDER BY createdAt DESC")
  fun getOrdersForMerchant(merchantId: String): Flow<List<OrderEntity>>

  @Query("SELECT * FROM orders WHERE merchantId = :merchantId AND orderStatus = 'NEW' AND isAcknowledgedByMerchant = 0")
  fun getNewUnacknowledgedOrdersForMerchant(merchantId: String): Flow<List<OrderEntity>>

  @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
  suspend fun getOrderById(orderId: String): OrderEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: OrderEntity)

  @Update
  suspend fun updateOrder(order: OrderEntity)

  // Order Items
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrderItems(items: List<OrderItemEntity>)

  @Query("SELECT * FROM order_items WHERE orderId = :orderId")
  fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>>

  @Query("SELECT * FROM order_items WHERE orderId = :orderId")
  suspend fun getOrderItemsDirect(orderId: String): List<OrderItemEntity>

  // Debt Payment Requests
  @Query("SELECT * FROM debt_payment_requests ORDER BY createdAt DESC")
  fun getAllDebtPayments(): Flow<List<DebtPaymentRequestEntity>>

  @Query("SELECT * FROM debt_payment_requests WHERE merchantId = :merchantId ORDER BY createdAt DESC")
  fun getDebtPaymentsForMerchant(merchantId: String): Flow<List<DebtPaymentRequestEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDebtPayment(payment: DebtPaymentRequestEntity)

  @Update
  suspend fun updateDebtPayment(payment: DebtPaymentRequestEntity)

  // Support & Complaints
  @Query("SELECT * FROM support_complaints ORDER BY createdAt DESC")
  fun getAllComplaints(): Flow<List<SupportComplaintEntity>>

  @Query("SELECT * FROM support_complaints WHERE userId = :userId ORDER BY createdAt DESC")
  fun getComplaintsForUser(userId: String): Flow<List<SupportComplaintEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertComplaint(complaint: SupportComplaintEntity)

  @Update
  suspend fun updateComplaint(complaint: SupportComplaintEntity)

  // Password Resets
  @Query("SELECT * FROM password_reset_requests ORDER BY createdAt DESC")
  fun getAllPasswordResets(): Flow<List<PasswordResetRequestEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPasswordReset(request: PasswordResetRequestEntity)

  @Update
  suspend fun updatePasswordReset(request: PasswordResetRequestEntity)

  // App Settings
  @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
  fun getSettings(): Flow<AppSettingsEntity?>

  @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
  suspend fun getSettingsDirect(): AppSettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSettings(settings: AppSettingsEntity)

  @Update
  suspend fun updateSettings(settings: AppSettingsEntity)
}
