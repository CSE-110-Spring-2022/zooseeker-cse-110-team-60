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
    private List<Node> exhibits = Collections.emptyList();
    private Consumer<Node> onCheckBoxClicked;

    @SuppressLint("NotifyDataSetChanged")
    public void setExhibitList(List<Node> newExhibitList) {
        if (MainActivity.update) {
            this.exhibits.clear();
            this.exhibits = newExhibitList;
            notifyDataSetChanged();
        }
    }

    public void setOnCheckBoxClickedHandler(Consumer<Node> onCheckBoxClicked) {
        this.onCheckBoxClicked = onCheckBoxClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.node, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setExhibit(exhibits.get(position));
//        holder.setIsRecyclable(false);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return exhibits.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Node exhibit;
        private final TextView textView;
        private final CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.node_name);
            this.checkbox = itemView.findViewById(R.id.node_checkBox);

            this.checkbox.setOnClickListener(view -> {
                if (onCheckBoxClicked == null) return;
                onCheckBoxClicked.accept(exhibit);
            });
        }

        public Node getExhibit() {
            return exhibit;
        }

        public void setExhibit(Node exhibit) {
            this.exhibit = exhibit;
            this.textView.setText(exhibit.name);
            this.checkbox.setChecked(exhibit.added);
        }
    }
}
