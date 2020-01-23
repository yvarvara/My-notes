package com.example.mynotes.Repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.mynotes.DB.NotesRoomDatabase;
import com.example.mynotes.DB.TagDao;
import com.example.mynotes.Models.Tag;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TagRepository {
    private TagDao tagDao;
    private LiveData<List<Tag>> tags;

    public TagRepository(Application application) {
        NotesRoomDatabase db = NotesRoomDatabase.getDatabase(application);
        tagDao = db.tagDao();
        tags = tagDao.getAllTags();
    }

    public LiveData<List<Tag>> getAllTags() {
        return tags;
    }

    public Tag insert(Tag tag) {
        try {
            return new TagRepository.insertAsyncTask(tagDao).execute(tag).get();
        } catch (InterruptedException ignored) {
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class insertAsyncTask extends AsyncTask<Tag, Void, Tag> {
        private TagDao asyncTagDao;

        insertAsyncTask(TagDao dao) {
            asyncTagDao = dao;
        }

        @Override
        protected Tag doInBackground(final Tag... params) {
            params[0].id = asyncTagDao.insert(params[0]);
            return params[0];
        }
    }

    public void delete(Tag tag) {
        new TagRepository.deleteAsyncTask(tagDao).execute(tag);
    }

    private static class deleteAsyncTask extends AsyncTask<Tag, Void, Void> {
        private TagDao asyncTagDao;

        deleteAsyncTask(TagDao dao) {
            asyncTagDao = dao;
        }

        @Override
        protected Void doInBackground(final Tag... params) {
            asyncTagDao.delete(params[0]);
            return null;
        }
    }

    public Tag getTagByTitle(String tagTitle) {
        AsyncTask<String, Void, Tag> task = new getTagByTitleAsyncTask(tagDao).execute(tagTitle);
        Tag result = null;
        try {
            result = task.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static class getTagByTitleAsyncTask extends AsyncTask<String, Void, Tag> {
        private TagDao asyncTagDao;

        getTagByTitleAsyncTask(TagDao dao) {
            asyncTagDao = dao;
        }

        @Override
        protected Tag doInBackground(final String... params) {
            return asyncTagDao.getTagByTitle(params[0]);
        }
    }

}
