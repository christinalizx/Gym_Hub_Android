package edu.northeastern.gymhub.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu.northeastern.gymhub.Models.Post;
import edu.northeastern.gymhub.R;

public class PostContentActivity extends AppCompatActivity {
    private DatabaseReference postsRef;
    private Post post;
    private Toolbar toolbar;
    private TextView publishTime;
    private LinearLayout llContent;
    private TextView title;
    private TextView content;
    private TextView commentsCount;
    private TextView likesCount;
    private ListView commentsList;
    private LinearLayout llComment;
    private EditText newCommentContent;
    private TextView sendComment;
    private LinearLayout llBottom;
    private LinearLayout llBottomComment;
    private LinearLayout llBottomLike;
    private ImageView ivLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_content);
        postsRef = FirebaseDatabase.getInstance().getReference().child("posts");
        post = getIntent().getParcelableExtra("post");

        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.content_toolbar);
        publishTime = findViewById(R.id.content_release_time);
        llContent = findViewById(R.id.ll_content);
        title = findViewById(R.id.content_article_title);
        content = findViewById(R.id.content_article_content);
        commentsCount = findViewById(R.id.content_comment_sum);
        likesCount = findViewById(R.id.star_sum);
        commentsList = findViewById(R.id.content_comment_list);
        llComment = findViewById(R.id.ll_comment);
        newCommentContent = findViewById(R.id.comment_content);
        sendComment = findViewById(R.id.send_comment);
        llBottom = findViewById(R.id.ll_bottom);
        llBottomComment = findViewById(R.id.comment);
        llBottomLike = findViewById(R.id.ll_star);
        ivLike = findViewById(R.id.iv_star);

        // Set up values for text views
        publishTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date(post.getTimestamp())));
        title.setText(post.getTitle());
        content.setText(post.getContent());
        commentsCount.setText(String.valueOf(post.getCommentsCount()));
        likesCount.setText(String.valueOf(post.getLikes()));
        commentsList.setAdapter(new ListViewAdapter(new ArrayList<>(post.getCommentsContent())));

        // Set up back event
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Hide the edit comment area if post content is clicked
        llContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llBottom.setVisibility(View.VISIBLE);
                llComment.setVisibility(View.GONE);
            }
        });
        // Show the comment area if "comment button" is clicked
        llBottomComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llBottom.setVisibility(View.GONE);
                llComment.setVisibility(View.VISIBLE);
            }
        });

        // Set up publish comment event
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentContent = newCommentContent.getText().toString();
                int currentCount = post.getCommentsCount();
                post.setCommentsCount(currentCount + 1);
                post.addComment(commentContent);
                commentsCount.setText(String.valueOf(currentCount + 1));
                ListViewAdapter listViewAdapter = (ListViewAdapter) commentsList.getAdapter();
                listViewAdapter.addComment(commentContent);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            DatabaseReference postRef = postsRef.child(post.getPostID());
                            postRef.child("commentsCount").setValue(post.getCommentsCount());
                            postRef.child("commentsContent").setValue(post.getCommentsContent());
                        }catch (Exception e){
                            String error;
                            error = e.toString();
                            System.out.println(error);
                        }
                    }
                }.start();
                Toast.makeText(PostContentActivity.this,"Successfully commented", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up like click event
        llBottomLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivLike.setSelected(true);
                int currentLikesCount = post.getLikes();
                post.setLikes(currentLikesCount + 1);
                likesCount.setText(String.valueOf(currentLikesCount + 1));
                new Thread(() -> {
                    try {
                        DatabaseReference postRef = postsRef.child(post.getPostID());
                        postRef.child("likes").setValue(post.getLikes());
                    } catch (Exception e) {
                        String error;
                        error = e.toString();
                        System.out.println(error);
                    }
                }).start();
                Toast.makeText(PostContentActivity.this, "Successfully liked!", Toast.LENGTH_SHORT).show();
            }
        });

        // Add one view count
        int currentViews = post.getViews();
        post.setViews(currentViews + 1);
        new Thread(() -> {
            try {
                DatabaseReference postRef = postsRef.child(post.getPostID());
                postRef.child("views").setValue(post.getViews());
            } catch (Exception e) {
                String error;
                error = e.toString();
                System.out.println(error);
            }
        }).start();
    }

    // close keyboard if clicking outside
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (shouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean shouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    /**
     * ListView adapter
     */
    class ListViewAdapter extends BaseAdapter {
        private ArrayList<String> commentContentList;

        public ListViewAdapter(ArrayList<String> content) {
            this.commentContentList = content;
        }

        @Override
        public int getCount() {
            return commentContentList.size();
        }

        @Override
        public Object getItem(int position) {
            return commentContentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(PostContentActivity.this, R.layout.list_items_comment, null);
            TextView commentContent = view.findViewById(R.id.commentContent);
            commentContent.setText(commentContentList.get(position));
            return view;
        }

        public void addComment(String comment) {
            commentContentList.add(comment);
            notifyDataSetChanged();
        }
    }
}
