package com.example.mynotes.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "tagNotes",
        primaryKeys = { "tagId", "noteId" },
        foreignKeys = {
        @ForeignKey(entity = Note.class,
                parentColumns = "noteId",
                childColumns = "noteId"),
        @ForeignKey(entity = Tag.class,
                parentColumns = "tagId",
                childColumns = "tagId")
        })
public class TagNote {
    public long tagId;
    public long noteId;

    public TagNote(final long tagId, final long noteId) {
        this.tagId = tagId;
        this.noteId = noteId;
    }
}