package com.example.parsetagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.parsetagram.Post;
import com.example.parsetagram.PostAdapter;
import com.example.parsetagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class postFragment extends Fragment {

    public static final  String TAG = "PostFragment";

    RecyclerView rvPosts;
    PostAdapter adapter;
    List<Post> allPosts;
    SwipeRefreshLayout swipeContainer;
    ProgressBar progressBar;
    public postFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        progressBar = view.findViewById(R.id.pbLoading);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // Set refresh listener which triggers new data loading.
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data!");
                queryPost();
            }
        });


        // Always remeber to initialize list array so it's not null
        allPosts = new ArrayList<>();
        // create the adapter
        adapter = new PostAdapter(getContext(), allPosts, new PostAdapter.PostAdapterListener() {
            // note I could have also had this fragment class implement PostAdapter.PostAdapterListener instead and passed in this
            @Override
            public int getCurFragment() {
                // If i call getCurFragment and am in the post Fragment screen return 2

                return 2;
            }
        });
        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPost();
    }

    protected void queryPost() {
        // show progress bar
        progressBar.setVisibility(View.VISIBLE);
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        //this query.include gets all the User information from parse as well
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error with getting Post", e);
                }
                for (Post p : posts){
                    //for each post that we get
                    Log.i(TAG, "POST: " + p.getDescription() + ", username: " + p.getUser().getUsername());
                }
                adapter.clear();
                //allPosts.addAll(posts);
                adapter.addAll(posts);
                //adapter.notifyDataSetChanged();
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
                // remove progress bar
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
