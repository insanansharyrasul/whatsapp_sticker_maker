package com.insan.whatsapp_sticker_maker.features.sticker_pack.presentation.state

/** One-shot events dispatched via [SharedFlow] — navigation, toasts, etc. */
sealed interface HomeUiEvent {
  /** A new sticker pack was successfully created. */
  data class ShowSnackbar(val message: String) : HomeUiEvent
}
