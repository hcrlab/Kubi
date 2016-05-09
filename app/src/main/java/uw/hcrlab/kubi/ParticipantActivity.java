package uw.hcrlab.kubi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class ParticipantActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static String TAG = ParticipantActivity.class.getSimpleName();

    private String language = "dutch";

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
        startActivity(intent);
    }
}
