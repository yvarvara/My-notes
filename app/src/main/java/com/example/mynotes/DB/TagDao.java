package com.example.mynotes.DB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mynotes.Models.Tag;

import java.util.List;

@Dao
public interface TagDao {
    @Insert
    long insert(Tag tag);

    @Delete
    void delete(Tag tag);

    @Query("SELECT * FROM tags WHERE tagName=:tagTitle")
    Tag getTagByTitle(String tagTitle);

    @Query("SELECT * FROM tags")
    LiveData<List<Tag>> getAllTags();
}
