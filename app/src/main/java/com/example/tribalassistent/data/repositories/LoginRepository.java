package com.example.tribalassistent.data.repositories;

import android.util.Log;

import com.example.tribalassistent.data.comunication.OnResultListener;
import com.example.tribalassistent.data.comunication.request.CharacterSelectRequest;
import com.example.tribalassistent.data.comunication.request.LoginRequest;
import com.example.tribalassistent.data.model.authentication.CharacterSelected;
import com.example.tribalassistent.data.model.authentication.Player;

public class LoginRepository {
    private static final String TAG = "LoginRepository";
    private static LoginRepository instance;
    private Player user = null;

    private OnResultListener<Player> onLogin;
    private OnResultListener<CharacterSelected> onCharacterSelected;

    // private constructor : singleton access
    private LoginRepository() {
    }

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
    }

    public Player getUser() {
        return user;
    }

    public void setLoggedInUser(Player user) {
        Log.d(TAG, "Logging in with user " + user.getName());
        this.user = user;
        SystemRepository.getInstance().systemIdentify();
    }

    public void login(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        loginRequest.onResultListener(result -> {
            onLogin.onResult(result);
            try {
                user = result.getData();
            } catch (NoSuchFieldException ignored) {
            }
        });
        loginRequest.doInBackground();
    }

    public void select(int id, String world_id) {
        CharacterSelectRequest selectRequest = new CharacterSelectRequest(id, world_id);
        selectRequest.onResultListener(onCharacterSelected);
        selectRequest.doInBackground();
    }

    public void setOnLogin(OnResultListener<Player> onLogin) {
        this.onLogin = onLogin;
    }

    public void setOnCharacterSelected(OnResultListener<CharacterSelected> onCharacterSelected) {
        this.onCharacterSelected = onCharacterSelected;
    }
}
