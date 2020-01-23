package com.example.mynotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mynotes.Models.Note;
import com.example.mynotes.Models.Tag;
import com.example.mynotes.ViewModels.EditorViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import petrov.kristiyan.colorpicker.ColorPicker;

public class EditorActivity extends AppCompatActivity {

    private EditText titleEditText, bodyEditText;
    private AutoCompleteTextView tagsAutoCompleteTextView;
    private ChipGroup chipGroup;
    private View parent;

    private EditorViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        titleEditText = findViewById(R.id.titleEditText);
        bodyEditText = findViewById(R.id.bodyEditText);
        tagsAutoCompleteTextView = findViewById(R.id.tagsAutoCompleteTextView);
        chipGroup = findViewById(R.id.chipGroup);
        parent = findViewById(R.id.parent);

        final TagsAutoCompleteAdapter tagsAdapter = new TagsAutoCompleteAdapter(this,
                R.layout.string_dropdown_item);
        tagsAutoCompleteTextView.setAdapter(tagsAdapter);

        tagsAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> tagsAdapterView, View view, int i, long l) {
                tagsAutoCompleteTextView.setText("");
                String tagTitle = (String) tagsAdapterView.getItemAtPosition(i);
                if (tagTitle.startsWith("Create tag \""))
                    tagTitle = tagTitle.substring("Create tag \"".length(), tagTitle.length() - 1);

                if (!viewModel.addCurrentTagTitle(tagTitle))
                    return;
                addSelectedTag(tagTitle);
            }
        });

        tagsAutoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String tagTitle = tagsAutoCompleteTextView.getText().toString();
                    tagsAutoCompleteTextView.setText("");

                    if (!tagTitle.equals("")) {
                        if (!viewModel.addCurrentTagTitle(tagTitle))
                            return false;
                        addSelectedTag(tagTitle);
                    }
                }
                return true;
            }
        });

        viewModel = ViewModelProviders.of(this).get(EditorViewModel.class);
        viewModel.getAllTags().observe(this, new Observer<List<Tag>>() {
            @Override
            public void onChanged(List<Tag> tags) {
                tagsAdapter.setTags(tags);
            }
        });

        Intent intent = getIntent();
        if (intent.getExtras() != null && intent.getExtras().getSerializable("note") != null) {
            Note note = (Note) intent.getExtras().getSerializable("note");
            viewModel.setCurrentId(note.id);
            titleEditText.setText(note.title);
            bodyEditText.setText(note.body);
            viewModel.setCurrentColor(note.color);
            parent.setBackgroundColor(note.color);
        }
        if (intent.getExtras() != null && intent.getExtras().getSerializable("tags") != null) {
            ArrayList<Tag> tags = (ArrayList<Tag>) intent.getExtras().getSerializable("tags");
            for (Tag tag : tags)
                viewModel.addCurrentTagTitle(tag.tagName);
        }

        for (String tagTitle : viewModel.currentTagTitles) {
            addSelectedTag(tagTitle);
        }

    }

    private void addSelectedTag(String tagTitle) {
        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip, null, false);
        chip.setText(tagTitle);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipGroup.removeView(view);
                viewModel.removeCurrentTagTitle(((Chip) view).getText().toString());
            }
        });

        chipGroup.addView(chip);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.palette:
                showColorPicker();
                break;
            case R.id.save:
                saveNote();
                break;
        }
        return true;
    }

    private void showColorPicker() {
        ColorPicker colorPicker = new ColorPicker(this);
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                parent.setBackgroundColor(color);
                viewModel.setCurrentColor(color);
            }

            @Override
            public void onCancel() { }
        })
        .setColors(R.array.colorPicker)
        .setRoundColorButton(true)
        .setDefaultColorButton(viewModel.getCurrentColor())
        .show();
    }

    private void saveNote() {
        String title = titleEditText.getText().toString();
        String body = bodyEditText.getText().toString();

        Intent intent = getIntent();
        if (intent.getExtras() == null) {
            viewModel.createNote(title, body);
        } else {
            Note note = new Note(title, body, viewModel.getCurrentColor());
            note.id = viewModel.getCurrentId();
            viewModel.updateNote(note);
        }

        finish();
    }
}