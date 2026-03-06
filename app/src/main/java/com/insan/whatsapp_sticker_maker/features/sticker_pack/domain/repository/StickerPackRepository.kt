package com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.repository

import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model.StickerPack
import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model.StickerPackId
import kotlinx.coroutines.flow.Flow

/**
 * Port (pure Kotlin interface) — zero Android/Room imports. The infrastructure layer provides the
 * adapter implementation.
 */
interface StickerPackRepository {

  /** Observe all sticker packs ordered by creation time descending. */
  fun observeAll(): Flow<List<StickerPack>>

  /** Persist a new or updated sticker pack. */
  suspend fun save(pack: StickerPack): Result<StickerPackId>

  /** Remove a sticker pack by its ID. */
  suspend fun delete(id: StickerPackId): Result<Unit>
}
