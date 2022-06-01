package com.example.zooseeker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class DirectionAdapter extends RecyclerView.Adapter<DirectionAdapter.ViewHolder> {
    private List<String> directions = Collections.emptyList();

    /**
     * Name:     setDirections
     * Behavior: Given a list of string objects, set the recyclerVIew to reflect that list.
     * - @param  List<String>                       directions
     */
    public void setDirections(List<String> directions) {
        this.directions.clear();
        this.directions = directions;
        notifyDataSetChanged();
    }

    public List<String> getDirections() { return directions; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.direction_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setDirection(directions.get(position));
    }

    @Override
    public int getItemCount() { return directions.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.direction_item_text);
        }

        public void setDirection(String direction) {
            this.textView.setText(direction);
        }
    }
}
