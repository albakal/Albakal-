package com.example.ui.customer

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.CartItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SponsoredAdEntity
import com.example.data.model.UserEntity
import com.example.ui.AlbakalViewModel
import com.example.ui.common.AlbakalTopBar
import com.example.ui.common.OrderStatusBadge
import com.example.ui.common.dialPhone
import com.example.ui.common.openWhatsApp
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary

@Composable
fun CustomerHomeScreen(viewModel: AlbakalViewModel) {
  val currentUser by viewModel.currentUser.collectAsState()
  val cartItems by viewModel.cart.collectAsState()
  var selectedTab by remember { mutableIntStateOf(0) }

  Scaffold(
    topBar = {
      AlbakalTopBar(
        title = "البقال - سوبر ماركت",
        subtitle = "مرحباً ${currentUser?.name ?: ""}",
        showLogoutButton = true,
        onLogoutClick = { viewModel.logout() }
      )
    },
    bottomBar = {
      CustomerBottomNav(
        selectedTab = selectedTab,
        cartCount = cartItems.sumOf { it.quantity },
        onTabSelected = { selectedTab = it }
      )
    }
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      when (selectedTab) {
        0 -> StoreTab(viewModel)
        1 -> CartTab(viewModel)
        2 -> MyOrdersTab(viewModel)
        3 -> ProfileTab(viewModel)
        4 -> FinancialsAndSubscriptionTab(viewModel)
        5 -> SupportTab(viewModel)
      }
    }
  }
}

