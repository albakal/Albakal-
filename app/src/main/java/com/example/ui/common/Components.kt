package com.example.ui.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbakalTopBar(
  title: String,
  subtitle: String? = null,
  showBackButton: Boolean = false,
  onBackClick: (() -> Unit)? = null,
  showLogoutButton: Boolean = true,
  onLogoutClick: (() -> Unit)? = null,
  actions: @Composable (() -> Unit)? = null
) {
  TopAppBar(
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = "Albakal Logo",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
          if (subtitle != null) {
            Text(
              text = subtitle,
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = Color.White.copy(alpha = 0.85f)
            )
          }
        }
      }
    },
    navigationIcon = {
      if (showBackButton && onBackClick != null) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.testTag("topbar_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "رجوع",
            tint = Color.White
          )
        }
      }
    },
    actions = {
      val context = androidx.compose.ui.platform.LocalContext.current
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = AmberSecondary,
        modifier = Modifier
          .padding(end = 6.dp)
          .clickable { com.example.util.ApkInstaller.installOrShareApk(context) }
          .testTag("topbar_apk_button")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "APK",
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "تثبيت APK",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }
      if (actions != null) {
        actions()
      }
      if (showLogoutButton && onLogoutClick != null) {
        IconButton(
          onClick = onLogoutClick,
          modifier = Modifier.testTag("topbar_logout_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = "تسجيل الخروج",
            tint = Color.White
          )
        }
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = GreenPrimary
    )
  )
}

@Composable
fun OrderStatusBadge(status: String) {
  val (bgColor, textColor, label) = when (status) {
    "NEW" -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), "طلب جديد 🔔")
    "PREPARING" -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "جاري تحضير المنتج 📦")
    "SHIPPED" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "جاري إرسال المنتج 🛵")
    "DELIVERED" -> Triple(Color(0xFFE0F2F1), Color(0xFF00695C), "تم التسليم بنجاح ✅")
    "CANCELLED" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "ملغي ❌")
    else -> Triple(Color(0xFFF5F5F5), Color.DarkGray, status)
  }

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = bgColor,
    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
  ) {
    Text(
      text = label,
      color = textColor,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
    )
  }
}

@Composable
fun MerchantHonorCodeDialog(
  onAccept: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "ميثاق الشرف",
        tint = GreenPrimary,
        modifier = Modifier.size(40.dp)
      )
    },
    title = {
      Text(
        text = "ميثاق الشرف للتاجر",
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
    },
    text = {
      Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
          text = "أهلاً بك في منصة البقال! للبدء في بيع منتجاتك واستقبال طلبات الزبائن، يرجى الالتزام بالبنود التالية:",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(bottom = 12.dp)
        )
        HonorItem(
          icon = Icons.Default.CheckCircle,
          text = "البضائع والمنتجات المعروضة يجب أن تكون سليمة بنسبة 100% وطازجة وذات جودة عالية."
        )
        Spacer(modifier = Modifier.height(8.dp))
        HonorItem(
          icon = Icons.Default.Info,
          text = "إذا استلم العميل بضائع تالفة، يحق له إرجاعها فوراً بدون تحمل أي مصاريف إضافية للإرجاع."
        )
        Spacer(modifier = Modifier.height(8.dp))
        HonorItem(
          icon = Icons.Default.Warning,
          text = "تقوم الإدارة بمراجعة بلاغات التلف بكل دقة. وفي حال ثبوت تكرار المخالفة يتم اتخاذ إجراءات تشمل حظر الحساب."
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onAccept,
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
        modifier = Modifier.testTag("accept_honor_code_btn")
      ) {
        Text("أوافق على ميثاق الشرف والالتزام به")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("لاحقاً")
      }
    }
  )
}

@Composable
private fun HonorItem(icon: ImageVector, text: String) {
  Row(verticalAlignment = Alignment.Top) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = AmberSecondary,
      modifier = Modifier
        .size(18.dp)
        .padding(top = 2.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}

// Intent utilities for WhatsApp and Direct Calling
fun openWhatsApp(context: Context, rawPhone: String, message: String = "") {
  try {
    val cleanPhone = rawPhone.replace("+", "").replace(" ", "").replace("-", "")
    val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
  } catch (_: Exception) {
    // Fallback dial
    dialPhone(context, rawPhone)
  }
}

fun dialPhone(context: Context, phone: String) {
  try {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
  } catch (_: Exception) {}
}
