package com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model

import com.insan.whatsapp_sticker_maker.core.domain.DomainException

private const val MAX_LENGTH = 128

/** Value Object representing the publisher/author of a sticker pack. */
data class Publisher(val value: String) {
  init {
    if (value.isBlank()) throw DomainException("Publisher name must not be blank.")
    if (value.length > MAX_LENGTH)
            throw DomainException("Publisher name must not exceed $MAX_LENGTH characters.")
  }
}
