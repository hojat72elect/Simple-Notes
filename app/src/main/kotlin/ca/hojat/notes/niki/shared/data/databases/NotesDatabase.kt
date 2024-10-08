package ca.hojat.notes.niki.shared.data.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ca.hojat.notes.niki.R
import ca.hojat.notes.niki.shared.interfaces.NotesDao
import ca.hojat.notes.niki.shared.interfaces.WidgetsDao
import ca.hojat.notes.niki.shared.data.models.Note
import ca.hojat.notes.niki.shared.data.models.NoteType
import ca.hojat.notes.niki.shared.data.models.Widget
import ca.hojat.notes.niki.shared.helpers.DEFAULT_WIDGET_TEXT_COLOR
import ca.hojat.notes.niki.shared.helpers.PROTECTION_NONE
import java.util.concurrent.Executors

@Database(entities = [Note::class, Widget::class], version = 4)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun NotesDao(): NotesDao

    abstract fun WidgetsDao(): WidgetsDao

    companion object {
        private var db: NotesDatabase? = null
        private var defaultWidgetBgColor = 0

        fun getInstance(context: Context): NotesDatabase {
            defaultWidgetBgColor =
                context.resources.getColor(R.color.default_widget_bg_color)
            if (db == null) {
                synchronized(NotesDatabase::class) {
                    if (db == null) {
                        db = Room.databaseBuilder(
                            context.applicationContext,
                            NotesDatabase::class.java,
                            "notes.db"
                        )
                            .addCallback(object : Callback() {
                                override fun onCreate(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                    insertFirstNote(context)
                                }
                            })
                            .addMigrations(MIGRATION_1_2)
                            .addMigrations(MIGRATION_2_3)
                            .addMigrations(MIGRATION_3_4)
                            .build()
                        db!!.openHelper.setWriteAheadLoggingEnabled(true)
                    }
                }
            }
            return db!!
        }

        fun destroyInstance() {
            db = null
        }

        private fun insertFirstNote(context: Context) {
            Executors.newSingleThreadScheduledExecutor().execute {
                val generalNote = context.resources.getString(R.string.general_note)
                val note = Note(null, generalNote, "", NoteType.TYPE_TEXT, "", PROTECTION_NONE, "")
                db!!.NotesDao().insertOrUpdate(note)
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.apply {
                    execSQL("ALTER TABLE widgets ADD COLUMN widget_bg_color INTEGER NOT NULL DEFAULT $defaultWidgetBgColor")
                    execSQL("ALTER TABLE widgets ADD COLUMN widget_text_color INTEGER NOT NULL DEFAULT $DEFAULT_WIDGET_TEXT_COLOR")
                }
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.apply {
                    execSQL("ALTER TABLE notes ADD COLUMN protection_type INTEGER DEFAULT $PROTECTION_NONE NOT NULL")
                    execSQL("ALTER TABLE notes ADD COLUMN protection_hash TEXT DEFAULT '' NOT NULL")
                }
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE widgets ADD COLUMN widget_show_title INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
