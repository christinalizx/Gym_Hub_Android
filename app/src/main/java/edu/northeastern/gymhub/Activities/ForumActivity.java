package edu.northeastern.gymhub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.gymhub.Adapters.ForumPostsAdapter;
import edu.northeastern.gymhub.Models.Post;
import edu.northeastern.gymhub.R;

public class ForumActivity extends AppCompatActivity {
    private DatabaseReference postsRef;
    private RecyclerView forumPostsRecyclerView;
    private ForumPostsAdapter forumPostsAdapter;
    private List<Post> postList;
    private ImageView addArticleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        if (savedInstanceState != null) {
            postList = savedInstanceState.getParcelableArrayList("postList");
        } else {
            postList = new ArrayList<>();
        }
        initRecyclerView();
        addArticleButton = findViewById(R.id.toWriteArticle);
        addArticleButton.setOnClickListener(view -> {
            Intent intent = new Intent(ForumActivity.this, EditArticleActivity.class);
            startActivity(intent);
        });

        // Listen to DB for new posts
        postsRef = FirebaseDatabase.getInstance().getReference().child("posts");
        postsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Post newPost = dataSnapshot.getValue(Post.class);
                forumPostsAdapter.addPost(newPost);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Post changedPost = dataSnapshot.getValue(Post.class);
                int position = 0;
                for (Post post : forumPostsAdapter.getAllPosts()) {
                    if (post.getPostID().equals(changedPost.getPostID())) {
                        post.setLikes(changedPost.getLikes());
                        post.setCommentsCount(changedPost.getCommentsCount());
                        post.setViews(changedPost.getViews());
                        forumPostsAdapter.notifyItemChanged(position);
                        break;
                    }
                    position++;
                }

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("postList", (ArrayList<Post>) postList);
        Log.i("aa", "savebundle");
    }

    private void initRecyclerView() {
        if (forumPostsRecyclerView == null) {
            forumPostsRecyclerView = findViewById(R.id.forumPostsRecyclerView);
            forumPostsAdapter = new ForumPostsAdapter(postList);
            forumPostsRecyclerView.setHasFixedSize(true);
            forumPostsRecyclerView.setLayoutManager(new LinearLayoutManager(ForumActivity.this));
            forumPostsRecyclerView.setAdapter(forumPostsAdapter);
            Log.i("aa", "create forumPostsRecyclerView");
        }
    }
}