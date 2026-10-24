package com.example.ui.captain

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AlbakalViewModel
import com.example.ui.common.AlbakalTopBar
import com.example.ui.common.OrderStatusBadge
import com.example.ui.common.dialPhone
import com.example.ui.common.openWhatsApp
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary

@Composable
fun CaptainsFleetScreen(viewModel: AlbakalViewModel) {
  val orders by viewModel.allOrders.collectAsState()
  val context = LocalContext.current

  val activeDeliveries = orders.filter { it.orderStatus == "SHIPPED" }

  Scaffold(
    topBar = {
      AlbakalTopBar(
        title = "أسطول كباتن التوصيل",
        subtitle = "متابعة وتوصيل طلبات البقال",
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
        .padding(14.dp)
    ) {
      Text(
        text = "الطلبات الجارية للتوصيل (${activeDeliveries.size})",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = GreenDark
      )
      Spacer(modifier = Modifier.height(10.dp))

      if (activeDeliveries.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text("لا توجد طلبات جارية قيد التوصيل حالياً", color = Color.Gray)
        }
      } else {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 20.dp)
        ) {
          items(activeDeliveries) { order ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                Text(text = "التاجر: ${order.merchantName}", fontSize = 11.sp, color = GreenPrimary)
                Text(text = "المبلغ المطلوب تحصيله: ${order.grandTotal} ج.م", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  Button(
                    onClick = { dialPhone(context, order.customerPhone) },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(Icons.Default.Call, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("اتصال بالعميل", fontSize = 11.sp)
                  }
                  Button(
                    onClick = { openWhatsApp(context, order.customerPhone, "أهلاً بك، أنا كابتن التوصيل من البقال بخصوص طلبك ${order.orderNumber}") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(Icons.Default.Chat, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("واتساب", fontSize = 11.sp)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
