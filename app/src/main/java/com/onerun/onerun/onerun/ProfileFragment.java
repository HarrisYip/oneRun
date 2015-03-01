package com.onerun.onerun.onerun;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onerun.onerun.onerun.Model.Person;
import com.onerun.onerun.onerun.Model.PersonDataSource;

/**
 * Created by Jason on 15-02-28.
 */
public class ProfileFragment extends Fragment {
    PersonDataSource personDB;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        // get db connection
        personDB = new PersonDataSource(getActivity());
        personDB.open();
        Person currentPerson;
        try {
            currentPerson = personDB.getPerson(1); // get the first person from DB
        } catch(Exception e) {
            // none in the database (CREATE FAKE USER)
            personDB.insertProfile("John Smith", 20, 76.9, 185.3);
            currentPerson = personDB.getPerson(1);
        }
        personDB.close();

        // populate fields
        TextView nameTextView = (TextView) rootView.findViewById(R.id.personName);
        TextView ageTextView = (TextView) rootView.findViewById(R.id.personAge);
        TextView weightTextView = (TextView) rootView.findViewById(R.id.personWeight);
        TextView heightTextView = (TextView) rootView.findViewById(R.id.personHeight);
        nameTextView.setText(currentPerson.getName());
        ageTextView.setText("is currently " + currentPerson.getAge() + " years old");
        weightTextView.setText("weighs " + currentPerson.getWeight() + " kg");
        heightTextView.setText("and " + currentPerson.getHeight() + " cm tall.");

        super.onResume();
    }

}
