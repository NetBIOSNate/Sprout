package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColorScheme = lightColorScheme(
    primary = SproutPrimary,
    onPrimary = SproutOnPrimary,
    primaryContainer = SproutPrimaryContainer,
    onPrimaryContainer = SproutOnPrimaryContainer,
    secondary = SproutSecondary,
    onSecondary = SproutOnSecondary,
    secondaryContainer = SproutSecondaryContainer,
    onSecondaryContainer = SproutOnSecondaryContainer,
    tertiary = SproutTertiary,
    onTertiary = SproutOnTertiary,
    tertiaryContainer = SproutTertiaryContainer,
    onTertiaryContainer = SproutOnTertiaryContainer,
    background = SproutBackground,
    onBackground = SproutOnBackground,
    surface = SproutSurface,
    onSurface = SproutOnSurface,
    surfaceVariant = SproutSurfaceVariant,
    onSurfaceVariant = SproutOnSurfaceVariant,
    error = SproutError,
    errorContainer = SproutErrorContainer,
    onErrorContainer = SproutOnErrorContainer,
    outline = SproutOutline,
    outlineVariant = SproutOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBCF0AE),
    onPrimary = Color(0xFF002201),
    primaryContainer = Color(0xFF2D5A27),
    onPrimaryContainer = Color(0xFFBCF0AE),
    secondary = Color(0xFFBDCABE),
    onSecondary = Color(0xFF131E17),
    secondaryContainer = Color(0xFF3E4A41),
    onSecondaryContainer = Color(0xFFD9E6DA),
    tertiary = Color(0xFFCDC897),
    onTertiary = Color(0xFF1E1C00),
    tertiaryContainer = Color(0xFF4B4822),
    onTertiaryContainer = Color(0xFFEAE4B1),
    background = Color(0xFF131110),
    onBackground = Color(0xFFEDE0DD),
    surface = Color(0xFF131110),
    onSurface = Color(0xFFEDE0DD),
    surfaceVariant = Color(0xFF534340),
    onSurfaceVariant = Color(0xFFD8C2BE),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF857371),
    outlineVariant = Color(0xFF534340)
)

private val CozyForestColorScheme = lightColorScheme(
    primary = Color(0xFF2E5A36),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD3E8D7),
    onPrimaryContainer = Color(0xFF032209),
    secondary = Color(0xFF7A5C3E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF7F3ED),
    onSecondaryContainer = Color(0xFF261D15),
    tertiary = Color(0xFF53634F),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF4F1EA),
    onBackground = Color(0xFF1B1C1A),
    surface = Color(0xFFF4F1EA),
    onSurface = Color(0xFF1B1C1A),
    surfaceVariant = Color(0xFFDFD8CC),
    onSurfaceVariant = Color(0xFF4C473E),
    outline = Color(0xFF7D766A),
    outlineVariant = Color(0xFFD0C5B4)
)

private val NeonCyberpunkColorScheme = darkColorScheme(
    primary = Color(0xFF00F0FF),
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004E58),
    onPrimaryContainer = Color(0xFF8CF4FF),
    secondary = Color(0xFFFF007F),
    onSecondary = Color(0xFF5F002B),
    secondaryContainer = Color(0xFF320014),
    onSecondaryContainer = Color(0xFFFFD9E2),
    tertiary = Color(0xFFBD00FF),
    onTertiary = Color(0xFF5B0080),
    background = Color(0xFF090810),
    onBackground = Color(0xFFE2E1EC),
    surface = Color(0xFF090810),
    onSurface = Color(0xFFE2E1EC),
    surfaceVariant = Color(0xFF231F31),
    onSurfaceVariant = Color(0xFFC9C5D9),
    outline = Color(0xFF938F9F),
    outlineVariant = Color(0xFF231F31)
)

