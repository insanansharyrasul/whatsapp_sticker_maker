package com.insan.whatsapp_sticker_maker.core.navigation

import kotlinx.serialization.Serializable

/** Type-safe navigation destinations for Compose Navigation. */
sealed interface Destination {

  @Serializable data object Home : Destination
}
