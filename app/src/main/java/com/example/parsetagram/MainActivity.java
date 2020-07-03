package com.example.parsetagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.parsetagram.fragments.composeFragment;
import com.example.parsetagram.fragments.postDetailsFragment;
import com.example.parsetagram.fragments.postFragment;
import com.example.parsetagram.fragments.profileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PostAdapter.PostAdapterTransferListener {

    public static final String TAG = "MainActivity";


    BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment fragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assign variables

        bottomNavigationView = findViewById(R.id.bottomNavigation);




        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        // goto home fragment
                        fragment = new postFragment();
                        Toast.makeText(MainActivity.this, "HomeFragment", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_compose:
                        // goto compose fragment
                        fragment = new composeFragment();
                        Toast.makeText(MainActivity.this, "composeFragment", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_profile:
                        // goto profile fragment

                        fragment = new profileFragment();
                        Toast.makeText(MainActivity.this, "profileFragment", Toast.LENGTH_SHORT).show();
                        break;
                }
                // display selected fragment
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // set the default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }


    @Override
    public void sendPostToMainActivity(Post post) {
        Log.d(TAG, "Made it to here");
        // now my Post retrieved from the adapter is here in the mainActivity
        // I can pass this data to my PostDetailsFragment
        postDetailsFragment detailsFragment = new postDetailsFragment();
        // Set the post of the details fragment to be post passed in
        detailsFragment.myPost = post;
        // now I can change fragmentManager
        fragmentManager.beginTransaction().replace(R.id.flContainer, detailsFragment).commit();

    }
}
