package com.codex.listenandwrite.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codex.listenandwrite.data.dao.DictationDao
import com.codex.listenandwrite.data.dao.MistakeDao
import com.codex.listenandwrite.data.dao.TextbookDao
import com.codex.listenandwrite.data.dao.UnitDao
import com.codex.listenandwrite.data.dao.WordBookDao
import com.codex.listenandwrite.data.dao.WordDao
import com.codex.listenandwrite.data.entity.DictationItemEntity
import com.codex.listenandwrite.data.entity.DictationSessionEntity
import com.codex.listenandwrite.data.entity.MistakeRecordEntity
import com.codex.listenandwrite.data.entity.TextbookEntity
import com.codex.listenandwrite.data.entity.UnitEntity
import com.codex.listenandwrite.data.entity.WordBookEntity
import com.codex.listenandwrite.data.entity.WordBookItemEntity
import com.codex.listenandwrite.data.entity.WordEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TextbookEntity::class,
        UnitEntity::class,
        WordEntity::class,
        WordBookEntity::class,
        WordBookItemEntity::class,
        DictationSessionEntity::class,
        DictationItemEntity::class,
        MistakeRecordEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun textbookDao(): TextbookDao
    abstract fun unitDao(): UnitDao
    abstract fun wordDao(): WordDao
    abstract fun wordBookDao(): WordBookDao
    abstract fun dictationDao(): DictationDao
    abstract fun mistakeDao(): MistakeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context, scope).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "listen_and_write.db"
            ).fallbackToDestructiveMigration()
            .addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Prepopulate database with complete PEP textbooks and sample data
                    scope.launch {
                        getInstance(context, scope).prepopulate(context)
                    }
                }
            }).build()
        }
    }

    suspend fun prepopulate(context: Context) {
        TextbookImporter.importFromAssets(context, this)
    }
}
