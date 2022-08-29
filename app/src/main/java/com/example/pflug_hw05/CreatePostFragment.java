/**
 * Assignment #: HW05
 * File Name: Pflug_HW05 CreatePostFragment.java
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreatePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePostFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();

    private static final String ARG_USER_TOKEN = "ARG_USER_TOKEN";

    private String token;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userToken Parameter 1.
     * @return A new instance of fragment CreatePostFragment.
     */
    public static CreatePostFragment newInstance(String userToken) {
        CreatePostFragment fragment = new CreatePostFragment();
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
        return inflater.inflate(R.layout.fragment_create_post, container, false);
    }

    TextView postTextBox;
    Button submitButton;
    Button cancelButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Create Post");

        postTextBox = view.findViewById(R.id.createPost_enterPostText);

        submitButton = view.findViewById(R.id.createPost_SubmitButton);
        cancelButton = view.findViewById(R.id.createPost_cancelPost);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(postTextBox.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Error")
                            .setMessage("Please enter a valid post in the textbox")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    builder.create().show();
                } else {
                    String postText = postTextBox.getText().toString();
                    createNewPost(postText);
                    //mListener.createPost();
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.cancel();
            }
        });
    }

    void createNewPost(String text) {
        FormBody createPostBody = new FormBody.Builder()
                .add("post_text", text)
                .build();

        Request createPostRequest = new Request.Builder()
                .url("https://www.theappsdr.com/posts/create")
                .post(createPostBody)
                .addHeader("Authorization", "BEARER " + token)
                .build();

        client.newCall(createPostRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try {
                        JSONObject createPostResponseObject = new JSONObject(response.body().string());
                        String message = createPostResponseObject.getString("message");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), message,Toast.LENGTH_SHORT).show();
                            }
                        });
                        mListener.createPost();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        JSONObject createPostFailureObject = new JSONObject(response.body().string());
                        String createErrorMessage = createPostFailureObject.getString("message");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Error")
                                        .setMessage(createErrorMessage)
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

    CreatePostFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CreatePostFragmentListener) context;
    }

    interface CreatePostFragmentListener {
        void cancel();
        void createPost();
    }
}