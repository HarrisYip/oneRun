package com.onerun.onerun.onerun;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by Terrence on 2/27/2015.
 */
public class WorkoutSetFragment extends Fragment {

    public static WorkoutSetFragment newInstance(int sectionNumber) {
        WorkoutSetFragment frag = new WorkoutSetFragment();
        Bundle args = new Bundle();
        args.putInt("SPEECH_SECTION_NUMBER", sectionNumber);
        frag.setArguments(args);
        return frag;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.workout_set_fragment,
                container, false);

        Button speakButton = (Button) view.findViewById(R.id.start);
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing for now
            }
        });

        final ToggleButton bpm = (ToggleButton) view.findViewById(R.id.cadenceSet);
        bpm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked) {
                    TextView bpmText = (TextView) view.findViewById(R.id.bpm);
                    bpmText.setVisibility(View.VISIBLE);

                    EditText bpmSet = (EditText) view.findViewById(R.id.bpmSet);
                    bpmSet.setVisibility(View.VISIBLE);
                } else {
                    TextView bpmText = (TextView) view.findViewById(R.id.bpm);
                    bpmText.setVisibility(View.INVISIBLE);

                    EditText bpmSet = (EditText) view.findViewById(R.id.bpmSet);
                    bpmSet.setVisibility(View.INVISIBLE);
                }
            }
        });


        return view;
    }

}
