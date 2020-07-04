package com.example.parsetagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parsetagram.LoginActivity;
import com.example.parsetagram.MainActivity;
import com.example.parsetagram.Post;
import com.example.parsetagram.PostAdapter;
import com.example.parsetagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class profileFragment extends postFragment {

    RecyclerView rvMyPost;
    List<Post> allPosts;
    PostAdapter adapter;
    Button btnLogout;
    ProgressBar progressBar;
    ImageView profilePic;
    profileFragmentListener listener;

    public  interface profileFragmentListener{
        public void choosePhotoFromGallery(View view);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
       // return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        rvMyPost = view.findViewById(R.id.rvMyPost);
        btnLogout = view.findViewById(R.id.btnLogout);
        progressBar = view.findViewById(R.id.pbLoading);
        profilePic = view.findViewById(R.id.profilePic);

        // set listener
        listener = (profileFragmentListener) getContext();
        // Always remember to initialize list array so it's not null
        allPosts = new ArrayList<>();
        // create the adapter
        adapter = new PostAdapter(getContext(), allPosts, new PostAdapter.PostAdapterListener() {
            // note I could have also had this fragment class implement PostAdapter.PostAdapterListener instead and passed in this

            @Override
            public int getCurFragment() {
                // If i call getCurFragment and am in the profile screen return 1
                return 1;
            }
        });
        rvMyPost.setAdapter(adapter);
        rvMyPost.setLayoutManager(new GridLayoutManager(getContext(), 2, RecyclerView.HORIZONTAL, false ));
        queryPost();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // logout of account
                ParseUser.logOut();
                // go to login screen
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish(); // we don't want to go back to profile screen if user presses back arrow
            }
        });



        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to gallery and get picture
                //MainActivity.choose
                //((MainActivity)getContext()).choosePhotoFromGallery();
                listener.choosePhotoFromGallery(v);
                Log.i("ProfileFragment", "Selecting profile photo");
            }
        });

      //  ParseUser user = ParseUser.getCurrentUser();
        ParseFile profileImage = ParseUser.getCurrentUser().getParseFile("profileImage");
        if (profileImage != null){
            Glide.with(getContext()).load(profileImage.getUrl()).circleCrop().into(profilePic);
        }
    }

    @Override
    protected void queryPost() {
        progressBar.setVisibility(View.VISIBLE);
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        //this query.include gets all the User information from parse as well
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser()); // On Profile page only show my posts
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null){
                    Log.e("ProfileFragment", "Error with getting Post", e);
                }
                for (Post p : posts){
                    //for each post that we get
                    Log.i("ProfileFragment", "POST: " + p.getDescription() + ", username: " + p.getUser().getUsername());
                }
                allPosts.clear();
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
                // remove progress bar
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ParseFile profileImage = ParseUser.getCurrentUser().getParseFile("profileImage");
        if (profileImage != null){
            Glide.with(getContext()).load(profileImage.getUrl()).circleCrop().placeholder(R.drawable.instagram_user_filled_24).into(profilePic);
        }
    }
}
