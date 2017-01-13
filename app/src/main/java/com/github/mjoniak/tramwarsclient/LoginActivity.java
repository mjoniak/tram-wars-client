package com.github.mjoniak.tramwarsclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @SuppressWarnings("UnusedParameters")
    public void submitButtonOnClick(View view) {
        EditText userNameEdit = (EditText)findViewById(R.id.userNameEdit);
        EditText passwordEdit = (EditText)findViewById(R.id.passwordEdit);
        String userName = userNameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        ApiClient client = new ApiClient(this, new IErrorHandler() {
            @Override
            public void handle(String message) {
                Toast.makeText(LoginActivity.this, R.string.connectivity_error, Toast.LENGTH_LONG).show();
            }
        });
        client.authorise(userName, password, new IContinuation<AuthorisationTokenDTO>() {
            @Override
            public void continueWith(AuthorisationTokenDTO response) {
                //TODO: pass token to next activity
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                intent.putExtra(Const.AUTH_TOKEN_EXTRA_KEY, response.getAccessToken());
                startActivity(intent);
            }
        });
    }
}
