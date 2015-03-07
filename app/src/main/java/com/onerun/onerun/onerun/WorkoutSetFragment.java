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
    public static final String GHOSTRUN = "GHOSTRUN";
    public static final String PACEMIN = "PACEMIN";
    public static final String PACESEC = "PACESEC";
    public static final String INTERVALMIN = "INTERVALMIN";
    public static final String INTERVALSEC = "INTERVALSEC";
    public static final String EXERCISE = "EXERCISE";
    private Spinner mIntervalMinute;
    private Spinner mIntervalSeconds;
    private Spinner mPaceMinute;
    private Spinner mPaceSeconds;
    private Spinner mExercise;
    private TextView mBpmTextView;
    private ToggleButton mCadenceToggle;
    private ToggleButton mGhostRun;
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
        mGhostRun = (ToggleButton) view.findViewById(R.id.ghostRunSet);
        mIntervalMinute = (Spinner) view.findViewById(R.id.intervalSetMin);
        mIntervalSeconds = (Spinner) view.findViewById(R.id.intervalSetSec);
        mPaceMinute = (Spinner) view.findViewById(R.id.paceSetMin);
        mPaceSeconds = (Spinner) view.findViewById(R.id.paceSetSec);
        mExercise = (Spinner) view.findViewById(R.id.exerciseType);
        mBpmEditText = (EditText) view.findViewById(R.id.bpmSet);

        Button start = (Button) view.findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getActivity().getApplicationContext(), Running.class);
            if(mCadenceToggle.isChecked()) {
                intent.putExtra(CADENCE, Integer.parseInt(mBpmEditText.getText().toString()));
            }
            if(mGhostRun.isChecked()) {
                //TODO: Ghost Run stuff
                //intent.putExtra(GHOSTRUN, "YES");
            }
            intent.putExtra(PACEMIN, Integer.parseInt(mPaceMinute.getSelectedItem().toString()));
            intent.putExtra(PACESEC, Integer.parseInt(mPaceSeconds.getSelectedItem().toString()));
            intent.putExtra(INTERVALMIN, Integer.parseInt(mIntervalMinute.getSelectedItem().toString()));
            intent.putExtra(INTERVALSEC, Integer.parseInt(mIntervalSeconds.getSelectedItem().toString()));
            intent.putExtra(EXERCISE, mExercise.getSelectedItem().toString());
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
