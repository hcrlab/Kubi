package uw.hcrlab.kubi;

import android.app.Activity;
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

/**
 * Activity for collecting basic information at the beginning of a run of the study with a new participant.
 */
public class ParticipantActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {
    private static String TAG = ParticipantActivity.class.getSimpleName();

    /** Language to teach the current participant */
    private String language = "dutch";

    /** The current participant's name */
    private String participant = "";

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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.study_phases, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Button btn = (Button) findViewById(R.id.participant_submit_btn);
        btn.setOnClickListener(this);

        EditText editor = (EditText) findViewById(R.id.participant_name);
        editor.addTextChangedListener(this);
        editor.setShowSoftInputOnFocus(false);
    }

    /**
     * Callback for when a study phase has been selected
     *
     * @param adapterView The list adapter for study phases
     * @param view The spinner view
     * @param position Position of the view in the adapter
     * @param id Row id of the item that was selected
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        String selected = (String) adapterView.getSelectedItem();

        if(selected.contains("Swedish")) {
            language = "swedish";
        } else {
            language = "dutch";
        }
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
        intent.putExtra(MainActivity.PARTICIPANT_NAME, participant);
        startActivity(intent);
    }

    /**
     * Callback for when the participant's name changes
     * @param s The participant's name
     * @param start The starting position of the change
     * @param before The old length of the text
     * @param count The number of characters in the change
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean enable = s.length() > 0;

        Button btn = (Button) findViewById(R.id.participant_submit_btn);

        if(btn != null) {
            btn.setEnabled(enable);
        }

        participant = s.toString();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Intentionally doesn't do anything...
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Intentionally doesn't do anything...
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Intentionally doesn't do anything...
    }
}
