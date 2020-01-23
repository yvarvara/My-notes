package com.example.mynotes.ViewModels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mynotes.Models.Note;
import com.example.mynotes.Models.Tag;
import com.example.mynotes.Models.TagNote;
import com.example.mynotes.Repositories.NoteRepository;
import com.example.mynotes.Repositories.TagNoteRepository;
import com.example.mynotes.Repositories.TagRepository;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private NoteRepository noteRepository;
    private TagRepository tagRepository;
    private TagNoteRepository tagNoteRepository;

    private LiveData<List<Note>> notes;
    private LiveData<List<TagNote>> tagNotes;
    private LiveData<List<Tag>> tags;
    private MutableLiveData<List<Note>> filteredNotes = new MutableLiveData<>();

    public List<String> filterTags = new ArrayList<>();

    public MainViewModel(@NonNull Application application) {
        super(application);

        noteRepository = new NoteRepository(application);
        tagRepository = new TagRepository(application);
        tagNoteRepository = new TagNoteRepository(application);

        tags = tagRepository.getAllTags();
        tagNotes = tagNoteRepository.getAllTagNotes();
        notes = noteRepository.getAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return notes;
    }

    public MutableLiveData<List<Note>> getFilteredNotes(){
        return filteredNotes;
    }

    private AsyncTask<Note, Void, Note> insertNote(Note note) {
        return noteRepository.insert(note);
    }

    private AsyncTask<Note, Void, Note> updateNote(Note note) {
        return noteRepository.update(note);
    }

    private void insertTagToNote(int tagId, AsyncTask<Note, Void, Note> noteInsertion) {
        tagNoteRepository.insert(tagId, noteInsertion);
    }

    public Tag getTagByTitle(String title) {
        return tagRepository.getTagByTitle(title);
    }

    public LiveData<List<Tag>> getAllTags() {
        return tags;
    }

    public LiveData<List<TagNote>> getAllTagNotes() {
        return tagNotes;
    }

    public void deleteNote(Note note) {
        tagNoteRepository.deleteAllTagsForNote(note.id);
        noteRepository.delete(note);
    }

    private List<Tag> getTagsForNote(long nodeId) {
        return tagNoteRepository.getTagsForNote(nodeId);
    }

    public boolean addFilterTag(String tagTitle) {
        if (filterTags.contains(tagTitle))
            return false;

        filterTags.add(tagTitle);
        return true;
    }

    public void removeFilterTag(String tagTitle) {
        filterTags.remove(tagTitle);
    }

    public void displayNotesByTags() {
        if (filteredNotes.getValue() == null) {
            return;
        }

        List<Note> newFilteredNotes = new ArrayList<>();

        for (Note note: filteredNotes.getValue()) {
            boolean containsAll = true;
            for (String title : filterTags) {
                Tag tag = getTagByTitle(title);
                if (!getTagsForNote(note.id).contains(tag))
                    containsAll = false;
            }
            if (containsAll)
                newFilteredNotes.add(note);
        }

        filteredNotes.setValue(newFilteredNotes);
    }

    public void setAllNotes() {
        filteredNotes.setValue(notes.getValue());
        displayNotesByTags();
    }
}
