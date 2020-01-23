package com.example.mynotes.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "notes")
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "noteId")
    public long id;

    public String title;
    public String body;
    public Date creationDate;
    public int color;

    public Note(String title, String body, int color) {
        creationDate = new Date();

        if (title.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH);
            this.title = sdf.format(creationDate);
        }
        else {
            this.title = title;
        }

        this.body = body;
        this.color = color;
    }
}
