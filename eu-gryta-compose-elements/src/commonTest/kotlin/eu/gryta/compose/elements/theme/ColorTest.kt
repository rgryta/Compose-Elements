package eu.gryta.compose.elements.theme

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ColorTest {

    @Test
    fun `light primary color is defined`() {
        assertEquals(Color(0xFF006C4C), md_theme_light_primary)
    }

    @Test
    fun `dark primary color is defined`() {
        assertEquals(Color(0xFF6CDBAC), md_theme_dark_primary)
    }

    @Test
    fun `light and dark primary colors are different`() {
        assertNotEquals(md_theme_light_primary, md_theme_dark_primary)
    }

    @Test
    fun `light and dark surface colors are different`() {
        assertNotEquals(md_theme_light_surface, md_theme_dark_surface)
    }

    @Test
    fun `light and dark background colors are different`() {
        assertNotEquals(md_theme_light_background, md_theme_dark_background)
    }

    @Test
    fun `light and dark error colors are different`() {
        assertNotEquals(md_theme_light_error, md_theme_dark_error)
    }

    @Test
    fun `light on primary color contrasts with primary`() {
        assertNotEquals(md_theme_light_onPrimary, md_theme_light_primary)
    }

    @Test
    fun `dark on primary color contrasts with primary`() {
        assertNotEquals(md_theme_dark_onPrimary, md_theme_dark_primary)
    }

    @Test
    fun `light and dark secondary colors are different`() {
        assertNotEquals(md_theme_light_secondary, md_theme_dark_secondary)
    }

    @Test
    fun `light and dark tertiary colors are different`() {
        assertNotEquals(md_theme_light_tertiary, md_theme_dark_tertiary)
    }

    @Test
    fun `light surface variant is defined`() {
        assertEquals(Color(0xFFDBE5DD), md_theme_light_surfaceVariant)
    }

    @Test
    fun `dark surface variant is defined`() {
        assertEquals(Color(0xFF404943), md_theme_dark_surfaceVariant)
    }
}
