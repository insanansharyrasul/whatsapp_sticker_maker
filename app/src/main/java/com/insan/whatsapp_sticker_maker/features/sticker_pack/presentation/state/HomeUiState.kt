package com.insan.whatsapp_sticker_maker.features.sticker_pack.presentation.state

import com.insan.whatsapp_sticker_maker.features.sticker_pack.application.dto.StickerPackSummary

/** Immutable snapshot of the Home screen state, driven by [StateFlow]. */
sealed interface HomeUiState {

  /** Data is loading from the database for the first time. */
  data object Loading : HomeUiState

  /** The pack list is ready to display (may be empty). */
  data class Ready(
          val packs: List<StickerPackSummary> = emptyList(),
          val isCreateDialogVisible: Boolean = false,
          val newPackNameInput: String = "",
          val newPackPublisherInput: String = "",
          val isCreating: Boolean = false,
          val inputError: String? = null,
  ) : HomeUiState

  /** An unrecoverable error occurred (e.g. DB failure). */
  data class Error(val message: String) : HomeUiState
}
