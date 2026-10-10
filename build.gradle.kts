// Archivo de configuración del proyecto raíz.
// Aquí solo se declaran los plugins que usan los módulos, sin aplicarlos todavía.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
