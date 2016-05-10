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

/**
 * This is the initial activity that starts up when users install and run KubiLingo for the first
 * time. It is in charge of naming this device (so that multiple devices can interact with the
 * chrome extension at once) and getting the study facilitator's Firebase credentials. After a
 * facilitator logs into Firebase successfully, this activity finishes and starts the Participant
 * Activity. If valid credentials are already stored, this activity will immediately finish and
 * start the Participant Activity without ever rendering to the screen.
 */
public class SetupActivity extends Activity implements View.OnClickListener, Firebase.AuthResultHandler {

    ProgressDialog progress;

    /**
     * Checks if the app already has stored login credentials. If so, it cedes control to the
     * Participant Activity. If not, it requests bluetooth permissions and displays the UI.
     *
     * @param savedInstanceState Saved state from previous creation of this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App app = (App) getApplication();
        if(app.authenticate()) {
            Intent intent = new Intent(this, ParticipantActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_setup);

        progress = new ProgressDialog(this);
        progress.setTitle("KubiLingo Setup");
        progress.setMessage("Checking Credentials...");

        // make sure we have the permissions needed to connect bluetooth
        PermissionsManager.requestPermissionsDialogIfNecessary(this);

        Button btn = (Button) findViewById(R.id.startup_submit_btn);
        btn.setOnClickListener(this);
    }

    /**
     * Callback for responding to user's decision to grant or deny bluetooth permissions.
     *
     * @param requestCode The request code
     * @param permissions The permissions requested
     * @param grantResults The results of each requested permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        PermissionsManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    /**
     * This will attempt to authenticate the application using the supplied credentials.
     *
     * @param view The button that was clicked
     */
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

    /**
     * Callback for successful authentication with Firebase. This will dismiss the progress display
     * and start the Participant activity.
     *
     * @param authData Authentication data from Firebase
     */
    @Override
    public void onAuthenticated(AuthData authData) {
        progress.dismiss();

        Intent intent = new Intent(this, ParticipantActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Callback for indicating that the Firebase credentials were incorrect. Indicates this to the
     * user and lets the user try again.
     *
     * @param firebaseError The error object returned by the Firebase library
     */
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
