package com.insan.whatsapp_sticker_maker.features.sticker_pack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insan.whatsapp_sticker_maker.features.sticker_pack.application.dto.CreateStickerPackInput
import com.insan.whatsapp_sticker_maker.features.sticker_pack.application.dto.StickerPackSummary
import com.insan.whatsapp_sticker_maker.features.sticker_pack.application.usecase.CreateStickerPackUseCase
import com.insan.whatsapp_sticker_maker.features.sticker_pack.application.usecase.GetAllStickerPacksUseCase
import com.insan.whatsapp_sticker_maker.features.sticker_pack.presentation.state.HomeUiEvent
import com.insan.whatsapp_sticker_maker.features.sticker_pack.presentation.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for [HomeScreen]. Exposes [uiState] as a [StateFlow] and [uiEvent] as a [SharedFlow]
 * (UDF pattern).
 */
@HiltViewModel
class HomeViewModel
@Inject
constructor(
        getAllStickerPacks: GetAllStickerPacksUseCase,
        private val createStickerPack: CreateStickerPackUseCase,
) : ViewModel() {

  // ---- Internal dialog / form state -------------------------------------------

  /** Holds ephemeral dialog state independently of the pack list. */
  private data class DialogState(
          val isVisible: Boolean = false,
          val nameInput: String = "",
          val publisherInput: String = "",
          val isCreating: Boolean = false,
          val inputError: String? = null,
  )

  private val _dialogState = MutableStateFlow(DialogState())

  // ---- Public UiState ---------------------------------------------------------

  /**
   * Combines the database-backed pack list with ephemeral dialog state so that both sources of
   * change re-emit correctly.
   */
  val uiState: StateFlow<HomeUiState> =
          combine(
                          getAllStickerPacks(),
                          _dialogState,
                  ) { packs: List<StickerPackSummary>, dialog: DialogState ->
                    HomeUiState.Ready(
                            packs = packs,
                            isCreateDialogVisible = dialog.isVisible,
                            newPackNameInput = dialog.nameInput,
                            newPackPublisherInput = dialog.publisherInput,
                            isCreating = dialog.isCreating,
                            inputError = dialog.inputError,
                    ) as
                            HomeUiState
                  }
                  .catch { emit(HomeUiState.Error(it.message ?: "Unknown error")) }
                  .stateIn(
                          scope = viewModelScope,
                          started = SharingStarted.WhileSubscribed(5_000),
                          initialValue = HomeUiState.Loading,
                  )

  // ---- Public UiEvent ---------------------------------------------------------

  private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
  val uiEvent = _uiEvent.asSharedFlow()

  // ---- Intents ----------------------------------------------------------------

  fun onFabClicked() {
    _dialogState.value = DialogState(isVisible = true)
  }

  fun onDismissDialog() {
    _dialogState.value = DialogState()
  }

  fun onPackNameChanged(value: String) {
    _dialogState.value = _dialogState.value.copy(nameInput = value, inputError = null)
  }

  fun onPublisherChanged(value: String) {
    _dialogState.value = _dialogState.value.copy(publisherInput = value, inputError = null)
  }

  fun onConfirmCreate() {
    val dialog = _dialogState.value
    if (dialog.nameInput.isBlank()) {
      _dialogState.value = dialog.copy(inputError = "Pack name must not be empty.")
      return
    }
    if (dialog.publisherInput.isBlank()) {
      _dialogState.value = dialog.copy(inputError = "Author name must not be empty.")
      return
    }

    _dialogState.value = dialog.copy(isCreating = true, inputError = null)

    viewModelScope.launch {
      val result =
              createStickerPack(
                      CreateStickerPackInput(
                              name = dialog.nameInput.trim(),
                              publisher = dialog.publisherInput.trim(),
                      )
              )
      result.fold(
              onSuccess = {
                _dialogState.value = DialogState()
                _uiEvent.emit(HomeUiEvent.ShowSnackbar("Sticker pack created!"))
              },
              onFailure = { error ->
                _dialogState.value =
                        _dialogState.value.copy(
                                isCreating = false,
                                inputError = error.message,
                        )
              }
      )
    }
  }
}
