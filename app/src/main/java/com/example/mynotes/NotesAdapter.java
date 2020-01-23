package com.example.mynotes;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotes.Models.Note;
import com.example.mynotes.Models.Tag;
import com.example.mynotes.Models.TagNote;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();
    private List<TagNote> tagNotes = new ArrayList<>();

    private Context context;
    private OnListItemClickListener onListItemClickListener;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH);

    NotesAdapter(Context context, OnListItemClickListener onListItemClickListener) {

        this.onListItemClickListener = onListItemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.recyclerview_item, parent, false);
        return new NoteViewHolder(itemView, onListItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final NoteViewHolder holder, int position) {
        final Note note = notes.get(position);
        holder.noteTitleItemView.setText(note.title);
        holder.noteBodyItemView.setText(note.body);
        holder.noteDateItemView.setText(context.getString(R.string.date_modified, sdf.format(note.creationDate)));
        holder.cardView.setCardBackgroundColor(note.color);
        holder.noteOptions.setBackgroundColor(note.color);
        holder.tagsGroup.removeAllViews();

        for (Tag tag: getTagsForNote(note.id)) {
            Chip chip = new Chip(context);
            chip.setText(tag.tagName);
            chip.setCheckable(false);
            chip.setCloseIconVisible(false);
            chip.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.colorChip)));
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.onListItemClickListener.onTagClicked(((Chip) view).getText().toString());
                }
            });
            holder.tagsGroup.addView(chip);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView noteTitleItemView, noteBodyItemView, noteDateItemView;
        private final ImageButton noteOptions;
        private final ChipGroup tagsGroup;
        private final OnListItemClickListener onListItemClickListener;

        NoteViewHolder(@NonNull View itemView, final OnListItemClickListener onListItemClickListener) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            noteTitleItemView = itemView.findViewById(R.id.noteTitle);
            noteBodyItemView = itemView.findViewById(R.id.noteBody);
            tagsGroup = itemView.findViewById(R.id.tagGroupNote);
            noteDateItemView = itemView.findViewById(R.id.noteDate);
            noteOptions = itemView.findViewById(R.id.noteOptions);
            this.onListItemClickListener = onListItemClickListener;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Note note = notes.get(getAdapterPosition());
                    onListItemClickListener.onNoteClick(note, getTagsForNote(note.id));
                }
            });
            noteOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onListItemClickListener.onOptionsClick(notes.get(getAdapterPosition()), view);
                }
            });
        }
    }

    void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    void setTags(List<Tag> tags) {
        this.tags = tags;
        notifyDataSetChanged();
    }


    void setTagNotes(List<TagNote> tagNotes) {
        this.tagNotes = tagNotes;
        notifyDataSetChanged();
    }

    void sort(final SortingType sortingType) {
        switch (sortingType) {
            case DATE:
                Collections.sort(notes, new Comparator<Note>() {
                    @Override
                    public int compare(final Note o1,
                                       final Note o2) {
                        return o1.creationDate.compareTo(o2.creationDate);
                    }
                });
                Collections.reverse(notes);

                break;
            case TITLE:
                Collections.sort(notes, new Comparator<Note>() {
                    @Override
                    public int compare(final Note o1,
                                       final Note o2) {
                        return o1.title.compareTo(o2.title);
                    }
                });

                break;
        }

        notifyDataSetChanged();
    }

    private ArrayList<Tag> getTagsForNote(long noteId) {
        ArrayList<Tag> result = new ArrayList<>();

        for (TagNote tagNote : this.tagNotes) {
            if (tagNote.noteId == noteId) {
                for (Tag tag : this.tags) {
                    if (tag.id == tagNote.tagId)
                        result.add(tag);
                }
            }
        }
        return result;
    }

    public interface OnListItemClickListener {
        void onNoteClick(Note note, ArrayList<Tag> tags);
        void onOptionsClick(Note note, View view);
        void onTagClicked(String tagTitle);
    }
}
