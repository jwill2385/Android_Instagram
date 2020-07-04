package com.example.parsetagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseFile;

import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;
    private PostAdapterListener listener;
    private  PostAdapterTransferListener transferListener;



    // create a interface
    public  interface  PostAdapterListener {
        // it will be able access this function from other fragments
        public int getCurFragment();
    }

    public  interface PostAdapterTransferListener{
        // this will communicate with my main activity
        // Had to create an interface because I am sending data to main Activity from adapter
        public void sendPostToMainActivity(Post post);
    }


    // Create constructor for adapter
    public PostAdapter(Context context, List<Post> posts, PostAdapterListener listener){
        this.context = context;
        this.posts = posts;
        this.listener = listener;
        this.transferListener = null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate viewholder to display item posts.
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get a transfer listener from context -- which is the MainActivity
        transferListener = (PostAdapterTransferListener) context;
        // Get the data at position
        Post post = posts.get(position);
        // Bind the Post with the viewholder
        holder.bind(post);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // Create all variables
        ImageView ivProfile;
        ImageView ivComment;
        ImageView ivLike;
        ImageView ivSave;
        ImageView ivShare;
        ImageView ivPostPicture;
        TextView tvTimeStamp;
        TextView tvCaption;
        TextView tvUsername;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initialize variables
            ivProfile = itemView.findViewById(R.id.ivProfile);
            ivComment = itemView.findViewById(R.id.ivComment);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivSave = itemView.findViewById(R.id.ivSave);
            ivShare = itemView.findViewById(R.id.ivShare);
            ivPostPicture = itemView.findViewById(R.id.ivPostPicture);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvUsername = itemView.findViewById(R.id.tvUsername);

            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // This should switch the color of the like button if I select it.
                    ivLike.setActivated(!ivLike.isActivated());
                    if (ivLike.isActivated()){
                        ivLike.setImageResource(R.drawable.ufi_heart_active);
                        ivLike.setColorFilter(context.getResources().getColor(R.color.Red));
                    } else {
                        ivLike.setImageResource(R.drawable.ufi_heart);
                        ivLike.setColorFilter(context.getResources().getColor(R.color.Black));
                    }
                    Toast.makeText(context, "Selected", Toast.LENGTH_SHORT).show();
                }
            });

            ivSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // This should switch the color of the save button if I select it.
                    ivSave.setActivated(!ivSave.isActivated());
                    if (ivSave.isActivated()){
                        ivSave.setImageResource(R.drawable.ufi_save_active);
                        ivSave.setColorFilter(context.getResources().getColor(R.color.Red));
                    } else {
                        ivSave.setImageResource(R.drawable.ufi_save);
                        ivSave.setColorFilter(context.getResources().getColor(R.color.Black));
                    }
                }
            });

            // Always remember. You have to set the onClickListener for the itemView
            itemView.setOnClickListener(this); // we can put this because our viewHolder implements a onClickListener
        }

        public void bind(Post post) {
            // bind data of post into the item_post xml view

            ParseFile profileImage = post.getUser().getParseFile("profileImage");
            // bind profile photo to each post
            Glide.with(context).load(profileImage.getUrl()).circleCrop().into(ivProfile);

            tvUsername.setText(post.getUser().getUsername());
            tvCaption.setText(post.getDescription());
            tvTimeStamp.setText(getRelativeTime(post.getCreatedAt()));
            ParseFile image = post.getImage();
            if (image != null) {
                if (listener.getCurFragment() == 1){
                    // if we are on the profile fragment I want to see circle crop images
                    Glide.with(context).load(post.getImage().getUrl()).apply(new RequestOptions().circleCrop()).into(ivPostPicture);
                } else {
                    // otherwise we are on the postFragment so I want to see the full image
                    Glide.with(context).load(post.getImage().getUrl()).into(ivPostPicture);

                }
            }
        }

        @Override
        public void onClick(View v) {
            // go to post details fragment
            // get the position
            Log.d("PostAdapter", "Clicking");
            int position = getAdapterPosition();
            // check if the position is not empty
            if(position != RecyclerView.NO_POSITION){
                // get the post
                Post p = posts.get(position);
                Toast.makeText(v.getContext(), "Got the post for " + p.getUser(), Toast.LENGTH_SHORT).show();
                transferListener.sendPostToMainActivity(p);
            }
        }
    }

    // Converts date to a string
    public static String getRelativeTime(Date date) {
        //String Format = "yyyy-mm-dd'T'HH:mm:ss.sss'Z'";
        String relativeDate;
        long dateMillis = date.getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();

        return relativeDate;
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> postList) {
        posts.addAll(postList);
        notifyDataSetChanged();
    }
}
