package com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model

import com.insan.whatsapp_sticker_maker.core.domain.DomainException

private const val MAX_LENGTH = 128

/** Value Object representing the display name of a sticker pack. */
data class StickerPackName(val value: String) {
  init {
    if (value.isBlank()) throw DomainException("Sticker pack name must not be blank.")
    if (value.length > MAX_LENGTH)
            throw DomainException("Sticker pack name must not exceed $MAX_LENGTH characters.")
  }
}
