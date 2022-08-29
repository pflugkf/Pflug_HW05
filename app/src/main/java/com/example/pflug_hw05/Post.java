/**
 * Assignment #: HW05
 * File Name: Pflug_HW05 Post.java
 * Full Name: Kristin Pflug
 */

package com.example.pflug_hw05;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Post {
    int postID;
    String postText;
    String postAuthor;
    int postAuthorID;
    String postCreationDateTime;

    public Post() {
        this.postID = 0;
        this.postText = "Post Title";
        this.postAuthor = "Username";
        this.postAuthorID = 1;
        this.postCreationDateTime = "5/24/1996 12:30 PM";
    }

    public Post(int postID, String postText, String postAuthor, int postAuthorID, String postCreationDateTime) {
        this.postID = postID;
        this.postText = postText;
        this.postAuthor = postAuthor;
        this.postAuthorID = postAuthorID;
        this.postCreationDateTime = postCreationDateTime;
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostAuthor() {
        return postAuthor;
    }

    public void setPostAuthor(String postAuthor) {
        this.postAuthor = postAuthor;
    }

    public int getPostAuthorID() {
        return postAuthorID;
    }

    public void setPostAuthorID(int postAuthorID) {
        this.postAuthorID = postAuthorID;
    }

    public String getPostCreationDateTime() {
        return postCreationDateTime;
    }

    public void setPostCreationDateTime(String postCreationDateTime) {
        this.postCreationDateTime = postCreationDateTime;
    }

    public String formatDateString(String dateString){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date convertedDate = new Date();
        try {
            convertedDate = df.parse(dateString);
            SimpleDateFormat newFormat = new SimpleDateFormat("MM/dd/yyyy 'at' h:mm a");
            String newConvertedDateString = newFormat.format(convertedDate);

            return newConvertedDateString;
        } catch (ParseException parseException) {
            parseException.printStackTrace();

            return dateString;
        }
    }
}
