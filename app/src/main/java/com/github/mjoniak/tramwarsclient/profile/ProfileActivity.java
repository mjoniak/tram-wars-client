package com.github.mjoniak.tramwarsclient.profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjoniak.tramwarsclient.R;
import com.github.mjoniak.tramwarsclient.datasource.ApiClient;
import com.github.mjoniak.tramwarsclient.datasource.IErrorHandler;
import com.github.mjoniak.tramwarsclient.domain.UserProfile;

public class ProfileActivity extends AppCompatActivity implements ProfileContract.View, IErrorHandler, View.OnClickListener {

    private ProfileContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        presenter = new ProfilePresenter(this, new ApiClient(getApplicationContext(), this));
        presenter.start();

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);
    }

    @Override
    public void displayProfile(UserProfile profile) {
        EditText userNameEdit = (EditText)findViewById(R.id.user_name_edit);
        TextView scoreView = (TextView)findViewById(R.id.number_of_points_text);
        userNameEdit.setText(profile.getName());
        scoreView.setText(profile.getScore() + "");
    }

    @Override
    public void displayProfileUpdatedMessage() {
        Toast.makeText(getApplicationContext(), R.string.saved_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void handle(String message) {
        Toast.makeText(getApplicationContext(), R.string.connectivity_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {

        EditText userNameEdit = (EditText)findViewById(R.id.user_name_edit);
        EditText passwordEdit = (EditText)findViewById(R.id.password_edit);
        EditText newPasswordEdit = (EditText)findViewById(R.id.new_password_edit);

        presenter.saveProfile(new UserProfile(userNameEdit.getText().toString(), newPasswordEdit.getText().toString()),
                passwordEdit.getText().toString());
    }
}
