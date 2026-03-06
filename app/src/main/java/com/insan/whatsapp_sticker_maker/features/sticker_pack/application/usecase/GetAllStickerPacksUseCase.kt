package com.insan.whatsapp_sticker_maker.features.sticker_pack.application.usecase

import com.insan.whatsapp_sticker_maker.features.sticker_pack.application.dto.StickerPackSummary
import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.repository.StickerPackRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Observes all sticker packs and maps them to presentation-safe [StickerPackSummary] DTOs. */
class GetAllStickerPacksUseCase @Inject constructor(private val repository: StickerPackRepository) {
  /** Returns a cold [Flow] that emits whenever the list of packs changes. */
  operator fun invoke(): Flow<List<StickerPackSummary>> =
          repository.observeAll().map { packs ->
            packs.map { pack ->
              StickerPackSummary(
                      id = pack.id.value,
                      name = pack.name.value,
                      publisher = pack.publisher.value,
                      stickerCount = 0, // sticker sub-aggregate not wired yet
                      isAddedToWhatsApp = pack.isAddedToWhatsApp,
                      createdAt = pack.createdAt,
              )
            }
          }
}
