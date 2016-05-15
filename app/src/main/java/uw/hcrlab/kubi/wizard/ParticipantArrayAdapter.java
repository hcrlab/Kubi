package uw.hcrlab.kubi.wizard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import uw.hcrlab.kubi.R;

/**
 * Created by Alexander on 5/14/2016.
 */
public class ParticipantArrayAdapter extends ArrayAdapter<Participant> {
    private final Context context;
    private final int resource;

    private final ArrayList<Participant> participants;

    public ParticipantArrayAdapter(Context context, int resource,  ArrayList<Participant> participants) {
        super(context, resource, participants);

        this.resource = resource;
        this.context = context;
        this.participants = participants;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(resource, parent, false);
        }

        TextView tv = (TextView) convertView;
        tv.setText(participants.get(position).getId());

        return convertView;
    }
}
