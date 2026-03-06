package com.insan.whatsapp_sticker_maker.features.sticker_pack.infrastructure.di

import android.content.Context
import androidx.room.Room
import com.insan.whatsapp_sticker_maker.features.sticker_pack.domain.repository.StickerPackRepository
import com.insan.whatsapp_sticker_maker.features.sticker_pack.infrastructure.local.AppDatabase
import com.insan.whatsapp_sticker_maker.features.sticker_pack.infrastructure.local.StickerPackDao
import com.insan.whatsapp_sticker_maker.features.sticker_pack.infrastructure.repository.StickerPackRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StickerPackModule {

  @Binds
  @Singleton
  abstract fun bindStickerPackRepository(impl: StickerPackRepositoryImpl): StickerPackRepository

  companion object {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "sticker_maker.db").build()

    @Provides fun provideStickerPackDao(db: AppDatabase): StickerPackDao = db.stickerPackDao()
  }
}
