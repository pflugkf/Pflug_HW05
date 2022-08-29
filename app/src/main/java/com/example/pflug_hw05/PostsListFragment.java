/**
 * Assignment #: HW05
 * File Name: Pflug_HW05 PostsListFragment.java
 * Full Name: Kristin Pflug
 */

package com.example.pflug_hw05;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsListFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();

    private static final String ARG_USER_TOKEN = "ARG_USER_TOKEN";

    private String token;

    public PostsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userToken Parameter1.
     * @return A new instance of fragment PostsListFragment.
     */
    public static PostsListFragment newInstance(String userToken) {
        PostsListFragment fragment = new PostsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_TOKEN, userToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            token = getArguments().getString(ARG_USER_TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts_list, container, false);
    }

    RecyclerView postsRecyclerView;
    RecyclerView pagesRecyclerView;
    LinearLayoutManager postsLayoutManager;
    LinearLayoutManager pagesLayoutManager;
    PostsRecyclerViewAdapter postsAdapter;
    PagesRecyclerViewAdapter pagesAdapter;

    ArrayList<Post> postsList;

    ArrayList<Integer> pages;
    int currentPage;
    int totalPageCount;

    TextView welcomeText;
    TextView pageCounter;
    Button createPostButton;
    Button logoutButton;

    String name;

    PostsListFragmentListener mListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Posts");

        postsList = new ArrayList<>();
        currentPage = 1;
        getPosts(currentPage);
        name = mListener.getUserName();

        postsRecyclerView = view.findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setHasFixedSize(true);
        postsLayoutManager = new LinearLayoutManager(getActivity());
        postsRecyclerView.setLayoutManager(postsLayoutManager);
        postsAdapter = new PostsRecyclerViewAdapter(postsList);
        postsRecyclerView.setAdapter(postsAdapter);

        pages = new ArrayList<>();

        pagesRecyclerView = view.findViewById(R.id.pagesRecyclerView);
        pagesRecyclerView.setHasFixedSize(true);
        pagesLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        pagesRecyclerView.setLayoutManager(pagesLayoutManager);
        pagesAdapter = new PagesRecyclerViewAdapter(pages);
        pagesRecyclerView.setAdapter(pagesAdapter);

        welcomeText = view.findViewById(R.id.posts_welcomeText);
        pageCounter = view.findViewById(R.id.posts_pageCounter);
        createPostButton = view.findViewById(R.id.posts_createButton);
        logoutButton = view.findViewById((R.id.posts_logoutButton));

        welcomeText.setText("Welcome, " + mListener.getUserName());

        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToCreatePost(token);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.logout();
            }
        });
    }

    void getPosts(int pageNumber){
        HttpUrl postsURL = HttpUrl.parse("https://www.theappsdr.com/posts").newBuilder()
                .addQueryParameter("page", String.valueOf(pageNumber))
                .build();

        Request postsRequest = new Request.Builder()
                .url(postsURL)
                .addHeader("Authorization", "BEARER " + token)
                .build();

        client.newCall(postsRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try {
                        postsList.clear();

                        JSONObject postsResponseObject = new JSONObject(response.body().string());
                        JSONArray listOfPosts = postsResponseObject.getJSONArray("posts");

                        for(int i = 0; i < listOfPosts.length(); i++){
                            JSONObject postJSONObject = listOfPosts.getJSONObject(i);
                            Post post = new Post();
                            post.setPostID(postJSONObject.getInt("post_id"));
                            post.setPostAuthor(postJSONObject.getString("created_by_name"));
                            post.setPostAuthorID(postJSONObject.getInt("created_by_uid"));
                            post.setPostText(postJSONObject.getString("post_text"));
                            post.setPostCreationDateTime(postJSONObject.getString("created_at"));

                            postsList.add(post);
                        }

                        int totalPostCount = postsResponseObject.getInt("totalCount");
                        int pageLength = postsResponseObject.getInt("pageSize");
                        totalPageCount = totalPostCount/pageLength;
                        for(int i = 1; i <= totalPageCount; i++){
                            pages.add(i);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                postsAdapter.notifyDataSetChanged();
                                pagesAdapter.notifyDataSetChanged();
                                pageCounter.setText("Showing " + currentPage + " of " + totalPageCount + " pages");
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ResponseBody responseBody = response.body();
                    String body = responseBody.string();
                }
            }
        });
    }

    class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.PostsViewHolder> {
        ArrayList<Post> postsArrayList;

        public PostsRecyclerViewAdapter(ArrayList<Post> posts) {
            this.postsArrayList = posts;
        }

        @NonNull
        @Override
        public PostsRecyclerViewAdapter.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false);
            PostsViewHolder postsViewHolder = new PostsRecyclerViewAdapter.PostsViewHolder(view);

            return postsViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
            if(postsArrayList.size() != 0) {
                Post post = postsArrayList.get(position);
                holder.postTitle.setText(post.getPostText());
                holder.postAuthor.setText(post.getPostAuthor());
                holder.postDateTime.setText(post.formatDateString(post.getPostCreationDateTime()));

                holder.postID = post.getPostID();

                //adds delete button to user's posts only
                //NOTE: checks against userID, as multiple users can have the same name
                int userID = mListener.getUserID();
                if(post.getPostAuthorID() == userID){
                    holder.deleteButton.setClickable(true);
                    holder.deleteButton.setVisibility(View.VISIBLE);
                } else {
                    holder.deleteButton.setClickable(false);
                    holder.deleteButton.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return this.postsArrayList.size();
        }

        class PostsViewHolder extends RecyclerView.ViewHolder {
            TextView postTitle;
            TextView postAuthor;
            TextView postDateTime;
            ImageView deleteButton;
            int postID;

            public PostsViewHolder(@NonNull View itemView) {
                super(itemView);
                this.postTitle = itemView.findViewById(R.id.post_title);
                this.postAuthor = itemView.findViewById(R.id.user_name);
                this.postDateTime = itemView.findViewById(R.id.post_creation_time);
                this.deleteButton = itemView.findViewById(R.id.post_delete);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //call api and delete post record
                        deletePost(postID);
                    }
                });
            }
        }
    }

    void deletePost(int postID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Post")
                .setMessage("Delete selected post?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        FormBody deleteBody = new FormBody.Builder()
                                .add("post_id", String.valueOf(postID))
                                .build();

                        Request deletePostRequest = new Request.Builder()
                                .url("https://www.theappsdr.com/posts/delete")
                                .post(deleteBody)
                                .addHeader("Authorization", "BEARER " + token)
                                .build();

                        client.newCall(deletePostRequest).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                if(response.isSuccessful()){
                                    try {
                                        JSONObject deleteResponseObject = new JSONObject(response.body().string());
                                        String message = deleteResponseObject.getString("message");
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        getPosts(currentPage);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        JSONObject deleteFailureObject = new JSONObject(response.body().string());
                                        String errorMessage = deleteFailureObject.getString("message");
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                builder.setTitle("Error")
                                                        .setMessage(errorMessage)
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                            }
                                                        });
                                                builder.create().show();
                                            }
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create().show();
    }

    class PagesRecyclerViewAdapter extends RecyclerView.Adapter<PagesRecyclerViewAdapter.PagesViewHolder> {
        ArrayList<Integer> numPages;

        public PagesRecyclerViewAdapter(ArrayList<Integer> pages) {
            this.numPages = pages;
        }

        @NonNull
        @Override
        public PagesRecyclerViewAdapter.PagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_list_item, parent, false);
            PagesViewHolder pagesViewHolder = new PagesRecyclerViewAdapter.PagesViewHolder(view);

            return pagesViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull PagesViewHolder holder, int position) {
            if(numPages.size() != 0) {
                holder.pageNumberText.setText(String.valueOf(numPages.get(position)));
            }
        }

        @Override
        public int getItemCount() {
            return this.numPages.size();
        }

        class PagesViewHolder extends RecyclerView.ViewHolder {
            TextView pageNumberText;

            public PagesViewHolder(@NonNull View itemView) {
                super(itemView);
                this.pageNumberText = itemView.findViewById(R.id.page_number_text);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //call getPosts and send it number clicked for page #
                        currentPage = Integer.parseInt(pageNumberText.getText().toString());
                        getPosts(currentPage);
                    }
                });
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (PostsListFragmentListener) context;
    }

    interface PostsListFragmentListener {
        String getUserName();
        int getUserID();
        void goToCreatePost(String token);
        void logout();
    }
}