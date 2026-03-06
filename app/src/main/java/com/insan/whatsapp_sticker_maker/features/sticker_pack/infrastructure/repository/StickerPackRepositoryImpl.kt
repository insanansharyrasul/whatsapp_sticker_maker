package com.insan.whatsapp_sticker_maker.features.sticker_pack.infrastructure.repository

import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model.StickerPack
import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.model.StickerPackId
import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.repository.StickerPackRepository
import com.insan.whatsapp_sticker_maker.features.sticker_pack.infrastructure.local.StickerPackDao
import com.insan.whatsapp_sticker_maker.features.sticker_pack.infrastructure.mapper.StickerPackMapper
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class StickerPackRepositoryImpl @Inject constructor(private val dao: StickerPackDao) :
        StickerPackRepository {

  override fun observeAll(): Flow<List<StickerPack>> =
          dao.observeAll().map { entities -> entities.map(StickerPackMapper::toDomain) }

  override suspend fun save(pack: StickerPack): Result<StickerPackId> = runCatching {
    dao.upsert(StickerPackMapper.toEntity(pack))
    pack.id
  }

  override suspend fun delete(id: StickerPackId): Result<Unit> = runCatching {
    dao.deleteById(id.value)
  }
}
