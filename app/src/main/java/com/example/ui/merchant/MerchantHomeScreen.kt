package com.example.ui.merchant

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.ui.AlbakalViewModel
import com.example.ui.common.AlbakalTopBar
import com.example.ui.common.MerchantHonorCodeDialog
import com.example.ui.common.OrderStatusBadge
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import java.util.UUID

@Composable
fun MerchantHomeScreen(viewModel: AlbakalViewModel) {
  val currentUser by viewModel.currentUser.collectAsState()
  val hasActiveAlarm by viewModel.hasActiveAlarm.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) }
  var showHonorDialog by remember { mutableStateOf(currentUser?.honorCodeAccepted == false) }
  var showAddProductDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      AlbakalTopBar(
        title = "لوحة التاجر - البقال",
        subtitle = currentUser?.name ?: "التاجر",
        showLogoutButton = true,
        onLogoutClick = { viewModel.logout() }
      )
    },
    bottomBar = {
      MerchantBottomNav(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it }
      )
    },
    floatingActionButton = {
      if (selectedTab == 0) {
        FloatingActionButton(
          onClick = { showAddProductDialog = true },
          containerColor = GreenPrimary,
          contentColor = Color.White,
          modifier = Modifier.testTag("merchant_add_product_fab")
        ) {
          Icon(Icons.Default.Add, contentDescription = "إضافة منتج")
        }
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      // Active Sound Alert Banner (Ceases when order is acknowledged)
      AnimatedVisibility(visible = hasActiveAlarm) {
        Surface(
          color = Color(0xFFD32F2F),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "🚨 تنبيه: لديك طلبات جديدة واردة! الرنين مستمر حتى تضغط على 'تحضير المنتج'",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      when (selectedTab) {
        0 -> MerchantProductsTab(viewModel)
        1 -> MerchantOrdersTab(viewModel)
        2 -> MerchantDebtAndFinancialsTab(viewModel)
        3 -> MerchantProfileTab(viewModel)
      }
    }
  }

  if (showHonorDialog) {
    MerchantHonorCodeDialog(
      onAccept = {
        viewModel.acceptHonorCode()
        showHonorDialog = false
      },
      onDismiss = { showHonorDialog = false }
    )
  }

  if (showAddProductDialog) {
    MerchantAddProductDialog(
      viewModel = viewModel,
      onDismiss = { showAddProductDialog = false }
    )
  }
}

@Composable
private fun MerchantBottomNav(
  selectedTab: Int,
  onTabSelected: (Int) -> Unit
) {
  NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
    NavigationBarItem(
      selected = selectedTab == 0,
      onClick = { onTabSelected(0) },
      icon = { Icon(Icons.Default.Inventory, contentDescription = "منتجاتي") },
      label = { Text("منتجاتي", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("m_tab_products")
    )
    NavigationBarItem(
      selected = selectedTab == 1,
      onClick = { onTabSelected(1) },
      icon = { Icon(Icons.Default.LocalShipping, contentDescription = "الطلبات") },
      label = { Text("الطلبات", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("m_tab_orders")
    )
    NavigationBarItem(
      selected = selectedTab == 2,
      onClick = { onTabSelected(2) },
      icon = { Icon(Icons.Default.AttachMoney, contentDescription = "المديونية") },
      label = { Text("المديونية", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("m_tab_debt")
    )
    NavigationBarItem(
      selected = selectedTab == 3,
      onClick = { onTabSelected(3) },
      icon = { Icon(Icons.Default.Person, contentDescription = "حسابي") },
      label = { Text("حسابي", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("m_tab_profile")
    )
  }
}

// 1. MERCHANT PRODUCTS TAB
@Composable
private fun MerchantProductsTab(viewModel: AlbakalViewModel) {
  val products by viewModel.allProducts.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  val myProducts = remember(products, currentUser) {
    products.filter { it.merchantId == currentUser?.id }
  }

  if (myProducts.isEmpty()) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Inventory, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(60.dp))
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "لم تقم بإضافة أي منتجات حتى الآن", color = Color.Gray)
        Text(text = "اضغط على الزر الدائري (+) لإضافة منتجك الأول", fontSize = 12.sp, color = Color.Gray)
      }
    }
    return
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    contentPadding = PaddingValues(vertical = 14.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text(
        text = "منتجات متجرك المعروضة (${myProducts.size})",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = GreenDark
      )
    }

    items(myProducts) { product ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "القسم: ${product.categoryName} • الوحدة: ${product.unit}", fontSize = 11.sp, color = Color.Gray)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(text = "${product.price} ج.م", fontWeight = FontWeight.Bold, color = GreenPrimary)
              if (product.discountPrice != null) {
                Text(text = "خصم: ${product.discountPrice} ج.م", color = AmberSecondary, fontSize = 12.sp)
              }
            }
          }
          IconButton(
            onClick = { viewModel.deleteProduct(product.id) },
            modifier = Modifier.testTag("delete_prod_${product.id}")
          ) {
            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red.copy(alpha = 0.7f))
          }
        }
      }
    }
  }
}

