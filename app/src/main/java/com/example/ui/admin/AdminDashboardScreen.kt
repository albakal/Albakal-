package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.util.ImageUtils
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettingsEntity
import com.example.data.model.DebtPaymentRequestEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SponsoredAdEntity
import com.example.data.model.SupervisorPermissionEntity
import com.example.data.model.SupportComplaintEntity
import com.example.data.model.UserEntity
import com.example.ui.AlbakalViewModel
import com.example.ui.common.AlbakalTopBar
import com.example.ui.common.OrderStatusBadge
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary

@Composable
fun AdminDashboardScreen(viewModel: AlbakalViewModel) {
  val currentUser by viewModel.currentUser.collectAsState()
  val pendingUsers by viewModel.pendingUsers.collectAsState()
  val permissions by viewModel.supervisorPermissions.collectAsState()

  val isSuperAdmin = currentUser?.role == "ADMIN"
  val perms = permissions ?: SupervisorPermissionEntity(userId = currentUser?.id ?: "")

  // Tabs
  var selectedTab by remember { mutableIntStateOf(0) }

  val tabTitles = remember(isSuperAdmin, perms) {
    val list = mutableListOf<String>()
    if (isSuperAdmin || perms.canManageCustomers) list.add("العملاء")
    if (isSuperAdmin || perms.canManageMerchants) list.add("التجار")
    if (isSuperAdmin) list.add("المشرفين")
    if (isSuperAdmin || perms.canManageProducts) list.add("المنتجات والأقسام")
    if (isSuperAdmin || perms.canManageOrders) list.add("الطلبات")
    if (isSuperAdmin || perms.canViewFinancials) list.add("المالية والعمولات")
    if (isSuperAdmin || perms.canManageAds) list.add("الإعلانات")
    if (isSuperAdmin || perms.canManageSupport) list.add("الدعم والشكاوى")
    if (isSuperAdmin) list.add("استعادة الحساب")
    if (isSuperAdmin || perms.canManageStoreSettings) list.add("المحافظات")
    if (isSuperAdmin) list.add("أمان حساب الإدارة")
    list
  }

  Scaffold(
    topBar = {
      AlbakalTopBar(
        title = if (isSuperAdmin) "لوحة الإدارة الرئيسية" else "لوحة المشرف الإداري",
        subtitle = currentUser?.name ?: "الإدارة",
        showLogoutButton = true,
        onLogoutClick = { viewModel.logout() }
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      if (tabTitles.isNotEmpty()) {
        ScrollableTabRow(
          selectedTabIndex = selectedTab.coerceIn(0, tabTitles.size - 1),
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = GreenPrimary,
          edgePadding = 12.dp
        ) {
          tabTitles.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = {
                Text(
                  text = title,
                  fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 12.sp
                )
              },
              modifier = Modifier.testTag("admin_tab_$index")
            )
          }
        }
      }

      val currentTabTitle = tabTitles.getOrNull(selectedTab) ?: ""
      Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        when (currentTabTitle) {
          "العملاء" -> AdminCustomersSection(viewModel)
          "التجار" -> AdminMerchantsSection(viewModel, canBan = isSuperAdmin || perms.canBanMerchants, canDebts = isSuperAdmin || perms.canManageDebts)
          "المشرفين" -> AdminSupervisorsSection(viewModel)
          "المنتجات والأقسام" -> AdminProductsSection(viewModel)
          "الطلبات" -> AdminOrdersSection(viewModel)
          "المالية والعمولات" -> AdminFinancialsSection(viewModel)
          "الإعلانات" -> AdminAdsSection(viewModel)
          "الدعم والشكاوى" -> AdminSupportSection(viewModel)
          "استعادة الحساب" -> AdminPasswordResetsSection(viewModel)
          "المحافظات" -> AdminGovernoratesSection(viewModel)
          "أمان حساب الإدارة" -> AdminSecuritySection(viewModel)
          else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("اختر قسماً من القائمة أعلاه", color = Color.Gray)
          }
        }
      }
    }
  }
}

// 1. CUSTOMERS MANAGEMENT
@Composable
private fun AdminCustomersSection(viewModel: AlbakalViewModel) {
  val allUsers by viewModel.allUsers.collectAsState()
  val customers = remember(allUsers) { allUsers.filter { it.role == "CUSTOMER" } }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text(
        text = "إدارة العملاء (${customers.size})",
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = GreenDark
      )
    }

    items(customers) { customer ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = customer.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text(text = "${customer.phone} • ${customer.email}", fontSize = 11.sp, color = Color.Gray)
              Text(
                text = "${customer.governorate} - ${customer.district} - ${customer.street}",
                fontSize = 11.sp,
                color = Color.DarkGray
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (customer.status == "APPROVED") GreenContainer else Color(0xFFFFF3E0)
            ) {
              Text(
                text = if (customer.status == "APPROVED") "معتمد ✅" else "قيد المراجعة ⏳",
                color = if (customer.status == "APPROVED") GreenDark else AmberSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }

          if (customer.status == "PENDING_APPROVAL") {
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { viewModel.approveUser(customer.id) },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("الموافقة على الحساب", fontSize = 11.sp)
              }
              OutlinedButton(
                onClick = { viewModel.rejectUser(customer.id) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("رفض", fontSize = 11.sp, color = Color.Red)
              }
            }
          }
        }
      }
    }
  }
}

