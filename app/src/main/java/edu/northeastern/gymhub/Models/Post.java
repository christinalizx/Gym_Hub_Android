package edu.northeastern.gymhub.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Post implements Parcelable {
    private String postID;
    private String authorID;
    private String title;
    private String content;
    private long timestamp;
    private int likes;
    private int commentsCount;
    private List<String> commentsContent = new ArrayList<>();
    private int views;

    public Post() {
    }

    public Post(String postId, String authorId, String title, String content, long timestamp, int likes, int commentsCount, List<String> commentsContent, int views) {
        this.postID = postId;
        this.authorID = authorId;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.likes = likes;
        this.commentsCount = commentsCount;
        this.commentsContent = commentsContent;
        this.views = views;
    }

    protected Post(Parcel source) {
        this.postID = source.readString();
        this.authorID = source.readString();
        this.title = source.readString();
        this.content = source.readString();
        this.timestamp = source.readLong();
        this.likes = source.readInt();
        this.commentsCount = source.readInt();
        source.readStringList(this.commentsContent);
        this.views = source.readInt();
    }

    // Getter methods
    public String getPostID() {
        return postID;
    }

    public String getAuthorID() {
        return authorID;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getLikes() {
        return likes;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public List<String> getCommentsContent() {
        return commentsContent;
    }

    public int getViews() {
        return views;
    }

    // Setter methods
    public void setPostID(String postID) {
        this.postID = postID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public void addComment(String comment) {
        commentsContent.add(comment);
    }

    public void setViews(int views) {
        this.views = views;
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }
        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeString(postID);
        parcel.writeString(authorID);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeLong(timestamp);
        parcel.writeInt(likes);
        parcel.writeInt(commentsCount);
        parcel.writeStringList(commentsContent);
        parcel.writeInt(views);
    }
}
