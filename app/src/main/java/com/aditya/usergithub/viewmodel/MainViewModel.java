package com.aditya.usergithub.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aditya.usergithub.model.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainViewModel extends ViewModel {

    private MutableLiveData<ArrayList<User>> listUser = new MutableLiveData<>();


    public void setSearchQuery(final String query, final String EXTRA_AUTH) {

        final ArrayList<User> list = new ArrayList<>();

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "token " + EXTRA_AUTH);
        client.addHeader("User-Agent", "request");
        client.get(query, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject responseObject = new JSONObject(result);
                    JSONArray listUserArray = responseObject.getJSONArray("items");

                    if (listUserArray.length() != 0) {
                        for (int i = 0; i < listUserArray.length(); i++) {
                            JSONObject userObject = listUserArray.getJSONObject(i);
                            User userData = new User();
                            userData.setUserName(userObject.getString("login"));
                            userData.setDetailUrl(userObject.getString("url"));
                            userData.setAvatarUrl(userObject.getString("avatar_url"));
                            userData.setHtmlUrl(userObject.getString("html_url"));
                            list.add(userData);
                        }
                        listUser.postValue(list);
                    } else {
                        User userNull = new User();
                        userNull.setUserName("not found");
                        list.add(userNull);
                        listUser.postValue(list);
                    }
                } catch (JSONException e) {
                    Log.d("JSONException", e.getMessage());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage;

                switch (statusCode) {
                    case 401:
                        errorMessage = statusCode + " : Bad Request";
                        break;
                    case 403:
                        errorMessage = statusCode + " : Forbidden";
                        break;
                    case 404:
                        errorMessage = statusCode + " : Not Found";
                        break;
                    default:
                        errorMessage = statusCode + " : " + error.getMessage();
                        break;
                }
                Log.d("onFailure SearchUser : ", errorMessage);
            }
        });
    }

    public LiveData<ArrayList<User>> getListUser() {
        return listUser;
    }
}
