package com.insan.whatsapp_sticker_maker.features.sticker_pack.application.usecase

import com.insan.whatsapp_sticker_maker.features.sticker_pack.application.dto.CreateStickerPackInput
import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model.Publisher
import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model.StickerPack
import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model.StickerPackId
import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model.StickerPackName
import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.repository.StickerPackRepository
import javax.inject.Inject

/**
 * Creates and persists a new [StickerPack].
 *
 * Wraps raw DTO strings into domain Value Objects so that all invariant validation happens here,
 * not in the ViewModel.
 */
class CreateStickerPackUseCase @Inject constructor(private val repository: StickerPackRepository) {
  suspend operator fun invoke(input: CreateStickerPackInput): Result<StickerPackId> = runCatching {
    val pack =
            StickerPack.create(
                    name = StickerPackName(input.name),
                    publisher = Publisher(input.publisher),
            )
    repository.save(pack).getOrThrow()
  }
}
