package com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model

import java.util.UUID
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * AggregateRoot
 *
 * Represents a WhatsApp sticker pack. This is the consistency boundary for all sticker-related
 * invariants (3–30 stickers, identifier format, etc.).
 *
 * All mutation must go through methods on this class — never mutate fields directly.
 */
class StickerPack
private constructor(
        val id: StickerPackId,
        val name: StickerPackName,
        val publisher: Publisher,
        val createdAt: Instant,
        val isAddedToWhatsApp: Boolean,
) {
        companion object {
                /**
                 * Factory method to create a new sticker pack with validation. Generates a unique
                 * [StickerPackId] automatically.
                 */
                fun create(name: StickerPackName, publisher: Publisher): StickerPack {
                        val id = StickerPackId(UUID.randomUUID().toString().replace("-", "_"))
                        return StickerPack(
                                id = id,
                                name = name,
                                publisher = publisher,
                                createdAt = Clock.System.now(),
                                isAddedToWhatsApp = false,
                        )
                }

                /** Reconstitute from persistence without raising domain events. */
                fun reconstitute(
                        id: StickerPackId,
                        name: StickerPackName,
                        publisher: Publisher,
                        createdAt: Instant,
                        isAddedToWhatsApp: Boolean,
                ): StickerPack =
                        StickerPack(
                                id = id,
                                name = name,
                                publisher = publisher,
                                createdAt = createdAt,
                                isAddedToWhatsApp = isAddedToWhatsApp,
                        )
        }

        /** Returns a copy with [isAddedToWhatsApp] set to true. */
        fun markAsAddedToWhatsApp(): StickerPack = copy(isAddedToWhatsApp = true)

        private fun copy(
                id: StickerPackId = this.id,
                name: StickerPackName = this.name,
                publisher: Publisher = this.publisher,
                createdAt: Instant = this.createdAt,
                isAddedToWhatsApp: Boolean = this.isAddedToWhatsApp,
        ) = StickerPack(id, name, publisher, createdAt, isAddedToWhatsApp)
}
