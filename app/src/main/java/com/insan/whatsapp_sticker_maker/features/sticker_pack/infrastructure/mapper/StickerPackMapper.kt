package com.insan.whatsapp_sticker_maker.features.sticker_pack.infrastructure.mapper

import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model.Publisher
import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model.StickerPack
import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model.StickerPackId
import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model.StickerPackName
import com.insan.whatsapp_sticker_maker.features.sticker_pack.infrastructure.local.StickerPackEntity
import kotlinx.datetime.Instant

/** Maps between the [StickerPack] aggregate and its Room persistence model. */
object StickerPackMapper {

        fun toEntity(pack: StickerPack): StickerPackEntity =
                StickerPackEntity(
                        id = pack.id.value,
                        name = pack.name.value,
                        publisher = pack.publisher.value,
                        createdAtEpochSeconds = pack.createdAt.epochSeconds,
                        isAddedToWhatsApp = pack.isAddedToWhatsApp,
                )

        fun toDomain(entity: StickerPackEntity): StickerPack =
                StickerPack.reconstitute(
                        id = StickerPackId(entity.id),
                        name = StickerPackName(entity.name),
                        publisher = Publisher(entity.publisher),
                        createdAt = Instant.fromEpochSeconds(entity.createdAtEpochSeconds),
                        isAddedToWhatsApp = entity.isAddedToWhatsApp,
                )
}
