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

public class ParticipantActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {
    private static String TAG = ParticipantActivity.class.getSimpleName();

    private String language = "dutch";
    private String participant = "";

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
    }

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

    @Override
    public void onClick(View view) {
        App app = (App) getApplication();
        app.loadAudio(language);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.TEACHING_LANGUAGE, language);
        intent.putExtra(MainActivity.PARTICIPANT_NAME, participant);
        startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

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
    public void afterTextChanged(Editable s) {

    }
}