@Composable
private fun CustomerBottomNav(
  selectedTab: Int,
  cartCount: Int,
  onTabSelected: (Int) -> Unit
) {
  NavigationBar(
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 6.dp
  ) {
    NavigationBarItem(
      selected = selectedTab == 0,
      onClick = { onTabSelected(0) },
      icon = { Icon(Icons.Default.Storefront, contentDescription = "المتجر") },
      label = { Text("المتجر", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("tab_store")
    )
    NavigationBarItem(
      selected = selectedTab == 1,
      onClick = { onTabSelected(1) },
      icon = {
        BadgedBox(
          badge = {
            if (cartCount > 0) {
              Badge(containerColor = AmberSecondary) {
                Text(cartCount.toString(), color = Color.White)
              }
            }
          }
        ) {
          Icon(Icons.Default.ShoppingCart, contentDescription = "السلة")
        }
      },
      label = { Text("السلة", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("tab_cart")
    )
    NavigationBarItem(
      selected = selectedTab == 2,
      onClick = { onTabSelected(2) },
      icon = { Icon(Icons.Default.LocalShipping, contentDescription = "طلباتي") },
      label = { Text("طلباتي", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("tab_orders")
    )
    NavigationBarItem(
      selected = selectedTab == 3,
      onClick = { onTabSelected(3) },
      icon = { Icon(Icons.Default.Person, contentDescription = "حسابي") },
      label = { Text("حسابي", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("tab_profile")
    )
    NavigationBarItem(
      selected = selectedTab == 4,
      onClick = { onTabSelected(4) },
      icon = { Icon(Icons.Default.CreditCard, contentDescription = "الاشتراك") },
      label = { Text("الاشتراك", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("tab_subscription")
    )
    NavigationBarItem(
      selected = selectedTab == 5,
      onClick = { onTabSelected(5) },
      icon = { Icon(Icons.Default.HeadsetMic, contentDescription = "الدعم") },
      label = { Text("الدعم", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("tab_support")
    )
  }
}

// 1. STORE TAB
@Composable
private fun StoreTab(viewModel: AlbakalViewModel) {
  val products by viewModel.publishedProducts.collectAsState()
  val categories by viewModel.categories.collectAsState()
  val ads by viewModel.sponsoredAds.collectAsState()

  var selectedCategory by remember { mutableStateOf("الكل") }
  var searchQuery by remember { mutableStateOf("") }

  val filteredProducts = remember(products, selectedCategory, searchQuery) {
    products.filter { p ->
      val matchesCat = selectedCategory == "الكل" || p.categoryName == selectedCategory
      val matchesQuery = searchQuery.isBlank() || p.name.contains(searchQuery, ignoreCase = true) ||
          p.description.contains(searchQuery, ignoreCase = true) ||
          p.merchantName.contains(searchQuery, ignoreCase = true)
      matchesCat && matchesQuery
    }
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(bottom = 20.dp)
  ) {
    // Top Hero Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(140.dp)
          .padding(horizontal = 14.dp, vertical = 8.dp)
          .clip(RoundedCornerShape(16.dp))
      ) {
        AsyncImage(
          model = R.drawable.store_hero_banner_1792756082201,
          contentDescription = "Albakal Banner",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }
    }

    // Sponsored Ads Carousel
    if (ads.isNotEmpty()) {
      item {
        LazyRow(
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(ads) { ad ->
            SponsoredAdCard(ad)
          }
        }
      }
    }

    // Search Box
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("ابحث عن خضار، فاكهة، أرز، شوكولاتة...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GreenPrimary) },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp)
          .testTag("store_search_input"),
        shape = RoundedCornerShape(12.dp)
      )
    }

    // Categories Horizontal Filter
    item {
      val allCats = listOf("الكل") + categories.map { it.name }
      LazyRow(
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(allCats) { cat ->
          val isSelected = cat == selectedCategory
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) GreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .clickable { selectedCategory = cat }
              .testTag("filter_cat_$cat")
          ) {
            Text(
              text = cat,
              color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 13.sp,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
          }
        }
      }
    }

    // Products Section Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "المنتجات المتوفرة (${filteredProducts.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = GreenDark
        )
      }
    }

    if (filteredProducts.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "لا توجد منتجات مطابقة في هذا القسم حالياً",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }
    } else {
      // Chunk into pairs of 2 for clean responsive grid inside LazyColumn
      val pairs = filteredProducts.chunked(2)
      items(pairs) { pair ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ProductCard(
            product = pair[0],
            onAddToCart = { viewModel.addToCart(pair[0]) },
            modifier = Modifier.weight(1f)
          )
          if (pair.size > 1) {
            ProductCard(
              product = pair[1],
              onAddToCart = { viewModel.addToCart(pair[1]) },
              modifier = Modifier.weight(1f)
            )
          } else {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }
}

@Composable
private fun SponsoredAdCard(ad: SponsoredAdEntity) {
  Card(
    modifier = Modifier
      .width(260.dp)
      .height(100.dp),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = AmberSecondary.copy(alpha = 0.12f)),
    border = androidx.compose.foundation.BorderStroke(1.dp, AmberSecondary.copy(alpha = 0.4f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(AmberSecondary),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White)
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Surface(
          color = AmberSecondary,
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = ad.category,
            fontSize = 9.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = ad.title,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          maxLines = 1
        )
        Text(
          text = ad.details,
          fontSize = 10.sp,
          color = Color.DarkGray,
          maxLines = 2
        )
      }
    }
  }
}

@Composable
private fun ProductCard(
  product: ProductEntity,
  onAddToCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      // Product Image or Banner
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(100.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
      ) {
        val imgModel = if (product.imageUrl.isNotBlank() && (product.imageUrl.startsWith("/") || product.imageUrl.startsWith("content:") || product.imageUrl.startsWith("file:"))) {
          product.imageUrl
        } else {
          R.drawable.store_hero_banner_1792756082201
        }
        AsyncImage(
          model = imgModel,
          contentDescription = product.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        if (product.discountPrice != null) {
          Surface(
            color = AmberSecondary,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(4.dp)
          ) {
            Text(
              text = "عرض خاص",
              fontSize = 10.sp,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = product.name,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        maxLines = 2,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "التاجر: ${product.merchantName}",
        fontSize = 10.sp,
        color = GreenDark,
        maxLines = 1
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val currentPrice = product.discountPrice ?: product.price
        Text(
          text = "${currentPrice} ج.م",
          fontWeight = FontWeight.Bold,
          color = GreenPrimary,
          fontSize = 14.sp
        )
        if (product.discountPrice != null) {
          Text(
            text = "${product.price} ج.م",
            color = Color.Gray,
            fontSize = 11.sp,
            textDecoration = TextDecoration.LineThrough
          )
        }
      }

      Text(
        text = "/ ${product.unit}",
        fontSize = 10.sp,
        color = Color.Gray
      )

      Spacer(modifier = Modifier.height(8.dp))

      Button(
        onClick = onAddToCart,
        modifier = Modifier
          .fillMaxWidth()
          .height(38.dp)
          .testTag("add_to_cart_${product.id}"),
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("أضف للسلة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

// 2. CART TAB
@Composable
private fun CartTab(viewModel: AlbakalViewModel) {
  val cartItems by viewModel.cart.collectAsState()
  val settings by viewModel.appSettings.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  var selectedPaymentMethod by remember { mutableStateOf("CASH_ON_DELIVERY") }
  var paymentNote by remember { mutableStateOf("") }

  if (cartItems.isEmpty()) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          imageVector = Icons.Default.ShoppingCart,
          contentDescription = null,
          tint = Color.LightGray,
          modifier = Modifier.size(70.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "سلة المشتريات فارغة",
          style = MaterialTheme.typography.titleMedium,
          color = Color.Gray
        )
        Text(
          text = "تصفح المتجر وأضف ما تحتاجه من خضار وفاكهة وبقالة",
          style = MaterialTheme.typography.bodySmall,
          color = Color.Gray
        )
      }
    }
    return
  }

  val subtotal = cartItems.sumOf { it.price * it.quantity }
  val shippingFee = settings.shippingFee
  val grandTotal = subtotal + shippingFee

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(vertical = 16.dp)
  ) {
    item {
      Text(
        text = "سلة المشتريات (${cartItems.size} أصناف)",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = GreenDark
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    items(cartItems) { item ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = item.productName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "التاجر: ${item.merchantName}", fontSize = 11.sp, color = Color.Gray)
            Text(
              text = "${item.price} ج.م × ${item.quantity} = ${item.price * item.quantity} ج.م",
              fontWeight = FontWeight.Bold,
              color = GreenPrimary,
              fontSize = 12.sp
            )
          }
          IconButton(
            onClick = { viewModel.removeFromCart(item.id) },
            modifier = Modifier.testTag("remove_cart_${item.id}")
          ) {
            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red.copy(alpha = 0.7f))
          }
        }
      }
    }

    // Delivery Address Details Card
    item {
      Spacer(modifier = Modifier.height(14.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = GreenPrimary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "عنوان التوصيل المسجل بحسابك:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Spacer(modifier = Modifier.height(6.dp))
          currentUser?.let { u ->
            Text(
              text = "${u.name} - ${u.phone}\n${u.governorate} - ${u.district} - ${u.street} - عمارة/شقة ${u.apartment} - الدور ${u.floor}",
              fontSize = 12.sp,
              color = Color.DarkGray
            )
          }
        }
      }
    }

    // Payment Methods Card
    item {
      Spacer(modifier = Modifier.height(14.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "اختر طريقة الدفع:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(8.dp))

          if (settings.cashOnDeliveryEnabled) {
            PaymentOptionRow(
              title = "الدفع نقدًا عند الاستلام (كاش)",
              subtitle = "تدفع المبلغ الإجمالي للمندوب عند استلام طلبك",
              isSelected = selectedPaymentMethod == "CASH_ON_DELIVERY",
              onSelect = { selectedPaymentMethod = "CASH_ON_DELIVERY" }
            )
          }

          if (settings.vodafoneCashEnabled) {
            PaymentOptionRow(
              title = "فودافون كاش (تحويل إلكتروني)",
              subtitle = "التحويل إلى رقم المحفظة: ${settings.vodafoneCashNumber}",
              isSelected = selectedPaymentMethod == "VODAFONE_CASH",
              onSelect = { selectedPaymentMethod = "VODAFONE_CASH" }
            )
          }

          if (settings.instaPayEnabled) {
            PaymentOptionRow(
              title = "إنستا باي (InstaPay)",
              subtitle = "التحويل إلى العنوان: ${settings.instaPayHandle}",
              isSelected = selectedPaymentMethod == "INSTAPAY",
              onSelect = { selectedPaymentMethod = "INSTAPAY" }
            )
          }

          if (selectedPaymentMethod != "CASH_ON_DELIVERY") {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = paymentNote,
              onValueChange = { paymentNote = it },
              label = { Text("رقم العملية أو هاتف المحول منه لإثبات الدفع") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("payment_ref_input")
            )
          }
        }
      }
    }

    // Price Breakdown Summary Card
    item {
      Spacer(modifier = Modifier.height(14.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GreenContainer.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "إجمالي أسعار المنتجات:", fontSize = 13.sp)
            Text(text = "$subtotal ج.م", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "مصاريف الشحن والتوصيل:", fontSize = 13.sp)
            Text(text = "$shippingFee ج.م", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          }
          HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "إجمالي المبلغ المستحق:",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = GreenDark
            )
            Text(
              text = "$grandTotal ج.م",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = GreenPrimary
            )
          }
        }
      }
    }

    // Submit Order Button
    item {
      Spacer(modifier = Modifier.height(20.dp))
      Button(
        onClick = {
          viewModel.placeOrder(
            paymentMethod = selectedPaymentMethod,
            paymentProofUrl = if (paymentNote.isNotBlank()) paymentNote else null,
            onSuccess = {}
          )
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("confirm_order_btn"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) {
        Text("تأكيد وإرسال الطلب الآن", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
private fun PaymentOptionRow(
  title: String,
  subtitle: String,
  isSelected: Boolean,
  onSelect: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onSelect() }
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    RadioButton(selected = isSelected, onClick = onSelect)
    Spacer(modifier = Modifier.width(6.dp))
    Column {
      Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
      Text(text = subtitle, fontSize = 11.sp, color = Color.Gray)
    }
  }
}

// 3. MY ORDERS TAB
@Composable
private fun MyOrdersTab(viewModel: AlbakalViewModel) {
  val orders by viewModel.allOrders.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val context = LocalContext.current

  val myOrders = remember(orders, currentUser) {
    orders.filter { it.customerId == currentUser?.id }
  }

  if (myOrders.isEmpty()) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text(text = "لا توجد طلبات سابقة حتى الآن", color = Color.Gray)
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
            Column {
              Text(text = order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text(text = "التاجر: ${order.merchantName}", fontSize = 11.sp, color = Color.Gray)
            }
            OrderStatusBadge(status = order.orderStatus)
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(text = "إجمالي الطلب: ${order.grandTotal} ج.م", fontWeight = FontWeight.Bold, color = GreenPrimary)
          Text(text = "طريقة الدفع: ${order.paymentMethod}", fontSize = 11.sp, color = Color.DarkGray)

          // Delivery Captain info if status is SHIPPED
          if (order.orderStatus == "SHIPPED" && !order.captainName.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = GreenContainer.copy(alpha = 0.5f))
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "🛵 طلبك في الطريق إليك مع الكابتن:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = GreenDark
                )
                Text(text = "الاسم: ${order.captainName}", fontSize = 12.sp)
                Text(text = "الهاتف: ${order.captainPhone}", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Button(
                    onClick = { dialPhone(context, order.captainPhone ?: "") },
                    modifier = Modifier
                      .weight(1f)
                      .testTag("call_captain_btn_${order.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("اتصال هاتفي", fontSize = 11.sp)
                  }
                  Button(
                    onClick = {
                      openWhatsApp(
                        context,
                        order.captainPhone ?: "",
                        "مرحباً كابتن ${order.captainName}، أنا بخصوص طلبي من البقال رقم ${order.orderNumber}"
                      )
                    },
                    modifier = Modifier
                      .weight(1f)
                      .testTag("whatsapp_captain_btn_${order.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("واتساب", fontSize = 11.sp)
                  }
                }
              }
            }
          }

          // Customer delivery confirmation button
          if (order.orderStatus != "DELIVERED" && !order.customerConfirmedDelivered) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
              onClick = { viewModel.customerConfirmDelivered(order.id) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("customer_confirm_delivery_${order.id}"),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenPrimary)
              Spacer(modifier = Modifier.width(6.dp))
              Text("تم الاستلام (تأكيد وصول الطلب)")
            }
          }
        }
      }
    }
  }
}

// 4. PROFILE TAB
@Composable
private fun ProfileTab(viewModel: AlbakalViewModel) {
  val currentUser by viewModel.currentUser.collectAsState()

  currentUser?.let { user ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
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
                .background(GreenContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(40.dp)
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(text = "عميل في البقال", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(14.dp))

            ProfileField(label = "البريد الإلكتروني", value = user.email)
            ProfileField(label = "رقم الهاتف", value = user.phone)
            ProfileField(label = "المحافظة", value = user.governorate)
            ProfileField(label = "القسم / الحي", value = user.district)
            ProfileField(label = "الشارع والعمارة", value = "${user.street} - شقة ${user.apartment} - دور ${user.floor}")
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
        Button(
          onClick = { viewModel.logout() },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("profile_logout_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("تسجيل الخروج من الحساب", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun ProfileField(label: String, value: String) {
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

// 5. FINANCIALS & SUBSCRIPTION TAB
@Composable
private fun FinancialsAndSubscriptionTab(viewModel: AlbakalViewModel) {
  val settings by viewModel.appSettings.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val orders by viewModel.allOrders.collectAsState()

  val myOrders = remember(orders, currentUser) {
    orders.filter { it.customerId == currentUser?.id }
  }
  val totalSpent = remember(myOrders) {
    myOrders.sumOf { it.grandTotal }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text(
        text = "المعاملات المالية والاشتراك الشهري",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = GreenDark
      )
    }

    // Monthly Subscription Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "نظام الاشتراك الشهري للعملاء", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(6.dp))

          if (settings.isCustomerSubscriptionActive) {
            Text(
              text = "نظام الاشتراك الشهري مفعّل من الإدارة! بقيمة ${settings.customerMonthlySubscriptionFee} ج.م شهرياً مع توصيل مجاني وغير محدود.",
              fontSize = 12.sp,
              color = GreenPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
              onClick = { viewModel.showMessage("تم تفعيل اشتراكك الشهري بنجاح!") },
              colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("تفعيل / تجديد الاشتراك الشهري")
            }
          } else {
            Text(
              text = "نظام الاشتراك الشهري غير مفروض حالياً، الشراء متاح بدون أي اشتراك مع احتساب مصاريف الشحن العادية فقط.",
              fontSize = 12.sp,
              color = Color.DarkGray
            )
          }
        }
      }
    }

    // Purchasing History Summary Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenContainer.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "ملخص المشتريات والحساب:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "إجمالي عدد الطلبات المنفذة: ${myOrders.size}", fontSize = 13.sp)
          Text(text = "إجمالي المبالغ المنفقة: $totalSpent ج.م", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GreenPrimary)
        }
      }
    }
  }
}

