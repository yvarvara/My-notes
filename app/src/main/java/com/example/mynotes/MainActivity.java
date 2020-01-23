package com.example.mynotes;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;

import com.example.mynotes.Models.Note;
import com.example.mynotes.Models.Tag;
import com.example.mynotes.Models.TagNote;
import com.example.mynotes.ViewModels.MainViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesAdapter.OnListItemClickListener {

    private MainViewModel viewModel;
    private NotesAdapter notesAdapter;
    private ChipGroup filterChipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
                startActivity(intent);
            }
        });

        notesAdapter = new NotesAdapter(this, this);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                if (notes != null)
                    viewModel.setFilteredNotesToAll();
            }
        });

        viewModel.getFilteredNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                if (notes != null)
                    notesAdapter.setNotes(notes);
            }
        });

        viewModel.getAllTags().observe(this, new Observer<List<Tag>>() {
            @Override
            public void onChanged(@Nullable final List<Tag> tags) {
                if (tags != null)
                    notesAdapter.setTags(tags);
            }
        });

        viewModel.getAllTagNotes().observe(this, new Observer<List<TagNote>>() {
            @Override
            public void onChanged(@Nullable final List<TagNote> tagNotes) {
                if (tagNotes != null)
                    notesAdapter.setTagNotes(tagNotes);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        else
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL ));

        recyclerView.setAdapter(notesAdapter);
        notesAdapter.setSortingType(viewModel.getSortingType());

        filterChipGroup = findViewById(R.id.filterChipGroup);
        for (String tagTitle : viewModel.filterTags) {
            addChipToGroup(tagTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_date:
                viewModel.setSortingType(SortingType.DATE);
                notesAdapter.setSortingType(SortingType.DATE);
                break;
            case R.id.sort_by_title:
                viewModel.setSortingType(SortingType.TITLE);
                notesAdapter.setSortingType(SortingType.TITLE);
                break;
        }
        return true;
    }

    @Override
    public void onNoteClick(Note note, ArrayList<Tag> tags) {
        Intent intent = new Intent(this, EditorActivity.class);

        intent.putExtra("note", note);
        intent.putExtra("tags", tags);
        startActivity(intent);
    }

    @Override
    public void onOptionsClick(final Note note, View view) {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
        popupMenu.inflate(R.menu.menu_card);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.deleteOption:
                        viewModel.deleteNote(note);
                        break;
                }
                return false;
            }

        });
        popupMenu.show();
    }

    @Override
    public void onTagClicked(String tagTitle) {
        if (!viewModel.addFilterTag(tagTitle))
            return;

        addChipToGroup(tagTitle);
    }

    private void addChipToGroup(String tagTitle) {
        Chip chip = new Chip(this);
        chip.setText(tagTitle);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.colorChip)));
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterChipGroup.removeView(view);
                viewModel.removeFilterTag(((Chip) view).getText().toString());
                viewModel.setFilteredNotesToAll();
            }
        });

        filterChipGroup.addView(chip);
        viewModel.displayNotesByTags();
    }
}
