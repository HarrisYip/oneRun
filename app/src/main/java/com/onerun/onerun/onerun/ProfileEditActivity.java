package com.onerun.onerun.onerun;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
    private Button mSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        personDB = new PersonDataSource(getApplicationContext());
        setContentView(R.layout.profile_edit_activity);

        mNameEditText = (EditText) findViewById(R.id.nameTextEdit);
        mAgeEditText = (EditText) findViewById(R.id.ageTextEdit);
        mWeightEditText = (EditText) findViewById(R.id.weightTextEdit);
        mHeightEditText = (EditText) findViewById(R.id.heightTextEdit);
        mSaveButton = (Button) findViewById(R.id.saveButton);


        // get db connection
        personDB = new PersonDataSource(this);

        Person currentPerson;

        try {
            currentPerson = personDB.getPerson(1); // get the first person from DB
            mNameEditText.setText(currentPerson.getName());
            mAgeEditText.setText(String.valueOf(currentPerson.getAge()));
            mWeightEditText.setText(String.valueOf(currentPerson.getWeight()));
            mHeightEditText.setText(String.valueOf(currentPerson.getHeight()));
            mSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    personDB.open();
                    Person editPerson = personDB.getPerson(1); // get the first person from DB

                    editPerson.setName(mNameEditText.getText().toString());
                    editPerson.setAge(Integer.parseInt(mAgeEditText.getText().toString()));
                    editPerson.setWeight(Double.parseDouble(mWeightEditText.getText().toString()));
                    editPerson.setHeight(Double.parseDouble(mHeightEditText.getText().toString()));

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
                    long id = personDB.insertProfile(name, age, weight, height);
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

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        final RelativeLayout rootView = (RelativeLayout) findViewById(R.id.person_edit);
//
//        // get db connection
//        personDB = new PersonDataSource(this);
//        personDB.open();
//        Person currentPerson = personDB.getPerson(1); // get the first person from DB
//        personDB.close();
//
//        // populate fields
//        EditText nameEditText = (EditText) rootView.findViewById(R.id.nameTextEdit);
//        EditText ageEditText = (EditText) rootView.findViewById(R.id.ageTextEdit);
//        EditText weightEditText = (EditText) rootView.findViewById(R.id.weightTextEdit);
//        EditText heightEditText = (EditText) rootView.findViewById(R.id.heightTextEdit);
//        nameEditText.setText(currentPerson.getName());
//        ageEditText.setText(String.valueOf(currentPerson.getAge()));
//        weightEditText.setText(String.valueOf(currentPerson.getWeight()));
//        heightEditText.setText(String.valueOf(currentPerson.getHeight()));
//
//        Button saveButton = (Button) rootView.findViewById(R.id.saveButton);
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // intent = new intent
//                // getactivity.getapplicationcontenx
//                // profileeditactivity.class
//                // getactivity.startActivity(intent)
//                personDB = new PersonDataSource(getApplicationContext());
//                personDB.open();
//                Person editPerson = personDB.getPerson(1); // get the first person from DB
//                EditText nameEditText2 = (EditText) rootView.findViewById(R.id.nameTextEdit);
//                EditText ageEditText2 = (EditText) rootView.findViewById(R.id.ageTextEdit);
//                EditText weightEditText2 = (EditText) rootView.findViewById(R.id.weightTextEdit);
//                EditText heightEditText2 = (EditText) rootView.findViewById(R.id.heightTextEdit);
//
//                editPerson.setName(nameEditText2.getText().toString());
//                editPerson.setAge(Integer.parseInt(ageEditText2.getText().toString()));
//                editPerson.setWeight(Double.parseDouble(weightEditText2.getText().toString()));
//                editPerson.setHeight(Double.parseDouble(heightEditText2.getText().toString()));
//
//                boolean status = personDB.updatePerson(editPerson);
//                personDB.close();
//                if(status) {
//                    ToastMessage.message(getApplicationContext(), "Profile Saved");
//                } else {
//                    ToastMessage.message(getApplicationContext(), "ERROR: Didn't update");
//                }
//            }
//        });
//        return rootView;
//    }
}
