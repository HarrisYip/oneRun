package com.onerun.onerun.onerun;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onerun.onerun.onerun.Model.Person;
import com.onerun.onerun.onerun.Model.PersonDataSource;
import com.onerun.onerun.onerun.Model.RunDataSource;

/**
 * Created by Jason on 15-02-28.
 */
public class ProfileFragment extends Fragment {
    PersonDataSource personDB;
    View rootView;
    private TextView mNameTextView;
    private TextView mAgeTextView;
    private TextView mWeightTextView;
    private TextView mHeightTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        personDB = new PersonDataSource(getActivity());
        personDB.open();
        int personids[] = personDB.getAllPeople();

        for(int x = 1; x < personids.length; x++){
            TextView test = new TextView(getActivity());
            test.setText(personDB.getPerson(personids[x]).getName());
            test.setBackgroundResource(R.drawable.layer_card_background);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            float d = getActivity().getResources().getDisplayMetrics().density;

            params.setMargins((int)(15*d),(int)(5*d),(int)(15*d),0);
            test.setPadding((int)(15*d),(int)(15*d),(int)(15*d),(int)(15*d));
            test.setLayoutParams(params);

            LinearLayout linearLayout = (LinearLayout)rootView.findViewById(R.id.friendslist);
            linearLayout.addView(test);
        }

        personDB.close();
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
            mNameTextView = (TextView) rootView.findViewById(R.id.personName);
            mAgeTextView = (TextView) rootView.findViewById(R.id.personAge);
            mWeightTextView = (TextView) rootView.findViewById(R.id.personWeight);
            mHeightTextView = (TextView) rootView.findViewById(R.id.personHeight);
            mNameTextView.setText(currentPerson.getName());
            mAgeTextView.setText("is currently " + currentPerson.getAge() + " years old");
            mWeightTextView.setText("weighs " + currentPerson.getWeight() + " kg");
            mHeightTextView.setText("and " + currentPerson.getHeight() + " cm tall.");
        } catch(Exception e) {
            // none in the database (CREATE FAKE USER)
            Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
            startActivity(intent);
        }
        personDB.close();

        // populate fields

        super.onResume();
    }

}
