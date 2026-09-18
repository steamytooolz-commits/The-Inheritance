package com.theinheritance.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val ClayTypography = Typography(
    displayLarge = TextStyle(FontFamily.Default, FontWeight.Bold, 32.sp, 40.sp),
    headlineMedium = TextStyle(FontFamily.Default, FontWeight.SemiBold, 24.sp, 32.sp),
    bodyLarge = TextStyle(FontFamily.Default, FontWeight.Normal, 16.sp, 24.sp),
    bodyMedium = TextStyle(FontFamily.Default, FontWeight.Normal, 14.sp, 20.sp),
    labelLarge = TextStyle(FontFamily.Default, FontWeight.SemiBold, 14.sp)
)
