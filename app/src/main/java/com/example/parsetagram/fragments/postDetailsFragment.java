package com.example.parsetagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.parsetagram.Post;
import com.example.parsetagram.PostAdapter;
import com.example.parsetagram.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 */
public class postDetailsFragment extends Fragment {

    // Initialize variables
    public Post myPost;
    ImageView ivProfile;
    ImageView ivComment;
    ImageView ivLike;
    ImageView ivSave;
    ImageView ivShare;
    ImageView ivPostPicture;
    TextView tvTimeStamp;
    TextView tvCaption;
    TextView tvUsername;
    TextView tvNumLikes;
    JSONArray likedby;
    String curUsername;
    int likeCount;
    Boolean liked = false;


    public postDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initialize variables
        ivProfile = view.findViewById(R.id.ivProfile);
        ivComment = view.findViewById(R.id.ivComment);
        ivLike = view.findViewById(R.id.ivLike);
        ivSave = view.findViewById(R.id.ivSave);
        ivShare = view.findViewById(R.id.ivShare);
        ivPostPicture = view.findViewById(R.id.ivPostPicture);
        tvTimeStamp = view.findViewById(R.id.tvTimeStamp);
        tvCaption = view.findViewById(R.id.tvCaption);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvNumLikes = view.findViewById(R.id.tvNumLikes);

        curUsername = ParseUser.getCurrentUser().getUsername(); // get the current user's username
        likedby = myPost.getLikedList(); // get the current list of who liked it.

        likeCount = 0;
        if(likedby != null){
            // set like count to cur size of like array
            likeCount = likedby.length();
            if(likedby.toString().contains(curUsername)){
              liked = true;
            }

        }

        if(liked){
            // then make color red
            ivLike.setImageResource(R.drawable.ufi_heart_active);
            ivLike.setColorFilter(getContext().getResources().getColor(R.color.Red));
        }

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                // This should switch the color of the like button if I select it.
                ivLike.setActivated(!ivLike.isActivated());
                if (ivLike.isActivated()){
                    ivLike.setImageResource(R.drawable.ufi_heart_active);
                    ivLike.setColorFilter(getContext().getResources().getColor(R.color.Red));
                } else {
                    ivLike.setImageResource(R.drawable.ufi_heart);
                    ivLike.setColorFilter(getContext().getResources().getColor(R.color.Black));
                }
                Toast.makeText(getContext(), "Selected", Toast.LENGTH_SHORT).show();
            }

                 */
                // case if post has never been liked yet
                if(likedby == null){
                    // Update liked list
                    likedby = new JSONArray();
                    likedby.put(ParseUser.getCurrentUser().getUsername());
                    // increment likecount by one
                    likeCount = likeCount + 1;
                    // update like variable
                    liked = true;
                    tvNumLikes.setText(String.valueOf(likeCount));
                    //update like color
                    ivLike.setImageResource(R.drawable.ufi_heart_active);
                    ivLike.setColorFilter(getContext().getResources().getColor(R.color.Red));


                    // update specific post liked list
                    myPost.setLikedList(likedby);
                    myPost.saveInBackground();
                    Log.i("DetailFragment", "updating post info. adding like");

                }
                // case where post has been liked by someone other than you
                else if((likedby != null) && (!liked))
                {

                    // Update liked list
                    likedby.put(ParseUser.getCurrentUser().getUsername());
                    likeCount = likeCount + 1 ;
                    liked = true;
                    tvNumLikes.setText(String.valueOf(likeCount));
                    //update like color
                    ivLike.setImageResource(R.drawable.ufi_heart_active);
                    ivLike.setColorFilter(getContext().getResources().getColor(R.color.Red));
                    // update specific post liked list
                    myPost.setLikedList(likedby);
                    myPost.saveInBackground();
                }

                else if(liked){
                    // This means we already liked so time to unlike
                    ivLike.setImageResource(R.drawable.ufi_heart);
                    ivLike.setColorFilter(getContext().getResources().getColor(R.color.Black));
                    //remove value from like array
                    Log.i("DetailsFragment", "String conversiton" + likedby.toString());
                    int idx = 0;
                    try {
                        idx = indexFinder(likedby);
                        likedby.remove(idx);
                        // reduce like count
                        likeCount = likeCount -1;
                        tvNumLikes.setText(String.valueOf(likeCount));


                        // update specific post of removal
                        myPost.setLikedList(likedby);
                        myPost.saveInBackground();
                        Log.i("DetailFragment", "updating post info. Removing like");

                        //set liked to false
                        liked = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This should switch the color of the save button if I select it.
                ivSave.setActivated(!ivSave.isActivated());
                if (ivSave.isActivated()){
                    ivSave.setImageResource(R.drawable.ufi_save_active);
                    ivSave.setColorFilter(getContext().getResources().getColor(R.color.Red));
                } else {
                    ivSave.setImageResource(R.drawable.ufi_save);
                    ivSave.setColorFilter(getContext().getResources().getColor(R.color.Black));
                }
            }
        });

        // set variables
        // bind data of post into the post_details_fragment xml view

        tvNumLikes.setText(String.valueOf(likeCount));
        tvUsername.setText(myPost.getUser().getUsername());
        tvCaption.setText(myPost.getDescription());
        tvTimeStamp.setText(PostAdapter.getRelativeTime(myPost.getCreatedAt()));
        ParseFile image = myPost.getImage();
        Glide.with(getContext()).load(myPost.getImage().getUrl()).into(ivPostPicture);
        Glide.with(getContext()).load(myPost.getUser().getParseFile("profileImage").getUrl()).placeholder(R.drawable.instagram_user_filled_24).into(ivProfile);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(liked){
            // then make color red
            ivLike.setImageResource(R.drawable.ufi_heart_active);
            ivLike.setColorFilter(getContext().getResources().getColor(R.color.Red));
        }
    }

    public int indexFinder(JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++){
            Log.i("detailFragment", "array got " + array.getString(i));
            if(curUsername.equals(array.getString(i))){
                // once I reach index of username return that index
                return  i;
            }
        }
        // if it is not in array return nothing
        return -1;
    }
}
