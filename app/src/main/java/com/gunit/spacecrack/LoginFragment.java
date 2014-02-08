package com.gunit.spacecrack;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.SessionLoginBehavior;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by Dimi on 6/02/14.
 */
public class LoginFragment extends Fragment {

    private EditText edtUsername;
    private EditText edtPassword;
    private Button login;
    private Button register;
    private LoginButton facebook;
    private SharedPreferences sharedPreferences;

    private SpaceCrackApplication application;
    private LoginActivity loginActivity;

    private static final String TAG = "LoginFragment";
    private final String URL = "http://10.0.2.2:8080/api/api/accesstokens";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);

        loginActivity = (LoginActivity) getActivity();
        application = (SpaceCrackApplication) loginActivity.getApplication();

        //Get the access token if available
        sharedPreferences = getActivity().getSharedPreferences("Login", 0);
        if (sharedPreferences != null && sharedPreferences.getString("accesToken", null) != null) {
            application.setAccesToken(sharedPreferences.getString("accesToken", ""));
        }

        //Find the views
        edtUsername = (EditText) view.findViewById(R.id.edt_login_username);
        edtPassword = (EditText) view.findViewById(R.id.edt_login_password);
        login = (Button) view.findViewById(R.id.btn_login_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoginTask(edtUsername.getText().toString(), edtPassword.getText().toString()).execute(URL);
            }
        });
        register = (Button) view.findViewById(R.id.btn_login_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        facebook = (LoginButton) view.findViewById(R.id.btn_login_facebook);
        //Set the permissions
        facebook.setReadPermissions(Arrays.asList("email"));
        facebook.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                application.setGraphUser(user);
                if (user != null) {
                    Intent intent = new Intent(loginActivity, HomeActivity.class);
                    startActivity(intent);
//                    user.getProperty("email").toString();
                }
            }
        });

        return view;
    }


    //POST request to log the user in
    public class LoginTask extends AsyncTask<String, Void, String> {

        private JSONObject user;

        public LoginTask (String username, String password )
        {
            super();
            //Create an user to log in
            user = new JSONObject();
            try {
                user.put("username", username);
                user.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            login.setEnabled(false);
        }

        @Override
        protected String doInBackground (String...url)
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url[0]);
            StringEntity stringEntity = null;
            try {
                stringEntity = new StringEntity(user.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //httpPost.setHeader("accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(stringEntity);

            String accessToken = null;

            try {
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httpPost);

                //Check the Status code of the response
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    try {
                        //Get the access token
                        JSONObject responseJson = new JSONObject(responseBody);
                        accessToken = responseJson.getString("value");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "Logged in");
                } else {
                    Log.i(TAG, "Login failed");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return accessToken;
        }

        @Override
        protected void onPostExecute (String result)
        {
            Toast.makeText(getActivity(), result != null ? getResources().getString(R.string.login_succes) : getResources().getString(R.string.login_fail), Toast.LENGTH_SHORT).show();

            //Save the access token
            application.setAccesToken(result);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("accessToken", result);
            editor.commit();

            if (result != null) {
                //Start the Home activity
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            }

            login.setEnabled(true);
        }
    }

}
