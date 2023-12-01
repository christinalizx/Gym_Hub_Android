package edu.northeastern.gymhub.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.gymhub.Models.GymUser;
import edu.northeastern.gymhub.R;

public class FindUsersAdapter extends RecyclerView.Adapter<FindUsersAdapter.MyViewHolder> {

    private ArrayList<GymUser> usersList;
    private RecyclerClickListener listener;
    private List<String> connections;
    private Context context;

    public FindUsersAdapter(Context context, ArrayList<GymUser> usersList, RecyclerClickListener listener, List<String> connections) {
        this.context = context;
        this.usersList = usersList;
        this.listener = listener;
        this.connections = connections;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private Button buttonFollow;
        private Button buttonUnfollow;

        public MyViewHolder(final View view) {
            super(view);
            userName = view.findViewById(R.id.textViewUserName);
            buttonFollow = view.findViewById(R.id.buttonFollow);
            buttonUnfollow = view.findViewById(R.id.buttonUnfollow);

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

            // Set OnClickListener for buttonUnfollow
            buttonUnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onUnfollowButtonClick(v, position);
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

        // Initially, make both buttons invisible and enabled
        holder.buttonFollow.setVisibility(View.GONE);
        holder.buttonFollow.setEnabled(true);

        holder.buttonUnfollow.setVisibility(View.GONE);
        holder.buttonUnfollow.setEnabled(true);

        // Check if the current user is in the connections of the displayed user
        String displayedUser = usersList.get(position).getUsername();
        boolean isFollowing = connections.contains(displayedUser);

        // Set the visibility of buttons based on the relationship between users
        if (isFollowing) {
            holder.buttonUnfollow.setVisibility(View.VISIBLE);
            holder.buttonUnfollow.setEnabled(true);

            holder.buttonFollow.setVisibility(View.GONE);
            holder.buttonFollow.setEnabled(false); // Disable the follow button
        } else {
            holder.buttonFollow.setVisibility(View.VISIBLE);
            holder.buttonFollow.setEnabled(true);

            holder.buttonUnfollow.setVisibility(View.GONE);
            holder.buttonUnfollow.setEnabled(false); // Disable the unfollow button
        }
    }


    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public interface RecyclerClickListener {
        void onFollowButtonClick(View v, int position);
        void onUnfollowButtonClick(View v, int position);
    }
}
