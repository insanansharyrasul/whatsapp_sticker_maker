package com.insan.whatsapp_sticker_maker.features.sticker_pack.infrastructure.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StickerPackDao {

  @Query("SELECT * FROM sticker_packs ORDER BY createdAtEpochSeconds DESC")
  fun observeAll(): Flow<List<StickerPackEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(entity: StickerPackEntity)

  @Query("DELETE FROM sticker_packs WHERE id = :id") suspend fun deleteById(id: String)
}
