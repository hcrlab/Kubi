package uw.hcrlab.kubi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import uw.hcrlab.kubi.wizard.Participant;
import uw.hcrlab.kubi.wizard.ParticipantArrayAdapter;

/**
 * Activity for collecting basic information at the beginning of a run of the study with a new participant.
 */
public class ParticipantActivity extends Activity implements View.OnClickListener {
    private static String TAG = ParticipantActivity.class.getSimpleName();

    /** Language to teach the current participant */
    private String language = "dutch";

    /** The current participant's ID */
    private String participant = "";

    private ProgressDialog progress;

    private ArrayList<Participant> participants;
    private ParticipantArrayAdapter participantArrayAdapter;

    /**
     * Creates the Participant activity by adding options to the Study Phase spinner (dropdown) and
     * attaching listeners for when the participant's name changes, when a study phase is selected,
     * and when the "start study" button is clicked.
     *
     * @param savedInstanceState Bundle from the previous creation of this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant);

        Spinner spinner = (Spinner) findViewById(R.id.participant_phase);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.study_phases, R.layout.participant_id_item);
        adapter.setDropDownViewResource(R.layout.participant_id_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = (String) adapterView.getSelectedItem();

                if(selected.contains("Swedish")) {
                    language = "swedish";
                } else {
                    language = "dutch";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button btn = (Button) findViewById(R.id.participant_submit_btn);
        btn.setOnClickListener(this);

        Button refresh = (Button) findViewById(R.id.participant_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParticipants();
            }
        });

        progress = new ProgressDialog(this);
        progress.setTitle("KubiLingo Study");
        progress.setMessage("Getting participants...");

        participants = new ArrayList<>();
        participantArrayAdapter = new ParticipantArrayAdapter(this, R.layout.participant_id_item, participants);

        Spinner partID = (Spinner) findViewById(R.id.participant_id);
        partID.setAdapter(participantArrayAdapter);
        partID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                participant = participants.get(i).getKey();

                Log.d(TAG, "Selected participant: " + participant);
                App.setParticipant(participant);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        App app = (App) getApplication();
        app.connectToKubi();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getParticipants();
    }

    private void getParticipants() {
        progress.show();
        App.getParticipants(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                progress.dismiss();

                if(!snap.exists()) {
                    return;
                }

                participants.clear();
                participantArrayAdapter.notifyDataSetChanged();

                for(DataSnapshot p : snap.getChildren()) {
                    String key = p.getKey();
                    String id = p.getValue(String.class);

                    Log.d(TAG, "Got participant: " + id);

                    participants.add(new Participant(key, id));
                }

                Collections.reverse(participants);
                participant = participants.get(0).getKey();
                participantArrayAdapter.notifyDataSetChanged();

                App.setParticipant(participant);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                progress.dismiss();
            }
        });
    }

    /**
     * Callback for when the "start study" button is clicked
     *
     * @param view The button that was clicked
     */
    @Override
    public void onClick(View view) {
        App app = (App) getApplication();
        app.loadAudio(language);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.TEACHING_LANGUAGE, language);
        intent.putExtra(MainActivity.PARTICIPANT_ID, participant);
        startActivity(intent);
    }
}
