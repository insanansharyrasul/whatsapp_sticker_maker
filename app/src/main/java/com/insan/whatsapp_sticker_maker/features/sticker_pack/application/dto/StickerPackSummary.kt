package com.insan.whatsapp_sticker_maker.features.sticker_pack.application.dto

import kotlinx.datetime.Instant

/**
 * Output DTO surfaced to the Presentation layer. The ViewModel must never reference domain models
 * directly.
 */
data class StickerPackSummary(
        val id: String,
        val name: String,
        val publisher: String,
        val stickerCount: Int,
        val isAddedToWhatsApp: Boolean,
        val createdAt: Instant,
)