// 6. SUPPORT & COMPLAINTS TAB
@Composable
private fun SupportTab(viewModel: AlbakalViewModel) {
  val settings by viewModel.appSettings.collectAsState()
  val context = LocalContext.current

  var subject by remember { mutableStateOf("") }
  var details by remember { mutableStateOf("") }
  var isDamagedReport by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text(
        text = "الدعم والشكاوى وميثاق الشرف",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = GreenDark
      )
    }

    // WhatsApp Direct Button
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "تواصل فوري عبر واتساب:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(text = "فريق خدمة عملاء البقال جاهز للرد على استفساراتكم", fontSize = 11.sp, color = Color.Gray)
          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = {
              openWhatsApp(
                context,
                settings.whatsappSupportNumber,
                "مرحباً خدمة عملاء البقال، أريد الاستفسار بخصوص طلبي"
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("whatsapp_support_btn")
          ) {
            Icon(Icons.Default.Chat, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("محادثة واتساب مباشرة (${settings.whatsappSupportNumber})")
          }
        }
      }
    }

    // Complaint / Damaged Goods Form
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "إرسال شكوى أو بلاغ تلف بضائع", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("عنوان الشكوى أو الاقتراح") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("complaint_subject_input")
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = details,
            onValueChange = { details = it },
            label = { Text("تفاصيل الشكوى أو المنتجات التالفة...") },
            minLines = 3,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("complaint_details_input")
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { isDamagedReport = !isDamagedReport }
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            androidx.compose.material3.Checkbox(
              checked = isDamagedReport,
              onCheckedChange = { isDamagedReport = it }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "بلاغ بضائع تالفة (ميثاق الشرف - استرجاع مجاني 100%)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDamagedReport) AmberSecondary else Color.DarkGray
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              if (subject.isBlank() || details.isBlank()) {
                viewModel.showMessage("يرجى ملء موضوع الشكوى والتفاصيل")
              } else {
                viewModel.submitComplaint(subject, details, isDamagedReport, null)
                subject = ""
                details = ""
                isDamagedReport = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("submit_complaint_btn")
          ) {
            Text("إرسال الشكوى للإدارة")
          }
        }
      }
    }
  }
}
