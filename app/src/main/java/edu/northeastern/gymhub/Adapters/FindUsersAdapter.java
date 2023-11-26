package edu.northeastern.gymhub.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.northeastern.gymhub.Models.GymUser;
import edu.northeastern.gymhub.R;

public class FindUsersAdapter extends RecyclerView.Adapter<FindUsersAdapter.MyViewHolder> {

    private ArrayList<GymUser> usersList;

    public FindUsersAdapter(ArrayList<GymUser> usersList){
        this.usersList = usersList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView userName;

        public MyViewHolder(final View view){
            super(view);
            userName = view.findViewById(R.id.textViewUserName);

        }
    }

    @NonNull
    @Override
    public FindUsersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_user_item, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull FindUsersAdapter.MyViewHolder holder, int position) {
        String name = usersList.get(position).getName();
        holder.userName.setText(name);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}
