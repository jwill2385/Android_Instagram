package com.example.parsetagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PostAdapter.PostAdapterTransferListener, profileFragment.profileFragmentListener {

    public static final String TAG = "MainActivity";
    public static final int PHOTO_CODE = 230;


    BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment fragment;



    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // give user a profile image if they don't already have one
        final ParseFile avatarFile = ParseUser.getCurrentUser().getParseFile("profileImage");
        if (avatarFile == null) {
            // This means account doesn't have an avatar. Lets give them a default
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.instagram_user_filled_24);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            // Create the ParseFile
            ParseFile file = new ParseFile("instagram_user_filled_24.png", image);
            // Upload the image into Parse Cloud
            ParseUser user = ParseUser.getCurrentUser();
            user.put("profileImage", file);

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                Log.i(TAG, "Added default profile image for " + ParseUser.getCurrentUser().getUsername());
                }
            });

        }

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

    // Accessing camera Roll
    @Override
    public void choosePhotoFromGallery(View view){
        // create an intent for picking a photo from gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PHOTO_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data != null) && requestCode == PHOTO_CODE) {
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri
            Bitmap selectedImage = null;
            try {
                // check build version
                if(Build.VERSION.SDK_INT < 28){
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                } else{
                    // newer API's can't use  getBitmap
                  ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), photoUri);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // now compress image and send file to parse

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            // Create the ParseFile
            ParseFile file = new ParseFile("gallery_image", image);
            // Upload the image into Parse Cloud
            ParseUser user = ParseUser.getCurrentUser();
            user.put("profileImage", file);

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.i(TAG, "Added custom profile image for " + ParseUser.getCurrentUser().getUsername());
                }
            });
        }
    }
}
