package edu.northeastern.gymhub.Views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.gymhub.Models.ScheduleItem;
import edu.northeastern.gymhub.R;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private List<ScheduleItem> scheduleList;

    public ScheduleAdapter() {
        this.scheduleList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_recycleview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleItem scheduleItem = scheduleList.get(position);
        holder.itemNameTextView.setText(scheduleItem.getItemName());
        holder.itemTimeTextView.setText(scheduleItem.getItemTime());
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public void setScheduleList(List<ScheduleItem> scheduleList) {
        this.scheduleList = scheduleList;
        notifyDataSetChanged();
    }

    public void addScheduleItem(ScheduleItem scheduleItem) {
        scheduleList.add(scheduleItem);
        notifyItemInserted(scheduleList.size() - 1);
    }

    public void clearSchedule() {
        scheduleList.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView;
        TextView itemTimeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.item_name);
            itemTimeTextView = itemView.findViewById(R.id.item_time);
        }
    }
}
