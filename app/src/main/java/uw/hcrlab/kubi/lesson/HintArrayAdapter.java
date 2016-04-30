package uw.hcrlab.kubi.lesson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import uw.hcrlab.kubi.R;

/**
 * Created by Alexander on 4/28/2016.
 */
public class HintArrayAdapter extends ArrayAdapter<PromptData.Hint> {
    private final Context context;
    private final ArrayList<PromptData.Hint> hints;

    public HintArrayAdapter(Context context, ArrayList<PromptData.Hint> hints) {
        super(context, -1, hints);

        this.context = context;
        this.hints = hints;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.hint_detail, parent, false);
        }

        TextView hint = (TextView) convertView.findViewById(R.id.hint_detail_text);
        hint.setText(hints.get(position).text);

        return convertView;
    }
}
