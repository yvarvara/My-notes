package com.example.mynotes.Repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;


import com.example.mynotes.DB.NoteDao;
import com.example.mynotes.DB.NotesRoomDatabase;
import com.example.mynotes.Models.Note;

import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Note>> notes;

    public NoteRepository(Application application) {
        NotesRoomDatabase db = NotesRoomDatabase.getDatabase(application);
        noteDao = db.noteDao();
        notes = noteDao.getAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return notes;
    }

    public AsyncTask<Note, Void, Note> insert(Note note) {
        return new insertAsyncTask(noteDao).execute(note);
    }

    private static class insertAsyncTask extends AsyncTask<Note, Void, Note> {
        private NoteDao asyncNoteDao;

        insertAsyncTask(NoteDao dao) {
            asyncNoteDao = dao;
        }

        @Override
        protected Note doInBackground(final Note... params) {
            params[0].id = asyncNoteDao.insert(params[0]);
            return params[0];
        }
    }

    public AsyncTask<Note, Void, Note> update(Note note) {
        return new updateAsyncTask(noteDao).execute(note);
    }

    private static class updateAsyncTask extends AsyncTask<Note, Void, Note> {
        private NoteDao asyncNoteDao;

        updateAsyncTask(NoteDao dao) {
            asyncNoteDao = dao;
        }

        @Override
        protected Note doInBackground(final Note... params) {
            asyncNoteDao.update(params[0]);
            return params[0];
        }
    }

    public AsyncTask<Note, Void, Note> delete(Note note) {
        new deleteAsyncTask(noteDao).execute(note);
        return null;
    }

    private static class deleteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao asyncNoteDao;

        deleteAsyncTask(NoteDao dao) {
            asyncNoteDao = dao;
        }

        @Override
        protected Void doInBackground(final Note... params) {
            asyncNoteDao.delete(params[0]);
            return null;
        }
    }
}
