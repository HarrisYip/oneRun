package com.onerun.onerun.onerun;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.inputmethod.InputMethodManager;

import android.widget.EditText;

import com.onerun.onerun.onerun.Model.MapDataSource;
import com.onerun.onerun.onerun.Model.PersonDataSource;
import com.onerun.onerun.onerun.Model.RunDataSource;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    // all datasource connections
    MapDataSource mapDataSource;
    PersonDataSource personDataSource;
    RunDataSource runDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();



        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // testing db
//        ToastMessage.message(this, "setting data db");
//        personDataSource = new PersonDataSource(this);
//        personDataSource.open();
//        long row = personDataSource.insertProfile("Jason Park", 24, 65, 171);
//        Person jason = personDataSource.getPerson((int)row);
//        ToastMessage.message(this, "Who? " + jason.getName());
//        ToastMessage.message(this, "Age? " + jason.getAge());
//        ToastMessage.message(this, "Weight? " + jason.getWeight());
//        ToastMessage.message(this, "Height? " + jason.getHeight());
//        personDataSource.close();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        //InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container,     WorkoutSetFragment.newInstance(position + 1))
                        .commit();
                break;
            case 1:

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new MapsFragment())
                        .commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new ProfileFragment())
                        .commit();
                break;
        }
    }

    @Override
    public void onActionBarItemSelected(int position) {
        switch(position) {
            case (R.id.edit_profile):
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.container, new ProfileEditActivity())
//                        .commit();

                // intent = new intent
                // getactivity.getapplicationcontenx
                // profileeditactivity.class
                // getactivity.startActivity(intent)
                Intent intent = new Intent(this, ProfileEditActivity.class);
                startActivity(intent);
        }
    }

    public void onSectionAttached(int number) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                //fragmentManager.beginTransaction()
                      //  .replace(R.id.container, PlaceholderFragment.newInstance(number))
                     //   .commit();
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                //fragmentManager.beginTransaction()
                       // .replace(R.id.container, MapsFragment.newInstance())
                       // .commit();
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
               // fragmentManager.beginTransaction()
                      //  .replace(R.id.container, PlaceholderFragment.newInstance(number))
                      //  .commit();
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(this.getCurrentFocus() != null && this.getCurrentFocus() instanceof EditText){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void speakText(View v) {
        EditText myText = (EditText) v.findViewById(R.id.enter);
        String myStr = myText.getText().toString();
        //SpeechFragment frag = (SpeechFragment) getFragmentManager().getFragment(Bundle)
        //speak(myStr);
    }

}
