package com.example.mynotes.ViewModels;

import android.app.Application;
import android.graphics.Color;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mynotes.Models.Note;
import com.example.mynotes.Models.Tag;
import com.example.mynotes.Repositories.NoteRepository;
import com.example.mynotes.Repositories.TagNoteRepository;
import com.example.mynotes.Repositories.TagRepository;

import java.util.ArrayList;
import java.util.List;

public class EditorViewModel extends AndroidViewModel {
    private NoteRepository noteRepository;
    private TagRepository tagRepository;
    private TagNoteRepository tagNoteRepository;

    public List<String> currentTagTitles = new ArrayList<>();

    private int currentColor;
    private long currentId;

    public EditorViewModel(@NonNull Application application) {
        super(application);

        noteRepository = new NoteRepository(application);
        tagRepository = new TagRepository(application);
        tagNoteRepository = new TagNoteRepository(application);

        currentColor = Color.parseColor("#ffffff");
    }

    public LiveData<List<Tag>> getAllTags() {
        return tagRepository.getAllTags();
    }

    private void insertNote(Note note, List<Tag> tags) {
        AsyncTask<Note, Void, Note> noteInsertion = noteRepository.insert(note);
        for (Tag t : tags) {
            if (t.id == 0)
                throw new AssertionError();
            tagNoteRepository.insert(t.id, noteInsertion);
        }
    }

    public void createNote(String title, String body) {
        Note note = new Note(title, body, currentColor);

        ArrayList<Tag> tags = new ArrayList<>();
        for (String tagTitle : currentTagTitles) {
            Tag tag = getTagByTitle(tagTitle);
            if (tag == null)
                tag = insertTag(new Tag(tagTitle));

            tags.add(tag);
        }

        insertNote(note, tags);
    }

    public void updateNote(Note note) {
        AsyncTask<Note, Void, Note> noteUpdate = noteRepository.update(note);
        tagNoteRepository.deleteAllTagsForNote(note.id);

        ArrayList<Tag> tags = new ArrayList<>();
        for (String tagTitle : currentTagTitles) {
            Tag tag = getTagByTitle(tagTitle);
            if (tag == null)
                tag = insertTag(new Tag(tagTitle));

            tags.add(tag);
        }

        for (Tag t : tags) {
            if (t.id == 0)
                throw new AssertionError();
            tagNoteRepository.insert(t.id, noteUpdate);
        }
    }

    private Tag getTagByTitle(String tagTitle) {
        return tagRepository.getTagByTitle(tagTitle);
    }

    private List<Tag> getTagsForNote(int nodeId) {
        return tagNoteRepository.getTagsForNote(nodeId);
    }

    private Tag insertTag(Tag tag) {
        return tagRepository.insert(tag);
    }

    public void deleteTag(Tag tag) {
        tagRepository.delete(tag);
    }

    public boolean addCurrentTagTitle(String tagTitle) {
        if (currentTagTitles.contains(tagTitle)) {
            return false;
        }
        return currentTagTitles.add(tagTitle);
    }

    public void removeCurrentTagTitle(String tagTitle) {
        currentTagTitles.remove(tagTitle);
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public void setCurrentId (long id) {
        this.currentId = id;
    }

    public long getCurrentId () {
        return currentId;
    }
}