// 2. MERCHANT ORDERS TAB (Stages & Sound Cease)
@Composable
private fun MerchantOrdersTab(viewModel: AlbakalViewModel) {
  val orders by viewModel.allOrders.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  var selectedOrderIdForShipping by remember { mutableStateOf<String?>(null) }

  val myOrders = remember(orders, currentUser) {
    orders.filter { it.merchantId == currentUser?.id }
  }

  if (myOrders.isEmpty()) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text(text = "لا توجد طلبات واردة حالياً", color = Color.Gray)
    }
    return
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    contentPadding = PaddingValues(vertical = 14.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    items(myOrders) { order ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            OrderStatusBadge(status = order.orderStatus)
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(text = "العميل: ${order.customerName} - ${order.customerPhone}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          Text(text = "العنوان: ${order.customerAddress}", fontSize = 11.sp, color = Color.DarkGray)
          Text(text = "إجمالي المنتجات: ${order.totalProductsPrice} ج.م | عمولة التطبيق: ${order.appCommission} ج.م", fontSize = 11.sp, color = GreenPrimary)

          Spacer(modifier = Modifier.height(10.dp))

          // Action buttons depending on order stage
          when (order.orderStatus) {
            "NEW" -> {
              Button(
                onClick = { viewModel.acknowledgeOrder(order.id) },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("ack_order_${order.id}")
              ) {
                Text("تحضير المنتج / قبول الطلب (إيقاف الرنين)")
              }
            }
            "PREPARING" -> {
              Button(
                onClick = { selectedOrderIdForShipping = order.id },
                colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("ship_order_${order.id}")
              ) {
                Text("جاري إرسال المنتج (تسجيل بيانات الكابتن)")
              }
            }
            "SHIPPED" -> {
              Text(
                text = "🛵 الطلب مع الكابتن: ${order.captainName} (${order.captainPhone})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GreenDark
              )
              Spacer(modifier = Modifier.height(6.dp))
              Button(
                onClick = { viewModel.merchantConfirmDelivered(order.id) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("delivered_order_${order.id}")
              ) {
                Text("تم التسليم للعميل بنجاح")
              }
            }
            "DELIVERED" -> {
              Surface(
                color = GreenContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = "✅ تم تسليم الطلب وإضافة الأرباح لمحفظتك",
                  color = GreenDark,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(8.dp)
                )
              }
            }
          }
        }
      }
    }
  }

  if (selectedOrderIdForShipping != null) {
    CaptainDataDialog(
      orderId = selectedOrderIdForShipping!!,
      onConfirm = { captainName, captainPhone ->
        viewModel.shipOrder(selectedOrderIdForShipping!!, captainName, captainPhone)
        selectedOrderIdForShipping = null
      },
      onDismiss = { selectedOrderIdForShipping = null }
    )
  }
}

