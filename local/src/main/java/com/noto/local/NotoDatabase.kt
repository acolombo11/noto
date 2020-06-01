package com.noto.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noto.domain.model.Label
import com.noto.domain.model.Library
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoLabel

private const val NOTO_DATABASE = "Noto Database"

@TypeConverters(
    NotoColorConverter::class,
    NotoIconConverter::class,
    SortTypeConverter::class,
    SortMethodConverter::class,
    DateConverter::class
)
@Database(entities = [Noto::class, Library::class, Label::class, NotoLabel::class], version = 1, exportSchema = false)
abstract class NotoDatabase : RoomDatabase() {

    abstract val labelDao: LabelDao

    abstract val notoDao: NotoDao

    abstract val libraryDao: LibraryDao

    companion object {

        @Volatile
        private var INSTANCE: NotoDatabase? = null

        fun getInstance(context: Context): NotoDatabase =
            INSTANCE ?: synchronized(this) { INSTANCE ?: buildDatabase(context).also { INSTANCE = it } }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, NotoDatabase::class.java, NOTO_DATABASE)
                .fallbackToDestructiveMigration()
                .build()
    }
}