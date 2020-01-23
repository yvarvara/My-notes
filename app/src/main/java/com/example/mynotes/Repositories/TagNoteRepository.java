package com.example.mynotes.Repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.mynotes.DB.NotesRoomDatabase;
import com.example.mynotes.DB.TagNoteDao;
import com.example.mynotes.Models.Note;
import com.example.mynotes.Models.Tag;
import com.example.mynotes.Models.TagNote;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TagNoteRepository {
    private TagNoteDao tagNoteDao;

    public TagNoteRepository(Application application) {
        NotesRoomDatabase db = NotesRoomDatabase.getDatabase(application);
        tagNoteDao = db.tagNoteDao();
    }

    public  List<Tag> getTagsForNote(long noteId) {
        try {
            return new getTagsForNoteAsyncTask(tagNoteDao).execute(noteId).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class getTagsForNoteAsyncTask extends AsyncTask<Long, Void, List<Tag>> {
        private TagNoteDao asyncTagNoteDao;


        getTagsForNoteAsyncTask(TagNoteDao dao) {
            asyncTagNoteDao = dao;
        }

        @Override
        protected List<Tag> doInBackground(final Long... params) {
            return asyncTagNoteDao.getTagsForNote(params[0]);
        }
    }

    public LiveData<List<TagNote>> getAllTagNotes() {
        return tagNoteDao.getAllTagNotes();
    }

    public void deleteAllTagsForNote(long noteId) {
        new deleteByNoteIdAsyncTask(tagNoteDao).execute(noteId);
    }

    private static class deleteByNoteIdAsyncTask extends AsyncTask<Long, Void, Void> {
        private TagNoteDao asyncTagNoteDao;

        deleteByNoteIdAsyncTask(TagNoteDao dao) {
            asyncTagNoteDao = dao;
        }

        @Override
        protected Void doInBackground(final Long... params) {
            asyncTagNoteDao.deleteAllTagsForNote(params[0]);
            return null;
        }
    }

    public void insert(final long tagId, final long nodeId) {
        new TagNoteRepository.insertAsyncTask(tagNoteDao, null).execute(
            new ArrayList<Long>() {
                {
                    add(tagId);
                    add(nodeId);
                }
            }
        );
    }

    public void insert(final long tagId, AsyncTask<Note, Void, Note> noteInsertion) {
        new TagNoteRepository.insertAsyncTask(tagNoteDao, noteInsertion).execute(
            new ArrayList<Long>() {
                {
                    add(tagId);
                }
            }
        );
    }

    private static class insertAsyncTask extends AsyncTask<List<Long>, Void, Void> {
        private TagNoteDao asyncTagNoteDao;
        private AsyncTask<Note, Void, Note> noteInsertion;

        insertAsyncTask(TagNoteDao dao, AsyncTask<Note, Void, Note> noteInsertion) {
            asyncTagNoteDao = dao;
            this.noteInsertion = noteInsertion;
        }

        @Override
        protected Void doInBackground(final List<Long>... params) {
            if (noteInsertion != null) {
                try {
                    asyncTagNoteDao.insert(new TagNote(params[0].get(0), noteInsertion.get().id));
                } catch (InterruptedException ignored) {
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                asyncTagNoteDao.insert(new TagNote(params[0].get(0), params[0].get(1)));
            }
            return null;
        }
    }
}
