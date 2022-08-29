/**
 * Assignment #: HW05
 * File Name: Pflug_HW05 MainActivity.java
 * Full Name: Kristin Pflug
 */

package com.example.pflug_hw05;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener, PostsListFragment.PostsListFragmentListener, CreateAccountFragment.CreateAccountFragmentListener, CreatePostFragment.CreatePostFragmentListener {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("appPreferences", Context.MODE_PRIVATE);

        //check user authentication here
        //based on result, go to specific screen
        String userToken = sharedPreferences.getString("token", "");
        spEditor = sharedPreferences.edit();

        if(userToken.equals("")){
            getSupportFragmentManager().beginTransaction().add(R.id.rootView, new LoginFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.rootView, PostsListFragment.newInstance(userToken)).commit();
        }
    }

    @Override
    public void goToCreateAccount() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new CreateAccountFragment()).commit();
    }


    @Override
    public void cancelToLogin() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new LoginFragment()).commit();
    }

    @Override
    public void login(String name, String token, int ID) {
        spEditor.putString("token", token);
        spEditor.putString("name", name);
        spEditor.putInt("id", ID);
        spEditor.apply();
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, PostsListFragment.newInstance(token)).commit();
    }

    @Override
    public String getUserName() {
        return sharedPreferences.getString("name", "Username");
    }

    @Override
    public int getUserID() {
        return sharedPreferences.getInt("id", 1);
    }

    @Override
    public void goToCreatePost(String token) {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, CreatePostFragment.newInstance(token)).addToBackStack(null).commit();
    }

    @Override
    public void logout() {
        //delete user token from shared preferences
        sharedPreferences.edit().clear().apply();
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new LoginFragment()).commit();
    }

    @Override
    public void cancel() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void createPost() {
        getSupportFragmentManager().popBackStack();
    }
}