// 2. MERCHANTS MANAGEMENT & BANNING
@Composable
private fun AdminMerchantsSection(viewModel: AlbakalViewModel, canBan: Boolean, canDebts: Boolean) {
  val allUsers by viewModel.allUsers.collectAsState()
  val merchants = remember(allUsers) { allUsers.filter { it.role == "MERCHANT" } }

  var banMerchantTarget by remember { mutableStateOf<UserEntity?>(null) }
  var settleDebtMerchantTarget by remember { mutableStateOf<UserEntity?>(null) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text(
        text = "إدارة التجار والمديونيات والحظر (${merchants.size})",
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = GreenDark
      )
    }

    items(merchants) { merchant ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(text = merchant.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text(text = "${merchant.phone} • ${merchant.email}", fontSize = 11.sp, color = Color.Gray)
              Text(text = "${merchant.governorate} - ${merchant.district}", fontSize = 11.sp, color = Color.DarkGray)
              if (merchant.currentDebt < 0.0) {
                Text(
                  text = "رصيد دائن بالموجب: +${String.format("%.2f", -merchant.currentDebt)} ج.م | الأرباح: ${merchant.totalEarnings} ج.م",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = GreenPrimary
                )
              } else {
                Text(
                  text = "المديونية: ${merchant.currentDebt} ج.م | الأرباح: ${merchant.totalEarnings} ج.م",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = if (merchant.currentDebt > 0.0) Color(0xFFC62828) else GreenPrimary
                )
              }
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = when (merchant.status) {
                "APPROVED" -> GreenContainer
                "BANNED" -> Color(0xFFFFEBEE)
                else -> Color(0xFFFFF3E0)
              }
            ) {
              Text(
                text = when (merchant.status) {
                  "APPROVED" -> "نشط ومعتمد ✅"
                  "BANNED" -> "محظور 🚫"
                  else -> "طلب جديد ⏳"
                },
                color = when (merchant.status) {
                  "APPROVED" -> GreenDark
                  "BANNED" -> Color.Red
                  else -> AmberSecondary
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (merchant.status == "PENDING_APPROVAL") {
              Button(
                onClick = { viewModel.approveUser(merchant.id) },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("الموافقة والتفعيل", fontSize = 11.sp)
              }
              OutlinedButton(
                onClick = { viewModel.rejectUser(merchant.id) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("رفض", fontSize = 11.sp, color = Color.Red)
              }
            } else if (merchant.status == "BANNED") {
              Button(
                onClick = { viewModel.unbanMerchant(merchant.id) },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("فك الحظر وإعادة المتجر", fontSize = 11.sp)
              }
            } else {
              if (canDebts) {
                Button(
                  onClick = { settleDebtMerchantTarget = merchant },
                  colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Text("سداد مديونية تاجر", fontSize = 11.sp)
                }
              }
              if (canBan) {
                OutlinedButton(
                  onClick = { banMerchantTarget = merchant },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                  modifier = Modifier.weight(1f)
                ) {
                  Text("حظر التاجر", fontSize = 11.sp)
                }
              }
            }
          }
        }
      }
    }
  }

  if (settleDebtMerchantTarget != null) {
    AdminSettleMerchantDebtDialog(
      merchant = settleDebtMerchantTarget!!,
      viewModel = viewModel,
      onDismiss = { settleDebtMerchantTarget = null }
    )
  }

  if (banMerchantTarget != null) {
    BanMerchantDialog(
      merchant = banMerchantTarget!!,
      onConfirm = { reason ->
        viewModel.banMerchant(banMerchantTarget!!.id, reason)
        banMerchantTarget = null
      },
      onDismiss = { banMerchantTarget = null }
    )
  }
}

