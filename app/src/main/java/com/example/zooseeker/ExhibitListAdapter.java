package com.example.zooseeker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class ExhibitListAdapter extends RecyclerView.Adapter<ExhibitListAdapter.ViewHolder> {
    private List<ExhibitItem> exhibitItems = Collections.emptyList();

    public void setExhibitListItems(List<ExhibitItem> newExhibitItems) {
        this.exhibitItems.clear();
        this.exhibitItems = newExhibitItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exhibit_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setExhibitItem(exhibitItems.get(position));
    }

    @Override
    public int getItemCount() { return exhibitItems.size(); }

    @Override
    public long getItemId(int position) { return exhibitItems.get(position).id; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final CheckBox checkBox;
        private ExhibitItem exhibitItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
             this.textView = itemView.findViewById(R.id.exhibit_item_text);
             this.checkBox = itemView.findViewById(R.id.exhibit_item_checkBox);

        }

        public ExhibitItem getExhibitItem() {return exhibitItem;}

        public void setExhibitItem(ExhibitItem exhibitItem) {
            this.exhibitItem = exhibitItem;
            this.textView.setText(exhibitItem.name);
        }
    }
}