@Composable
private fun CaptainDataDialog(
  orderId: String,
  onConfirm: (String, String) -> Unit,
  onDismiss: () -> Unit
) {
  var captainName by remember { mutableStateOf("") }
  var captainPhone by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("بيانات مندوب التوصيل (الكابتن)", fontWeight = FontWeight.Bold) },
    text = {
      Column {
        Text("أدخل اسم ورقم هاتف المندوب ليتمكن العميل من متابعته:", fontSize = 12.sp)
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
          value = captainName,
          onValueChange = { captainName = it },
          label = { Text("اسم الكابتن") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("captain_name_input")
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = captainPhone,
          onValueChange = { captainPhone = it },
          label = { Text("رقم هاتف الكابتن") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("captain_phone_input")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (captainName.isNotBlank() && captainPhone.isNotBlank()) {
            onConfirm(captainName, captainPhone)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
        modifier = Modifier.testTag("confirm_captain_btn")
      ) {
        Text("تأكيد إرسال الطلب")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

// 3. MERCHANT DEBT & FINANCIALS TAB
@Composable
private fun MerchantDebtAndFinancialsTab(viewModel: AlbakalViewModel) {
  val currentUser by viewModel.currentUser.collectAsState()
  val payments by viewModel.allDebtPayments.collectAsState()
  val settings by viewModel.appSettings.collectAsState()

  var showRepayDialog by remember { mutableStateOf(false) }

  val myPayments = remember(payments, currentUser) {
    payments.filter { it.merchantId == currentUser?.id }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text(
        text = "المالية والمديونية والعمولات",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = GreenDark
      )
    }

    // Financial Cards
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenContainer.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "الأرباح الشهرية الحالية:", fontSize = 13.sp)
          Text(
            text = "${currentUser?.totalEarnings ?: 0.0} ج.م",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = GreenPrimary
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "تُحسب من الطلبات المسلمة فعلياً وتبدأ دورة جديدة مع بداية كل شهر دون حذف السجلات.", fontSize = 10.sp, color = Color.DarkGray)
        }
      }
    }

    // Current Debt Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if ((currentUser?.currentDebt ?: 0.0) > 0) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, if ((currentUser?.currentDebt ?: 0.0) > 0) Color(0xFFEF5350) else Color.LightGray)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "المديونية الحالية (عمولات التطبيق المستحقة):", fontSize = 13.sp)
          Text(
            text = "${currentUser?.currentDebt ?: 0.0} ج.م",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = if ((currentUser?.currentDebt ?: 0.0) > 0) Color(0xFFC62828) else GreenPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(text = "نسبة عمولة المنصة: ${settings.commissionRatePercent}% من قيمة المبيعات.", fontSize = 11.sp, color = Color.Gray)

          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = { showRepayDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("merchant_repay_debt_btn")
          ) {
            Text("سداد المديونية / رفع إثبات التحويل")
          }
        }
      }
    }

    // Debt Payment Requests History
    item {
      Text(
        text = "سجل طلبات سداد المديونية (${myPayments.size})",
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
      )
    }

    items(myPayments) { pay ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(text = "مبلغ السداد: ${pay.amount} ج.م", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = "الطريقة: ${pay.paymentMethod}", fontSize = 11.sp, color = Color.Gray)
            if (!pay.notes.isNullOrBlank()) {
              Text(text = "ملاحظة: ${pay.notes}", fontSize = 10.sp, color = Color.DarkGray)
            }
          }
          val statusText = when (pay.status) {
            "APPROVED" -> "معتمد ومخصوم ✅"
            "REJECTED" -> "مرفوض ❌"
            else -> "قيد المراجعة ⏳"
          }
          val statusColor = when (pay.status) {
            "APPROVED" -> GreenPrimary
            "REJECTED" -> Color.Red
            else -> AmberSecondary
          }
          Text(text = statusText, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
    }
  }

  if (showRepayDialog) {
    RepayDebtDialog(
      viewModel = viewModel,
      onDismiss = { showRepayDialog = false }
    )
  }
}

