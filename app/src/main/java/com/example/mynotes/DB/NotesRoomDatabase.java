package com.example.mynotes.DB;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.Database;

import com.example.mynotes.Models.Note;
import com.example.mynotes.Models.Tag;
import com.example.mynotes.Models.TagNote;

@Database(entities = {Note.class, TagNote.class, Tag.class}, version = 4, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class NotesRoomDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();
    public abstract TagDao tagDao();
    public abstract TagNoteDao tagNoteDao();

    private static volatile NotesRoomDatabase INSTANCE;

    // singleton implementation using lazy initialization
    public static NotesRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NotesRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NotesRoomDatabase.class, "notes_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