@Composable
private fun AdminSettleMerchantDebtDialog(
  merchant: UserEntity,
  viewModel: AlbakalViewModel,
  onDismiss: () -> Unit
) {
  val currentDebt = merchant.currentDebt
  // Box 2: Starts empty as requested
  var amountText by remember { mutableStateOf("") }
  var notesText by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("سداد مديونية تاجر (${merchant.name})", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
    text = {
      Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // 1. BOX 1: Current Debt Display
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (currentDebt > 0.0) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (currentDebt > 0.0) Color(0xFFEF5350) else GreenPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "1. المديونية الحالية المسجلة:",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(2.dp))
            if (currentDebt < 0.0) {
              Text(
                text = "رصيد دائن بالموجب: +${String.format("%.2f", -currentDebt)} ج.م",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = GreenDark
              )
            } else {
              Text(
                text = "${String.format("%.2f", currentDebt)} ج.م",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (currentDebt > 0.0) Color(0xFFC62828) else GreenDark
              )
            }
          }
        }

        // 2. BOX 2: Empty Input for Amount to Pay
        OutlinedTextField(
          value = amountText,
          onValueChange = { amountText = it },
          label = { Text("2. المبلغ المراد سداده (ج.م) *") },
          placeholder = { Text("أدخل المبلغ المسدد...") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        // 3. BOX 3: Dynamic Calculation (Remaining or Positive Credit Balance)
        val enteredAmt = amountText.toDoubleOrNull() ?: 0.0
        val remaining = currentDebt - enteredAmt
        val isOverpaid = enteredAmt > currentDebt

        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (amountText.isBlank()) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else if (isOverpaid) Color(0xFFE8F5E9)
            else Color(0xFFFFF8E1)
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (amountText.isBlank()) Color.LightGray
            else if (isOverpaid) GreenPrimary
            else AmberSecondary
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            if (amountText.isBlank()) {
              Text(
                text = "3. المتبقي بعد السداد:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.DarkGray
              )
              Text(
                text = "أدخل المبلغ في الخانة أعلاه لعرض الحساب الفوري.",
                fontSize = 11.sp,
                color = Color.Gray
              )
            } else if (isOverpaid) {
              Text(
                text = "3. رصيد بالموجب (دائن للمتجر) 🌟:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = GreenDark
              )
              Text(
                text = "+${String.format("%.2f", enteredAmt - currentDebt)} ج.م (رصيد دائن)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = GreenPrimary
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "تم دفع مبلغ أكبر من المديونية بمقدار (+${String.format("%.2f", enteredAmt - currentDebt)} ج.م)؛ سيصبح رصيداً بالموجب لصالح التاجر يخصم من أي عمولات قادمة.",
                fontSize = 11.sp,
                color = GreenDark
              )
            } else {
              Text(
                text = "3. المتبقي من المديونية بعد السداد:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFFE65100)
              )
              Text(
                text = "${String.format("%.2f", remaining)} ج.م",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (remaining > 0.0) Color(0xFFC62828) else GreenDark
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "(${String.format("%.2f", currentDebt)} - ${String.format("%.2f", enteredAmt)} = ${String.format("%.2f", remaining)} ج.م)",
                fontSize = 11.sp,
                color = Color.DarkGray
              )
            }
          }
        }

        OutlinedTextField(
          value = notesText,
          onValueChange = { notesText = it },
          label = { Text("ملاحظات / وسيلة السداد (كاش، فودافون كاش، تحويل...)") },
          minLines = 2,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = amountText.toDoubleOrNull() ?: 0.0
          if (amt <= 0.0) {
            viewModel.showMessage("يرجى إدخال مبلغ صحيح")
          } else {
            viewModel.adminSettleMerchantDebt(merchant.id, amt, notesText) {
              onDismiss()
            }
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) {
        Text("اعتماد السداد وتحديث رصيد التاجر فوراً")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

@Composable
private fun BanMerchantDialog(
  merchant: UserEntity,
  onConfirm: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var reason by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("حظر التاجر: ${merchant.name}", fontWeight = FontWeight.Bold) },
    text = {
      Column {
        Text("سيؤدي الحظر إلى إخفاء جميع منتجات هذا التاجر من المتجر فوراً ومنعه من استقبال طلبات جديدة.", fontSize = 12.sp)
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
          value = reason,
          onValueChange = { reason = it },
          label = { Text("سبب قرار الحظر (مديونية مرتفعة، شكوى تلف...)") },
          minLines = 2,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (reason.isNotBlank()) onConfirm(reason)
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
      ) {
        Text("تأكيد حظر التاجر")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

// 3. SUPERVISORS MANAGEMENT
@Composable
private fun AdminSupervisorsSection(viewModel: AlbakalViewModel) {
  val allUsers by viewModel.allUsers.collectAsState()
  val supervisors = remember(allUsers) { allUsers.filter { it.role == "SUPERVISOR" } }
  var showAddDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "إدارة المشرفين والصلاحيات (${supervisors.size})",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = GreenDark
        )
        Button(
          onClick = { showAddDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("إضافة مشرف جديد", fontSize = 11.sp)
        }
      }
    }

    items(supervisors) { sup ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(text = sup.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text(text = "${sup.email} • ${sup.phone}", fontSize = 11.sp, color = Color.Gray)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "صلاحيات مخصصة من الإدارة الرئيسية (لا يحق للمشرف الوصول لحساب الأدمن الرئيسي)",
            fontSize = 10.sp,
            color = AmberSecondary
          )
        }
      }
    }
  }

  if (showAddDialog) {
    CreateSupervisorDialog(
      viewModel = viewModel,
      onDismiss = { showAddDialog = false }
    )
  }
}

