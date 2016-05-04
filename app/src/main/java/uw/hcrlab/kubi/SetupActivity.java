package uw.hcrlab.kubi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class SetupActivity extends Activity implements View.OnClickListener, Firebase.AuthResultHandler {

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        progress = new ProgressDialog(this);
        progress.setTitle("KubiLingo Setup");
        progress.setMessage("Checking Credentials...");

        Button btn = (Button) findViewById(R.id.startup_submit_btn);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        App app = (App) getApplication();

        EditText username = (EditText) findViewById(R.id.startup_username);
        EditText password = (EditText) findViewById(R.id.startup_password);
        EditText deviceName = (EditText) findViewById(R.id.startup_device_name);

        progress.show();

        app.saveCredentials(username.getText().toString(), password.getText().toString(), deviceName.getText().toString());
        app.authenticate(this);
    }

    @Override
    public void onAuthenticated(AuthData authData) {
        progress.dismiss();

        Intent result = new Intent();
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onAuthenticationError(FirebaseError firebaseError) {
        progress.dismiss();

        View error = findViewById(R.id.startup_error);
        error.setVisibility(View.VISIBLE);

        TextView username = (TextView) findViewById(R.id.startup_username_label);
        username.setTextColor(getResources().getColor(R.color.really_incorrect, getTheme()));

        TextView password = (TextView) findViewById(R.id.startup_password_label);
        password.setTextColor(getResources().getColor(R.color.really_incorrect, getTheme()));
    }
}
