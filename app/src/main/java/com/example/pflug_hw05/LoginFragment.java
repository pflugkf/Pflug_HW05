/**
 * Assignment #: HW05
 * File Name: Pflug_HW05 LoginFragment.java
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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


public class LoginFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();

    public LoginFragment() {
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    EditText emailTextbox;
    EditText passwordTextbox;
    Button loginButton;
    Button createAcctButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Login");

        emailTextbox = view.findViewById(R.id.login_emailTextBox);
        passwordTextbox = view.findViewById(R.id.login_passwordTextBox);
        loginButton = view.findViewById(R.id.login_loginButton);
        createAcctButton = view.findViewById(R.id.login_createAccountButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check for missing input, display alert dialog if so
                if(emailTextbox.getText().toString().equals("")) {
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    b.setTitle("Error")
                            .setMessage("Please enter a valid email address")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    b.create().show();
                } else {
                    if(passwordTextbox.getText().toString().equals("")) {
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
                        //attempt login
                        String email = emailTextbox.getText().toString();
                        String password = passwordTextbox.getText().toString();
                        attemptLogin(email, password);
                    }
                }
            }
        });

        createAcctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToCreateAccount();
            }
        });
    }

    void attemptLogin(String email, String password) {
        FormBody loginBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/posts/login")
                .post(loginBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try {
                        JSONObject formResponseObject = new JSONObject(response.body().string());
                        String token = formResponseObject.getString("token");
                        String fullName = formResponseObject.getString("user_fullname");
                        int userID = formResponseObject.getInt("user_id");
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

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    LoginFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (LoginFragmentListener) context;
    }

    interface LoginFragmentListener {
        void goToCreateAccount();
        void login(String name, String token, int ID);
    }
}