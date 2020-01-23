package com.example.mynotes.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "tags",
        indices = {@Index(value = {"tagName"},
        unique = true)})
public class Tag implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tagId")
    public long id;

    public String tagName;

    public Tag(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this)
            return true;

        if (!(object instanceof Tag))
            return false;

        Tag tag = (Tag) object;

        return tag.id == this.id;
    }
}
