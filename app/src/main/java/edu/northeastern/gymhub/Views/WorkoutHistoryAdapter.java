package edu.northeastern.gymhub.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import edu.northeastern.gymhub.Models.Workout;
import edu.northeastern.gymhub.R;

public class WorkoutHistoryAdapter extends RecyclerView.Adapter<WorkoutHistoryAdapter.ViewHolder> {

    private List<Workout> workoutList;
    private Context context;

    public WorkoutHistoryAdapter(List<Workout> workoutList, Context context) {
        this.workoutList = workoutList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_history_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workout workout = workoutList.get(position);

        // Bind data to the views in the card
        holder.textViewDate.setText("Date: " + workout.getDate());
        holder.textViewType.setText("Type: " + workout.getType());
        holder.textViewData.setText("Data: " + workout.getDataInfo());
        holder.textViewNotes.setText("Notes: " + workout.getNotes());
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewDate;
        TextView textViewType;
        TextView textViewData;
        TextView textViewNotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewCardDate);
            textViewType = itemView.findViewById(R.id.textViewCardType);
            textViewData = itemView.findViewById(R.id.textViewCardData);
            textViewNotes = itemView.findViewById(R.id.textViewCardNotes);
        }
    }
}

