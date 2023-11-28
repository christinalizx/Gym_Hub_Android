package edu.northeastern.gymhub.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import edu.northeastern.gymhub.Models.Post;
import edu.northeastern.gymhub.R;

public class EditArticleActivity extends AppCompatActivity {
    private Toolbar edit_article_toolbar;
    // Text edit boxes
    private EditText edit_article_title;
    private EditText edit_article_content;
    // Article type
    private TextView article_type;
    // Select article type
    private LinearLayout select_type;
    // Publish button
    private Button publish;
    // Post title
    private String title;
    // Post content
    private String content;
    // Type
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);
        initView();
        editArticle();
    }

    private void editArticle() {
        // Click event for submit button
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get title
                title = edit_article_title.getText().toString();
                // Get content
                content = edit_article_content.getText().toString();
                // Get article type
                type = article_type.getText().toString();
                // Get current time
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                String nowTime = simpleDateFormat.format(date);
                // Get author's id
                SharedPreferences sharedPreferences = getSharedPreferences("user", Activity.MODE_PRIVATE);
                Integer authorId = sharedPreferences.getInt("id", 1);

                if (title != null && content != null && type != null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(EditArticleActivity.this)
                            .setIcon(R.drawable.publish)
                            .setTitle("Publish Post")
                            .setMessage("Confirm to publish the current post?")
                            .setPositiveButton("Publish", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /* Define progress dialog */
                                    ProgressDialog progressDialog = new ProgressDialog(EditArticleActivity.this);
                                    progressDialog.setIcon(R.drawable.publish);
                                    progressDialog.setTitle("Publish Post");
                                    progressDialog.setMessage("Publishing post...");
                                    final int[] count = {0};
                                    Thread myThread = new Thread() {
                                        @Override
                                        public void run() {
                                            try {
                                                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                                DatabaseReference postsRef = database.child("posts");
                                                String postId = postsRef.push().getKey();
                                                Post toPublish = new Post(postId, String.valueOf(authorId), title, content, System.currentTimeMillis(), 0, 0, new ArrayList<>(), 0);
                                                postsRef.child(postId).setValue(toPublish);
                                            } catch (Exception e) {
                                                String error;
                                                error = e.toString();
                                                System.out.println(error);
                                            }
                                            while (count[0] <= 10) {
                                                progressDialog.setProgress(count[0]++);
                                                try {
                                                    Thread.sleep(100);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            /* Close the dialog after the process is finished */
                                            if (count[0] >= 100) {
                                                progressDialog.cancel();
                                            }
                                            finish();
                                        }
                                    };
                                    myThread.start();
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(EditArticleActivity.this, "Cancelled publishing, re-edit the article", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setCancelable(false);
                    dialog.create().show();
                }
            }
        });

        // Click event for selecting article category
        select_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] items = new String[]{"Event", "Make Friends", "Mood"};
                final int[] sign = {-1};
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditArticleActivity.this)
                        .setIcon(R.drawable.dialogicon)
                        .setTitle("Select Article Category")
                        .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                article_type.setText(items[which]);
                                sign[0] = which;
                            }
                        })
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false);
                dialog.create().show();
            }
        });
    }

    private void initView() {
        // Initialize toolbar
        edit_article_toolbar = findViewById(R.id.edit_article_toolbar);
        // Initialize text edit boxes
        edit_article_title = findViewById(R.id.edit_article_title);
        edit_article_content = findViewById(R.id.edit_article_content);
        // Initialize type display component
        article_type = findViewById(R.id.article_type);
        // Initialize submit button
        publish = findViewById(R.id.publish);
        // Initialize
        select_type = findViewById(R.id.select_type);

        // Clicking the back button returns to the previous Activity
        edit_article_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Override dispatchTouchEvent
     * Close the keyboard when clicking outside of the keyboard area
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // Get the currently focused View
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                // Close the keyboard based on the judgment
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

/**
 * Check if the area clicked by the user is an input box
 * @param v
 * @param event
 * @return
 */
private boolean isShouldHideInput(View v, MotionEvent event) {
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
}