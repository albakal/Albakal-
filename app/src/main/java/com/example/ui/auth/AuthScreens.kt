package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.AlbakalViewModel
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import java.util.UUID

@Composable
fun LoginScreen(viewModel: AlbakalViewModel) {
  var emailOrPhone by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var showForgotPasswordDialog by remember { mutableStateOf(false) }

  val scrollState = rememberScrollState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Albakal Logo & Header
      Box(
        modifier = Modifier
          .size(88.dp)
          .clip(CircleShape)
          .background(GreenPrimary),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.ShoppingBag,
          contentDescription = "Albakal Logo",
          tint = Color.White,
          modifier = Modifier.size(50.dp)
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "البقال",
        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
        color = GreenDark
      )
      Text(
        text = "سوقك وخضارك وبقالتك لباب بيتك",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(28.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "تسجيل الدخول",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = GreenPrimary
          )
          Text(
            text = "أدخل بيانات حسابك للمتابعة",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(20.dp))

          // Email or Phone
          OutlinedTextField(
            value = emailOrPhone,
            onValueChange = { emailOrPhone = it },
            label = { Text("البريد الإلكتروني أو رقم الهاتف") },
            leadingIcon = {
              Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = GreenPrimary)
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_input_email_or_phone"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Password
          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("كلمة المرور") },
            leadingIcon = {
              Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = GreenPrimary)
            },
            trailingIcon = {
              IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                  imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = "تبديل الرؤية"
                )
              }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_input_password"),
            shape = RoundedCornerShape(12.dp)
          )

          // Forgot password link
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(
              onClick = { showForgotPasswordDialog = true },
              modifier = Modifier.testTag("forgot_password_btn")
            ) {
              Text(
                text = "نسيت كلمة السر؟",
                color = AmberSecondary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Login Button
          Button(
            onClick = {
              if (emailOrPhone.isBlank() || password.isBlank()) {
                viewModel.showMessage("يرجى إدخال البريد الإلكتروني أو الهاتف وكلمة المرور")
              } else {
                viewModel.login(emailOrPhone, password) {}
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("login_submit_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
          ) {
            Text(
              text = "تسجيل الدخول",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Register Link
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "ليس لديك حساب؟ ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
              text = "إنشاء حساب جديد",
              color = GreenPrimary,
              fontWeight = FontWeight.Bold,
              modifier = Modifier
                .clickable { viewModel.navigateTo("REGISTER") }
                .testTag("goto_register_btn")
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }

  if (showForgotPasswordDialog) {
    ForgotPasswordDialog(
      viewModel = viewModel,
      onDismiss = { showForgotPasswordDialog = false }
    )
  }
}

@Composable
fun ForgotPasswordDialog(
  viewModel: AlbakalViewModel,
  onDismiss: () -> Unit
) {
  var phone by remember { mutableStateOf("") }
  var step by remember { mutableStateOf(1) } // 1: enter phone, 2: enter code & new pass
  var receivedCode by remember { mutableStateOf("") }
  var enteredCode by remember { mutableStateOf("") }
  var newPassword by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (step == 1) "استعادة الحساب" else "تعيين كلمة المرور الجديدة",
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column {
        if (step == 1) {
          Text(
            text = "أدخل رقم الهاتف المسجل بحسابك لطلب رمز التحقق المؤقت:",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 12.dp)
          )
          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("رقم الهاتف") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("forgot_phone_input")
          )
        } else {
          Text(
            text = "تم إنشاء رمز التحقق من الإدارة: $receivedCode\nيرجى كتابة الرمز وكلمة المرور الجديدة:",
            style = MaterialTheme.typography.bodySmall,
            color = GreenPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
          )
          OutlinedTextField(
            value = enteredCode,
            onValueChange = { enteredCode = it },
            label = { Text("رمز التحقق") },
            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("forgot_code_input")
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("كلمة المرور الجديدة") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("forgot_new_pass_input")
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (step == 1) {
            if (phone.isBlank()) {
              viewModel.showMessage("يرجى إدخال رقم الهاتف")
            } else {
              viewModel.requestPasswordReset(phone) { code ->
                receivedCode = code
                step = 2
              }
            }
          } else {
            if (enteredCode != receivedCode) {
              viewModel.showMessage("رمز التحقق غير متطابق")
            } else if (newPassword.isBlank()) {
              viewModel.showMessage("يرجى إدخال كلمة المرور الجديدة")
            } else {
              viewModel.verifyAndResetPassword(phone, enteredCode, newPassword) {
                onDismiss()
              }
            }
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
        modifier = Modifier.testTag("forgot_action_btn")
      ) {
        Text(if (step == 1) "إرسال الطلب" else "حفظ كلمة المرور")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("إلغاء")
      }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: AlbakalViewModel) {
  var selectedRole by remember { mutableStateOf("CUSTOMER") } // CUSTOMER or MERCHANT
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }

  // Address
  val areas by viewModel.governoratesWithDistricts.collectAsState()
  val distinctGovs = remember(areas) {
    areas.map { it.governorate }.distinct().ifEmpty { listOf("الإسكندرية", "القاهرة", "الجيزة") }
  }

  var selectedGov by remember { mutableStateOf("الإسكندرية") }
  var govExpanded by remember { mutableStateOf(false) }

  val districtsForGov = remember(areas, selectedGov) {
    areas.filter { it.governorate == selectedGov }.map { it.district }.distinct()
  }

  var district by remember { mutableStateOf(districtsForGov.firstOrNull() ?: "ميامي") }
  var street by remember { mutableStateOf("") }
  var apartment by remember { mutableStateOf("") }
  var floor by remember { mutableStateOf("") }

  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header
    Surface(
      color = GreenPrimary,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = { viewModel.navigateTo("LOGIN") },
          modifier = Modifier.testTag("register_back_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "رجوع",
            tint = Color.White
          )
        }
        Text(
          text = "إنشاء حساب جديد في البقال",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
      }
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Role Switcher Card
      Text(
        text = "اختر نوع الحساب:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        AccountTypeChip(
          title = "عميل (مشتري)",
          subtitle = "شراء وتوصيل للمنزل",
          icon = Icons.Default.Person,
          isSelected = selectedRole == "CUSTOMER",
          onClick = { selectedRole = "CUSTOMER" },
          modifier = Modifier
            .weight(1f)
            .testTag("role_customer_btn")
        )
        AccountTypeChip(
          title = "تاجر (محل/مورد)",
          subtitle = "عرض المنتجات والبيع",
          icon = Icons.Default.Storefront,
          isSelected = selectedRole == "MERCHANT",
          onClick = { selectedRole = "MERCHANT" },
          modifier = Modifier
            .weight(1f)
            .testTag("role_merchant_btn")
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "البيانات الشخصية",
            fontWeight = FontWeight.Bold,
            color = GreenPrimary,
            fontSize = 15.sp
          )
          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(if (selectedRole == "MERCHANT") "اسم التاجر أو المحل" else "الاسم الكامل") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reg_name_input")
          )
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("البريد الإلكتروني") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = GreenPrimary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reg_email_input")
          )
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("رقم الهاتف (للتواصل وتأكيد الطلبات)") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GreenPrimary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reg_phone_input")
          )
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("كلمة المرور") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reg_pass_input")
          )
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("تأكيد كلمة المرور") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reg_confirm_pass_input")
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Address Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "بيانات الموقع والعنوان",
            fontWeight = FontWeight.Bold,
            color = GreenPrimary,
            fontSize = 15.sp
          )
          Spacer(modifier = Modifier.height(12.dp))

          // Governorate Dropdown
          ExposedDropdownMenuBox(
            expanded = govExpanded,
            onExpandedChange = { govExpanded = it },
            modifier = Modifier.fillMaxWidth()
          ) {
            OutlinedTextField(
              value = selectedGov,
              onValueChange = {},
              readOnly = true,
              label = { Text("المحافظة") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = govExpanded) },
              modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
                .testTag("reg_gov_dropdown")
            )
            ExposedDropdownMenu(
              expanded = govExpanded,
              onDismissRequest = { govExpanded = false }
            ) {
              distinctGovs.forEach { gov ->
                DropdownMenuItem(
                  text = { Text(gov) },
                  onClick = {
                    selectedGov = gov
                    govExpanded = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // District / Section
          OutlinedTextField(
            value = district,
            onValueChange = { district = it },
            label = { Text("القسم / الحي / المنطقة") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = GreenPrimary) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reg_district_input")
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Street
          OutlinedTextField(
            value = street,
            onValueChange = { street = it },
            label = { Text("اسم الشارع") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reg_street_input")
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = apartment,
              onValueChange = { apartment = it },
              label = { Text("رقم الشقة/المحل") },
              modifier = Modifier
                .weight(1f)
                .testTag("reg_apartment_input")
            )
            OutlinedTextField(
              value = floor,
              onValueChange = { floor = it },
              label = { Text("رقم الدور") },
              modifier = Modifier
                .weight(1f)
                .testTag("reg_floor_input")
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = {
          if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
            viewModel.showMessage("يرجى إكمال جميع الحقول الأساسية")
          } else if (password != confirmPassword) {
            viewModel.showMessage("كلمة المرور وتأكيدها غير متطابقين")
          } else {
            val newUser = UserEntity(
              id = "usr_" + UUID.randomUUID().toString().take(8),
              name = name.trim(),
              email = email.trim(),
              phone = phone.trim(),
              password = password.trim(),
              role = selectedRole,
              status = "PENDING_APPROVAL", // Review by admin
              governorate = selectedGov,
              district = district.trim(),
              street = street.trim(),
              apartment = apartment.trim(),
              floor = floor.trim(),
              honorCodeAccepted = false
            )
            viewModel.registerUser(newUser) {
              viewModel.navigateTo("LOGIN")
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("register_submit_btn"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) {
        Text(
          text = "إرسال طلب إنشاء الحساب",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      TextButton(
        onClick = { viewModel.navigateTo("LOGIN") },
        modifier = Modifier.testTag("cancel_register_btn")
      ) {
        Text("العودة إلى تسجيل الدخول", color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  }
}

@Composable
private fun AccountTypeChip(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.clickable { onClick() },
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) GreenContainer else MaterialTheme.colorScheme.surface
    ),
    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, GreenPrimary) else null
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) GreenPrimary else Color.Gray,
        modifier = Modifier.size(28.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = if (isSelected) GreenDark else MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )
    }
  }
}