private val PastelSakuraColorScheme = lightColorScheme(
    primary = Color(0xFFE899A5),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDDE1),
    onPrimaryContainer = Color(0xFF3A0D15),
    secondary = Color(0xFFE6AAB4),
    onSecondary = Color(0xFF3D161C),
    secondaryContainer = Color(0xFFFFF0F1),
    onSecondaryContainer = Color(0xFF462228),
    tertiary = Color(0xFFD68390),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFFFF7F7),
    onBackground = Color(0xFF322627),
    surface = Color(0xFFFFF7F7),
    onSurface = Color(0xFF322627),
    surfaceVariant = Color(0xFFFFE6E9),
    onSurfaceVariant = Color(0xFF6F5155),
    outline = Color(0xFF907074),
    outlineVariant = Color(0xFFFFD1D6)
)

private val GoldenDesertColorScheme = lightColorScheme(
    primary = Color(0xFFCD7F32),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFE0B2),
    onPrimaryContainer = Color(0xFF5D2D00),
    secondary = Color(0xFF8D6E63),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFAF2EB),
    onSecondaryContainer = Color(0xFF3E2723),
    tertiary = Color(0xFFBF360C),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFFAF4EE),
    onBackground = Color(0xFF2C1E15),
    surface = Color(0xFFFAF4EE),
    onSurface = Color(0xFF2C1E15),
    surfaceVariant = Color(0xFFF3E5D8),
    onSurfaceVariant = Color(0xFF504439),
    outline = Color(0xFF837467),
    outlineVariant = Color(0xFFE6D2C0)
)

private val GoldenHourColorScheme = lightColorScheme(
    primary = Color(0xFFFFB300),
    onPrimary = Color(0xFF332200),
    primaryContainer = Color(0xFFFFF2CC),
    onPrimaryContainer = Color(0xFF4C3B00),
    secondary = Color(0xFFF1C40F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFCF8E3),
    onSecondaryContainer = Color(0xFF4C3B00),
    background = Color(0xFFFCFBF7),
    onBackground = Color(0xFF2C1E15),
    surface = Color(0xFFFCFBF7),
    onSurface = Color(0xFF2C1E15),
    surfaceVariant = Color(0xFFFDF6E3),
    onSurfaceVariant = Color(0xFF504439),
    outline = Color(0xFF837467),
    outlineVariant = Color(0xFFE6D2C0)
)

private val ObsidianDarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color(0xFF140026),
    primaryContainer = Color(0xFF1F1F1F),
    onPrimaryContainer = Color(0xFFE2E2E2),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color(0xFF000000),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE1E1E1),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFE1E1E1),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFF8D8D8D),
    outline = Color(0xFF7A7A7A),
    outlineVariant = Color(0xFF2C2C2C)
)

fun getDynamicTypography(fontFamily: FontFamily): Typography {
    return Typography(
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            lineHeight = 48.sp,
            letterSpacing = (-0.02).sp
        ),
        displayMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 38.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 36.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 28.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.01.sp
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    )
}

@Composable
fun SproutTheme(
    darkTheme: Boolean = false,
    selectedTheme: String = "Default",
    selectedFont: String = "System",
    content: @Composable () -> Unit
) {
    val colorScheme = when (selectedTheme) {
        "Cozy Forest" -> CozyForestColorScheme
        "Neon Cyberpunk" -> NeonCyberpunkColorScheme
        "Pastel Sakura" -> PastelSakuraColorScheme
        "Golden Desert" -> GoldenDesertColorScheme
        "Golden Hour" -> GoldenHourColorScheme
        "Obsidian Dark" -> ObsidianDarkColorScheme
        else -> if (darkTheme) DarkColorScheme else LightColorScheme
    }

    val fontFamily = when (selectedFont) {
        "Monospace" -> FontFamily.Monospace
        "Serif" -> FontFamily.Serif
        "Cursive" -> FontFamily.Cursive
        else -> FontFamily.SansSerif
    }

    val typography = getDynamicTypography(fontFamily)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