@Composable
private fun CreateSupervisorDialog(
  viewModel: AlbakalViewModel,
  onDismiss: () -> Unit
) {
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  // Checkboxes
  var canCustomers by remember { mutableStateOf(true) }
  var canMerchants by remember { mutableStateOf(true) }
  var canRegistrations by remember { mutableStateOf(true) }
  var canProducts by remember { mutableStateOf(true) }
  var canOrders by remember { mutableStateOf(true) }
  var canFinancials by remember { mutableStateOf(false) }
  var canDebts by remember { mutableStateOf(false) }
  var canBan by remember { mutableStateOf(false) }
  var canAds by remember { mutableStateOf(false) }
  var canSupport by remember { mutableStateOf(true) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("إنشاء حساب مشرف جديد وتحديد صلاحياته", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("اسم المشرف") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("البريد الإلكتروني") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("رقم الهاتف") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("كلمة المرور") }, singleLine = true, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(12.dp))
        Text("قائمة الصلاحيات الممنوحة:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GreenPrimary)

        PermissionCheckRow(label = "إدارة العملاء", checked = canCustomers, onCheckedChange = { canCustomers = it })
        PermissionCheckRow(label = "إدارة التجار", checked = canMerchants, onCheckedChange = { canMerchants = it })
        PermissionCheckRow(label = "مراجعة طلبات التسجيل", checked = canRegistrations, onCheckedChange = { canRegistrations = it })
        PermissionCheckRow(label = "إدارة المنتجات والأقسام", checked = canProducts, onCheckedChange = { canProducts = it })
        PermissionCheckRow(label = "إدارة الطلبات", checked = canOrders, onCheckedChange = { canOrders = it })
        PermissionCheckRow(label = "الاطلاع على المعاملات المالية", checked = canFinancials, onCheckedChange = { canFinancials = it })
        PermissionCheckRow(label = "إدارة المديونيات وسدادها", checked = canDebts, onCheckedChange = { canDebts = it })
        PermissionCheckRow(label = "حظر وفك حظر التجار", checked = canBan, onCheckedChange = { canBan = it })
        PermissionCheckRow(label = "إدارة الإعلانات الممولة", checked = canAds, onCheckedChange = { canAds = it })
        PermissionCheckRow(label = "إدارة الدعم والشكاوى", checked = canSupport, onCheckedChange = { canSupport = it })
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isBlank() || email.isBlank() || password.isBlank()) {
            viewModel.showMessage("يرجى إكمال البيانات الأساسية")
          } else {
            val perms = SupervisorPermissionEntity(
              userId = "",
              canManageCustomers = canCustomers,
              canManageMerchants = canMerchants,
              canReviewRegistrations = canRegistrations,
              canManageProducts = canProducts,
              canManageOrders = canOrders,
              canViewFinancials = canFinancials,
              canManageDebts = canDebts,
              canBanMerchants = canBan,
              canManageAds = canAds,
              canManageSupport = canSupport
            )
            viewModel.createSupervisor(name, email, phone, password, perms) {
              onDismiss()
            }
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) {
        Text("حفظ وتفعيل المشرف")
      }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
  )
}

@Composable
private fun PermissionCheckRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onCheckedChange(!checked) }
      .padding(vertical = 2.dp)
  ) {
    Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    Spacer(modifier = Modifier.width(6.dp))
    Text(text = label, fontSize = 12.sp)
  }
}

// 4. PRODUCTS & CATEGORIES
@Composable
private fun AdminProductsSection(viewModel: AlbakalViewModel) {
  val products by viewModel.allProducts.collectAsState()
  val categories by viewModel.categories.collectAsState()

  var newCategoryName by remember { mutableStateOf("") }
  var showAddProductForMerchantDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "إدارة أقسام المتجر (${categories.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GreenDark)
        Button(
          onClick = { showAddProductForMerchantDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("إضافة منتج لمتجر تاجر", fontSize = 11.sp)
        }
      }
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = newCategoryName,
          onValueChange = { newCategoryName = it },
          label = { Text("اسم القسم الجديد (مثل: فواكه، حلويات...)") },
          singleLine = true,
          modifier = Modifier.weight(1f)
        )
        Button(
          onClick = {
            if (newCategoryName.isNotBlank()) {
              viewModel.addCategory(newCategoryName)
              newCategoryName = ""
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.align(Alignment.CenterVertically)
        ) {
          Text("إضافة قسم")
        }
      }
    }

    item {
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { cat ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = GreenContainer
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = cat.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GreenDark)
            }
          }
        }
      }
    }

    item {
      Text(text = "جميع المنتجات في المنصة (${products.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GreenDark)
    }

    items(products) { p ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = p.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = "التاجر: ${p.merchantName} • القسم: ${p.categoryName}", fontSize = 11.sp, color = Color.Gray)
            Text(text = "${p.price} ج.م / ${p.unit}", fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 12.sp)
          }
          IconButton(onClick = { viewModel.deleteProduct(p.id) }) {
            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red.copy(alpha = 0.7f))
          }
        }
      }
    }
  }

  if (showAddProductForMerchantDialog) {
    AdminAddProductForMerchantDialog(
      viewModel = viewModel,
      onDismiss = { showAddProductForMerchantDialog = false }
    )
  }
}

