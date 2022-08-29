/**
 * Assignment #: HW05
 * File Name: Pflug_HW05 CreateAccountFragment.java
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
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateAccountFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    EditText nameTextBox;
    EditText emailTextBox;
    EditText passwordTextBox;
    Button submitButton;
    Button cancelButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Create New Account");

        nameTextBox = view.findViewById(R.id.createAccount_nameTextBox);
        emailTextBox = view.findViewById(R.id.createAccount_emailTextBox);
        passwordTextBox = view.findViewById(R.id.createAccount_passwordTextBox);
        submitButton = view.findViewById(R.id.createAccount_submitButton);
        cancelButton = view.findViewById(R.id.createAccount_cancelButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check for missing input, display alert dialog if so
                if(nameTextBox.getText().toString().equals("")) {
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    b.setTitle("Error")
                            .setMessage("Please enter a valid name")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    b.create().show();
                } else {
                    if(emailTextBox.getText().toString().equals("")) {
                        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                        b.setTitle("Error")
                                .setMessage("Please enter a valid email")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        b.create().show();
                    } else {
                        if(passwordTextBox.getText().toString().equals("")) {
                            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                            b.setTitle("Error")
                                    .setMessage("Please enter a valid password")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                            b.create().show();
                        } else {
                            String newUserName = nameTextBox.getText().toString();
                            String newUserEmail = emailTextBox.getText().toString();
                            String newUserPassword = passwordTextBox.getText().toString();
                            attemptRegistration(newUserName, newUserEmail, newUserPassword);
                        }
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.cancelToLogin();
            }
        });
    }

    void attemptRegistration (String name, String email, String password) {
        FormBody registrationBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .add("name", name)
                .build();

        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/posts/signup")
                .post(registrationBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try {
                        JSONObject regFormResponseObject = new JSONObject(response.body().string());
                        String token = regFormResponseObject.getString("token");
                        String fullName = regFormResponseObject.getString("user_fullname");
                        int userID = regFormResponseObject.getInt("user_id");
                        mListener.login(fullName, token, userID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject errorResponseObject = new JSONObject(response.body().string());
                        String errorMessage = errorResponseObject.getString("message");

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

    CreateAccountFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CreateAccountFragmentListener) context;
    }

    interface CreateAccountFragmentListener {
        void cancelToLogin();
        void login(String name, String token, int ID);
    }
}