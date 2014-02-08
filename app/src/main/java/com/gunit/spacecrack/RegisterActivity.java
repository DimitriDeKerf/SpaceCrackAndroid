package com.gunit.spacecrack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class RegisterActivity extends Activity {

    private Button register;
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private EditText email;

    private Context context;

    private static final String TAG = "RegisterActivity";
    private final String URL = "http://10.0.2.2:8080/api/api/accesstokens";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        context = this;
        username = (EditText) findViewById(R.id.edt_register_username);
        password = (EditText) findViewById(R.id.edt_register_password);
        confirmPassword = (EditText) findViewById(R.id.edt_register_password_confirm);
        email = (EditText) findViewById(R.id.edt_register_email);
        register = (Button) findViewById(R.id.btn_register_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPasswords(password.getText().toString(), confirmPassword.getText().toString())) {
                    new RegisterTask(username.getText().toString(), password.getText().toString(), email.getText().toString()).execute(URL);
                }
            }
        });
    }

    public boolean checkPasswords(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    //POST request to register the user
    public class RegisterTask extends AsyncTask<String, Void, Boolean> {

        private JSONObject newUser;

        public RegisterTask (String username, String password, String email)
        {
            super();
            newUser = new JSONObject();
            try {
                newUser.put("username", username);
                newUser.put("password", password);
                newUser.put("email", email);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            register.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground (String...url)
        {
            boolean posted = false;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url[0]);
            StringEntity stringEntity = null;
            try {
                stringEntity = new StringEntity(newUser.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //httpPost.setHeader("accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(stringEntity);

            try {
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httpPost);

                //Check the Status code of the response
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    try {
                        JSONObject responseJson = new JSONObject(responseBody);
                        posted = responseJson.getBoolean("succeeded");
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
            return posted;
        }

        @Override
        protected void onPostExecute (Boolean result)
        {
            Toast.makeText(RegisterActivity.this, result ? getResources().getString(R.string.register_succes) : getResources().getString(R.string.register_fail), Toast.LENGTH_SHORT).show();
            if (result) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
            register.setEnabled(true);
        }
    }

}
