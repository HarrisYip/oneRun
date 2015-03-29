package com.onerun.onerun.onerun;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.onerun.onerun.onerun.Model.Person;
import com.onerun.onerun.onerun.Model.PersonDataSource;

/**
 * Created by Jason on 15-02-28.
 */
public class ProfileEditActivity extends Activity {
    PersonDataSource personDB;
    private EditText mNameEditText;
    private EditText mAgeEditText;
    private EditText mWeightEditText;
    private EditText mHeightEditText;
    private ToggleButton mRunPassToggle;
    private Button mSaveButton;
    //private boolean runPassBool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit_activity);

        mNameEditText = (EditText) findViewById(R.id.nameTextEdit);
        mAgeEditText = (EditText) findViewById(R.id.ageTextEdit);
        mWeightEditText = (EditText) findViewById(R.id.weightTextEdit);
        mHeightEditText = (EditText) findViewById(R.id.heightTextEdit);
        mRunPassToggle = (ToggleButton) findViewById(R.id.runPassToggle);
        mSaveButton = (Button) findViewById(R.id.saveButton);

        personDB = new PersonDataSource(getApplicationContext());
        personDB.open();

        Person currentPerson;

        try {
            currentPerson = personDB.getPerson(1); // get the first person from DB
            mNameEditText.setText(currentPerson.getName());
            mAgeEditText.setText(String.valueOf(currentPerson.getAge()));
            mWeightEditText.setText(String.valueOf(currentPerson.getWeight()));
            mHeightEditText.setText(String.valueOf(currentPerson.getHeight()));
            mRunPassToggle.setChecked(currentPerson.getRunPass());

//            mRunPassToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked) {
//                        runPassBool = true;
//                    } else {
//                        runPassBool = false;
//                    }
//                }
//            });

            mSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    personDB.open();
                    Person editPerson = personDB.getPerson(1); // get the first person from DB

                    editPerson.setName(mNameEditText.getText().toString());
                    editPerson.setAge(Integer.parseInt(mAgeEditText.getText().toString()));
                    editPerson.setWeight(Double.parseDouble(mWeightEditText.getText().toString()));
                    editPerson.setHeight(Double.parseDouble(mHeightEditText.getText().toString()));
                    editPerson.setRunPass(mRunPassToggle.isChecked());

                    boolean status = personDB.updatePerson(editPerson);
                    personDB.close();
                    if (status) {
                        ToastMessage.message(getApplicationContext(), "Profile Saved");
                    } else {
                        ToastMessage.message(getApplicationContext(), "ERROR: Didn't update");
                    }
                    onBackPressed();
                }
            });
        } catch (Exception e) {
            mSaveButton.setText("Create new account");
            mSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    personDB.open();
                    String name = mNameEditText.getText().toString();
                    int age = Integer.parseInt(mAgeEditText.getText().toString());
                    double weight = Double.parseDouble(mWeightEditText.getText().toString());
                    double height = Double.parseDouble(mHeightEditText.getText().toString());
                    boolean runpassboolean = mRunPassToggle.isChecked();
                    long id = personDB.insertProfile("-1",name, age, weight, height, runpassboolean);
                    personDB.close();
                    if (id >= 0) {
                        ToastMessage.message(getApplicationContext(), "Profile Created");
                    } else {
                        ToastMessage.message(getApplicationContext(), "ERROR: Didn't Create");
                    }
                    onBackPressed();
                }
            });
        }
        personDB.close();
    }
}
