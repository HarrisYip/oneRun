package com.onerun.onerun.onerun;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.onerun.onerun.onerun.Model.RunDataSource;

import java.util.Date;

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

        //EditText pace = (EditText) view.findViewById(R.id.paceSet);

        Spinner intervalMinute = (Spinner) view.findViewById(R.id.updateIntervalSet);
        Integer[] mins = new Integer[]{1,2,3,4,5,6,7,8,9};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_dropdown_item_1line, mins);
        intervalMinute.setAdapter(adapter);

        Spinner intervalSecond = (Spinner) view.findViewById(R.id.updateIntervalSet2);
        Integer[] secs = new Integer[60];
        for(int i = 0; i < 60; i++) {
            secs[i] = i;
        }
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_dropdown_item_1line, secs);
        intervalSecond.setAdapter(adapter2);

        Spinner paceMin = (Spinner) view.findViewById(R.id.paceSet);
        Integer[] paceMins = new Integer[]{1,2,3,4,5,6,7,8,9,10};
        ArrayAdapter<Integer> paceAdpt = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_dropdown_item_1line, paceMins);
        paceMin.setAdapter(paceAdpt);

        Spinner paceSecs = (Spinner) view.findViewById(R.id.paceSet2);
        paceSecs.setAdapter(adapter2);

        Button startButton = (Button) view.findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Spinner paceMin = (Spinner) view.findViewById(R.id.paceSet);
                String myPaceMin = paceMin.getSelectedItem().toString();
                Spinner paceSecs = (Spinner) view.findViewById(R.id.paceSet2);
                String myPaceSecs = paceSecs.getSelectedItem().toString();

                RunDataSource runDB = new RunDataSource(getActivity());
                runDB.open();
                double setPaceMin = Double.parseDouble(myPaceMin);
                double setPaceSecs = Double.parseDouble(myPaceSecs);
                long rid = runDB.insertRun(1, new Date(), new Date(), setPace, 10);
                ToastMessage.message(getActivity(), "" + runDB.getRun((int)rid).getPace());
                runDB.close();
                */
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
                    bpmSet.setText("60");
                } else {
                    TextView bpmText = (TextView) view.findViewById(R.id.bpm);
                    bpmText.setVisibility(View.GONE);

                    EditText bpmSet = (EditText) view.findViewById(R.id.bpmSet);
                    bpmSet.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(bpmSet.getWindowToken(), 0);
                }
            }
        });


        return view;
    }

}
