package com.example.parsetagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(Post.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("jwill2385-redo-insta") // should correspond to APP_ID env variable
                .clientKey("CodePathParseSuperKey")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://jwill2385-redo-insta.herokuapp.com/parse/").build());
    }
}
