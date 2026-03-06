package com.insan.whatsapp_sticker_maker.features.sticker_pack.application.dto

/**
 * Input DTO for [CreateStickerPackUseCase]. Raw strings from the Presentation layer; the Use Case
 * wraps them in Value Objects.
 */
data class CreateStickerPackInput(
        val name: String,
        val publisher: String,
)
