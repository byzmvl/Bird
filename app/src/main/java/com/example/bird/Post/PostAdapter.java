package com.example.bird.Post;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bird.Profile.ProfileActivty;
import com.example.bird.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Holder> {

    ArrayList<PostData> postList;
    LayoutInflater inflater;
    Context context;

    public PostAdapter(Context ctx, ArrayList<PostData> postD) {
        this.context = ctx;
        this.inflater = LayoutInflater.from(context);
        this.postList = postD;
    }

    @NonNull
    @Override
    public PostAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_post, parent, false);
        Holder mHolder = new Holder(v);
        return mHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.Holder holder, int position) {
        PostData selectedPost = postList.get(position);
        holder.setData(selectedPost,position);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        StorageReference mStorageRef;
        StorageReference pathRef;
        TextView nameTextView;
        TextView lastNameTextView;
        TextView TextContent;
        TextView StatusText;
        ImageView profilePic;
        String PhotoUrl;
        ProgressBar pb;
        public Holder(@NonNull View itemView) {
            super(itemView);
            pb=itemView.findViewById(R.id.pbItem);
            pb.setVisibility(View.VISIBLE);
            mStorageRef = FirebaseStorage.getInstance().getReference();
            nameTextView = itemView.findViewById(R.id.txtName2);
            lastNameTextView = itemView.findViewById(R.id.txtLastName2);
            TextContent = itemView.findViewById(R.id.txtTextArea);
            StatusText = itemView.findViewById(R.id.txtStatus2);
            profilePic=itemView.findViewById(R.id.imgProfilePic);
        }

        public void setData(PostData post, int position) {

            this.PhotoUrl = post.getUser().getImageUrl();
            readStorage();
            this.nameTextView.setText(post.getUser().getName());
            this.lastNameTextView.setText(post.getUser().getLastname());
            this.TextContent.setText(post.getPosttext());
            this.StatusText.setText("statu  -  "+post.getCreatingDate());

        }

        public void readStorage(){
            pathRef = mStorageRef.child("images/" + PhotoUrl);
            mStorageRef.child("images/" + PhotoUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.i("Tag(pp)", uri.toString());
                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT > 8) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        try {
                            URL url = new URL(uri.toString());
                            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            System.out.println("byte count:" + image.getByteCount());
                            Bitmap b =  Bitmap.createScaledBitmap(image,250,250,false);
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(itemView.getResources(), b);
                            circularBitmapDrawable.setCircular(true);
                            profilePic.setImageDrawable(circularBitmapDrawable);
                            pb.setVisibility(View.INVISIBLE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.i("Storage","HATA");
                }
            });
        }
        @Override
        public void onClick(View v) {

        }
    }
}


