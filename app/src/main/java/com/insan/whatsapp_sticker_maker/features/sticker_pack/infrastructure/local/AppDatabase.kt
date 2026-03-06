package com.insan.whatsapp_sticker_maker.features.sticker_pack.infrastructure.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
        entities = [StickerPackEntity::class],
        version = 1,
        exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun stickerPackDao(): StickerPackDao
}
