package com.github.mjoniak.tramwarsclient.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.mjoniak.tramwarsclient.Const;
import com.github.mjoniak.tramwarsclient.R;
import com.github.mjoniak.tramwarsclient.datasource.ApiClient;
import com.github.mjoniak.tramwarsclient.datasource.IContinuation;
import com.github.mjoniak.tramwarsclient.datasource.IErrorHandler;
import com.github.mjoniak.tramwarsclient.datasource.dto.AuthorisationTokenDTO;
import com.github.mjoniak.tramwarsclient.map.MapsActivity;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @SuppressWarnings("UnusedParameters")
    public void submitButtonOnClick(View view) {
        progressBar.setVisibility(View.VISIBLE);

        EditText userNameEdit = (EditText)findViewById(R.id.userNameEdit);
        EditText passwordEdit = (EditText)findViewById(R.id.passwordEdit);
        String userName = userNameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        ApiClient client = new ApiClient(this, new IErrorHandler() {
            @Override
            public void handle(String message) {
                Toast.makeText(LoginActivity.this, R.string.connectivity_error, Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        client.authorise(userName, password, new IContinuation<AuthorisationTokenDTO>() {
            @Override
            public void continueWith(AuthorisationTokenDTO response) {
                progressBar.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                intent.putExtra(Const.AUTH_TOKEN_EXTRA_KEY, response.getAccessToken());
                startActivity(intent);

            }
        });
    }
}
