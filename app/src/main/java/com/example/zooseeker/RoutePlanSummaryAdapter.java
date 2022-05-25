/**
 * Adapter for using RecyclerView with Directions
 */

package com.example.zooseeker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class RoutePlanSummaryAdapter extends RecyclerView.Adapter<RoutePlanSummaryAdapter.ViewHolder> {
    private List<String> directionItems = Collections.emptyList();

    public void setDirectionItems(List<String> newDirections) {
        this.directionItems.clear();
        this.directionItems = newDirections;
        notifyDataSetChanged();
    }

    public List<String> getDirectionItems(){ return this.directionItems; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.route_plan_summary_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setDirection(directionItems.get(position));
    }

    @Override
    public int getItemCount() {
        return directionItems.size();
    }

    // ViewHolder for serving directions to RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private DirectionTracker direction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.summary_item_text);
        }

        public void setDirection(String direction) {
            this.textView.setText(direction);
        }

    }
}
