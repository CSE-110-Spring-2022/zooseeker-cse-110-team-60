package com.example.zooseeker;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ExhibitListAdapter extends RecyclerView.Adapter<ExhibitListAdapter.ViewHolder> {
    private List<ExhibitItem> exhibitItems = Collections.emptyList();
    private Consumer<ExhibitItem> onCheckBoxClicked;

    @SuppressLint("NotifyDataSetChanged")
    public void setExhibitListItems(List<ExhibitItem> newExhibitItems) {
        if (MainActivity.update) {
            this.exhibitItems.clear();
            this.exhibitItems = newExhibitItems;
            notifyDataSetChanged();
        }
    }

    public void setOnCheckBoxClickedHandler(Consumer<ExhibitItem> onCheckBoxClicked) {
        this.onCheckBoxClicked = onCheckBoxClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.exhibit_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setExhibitItem(exhibitItems.get(position));
    }


    @Override
    public int getItemCount() {
        return exhibitItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ExhibitItem exhibitItem;
        private final TextView textView;
        private final CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.exhibit_item_text);
            this.checkbox = itemView.findViewById(R.id.exhibit_item_checkBox);

            this.checkbox.setOnClickListener(view -> {
                if (onCheckBoxClicked == null) return;
                onCheckBoxClicked.accept(exhibitItem);
            });
        }

        public ExhibitItem getExhibitItem() {
            return exhibitItem;
        }

        public void setExhibitItem(ExhibitItem exhibitItem) {
            this.exhibitItem = exhibitItem;
            this.textView.setText(exhibitItem.name);
            this.checkbox.setChecked(exhibitItem.added);
        }
    }
}
