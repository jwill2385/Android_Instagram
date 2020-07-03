package com.example.parsetagram;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.Date;

@ParseClassName("Post")
public class Post extends ParseObject {
    // For our Post class in parse these are key component names
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_NUM_LIKES = "likedBy";

    // create getter and setter for each attribute

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }
    public void setDescription(String description){
        //set your description into the Post objects description on parse.
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile){
        //set your post image into the Post objects image on parse.
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user){
        //set your user into the Post objects user on parse.
        put(KEY_USER, user);
    }

    public JSONArray getLikedList(){return getJSONArray(KEY_NUM_LIKES);}
    public void setLikedList(JSONArray likedList){
        // set your post list of who liked it into the post image on parse
        put(KEY_NUM_LIKES, likedList);
    }

}
