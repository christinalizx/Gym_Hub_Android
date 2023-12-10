package edu.northeastern.gymhub.Adapters;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import edu.northeastern.gymhub.R;
import edu.northeastern.gymhub.Utils.AndroidUtil;
import edu.northeastern.gymhub.Views.FindUsersAdapter;

public class HorizontalRVAdapter extends RecyclerView.Adapter<HorizontalRVAdapter.MyHolder> {
    ArrayList<String> names;
    ArrayList<String> usernames;
    Context context;
    private RecyclerClickListener listener;

    public HorizontalRVAdapter(Context context, RecyclerClickListener listener, ArrayList<String> usernames, ArrayList<String> names) {
        this.context = context;
        this.names = names;
        this.usernames = usernames;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_rv_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.tvTitle.setText(names.get(position));

        FirebaseStorage.getInstance().getReference().child("profile_pics")
                .child(usernames.get(position)).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public interface RecyclerClickListener {
        void onUserClicked(View v, int position);
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView profilePic;
        RelativeLayout clickableRelativeLayout;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            profilePic = itemView.findViewById(R.id.horizontalRvPic);
            clickableRelativeLayout = itemView.findViewById(R.id.clickableRelativeLayout);

            // Set ability to see user information
            clickableRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onUserClicked(v, position);
                    }
                }
            });
        }
    }
}