@Composable
private fun AdminAddProductForMerchantDialog(
  viewModel: AlbakalViewModel,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val allUsers by viewModel.allUsers.collectAsState()
  val merchants = remember(allUsers) { allUsers.filter { it.role == "MERCHANT" && it.status == "APPROVED" } }
  val categories by viewModel.categories.collectAsState()

  var selectedMerchant by remember(merchants) { mutableStateOf(merchants.firstOrNull()) }
  var merchantExpanded by remember { mutableStateOf(false) }

  var selectedCategory by remember(categories) { mutableStateOf(categories.firstOrNull()?.name ?: "الخضروات") }
  var catExpanded by remember { mutableStateOf(false) }

  var name by remember { mutableStateOf("") }
  var priceText by remember { mutableStateOf("") }
  var discountText by remember { mutableStateOf("") }
  var unit by remember { mutableStateOf("كيلو") }
  var description by remember { mutableStateOf("") }
  var pickedImagePath by remember { mutableStateOf<String?>(null) }

  val photoPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
    onResult = { uri ->
      if (uri != null) {
        val path = ImageUtils.copyUriToInternalStorage(context, uri, prefix = "admin_prod")
        pickedImagePath = path
      }
    }
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("إضافة منتج لمتجر تاجر", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        if (merchants.isEmpty()) {
          Text("لا يوجد تجار معتمدون حالياً في النظام. يجب اعتماد تاجر أولاً لتتمكن من إضافة منتجات إلى متجره.", color = Color.Red, fontSize = 12.sp)
        } else {
          Text("اختر التاجر / المتجر:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GreenPrimary)
          Spacer(modifier = Modifier.height(4.dp))
          Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
              onClick = { merchantExpanded = true },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = selectedMerchant?.let { "${it.name} (${it.governorate} - ${it.district})" } ?: "اختر التاجر",
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
              }
            }
            DropdownMenu(
              expanded = merchantExpanded,
              onDismissRequest = { merchantExpanded = false }
            ) {
              merchants.forEach { m ->
                DropdownMenuItem(
                  text = { Text("${m.name} (${m.governorate} - ${m.district})") },
                  onClick = {
                    selectedMerchant = m
                    merchantExpanded = false
                  }
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("اسم المنتج") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text("اختر قسم المنتج:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GreenPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedButton(
            onClick = { catExpanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = selectedCategory)
              Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
          }
          DropdownMenu(
            expanded = catExpanded,
            onDismissRequest = { catExpanded = false }
          ) {
            categories.forEach { c ->
              DropdownMenuItem(
                text = { Text(c.name) },
                onClick = {
                  selectedCategory = c.name
                  catExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = priceText,
            onValueChange = { priceText = it },
            label = { Text("السعر (ج.م)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = discountText,
            onValueChange = { discountText = it },
            label = { Text("سعر الخصم") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = unit,
          onValueChange = { unit = it },
          label = { Text("الوحدة (كيلو، علبة...)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("وصف المنتج") },
          minLines = 2,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
          onClick = {
            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
          },
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Image, contentDescription = null, tint = GreenPrimary)
          Spacer(modifier = Modifier.width(6.dp))
          Text(if (pickedImagePath != null) "تم اختيار صورة المنتج (تغيير)" else "اختيار صورة المنتج من الهاتف")
        }

        if (pickedImagePath != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(100.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.LightGray.copy(alpha = 0.3f))
          ) {
            AsyncImage(
              model = pickedImagePath,
              contentDescription = "صورة المنتج",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val m = selectedMerchant
          val price = priceText.toDoubleOrNull()
          if (m == null) {
            viewModel.showMessage("يرجى اختيار التاجر أولاً")
          } else if (name.isBlank() || price == null || price <= 0.0) {
            viewModel.showMessage("يرجى إدخال اسم وسعر صحيح")
          } else {
            val discount = discountText.toDoubleOrNull()
            val prod = ProductEntity(
              id = "prod_" + java.util.UUID.randomUUID().toString().take(8),
              merchantId = m.id,
              merchantName = m.name,
              name = name.trim(),
              categoryName = selectedCategory,
              description = description.trim(),
              price = price,
              discountPrice = discount,
              imageUrl = pickedImagePath ?: "store_hero_banner_1792756082201",
              unit = unit.trim(),
              isPublished = true,
              isOffer = discount != null
            )
            viewModel.addProduct(prod)
            onDismiss()
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) {
        Text("حفظ ونشر بمتجر التاجر")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

// 5. ALL ORDERS MANAGEMENT
@Composable
private fun AdminOrdersSection(viewModel: AlbakalViewModel) {
  val orders by viewModel.allOrders.collectAsState()

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text(
        text = "سجل الطلبات الشامل في المنصة (${orders.size})",
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = GreenDark
      )
    }

    items(orders) { order ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            OrderStatusBadge(status = order.orderStatus)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = "العميل: ${order.customerName} (${order.customerPhone})", fontSize = 11.sp)
          Text(text = "التاجر: ${order.merchantName}", fontSize = 11.sp, color = GreenDark)
          Text(text = "الإجمالي: ${order.grandTotal} ج.م (عمولة التطبيق: ${order.appCommission} ج.م)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          if (!order.captainName.isNullOrBlank()) {
            Text(text = "الكابتن: ${order.captainName} (${order.captainPhone})", fontSize = 10.sp, color = Color.DarkGray)
          }
        }
      }
    }
  }
}

// 6. FINANCIALS, COMMISSIONS & DEBT PAYMENTS REVIEW
@Composable
private fun AdminFinancialsSection(viewModel: AlbakalViewModel) {
  val settings by viewModel.appSettings.collectAsState()
  val debtPayments by viewModel.allDebtPayments.collectAsState()
  val allUsers by viewModel.allUsers.collectAsState()
  var viewingProofImage by remember { mutableStateOf<String?>(null) }

  var shippingFeeText by remember(settings) { mutableStateOf(settings.shippingFee.toString()) }
  var commissionText by remember(settings) { mutableStateOf(settings.commissionRatePercent.toString()) }
  var subscriptionFeeText by remember(settings) { mutableStateOf(settings.customerMonthlySubscriptionFee.toString()) }
  var subscriptionActive by remember(settings) { mutableStateOf(settings.isCustomerSubscriptionActive) }
  var fleetActive by remember(settings) { mutableStateOf(settings.isCaptainsFleetInterfaceActive) }
  var codEnabled by remember(settings) { mutableStateOf(settings.cashOnDeliveryEnabled) }
  var vodaEnabled by remember(settings) { mutableStateOf(settings.vodafoneCashEnabled) }
  var instaEnabled by remember(settings) { mutableStateOf(settings.instaPayEnabled) }
  var vodaNumber by remember(settings) { mutableStateOf(settings.vodafoneCashNumber) }
  var instaHandle by remember(settings) { mutableStateOf(settings.instaPayHandle) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text(text = "إعدادات العمولات والشحن وطرق الدفع", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenDark)
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = shippingFeeText,
              onValueChange = { shippingFeeText = it },
              label = { Text("مصاريف الشحن (ج.م)") },
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = commissionText,
              onValueChange = { commissionText = it },
              label = { Text("نسبة العمولة (%)") },
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = vodaNumber,
              onValueChange = { vodaNumber = it },
              label = { Text("رقم فودافون كاش") },
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = instaHandle,
              onValueChange = { instaHandle = it },
              label = { Text("عنوان إنستا باي") },
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("تفعيل نظام الاشتراك الشهري للعملاء:")
            Switch(checked = subscriptionActive, onCheckedChange = { subscriptionActive = it })
          }

          if (subscriptionActive) {
            OutlinedTextField(
              value = subscriptionFeeText,
              onValueChange = { subscriptionFeeText = it },
              label = { Text("قيمة الاشتراك الشهري (ج.م)") },
              modifier = Modifier.fillMaxWidth()
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("إظهار واجهة أسطول الكباتن:")
            Switch(checked = fleetActive, onCheckedChange = { fleetActive = it })
          }

          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = {
              val ship = shippingFeeText.toDoubleOrNull() ?: 20.0
              val comm = commissionText.toDoubleOrNull() ?: 5.0
              val sub = subscriptionFeeText.toDoubleOrNull() ?: 50.0
              viewModel.updateSettings(
                settings.copy(
                  shippingFee = ship,
                  commissionRatePercent = comm,
                  customerMonthlySubscriptionFee = sub,
                  isCustomerSubscriptionActive = subscriptionActive,
                  isCaptainsFleetInterfaceActive = fleetActive,
                  vodafoneCashNumber = vodaNumber.trim(),
                  instaPayHandle = instaHandle.trim()
                )
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("حفظ التغييرات المالية")
          }
        }
      }
    }

    // Debt Payment Requests Review
    item {
      Text(
        text = "طلبات سداد المديونية من التجار وإثباتات الدفع (${debtPayments.size})",
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = GreenDark
      )
    }

    items(debtPayments) { pay ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "التاجر: ${pay.merchantName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = "${pay.amount} ج.م", fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 14.sp)
          }
          Text(text = "طريقة السداد: ${pay.paymentMethod}", fontSize = 11.sp, color = Color.Gray)
          if (!pay.notes.isNullOrBlank()) {
            Text(text = "ملاحظات السداد: ${pay.notes}", fontSize = 11.sp, color = Color.DarkGray)
          }

          // Proof Receipt Image Display
          if (!pay.proofImageUrl.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .clickable { viewingProofImage = pay.proofImageUrl }
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color.LightGray.copy(alpha = 0.3f))
              ) {
                AsyncImage(
                  model = pay.proofImageUrl,
                  contentDescription = "صورة إيصال السداد",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )
              }
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "إشعار التحويل البنكي / صورة الإيصال 📷",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = GreenDark
                )
                Text(
                  text = "انقر هنا لتكبير الصورة وفحص تفاصيل التحويل",
                  fontSize = 10.sp,
                  color = Color.Gray
                )
              }
            }
          }

          val merchant = allUsers.find { it.id == pay.merchantId }
          if (merchant != null) {
            val debt = merchant.currentDebt
            val remaining = debt - pay.amount
            Spacer(modifier = Modifier.height(4.dp))
            if (pay.amount > debt) {
              Surface(
                color = GreenContainer,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = "🌟 السداد يتجاوز المديونية الحالية (${debt} ج.م) ويمنح التاجر رصيداً بالموجب (+${String.format("%.2f", pay.amount - debt)} ج.م)",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = GreenDark,
                  modifier = Modifier.padding(6.dp)
                )
              }
            } else {
              Text(
                text = "المديونية الحالية: ${debt} ج.م | المتبقي بعد الاعتماد: ${String.format("%.2f", remaining)} ج.م",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "الحالة: " + when (pay.status) {
              "APPROVED" -> "تم الاعتماد وخصم المديونية بنجاح ✅"
              "REJECTED" -> "مرفوض ❌"
              else -> "قيد المراجعة والتدقيق ⏳"
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = when (pay.status) {
              "APPROVED" -> GreenDark
              "REJECTED" -> Color.Red
              else -> AmberSecondary
            }
          )

          if (pay.status == "PENDING") {
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { viewModel.approveDebtPayment(pay) },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("اعتماد وخصم المديونية", fontSize = 11.sp)
              }
              OutlinedButton(
                onClick = { viewModel.rejectDebtPayment(pay) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("رفض", fontSize = 11.sp, color = Color.Red)
              }
            }
          }
        }
      }
    }
  }

  // Full Image Preview Dialog
  if (viewingProofImage != null) {
    AlertDialog(
      onDismissRequest = { viewingProofImage = null },
      title = { Text("صورة إيصال التحويل البنكي", fontWeight = FontWeight.Bold) },
      text = {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.05f))
        ) {
          AsyncImage(
            model = viewingProofImage,
            contentDescription = "صورة الإيصال بالحجم الكامل",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { viewingProofImage = null },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
          Text("إغلاق")
        }
      }
    )
  }
}

