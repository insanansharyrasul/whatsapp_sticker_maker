package com.insan.whatsapp_sticker_maker.features.sticker_pack.infrastructure.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sticker_packs")
data class StickerPackEntity(
        @PrimaryKey val id: String,
        val name: String,
        val publisher: String,
        val createdAtEpochSeconds: Long,
        val isAddedToWhatsApp: Boolean,
)
