package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
  entities = [
    UserEntity::class,
    SupervisorPermissionEntity::class,
    GovernorateAreaEntity::class,
    ProductCategoryEntity::class,
    ProductEntity::class,
    SponsoredAdEntity::class,
    CartItemEntity::class,
    OrderEntity::class,
    OrderItemEntity::class,
    DebtPaymentRequestEntity::class,
    SupportComplaintEntity::class,
    PasswordResetRequestEntity::class,
    AppSettingsEntity::class
  ],
  version = 3,
  exportSchema = false
)
abstract class AlbakalDatabase : RoomDatabase() {
  abstract fun appDao(): AppDao

  companion object {
    @Volatile
    private var INSTANCE: AlbakalDatabase? = null

    fun getDatabase(context: Context): AlbakalDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AlbakalDatabase::class.java,
          "albakal_database"
        )
        .fallbackToDestructiveMigration()
        .addCallback(object : Callback() {
          override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Seed initial mandatory system data
            CoroutineScope(Dispatchers.IO).launch {
              val dao = getDatabase(context).appDao()
              seedInitialData(dao)
            }
          }
        })
        .build()
        INSTANCE = instance
        instance
      }
    }

    private suspend fun seedInitialData(dao: AppDao) {
      // 1. Super Admin Account (from requirements)
      val admin = UserEntity(
        id = "admin_super_01",
        name = "محمود عمران (الإدارة الرئيسية)",
        email = "mahmoudomran324@gmail.com",
        phone = "01000000001",
        password = "Aa&9119045",
        role = "ADMIN",
        status = "APPROVED",
        governorate = "الإسكندرية",
        district = "سموحة",
        street = "شارع فوزي معاذ",
        apartment = "12",
        floor = "4",
        createdAt = System.currentTimeMillis()
      )
      dao.insertUser(admin)

      // 2. Default App Settings
      dao.insertSettings(
        AppSettingsEntity(
          id = 1,
          shippingFee = 20.0,
          commissionRatePercent = 5.0,
          customerMonthlySubscriptionFee = 50.0,
          isCustomerSubscriptionActive = false,
          isCaptainsFleetInterfaceActive = false,
          whatsappSupportNumber = "+201012345678",
          vodafoneCashNumber = "01099887766",
          instaPayHandle = "albakal@instapay",
          cashOnDeliveryEnabled = true,
          vodafoneCashEnabled = true,
          instaPayEnabled = true
        )
      )

      // 3. Product Categories (from requirements)
      val categories = listOf(
        ProductCategoryEntity("cat_fruits", "الفواكه", 1),
        ProductCategoryEntity("cat_vegetables", "الخضروات", 2),
        ProductCategoryEntity("cat_dry_grocery", "البقالة الجافة", 3),
        ProductCategoryEntity("cat_sweets", "الحلويات", 4),
        ProductCategoryEntity("cat_chocolate", "الشوكولاتة", 5),
        ProductCategoryEntity("cat_supermarket", "السوبر ماركت", 6)
      )
      dao.insertCategories(categories)

      // 4. Egyptian Governorates & Key Districts (Especially Alexandria)
      val areas = listOf(
        // Alexandria
        GovernorateAreaEntity("area_alex_1", "الإسكندرية", "ميامي"),
        GovernorateAreaEntity("area_alex_2", "الإسكندرية", "سيدي جابر"),
        GovernorateAreaEntity("area_alex_3", "الإسكندرية", "سموحة"),
        GovernorateAreaEntity("area_alex_4", "الإسكندرية", "الرمل"),
        GovernorateAreaEntity("area_alex_5", "الإسكندرية", "المنتزه"),
        GovernorateAreaEntity("area_alex_6", "الإسكندرية", "محرم بك"),
        GovernorateAreaEntity("area_alex_7", "الإسكندرية", "العجمي"),
        GovernorateAreaEntity("area_alex_8", "الإسكندرية", "كرموز"),
        GovernorateAreaEntity("area_alex_9", "الإسكندرية", "الجمرك"),
        GovernorateAreaEntity("area_alex_10", "الإسكندرية", "لوران"),
        // Cairo
        GovernorateAreaEntity("area_cai_1", "القاهرة", "المعادي"),
        GovernorateAreaEntity("area_cai_2", "القاهرة", "مدينة نصر"),
        GovernorateAreaEntity("area_cai_3", "القاهرة", "التجمع الخامس"),
        GovernorateAreaEntity("area_cai_4", "القاهرة", "مصر الجديدة"),
        GovernorateAreaEntity("area_cai_5", "القاهرة", "الزمالك"),
        GovernorateAreaEntity("area_cai_6", "القاهرة", "شبرا"),
        // Giza
        GovernorateAreaEntity("area_giz_1", "الجيزة", "الدقي"),
        GovernorateAreaEntity("area_giz_2", "الجيزة", "المهندسين"),
        GovernorateAreaEntity("area_giz_3", "الجيزة", "الهرم"),
        GovernorateAreaEntity("area_giz_4", "الجيزة", "فيصل"),
        GovernorateAreaEntity("area_giz_5", "الجيزة", "الشيخ زايد"),
        GovernorateAreaEntity("area_giz_6", "الجيزة", "6 أكتوبر"),
        // Other Governorates
        GovernorateAreaEntity("area_qaly_1", "القليوبية", "بنها"),
        GovernorateAreaEntity("area_ghar_1", "الغربية", "طنطا"),
        GovernorateAreaEntity("area_daq_1", "الدقهلية", "المنصورة"),
        GovernorateAreaEntity("area_shar_1", "الشرقية", "الزقازيق"),
        GovernorateAreaEntity("area_mon_1", "المنوفية", "شبين الكوم"),
        GovernorateAreaEntity("area_beh_1", "البحيرة", "دمنهور"),
        GovernorateAreaEntity("area_port_1", "بورسعيد", "حي الشرق"),
        GovernorateAreaEntity("area_ism_1", "الإسماعيلية", "حي أول"),
        GovernorateAreaEntity("area_suez_1", "السويس", "حي الأربعين"),
        GovernorateAreaEntity("area_dam_1", "دمياط", "دمياط الجديدة"),
        GovernorateAreaEntity("area_kaf_1", "كفر الشيخ", "كفر الشيخ"),
        GovernorateAreaEntity("area_fay_1", "الفيوم", "الفيوم"),
        GovernorateAreaEntity("area_ben_1", "بني سويف", "بني سويف"),
        GovernorateAreaEntity("area_min_1", "المنيا", "المنيا"),
        GovernorateAreaEntity("area_asy_1", "أسيوط", "أسيوط"),
        GovernorateAreaEntity("area_soh_1", "سوهاج", "سوهاج"),
        GovernorateAreaEntity("area_qen_1", "قنا", "قنا"),
        GovernorateAreaEntity("area_lux_1", "الأقصر", "الأقصر"),
        GovernorateAreaEntity("area_asw_1", "أسوان", "أسوان"),
        GovernorateAreaEntity("area_mat_1", "مطروح", "مرسى مطروح"),
        GovernorateAreaEntity("area_red_1", "البحر الأحمر", "الغردقة"),
        GovernorateAreaEntity("area_sin_s_1", "جنوب سيناء", "شرم الشيخ"),
        GovernorateAreaEntity("area_sin_n_1", "شمال سيناء", "العريش"),
        GovernorateAreaEntity("area_wad_1", "الوادي الجديد", "الخارجة")
      )
      dao.insertAreas(areas)
      // Clean starter state: no fake sample products, fake merchants or fake ads
      // Real products will be created by real registered merchants or by admin for merchants.
    }
  }
}