// 7. SPONSORED ADS MANAGEMENT
@Composable
private fun AdminAdsSection(viewModel: AlbakalViewModel) {
  val ads by viewModel.allAds.collectAsState()
  var showAddAdDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "الإعلانات الممولة (${ads.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GreenDark)
        Button(
          onClick = { showAddAdDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("إضافة إعلان ممول جديد", fontSize = 11.sp)
        }
      }
    }

    if (ads.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(Icons.Default.Campaign, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("لا توجد إعلانات ممولة حالياً", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("يمكنك رفع إعلان مصور جديد وتحديد مدته الزمنية لعرضه للمستخدمين", fontSize = 11.sp, color = Color.Gray)
          }
        }
      }
    }

    items(ads) { ad ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(60.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.LightGray.copy(alpha = 0.3f))
          ) {
            AsyncImage(
              model = ad.imageUrl,
              contentDescription = ad.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(text = ad.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = "الفئة: ${ad.category}", fontSize = 11.sp, color = AmberSecondary)
            Text(text = "المدة: ${ad.durationDays} يوم", fontSize = 11.sp, color = GreenDark, fontWeight = FontWeight.Medium)
            Text(text = ad.details, fontSize = 11.sp, color = Color.Gray, maxLines = 2)
          }
          Switch(checked = ad.isActive, onCheckedChange = { viewModel.toggleAdActive(ad) })
          IconButton(onClick = { viewModel.deleteAd(ad.id) }) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
          }
        }
      }
    }
  }

  if (showAddAdDialog) {
    AddAdDialog(
      onSave = { title, cat, details, imgUrl, durationDays ->
        val now = System.currentTimeMillis()
        val expiry = now + (durationDays * 24L * 60 * 60 * 1000)
        viewModel.addAd(
          SponsoredAdEntity(
            id = "ad_" + java.util.UUID.randomUUID().toString().take(8),
            title = title,
            category = cat,
            details = details,
            imageUrl = imgUrl,
            durationDays = durationDays,
            expiryDate = expiry,
            isActive = true
          )
        )
        showAddAdDialog = false
      },
      onDismiss = { showAddAdDialog = false }
    )
  }
}

