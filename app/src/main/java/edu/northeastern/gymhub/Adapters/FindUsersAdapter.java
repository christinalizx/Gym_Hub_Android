package edu.northeastern.gymhub.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.northeastern.gymhub.Models.GymUser;
import edu.northeastern.gymhub.R;

public class FindUsersAdapter extends RecyclerView.Adapter<FindUsersAdapter.MyViewHolder> {

    private ArrayList<GymUser> usersList;
    private RecyclerClickListener listener;

    public FindUsersAdapter(ArrayList<GymUser> usersList, RecyclerClickListener listener) {
        this.usersList = usersList;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private Button buttonFollow;
        private Button buttonUnfollow;

        public MyViewHolder(final View view) {
            super(view);
            userName = view.findViewById(R.id.textViewUserName);
            buttonFollow = view.findViewById(R.id.buttonFollow);
//            buttonUnfollow = view.findViewById(R.id.buttonUnfollow);

            // Set OnClickListener for buttonFollow
            buttonFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onFollowButtonClick(v, position);
                    }
                }
            });
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

        //holder.buttonUnfollow.setVisibility(View.GONE);

//        // Check if the current user is in the connections of the displayed user
//        if (curUser != null) {
//            String displayedUser = usersList.get(position).getUsername();
//            boolean isFollowing = curUser.getConnections().contains(displayedUser);
//
//            // Set the visibility of buttons based on the relationship between users
//            if (isFollowing) {
//                holder.buttonFollow.setVisibility(View.GONE);
//                holder.buttonUnfollow.setVisibility(View.VISIBLE);
//            } else {
//                holder.buttonFollow.setVisibility(View.VISIBLE);
//                holder.buttonUnfollow.setVisibility(View.GONE);
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public interface RecyclerClickListener {
        void onFollowButtonClick(View v, int position);
    }
}
