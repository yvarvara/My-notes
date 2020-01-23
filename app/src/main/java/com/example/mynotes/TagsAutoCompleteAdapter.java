package com.example.mynotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mynotes.Models.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TagsAutoCompleteAdapter extends ArrayAdapter<String> {
    private int resource;
    private List<Tag> tags;
    private Context context;

    public TagsAutoCompleteAdapter(@NonNull Context context, int resource) {
        super(context, resource);

        this.resource = resource;
        this.context = context;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return tagFilter;
    }

    private Filter tagFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<String> suggestions = new ArrayList<>();

            String filterPattern = constraint.toString().toLowerCase();

            if (constraint.length() > 0) {
                boolean exactFound = false;
                for (Tag tag : tags) {
                    if (tag.tagName.toLowerCase().startsWith(filterPattern)) {
                        suggestions.add(tag.tagName);

                        if (tag.tagName.toLowerCase().equals(filterPattern))
                            exactFound = true;
                    }
                }

                if (!exactFound)
                    suggestions.add(0, "Create tag \"" + constraint.toString() + '"');
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                clear();
                addAll((List) results.values);
                notifyDataSetChanged();
            }
        }
    };

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);

        TextView tagItemView = convertView.findViewById(R.id.dropdownItem);
        String tag = getItem(position);

        if (tag != null)
            tagItemView.setText(tag);

        return convertView;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