@Composable
private fun AddAdDialog(
  onSave: (String, String, String, String, Int) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("عروض كبرى") }
  var details by remember { mutableStateOf("") }
  var durationDaysText by remember { mutableStateOf("30") }
  var pickedImagePath by remember { mutableStateOf<String?>(null) }

  val photoPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
    onResult = { uri ->
      if (uri != null) {
        val savedPath = ImageUtils.saveImageToInternalStorage(context, uri)
        if (savedPath != null) {
          pickedImagePath = savedPath
        }
      }
    }
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("رفع إعلان ممول مصور جديد", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("عنوان الإعلان") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = category,
          onValueChange = { category = it },
          label = { Text("فئة الإعلان (عروض كبرى، ترويج متجر...)") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = durationDaysText,
          onValueChange = { durationDaysText = it },
          label = { Text("مدة الإعلان بالأيام (مثال: 7، 15، 30)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = details,
          onValueChange = { details = it },
          label = { Text("تفاصيل الإعلان ورابط/وصف العرض") },
          minLines = 2,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Image Picker
        OutlinedButton(
          onClick = {
            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
          },
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Image, contentDescription = null, tint = GreenPrimary)
          Spacer(modifier = Modifier.width(6.dp))
          Text(if (pickedImagePath != null) "تم اختيار صورة الإعلان (تغيير)" else "اختيار صورة الإعلان من الهاتف")
        }

        if (pickedImagePath != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(110.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.LightGray.copy(alpha = 0.3f))
          ) {
            AsyncImage(
              model = pickedImagePath,
              contentDescription = "صورة الإعلان",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val dur = durationDaysText.toIntOrNull() ?: 30
          if (title.isNotBlank()) {
            onSave(title.trim(), category.trim(), details.trim(), pickedImagePath ?: "store_hero_banner_1792756082201", dur)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) { Text("حفظ ونشر الإعلان") }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
  )
}

// 8. SUPPORT & COMPLAINTS
@Composable
private fun AdminSupportSection(viewModel: AlbakalViewModel) {
  val complaints by viewModel.allComplaints.collectAsState()
  val settings by viewModel.appSettings.collectAsState()
  var whatsappNumber by remember(settings) { mutableStateOf(settings.whatsappSupportNumber) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(text = "رقم واتساب خدمة العملاء والدعم الفني:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = whatsappNumber,
              onValueChange = { whatsappNumber = it },
              label = { Text("رقم الواتساب بالصيغة الدولية") },
              modifier = Modifier.weight(1f)
            )
            Button(
              onClick = {
                viewModel.updateSettings(settings.copy(whatsappSupportNumber = whatsappNumber.trim()))
              },
              colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
              modifier = Modifier.align(Alignment.CenterVertically)
            ) {
              Text("تحديث")
            }
          }
        }
      }
    }

    item {
      Text(text = "قائمة الشكاوى وبلاغات التلف الواردة (${complaints.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GreenDark)
    }

    items(complaints) { comp ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = comp.subject, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (comp.isDamagedGoodsReport) Color(0xFFFFEBEE) else GreenContainer
            ) {
              Text(
                text = if (comp.isDamagedGoodsReport) "بلاغ تلف ⚠️" else "شكوى/اقتراح",
                color = if (comp.isDamagedGoodsReport) Color.Red else GreenDark,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text(text = "المستخدم: ${comp.userName} (${comp.userPhone}) - الدور: ${comp.userRole}", fontSize = 11.sp, color = Color.Gray)
          Text(text = comp.details, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
          Text(text = "الحالة: ${comp.status}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)

          if (comp.status != "RESOLVED") {
            Button(
              onClick = { viewModel.resolveComplaint(comp, "تمت المتابعة وحل المشكلة بنجاح") },
              colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.padding(top = 6.dp)
            ) {
              Text("تم حل المشكلة وإغلاق البلاغ", fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}

// 9. PASSWORD RESETS
@Composable
private fun AdminPasswordResetsSection(viewModel: AlbakalViewModel) {
  val resets by viewModel.passwordResets.collectAsState()

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text(text = "طلبات استعادة كلمة السر (${resets.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenDark)
    }

    items(resets) { req ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(text = "رقم الهاتف: ${req.phone}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text(text = "كود التحقق المؤقت: ${req.tempResetCode}", fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 13.sp)
          Text(text = "الحالة: ${req.status}", fontSize = 11.sp, color = Color.Gray)
        }
      }
    }
  }
}

// 10. GOVERNORATES & DISTRICTS
@Composable
private fun AdminGovernoratesSection(viewModel: AlbakalViewModel) {
  val areas by viewModel.governoratesWithDistricts.collectAsState()
  var newGov by remember { mutableStateOf("") }
  var newDist by remember { mutableStateOf("") }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text(text = "إضافة وتعديل المحافظات والأحياء", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenDark)
      Spacer(modifier = Modifier.height(6.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = newGov, onValueChange = { newGov = it }, label = { Text("المحافظة") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = newDist, onValueChange = { newDist = it }, label = { Text("القسم / الحي") }, modifier = Modifier.weight(1f))
          }
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = {
              if (newGov.isNotBlank() && newDist.isNotBlank()) {
                viewModel.addArea(newGov, newDist)
                newGov = ""
                newDist = ""
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("حفظ المنطقة الجديدة")
          }
        }
      }
    }

    item {
      Text(text = "المناطق المسجلة في النظام (${areas.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }

    items(areas) { area ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "${area.governorate} - ${area.district}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        IconButton(onClick = { viewModel.addArea(area.governorate, "") }) {
          Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Gray)
        }
      }
    }
  }
}

// 11. SUPER ADMIN SECURITY
@Composable
private fun AdminSecuritySection(viewModel: AlbakalViewModel) {
  val currentUser by viewModel.currentUser.collectAsState()
  var newEmail by remember { mutableStateOf(currentUser?.email ?: "mahmoudomran324@gmail.com") }
  var newPassword by remember { mutableStateOf(currentUser?.password ?: "Aa&9119045") }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "حماية وأمان حساب الإدارة الرئيسي", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenPrimary)
          Text(text = "بيانات هذا الحساب مشفرة ومحمية بالكامل، ولا يمكن لأي مشرف الوصول إليها أو تعديلها.", fontSize = 11.sp, color = Color.Gray)

          Spacer(modifier = Modifier.height(14.dp))
          OutlinedTextField(
            value = newEmail,
            onValueChange = { newEmail = it },
            label = { Text("البريد الإلكتروني للإدارة") },
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("كلمة مرور الإدارة") },
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = {
              if (newEmail.isNotBlank() && newPassword.isNotBlank()) {
                viewModel.updateAdminCredentials(newEmail, newPassword) {}
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("حفظ بيانات الدخول الجديدة للأدمن")
          }
        }
      }
    }
  }
}
