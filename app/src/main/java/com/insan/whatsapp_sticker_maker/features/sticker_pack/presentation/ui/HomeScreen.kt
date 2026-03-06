package com.insan.whatsapp_sticker_maker.features.sticker_pack.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.insan.whatsapp_sticker_maker.features.sticker_pack.application.dto.StickerPackSummary
import com.insan.whatsapp_sticker_maker.features.sticker_pack.presentation.state.HomeUiEvent
import com.insan.whatsapp_sticker_maker.features.sticker_pack.presentation.state.HomeUiState
import com.insan.whatsapp_sticker_maker.features.sticker_pack.presentation.viewmodel.HomeViewModel

/**
 * Home Dashboard screen — shows the "My Packs" list and a FAB to create a new sticker pack.
 *
 * In Flutter you would call this a Scaffold with a floatingActionButton. In Jetpack Compose the
 * equivalent widget is [FloatingActionButton] placed inside [Scaffold].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
        viewModel: HomeViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }

  // Collect one-shot events (e.g. snackbar after pack created)
  LaunchedEffect(Unit) {
    viewModel.uiEvent.collect { event ->
      when (event) {
        is HomeUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
      }
    }
  }

  Scaffold(
          topBar = {
            TopAppBar(
                    title = {
                      Text(
                              text = "Sticker Maker",
                              style = MaterialTheme.typography.titleLarge,
                              fontWeight = FontWeight.Bold,
                      )
                    },
                    colors =
                            TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                            ),
            )
          },
          floatingActionButton = {
            FloatingActionButton(
                    onClick = viewModel::onFabClicked,
                    containerColor = MaterialTheme.colorScheme.primary,
            ) {
              Icon(
                      imageVector = Icons.Filled.Add,
                      contentDescription = "Create new sticker pack",
              )
            }
          },
          snackbarHost = { SnackbarHost(snackbarHostState) },
  ) { innerPadding ->
    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
      when (val state = uiState) {
        is HomeUiState.Loading -> LoadingContent()
        is HomeUiState.Error -> ErrorContent(message = state.message)
        is HomeUiState.Ready -> {
          ReadyContent(
                  state = state,
                  onPackNameChanged = viewModel::onPackNameChanged,
                  onPublisherChanged = viewModel::onPublisherChanged,
                  onConfirmCreate = viewModel::onConfirmCreate,
                  onDismissDialog = viewModel::onDismissDialog,
          )
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// Content composables
// ---------------------------------------------------------------------------

@Composable
private fun LoadingContent() {
  Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}

@Composable
private fun ErrorContent(message: String) {
  Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text(
            text = "Something went wrong:\n$message",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
    )
  }
}

@Composable
private fun ReadyContent(
        state: HomeUiState.Ready,
        onPackNameChanged: (String) -> Unit,
        onPublisherChanged: (String) -> Unit,
        onConfirmCreate: () -> Unit,
        onDismissDialog: () -> Unit,
) {
  Column(modifier = Modifier.fillMaxSize()) {
    // "My Packs" tab-style header
    MyPacksTabHeader()

    if (state.packs.isEmpty()) {
      EmptyPacksPlaceholder()
    } else {
      PackList(packs = state.packs)
    }
  }

  if (state.isCreateDialogVisible) {
    CreatePackDialog(
            nameInput = state.newPackNameInput,
            publisherInput = state.newPackPublisherInput,
            isCreating = state.isCreating,
            inputError = state.inputError,
            onNameChanged = onPackNameChanged,
            onPublisherChanged = onPublisherChanged,
            onConfirm = onConfirmCreate,
            onDismiss = onDismissDialog,
    )
  }
}

@Composable
private fun MyPacksTabHeader() {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
            text = "MY PACKS",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
    )
    androidx.compose.material3.HorizontalDivider(
            color = MaterialTheme.colorScheme.primary,
            thickness = 2.dp,
    )
  }
}

@Composable
private fun EmptyPacksPlaceholder() {
  Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text(
            text = "No sticker packs yet.\nTap + to create one!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun PackList(packs: List<StickerPackSummary>) {
  LazyColumn(
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
  ) { items(items = packs, key = { it.id }) { pack -> StickerPackCard(pack = pack) } }
}

@Composable
private fun StickerPackCard(pack: StickerPackSummary) {
  Card(
          modifier = Modifier.fillMaxWidth(),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
                text = pack.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
                text = pack.publisher,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
                text = "${pack.stickerCount} sticker(s)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      if (pack.isAddedToWhatsApp) {
        Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Added to WhatsApp",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
        )
      }
    }
  }
}

// ---------------------------------------------------------------------------
// Create Pack Dialog
// ---------------------------------------------------------------------------

@Composable
private fun CreatePackDialog(
        nameInput: String,
        publisherInput: String,
        isCreating: Boolean,
        inputError: String?,
        onNameChanged: (String) -> Unit,
        onPublisherChanged: (String) -> Unit,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit,
) {
  AlertDialog(
          onDismissRequest = { if (!isCreating) onDismiss() },
          title = { Text("New Sticker Pack") },
          text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              OutlinedTextField(
                      value = nameInput,
                      onValueChange = onNameChanged,
                      label = { Text("Pack name") },
                      placeholder = { Text("e.g. My Funny Memes") },
                      singleLine = true,
                      isError = inputError != null && publisherInput.isBlank().not() == false,
                      modifier = Modifier.fillMaxWidth(),
                      enabled = !isCreating,
              )
              OutlinedTextField(
                      value = publisherInput,
                      onValueChange = onPublisherChanged,
                      label = { Text("Author name") },
                      placeholder = { Text("e.g. John Doe") },
                      singleLine = true,
                      modifier = Modifier.fillMaxWidth(),
                      enabled = !isCreating,
              )
              if (inputError != null) {
                Text(
                        text = inputError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                )
              }
            }
          },
          confirmButton = {
            TextButton(
                    onClick = onConfirm,
                    enabled = !isCreating,
            ) {
              if (isCreating) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
              } else {
                Text("Create")
              }
            }
          },
          dismissButton = {
            TextButton(
                    onClick = onDismiss,
                    enabled = !isCreating,
            ) { Text("Cancel") }
          },
  )
}
