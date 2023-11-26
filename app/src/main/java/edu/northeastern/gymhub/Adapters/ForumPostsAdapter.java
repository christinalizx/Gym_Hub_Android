package edu.northeastern.gymhub.Adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.northeastern.gymhub.Activities.PostContentActivity;
import edu.northeastern.gymhub.Models.Post;
import edu.northeastern.gymhub.R;

public class ForumPostsAdapter extends RecyclerView.Adapter<ForumPostsAdapter.PostViewHolder> {

    private List<Post> postList;


    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView titleTextView;
        public TextView contentTextView;
        public TextView likeTextView;
        public TextView commentTextView;
        public TextView viewTextView;
        public TextView timestampTextView;

        public PostViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.titleTextView);
            contentTextView = view.findViewById(R.id.contentTextView);
            likeTextView = view.findViewById(R.id.likeTextView);
            commentTextView = view.findViewById(R.id.commentTextView);
            viewTextView = view.findViewById(R.id.viewTextView);
            timestampTextView = view.findViewById(R.id.timestampTextView);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Start the article details activity
            Intent intent = new Intent(view.getContext(), PostContentActivity.class);
            intent.putExtra("post", postList.get(getAdapterPosition()));
            view.getContext().startActivity(intent);
        }
    }

    public ForumPostsAdapter(List<Post> dataSet) {
        postList = dataSet;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.forum_post_item, viewGroup, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder viewHolder, final int position) {
        Post post = postList.get(position);
        viewHolder.titleTextView.setText(post.getTitle());
        viewHolder.contentTextView.setText(post.getContent());
        viewHolder.likeTextView.setText(post.getLikes() + " likes");
        viewHolder.commentTextView.setText(post.getCommentsCount() + " comments");
        viewHolder.viewTextView.setText(post.getViews() + " views");
        viewHolder.timestampTextView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(new Date(post.getTimestamp())));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public List<Post> getAllPosts() {
        return postList;
    }

    public void addPost(Post post) {
        postList.add(0, post);
        Log.i("aa", "Add a new post: " + post.getPostID());
        notifyItemInserted(0);
    }
}
