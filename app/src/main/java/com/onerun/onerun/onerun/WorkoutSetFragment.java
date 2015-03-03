package com.onerun.onerun.onerun;

import android.content.Context;
import android.content.Intent;
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

    public static final String CADENCE = "CADENCE";
    private Spinner mIntervalMinute;
    private TextView mBpmTextView;
    private ToggleButton mCadenceToggle;
    private EditText mBpmEditText;

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
        mBpmTextView = (TextView) view.findViewById(R.id.bpm);
        mCadenceToggle = (ToggleButton) view.findViewById(R.id.cadenceSet);
        mIntervalMinute = (Spinner) view.findViewById(R.id.updateIntervalSet);
        mBpmEditText = (EditText) view.findViewById(R.id.bpmSet);

        Integer[] mins = new Integer[]{1,2,3,4,5,6,7,8,9};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_dropdown_item_1line, mins);
        mIntervalMinute.setAdapter(adapter);

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

        Button start = (Button) view.findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getActivity().getApplicationContext(), Running.class);
            if(mCadenceToggle.isChecked()) {
                intent.putExtra(CADENCE, Integer.parseInt(mBpmEditText.getText().toString()));
            }
            getActivity().startActivity(intent);
            }
        });

        mCadenceToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBpmTextView.setVisibility(View.VISIBLE);
                    mBpmEditText.setVisibility(View.VISIBLE);
                    mBpmEditText.setText("60");
                    mBpmEditText.setCursorVisible(false);
                } else {
                    TextView bpmText = (TextView) view.findViewById(R.id.bpm);
                    bpmText.setVisibility(View.GONE);

                    EditText bpmSet = (EditText) view.findViewById(R.id.bpmSet);
                    bpmSet.setVisibility(View.GONE);
                    bpmSet.setCursorVisible(false);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(bpmSet.getWindowToken(), 0);
                }
            }
        });


        return view;
    }

}
