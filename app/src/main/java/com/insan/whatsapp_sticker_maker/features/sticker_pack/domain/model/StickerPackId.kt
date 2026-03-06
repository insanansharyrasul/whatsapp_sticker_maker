package com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model

import com.insan.whatsapp_sticker_maker.core.domain.DomainException

/**
 * Typed identity for a [StickerPack] aggregate. Must only contain `[a-zA-Z0-9_-]` to satisfy the
 * WhatsApp Stickers API.
 */
@JvmInline
value class StickerPackId(val value: String) {
  init {
    if (value.isBlank()) throw DomainException("StickerPackId must not be blank.")
    if (!value.matches(Regex("[a-zA-Z0-9_-]+")))
            throw DomainException(
                    "StickerPackId '$value' contains invalid characters. Only [a-zA-Z0-9_-] are allowed."
            )
  }
}