@Composable
private fun RepayDebtDialog(
  viewModel: AlbakalViewModel,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val settings by viewModel.appSettings.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  var amountText by remember { mutableStateOf((currentUser?.currentDebt ?: 0.0).toString()) }
  var selectedMethod by remember { mutableStateOf("VODAFONE_CASH") }
  var proofNote by remember { mutableStateOf("") }
  var pickedProofPath by remember { mutableStateOf<String?>(null) }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
    onResult = { uri ->
      if (uri != null) {
        val path = ImageUtils.copyUriToInternalStorage(context, uri, prefix = "debt_proof")
        pickedProofPath = path
      }
    }
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("سداد مديونية التاجر", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("طرق السداد المتاحة للإدارة:", fontSize = 12.sp)
        Text("• فودافون كاش: ${settings.vodafoneCashNumber}", fontSize = 12.sp, color = GreenPrimary)
        Text("• إنستا باي: ${settings.instaPayHandle}", fontSize = 12.sp, color = GreenPrimary)

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
          value = amountText,
          onValueChange = { amountText = it },
          label = { Text("المبلغ المراد سداده (ج.م)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("repay_amount_input")
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          RadioButton(selected = selectedMethod == "VODAFONE_CASH", onClick = { selectedMethod = "VODAFONE_CASH" })
          Text("فودافون كاش", fontSize = 12.sp)
          Spacer(modifier = Modifier.width(12.dp))
          RadioButton(selected = selectedMethod == "INSTAPAY", onClick = { selectedMethod = "INSTAPAY" })
          Text("إنستا باي", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))
        // Photo Proof Picker
        OutlinedButton(
          onClick = {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
          },
          modifier = Modifier.fillMaxWidth().testTag("pick_debt_proof_btn")
        ) {
          Icon(Icons.Default.Image, contentDescription = null, tint = GreenPrimary)
          Spacer(modifier = Modifier.width(6.dp))
          Text(if (pickedProofPath != null) "تم اختيار صورة الإيصال (تغيير)" else "رفع صورة إيصال التحويل (إثبات الدفع)")
        }

        if (pickedProofPath != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(120.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.LightGray.copy(alpha = 0.3f))
          ) {
            AsyncImage(
              model = pickedProofPath,
              contentDescription = "صورة الإيصال",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = proofNote,
          onValueChange = { proofNote = it },
          label = { Text("بيانات التحويل أو رقم العملية كإثبات") },
          minLines = 2,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("repay_proof_input")
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
            viewModel.submitDebtPayment(amt, selectedMethod, pickedProofPath, proofNote) {
              onDismiss()
            }
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
        modifier = Modifier.testTag("submit_repay_btn")
      ) {
        Text("إرسال طلب السداد للإدارة")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

// 4. MERCHANT PROFILE TAB
@Composable
private fun MerchantProfileTab(viewModel: AlbakalViewModel) {
  val currentUser by viewModel.currentUser.collectAsState()
  var showHonorCodeDialog by remember { mutableStateOf(false) }

  currentUser?.let { user ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(AmberSecondary.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Storefront, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(text = "متجر / تاجر معتمد في البقال", color = GreenPrimary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))
            ProfileRow(label = "البريد الإلكتروني", value = user.email)
            ProfileRow(label = "رقم الهاتف", value = user.phone)
            ProfileRow(label = "المحافظة والحي", value = "${user.governorate} - ${user.district}")
            ProfileRow(label = "العنوان التجاري", value = "${user.street} - ${user.apartment}")
          }
        }
      }

      item {
        OutlinedButton(
          onClick = { showHonorCodeDialog = true },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("view_honor_code_btn"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = GreenPrimary)
          Spacer(modifier = Modifier.width(6.dp))
          Text("مراجعة بنود ميثاق الشرف للتجار")
        }
      }

      item {
        Button(
          onClick = { viewModel.logout() },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("merchant_logout_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("تسجيل الخروج من الحساب", fontWeight = FontWeight.Bold)
        }
      }
    }
  }

  if (showHonorCodeDialog) {
    MerchantHonorCodeDialog(
      onAccept = { showHonorCodeDialog = false },
      onDismiss = { showHonorCodeDialog = false }
    )
  }
}

@Composable
private fun ProfileRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(text = "$label:", color = Color.Gray, fontSize = 12.sp)
    Text(text = value, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
  }
}

// Add Product Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MerchantAddProductDialog(
  viewModel: AlbakalViewModel,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val currentUser = viewModel.currentUser.value ?: return
  val categories by viewModel.categories.collectAsState()

  var name by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(categories.firstOrNull()?.name ?: "الخضروات") }
  var catExpanded by remember { mutableStateOf(false) }
  var priceText by remember { mutableStateOf("") }
  var discountText by remember { mutableStateOf("") }
  var unit by remember { mutableStateOf("كيلو") }
  var description by remember { mutableStateOf("") }
  var pickedImagePath by remember { mutableStateOf<String?>(null) }

  val productPhotoPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
    onResult = { uri ->
      if (uri != null) {
        val path = ImageUtils.copyUriToInternalStorage(context, uri, prefix = "product")
        pickedImagePath = path
      }
    }
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("إضافة منتج جديد للمتجر", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("اسم المنتج") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("add_prod_name_input")
        )
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
          expanded = catExpanded,
          onExpandedChange = { catExpanded = it },
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text("القسم") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
            modifier = Modifier
              .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
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
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = priceText,
            onValueChange = { priceText = it },
            label = { Text("السعر (ج.م)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
              .weight(1f)
              .testTag("add_prod_price_input")
          )
          OutlinedTextField(
            value = discountText,
            onValueChange = { discountText = it },
            label = { Text("سعر العرض (اختياري)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
              .weight(1f)
              .testTag("add_prod_discount_input")
          )
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = unit,
          onValueChange = { unit = it },
          label = { Text("الوحدة (كيلو، علبة، كيس...)") },
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
            productPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
          },
          modifier = Modifier.fillMaxWidth().testTag("pick_product_photo_btn")
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
          val price = priceText.toDoubleOrNull()
          if (name.isBlank() || price == null || price <= 0.0) {
            viewModel.showMessage("يرجى إدخال اسم وسعر صحيح للمنتج")
          } else {
            val discountPrice = discountText.toDoubleOrNull()
            val prod = ProductEntity(
              id = "prod_" + UUID.randomUUID().toString().take(8),
              merchantId = currentUser.id,
              merchantName = currentUser.name,
              name = name.trim(),
              categoryName = selectedCategory,
              description = description.trim(),
              price = price,
              discountPrice = discountPrice,
              imageUrl = pickedImagePath ?: "store_hero_banner_1792756082201",
              unit = unit.trim(),
              isPublished = true,
              isOffer = discountPrice != null
            )
            viewModel.addProduct(prod)
            onDismiss()
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
        modifier = Modifier.testTag("save_new_product_btn")
      ) {
        Text("حفظ ونشر المنتج")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}
