package com.example.mynotes.DB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mynotes.Models.Note;
import com.example.mynotes.Models.Tag;
import com.example.mynotes.Models.TagNote;

import java.util.List;

@Dao
public interface TagNoteDao {
    @Insert
    void insert(TagNote tagToNote);

    @Query("SELECT * FROM notes " +
            "INNER JOIN tagNotes " +
            "ON notes.noteId=tagNotes.noteId " +
            "WHERE tagNotes.tagId=:tagId"
    )
    LiveData<List<Note>> getNotesByTag(final long tagId);

    @Query("SELECT * FROM tags " +
            "INNER JOIN tagNotes " +
            "ON tags.tagId=tagNotes.tagId " +
            "WHERE tagNotes.noteId=:noteId"
    )
    List<Tag> getTagsForNote(final long noteId);

    @Query("SELECT * FROM tagNotes ORDER BY noteId")
    LiveData<List<TagNote>> getAllTagNotes();

    @Query("DELETE FROM tagNotes WHERE tagNotes.noteId=:nodeId")
    void deleteAllTagsForNote(long nodeId);
}
