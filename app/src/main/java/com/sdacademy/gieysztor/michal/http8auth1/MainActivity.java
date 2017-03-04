package com.sdacademy.gieysztor.michal.http8auth1;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    EditText mLogin;
    EditText mPassword;

    Button mButton;

    String login;
    String password;

    TextView mUserId;
    TextView mUserLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogin = (EditText) findViewById(R.id.login);
        mPassword = (EditText) findViewById(R.id.password);
        mButton = (Button) findViewById(R.id.button);
        mUserId = (TextView) findViewById(R.id.userId);
        mUserLogin = (TextView) findViewById(R.id.userLogin);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = mLogin.getText().toString();
                password = mPassword.getText().toString();

                Log.i("GET L&P", "Login: " + login + ", password: " + password);

                new SubmitAuthorisation().execute();

            }
        });


    }


    private String authorisation() throws IOException {
        String credentials = login + ":" + password;
        byte[] credentialsBytes = credentials.getBytes();
        String encodedCredentials = Base64.encodeToString(credentialsBytes, Base64.NO_WRAP);

        Log.i("CREDENTIALS", encodedCredentials);

        Request.Builder builder = new Request.Builder();
        builder.url("https://api.github.com/user");
        builder.addHeader("Authorization", "Basic " + encodedCredentials);
//        builder.addHeader("GET", "user");
        builder.get();

        Request request = builder.build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();

//        Log.i("GITHUB RESPONSE ", response.body().string());
        String responseString = response.body().string();
        Log.i("GITHUB RESPONSE ", responseString);

        return responseString;

    }

    public class SubmitAuthorisation extends AsyncTask<Void, Void, String> {
        String result;

        @Override
        protected String doInBackground(Void... params) {
            try {
                result = authorisation();
                return result;
//                getUserData();
            } catch (IOException e) {
                e.printStackTrace();
                return "Błąd sieci";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            JSONObject githubResponse = null;
            try {
                githubResponse = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                mUserId.setText(githubResponse.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                mUserLogin.setText(githubResponse.getString("login"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}

