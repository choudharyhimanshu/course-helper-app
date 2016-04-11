package com.example.avikalpg.coursehelper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PersonalTemplate extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_template);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_personal_template, menu);
        return true;
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

        public PlaceholderFragment() {
        }

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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            Activity activity = getActivity();

            SQLiteDatabase db = activity.openOrCreateDatabase("personal_courses", MODE_PRIVATE, null);


            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                rootView = inflater.inflate(R.layout.fragment_personal_template, container, false);
                TextView txt_msg_stats = (TextView)rootView.findViewById(R.id.txtStatsMsg);

                SharedPreferences shared_pref = activity.getSharedPreferences("DegreeTemplate", MODE_PRIVATE);

                TableLayout table_credits = (TableLayout) rootView.findViewById(R.id.tableCreditsStats);

                TableLayout.LayoutParams row_params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
                TableRow.LayoutParams col_type_params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,0.6f);
                TableRow.LayoutParams col_required_params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,0.2f);
                TableRow.LayoutParams col_completed_params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,0.2f);


                if (shared_pref.contains("dept")){

                    class Course {
                        public String code;
                        public String title;
                        public String type;
                        public int credits;
                        public String grade;
                    }

                    List<Course> IC = new ArrayList<Course>();
                    List<Course> DC = new ArrayList<Course>();
                    List<Course> DE = new ArrayList<Course>();
                    List<Course> OE = new ArrayList<Course>();
                    List<Course> SO = new ArrayList<Course>();
                    List<Course> HSS1 = new ArrayList<Course>();
                    List<Course> HSS2 = new ArrayList<Course>();
                    List<Course> UGP1 = new ArrayList<Course>();
                    List<Course> UGP2 = new ArrayList<Course>();
                    List<Course> backlogs = new ArrayList<Course>();
                    int total = 0;

                    String query = "SELECT code,title,type,credits,grade FROM personal_courses";

                    try{
                        Cursor cursor = db.rawQuery(query,null);
                        cursor.moveToFirst();
                        while (!cursor.isAfterLast()){

                            Course course = new Course();
                            course.code = cursor.getString(0);
                            course.title = cursor.getString(1);
                            course.type = cursor.getString(2);
                            course.credits = cursor.getInt(3);
                            course.grade = cursor.getString(4);

                            if (!course.grade.equals("F")){
                                if (course.type.equals("COMPULSORY")){
                                    course.type = "COMPULSORY";
                                    IC.add(course);
                                }
                                else if (course.type.equals("HSS") || course.type.equals("HSS I")){
                                    course.type = "HSS I";
                                    HSS1.add(course);
                                }
                                else if (course.type.equals("HSS II")){
                                    course.type = "HSS II";
                                    HSS2.add(course);
                                }
                                else if (course.type.contains("SO")){
                                    course.type = "SO";
                                    SO.add(course);
                                }
                                else if (course.type.contains("DE")){
                                    course.type = "DE";
                                    DE.add(course);
                                }
                                else if (course.type.contains("OE") || course.type.equals("ELECTIVE")){
                                    course.type = "OE";
                                    OE.add(course);
                                }
                                else if (course.type.contains("UGP")){
                                    course.type = "UGP";
                                    UGP1.add(course);
                                }
                                else {
                                    Log.e("TYPENOTFOUND",course.code+','+course.type);
                                    txt_msg_stats.setText("There is some error.");
                                }
                            }
                            else {
                                backlogs.add(course);
                            }

                            cursor.moveToNext();
                        }
                        cursor.close();

                        LinearLayout courselist = (LinearLayout) rootView.findViewById(R.id.layoutCourseList);
                        LinearLayout.LayoutParams courselist_item_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f);

                        int i,j,total_credits=0,count_baskets=10;

                        String[] headings = {"Institute Core Courses","Department Compulsory","Department Elective Courses","Open Elective Courses","SO/ESO Courses","HSS-I Courses","HSS-II Courses","UGP-I Courses","UGP-II Courses","Backlog Courses"};
                        List<Course>[] baskets = new ArrayList[10];

                        baskets[0] = IC;
                        baskets[1] = DC;
                        baskets[2] = DE;
                        baskets[3] = OE;
                        baskets[4] = SO;
                        baskets[5] = HSS1;
                        baskets[6] = HSS2;
                        baskets[7] = UGP1;
                        baskets[8] = UGP2;
                        baskets[9] = backlogs;

                        for (i=0;i<count_baskets;i++){
                            TextView heading = new TextView(activity);
                            heading.setLayoutParams(courselist_item_params);
                            heading.setText(headings[i]);
                            heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            heading.setTextColor(Color.parseColor("#2980b9"));
                            heading.setGravity(Gravity.CENTER);

                            courselist.addView(heading);
                            for (j=0; j<baskets[i].size();j++){
                                TextView temp = new TextView(activity);
                                temp.setLayoutParams(courselist_item_params);
                                temp.setPadding(0,5,0,5);
                                temp.setText(baskets[i].get(j).code + " - " + baskets[i].get(j).title + ' ' + baskets[i].get(j).credits + ' ' + baskets[i].get(j).grade);
                                courselist.addView(temp);
                                total_credits += baskets[i].get(j).credits;
                            }
                        }
                    }
                    catch (Exception e){
                        txt_msg_stats.setText(e.toString());
                    }
                }
                else {
                    TableRow row = new TableRow(activity);
                    row.setLayoutParams(row_params);
                    row.setPadding(5, 10, 5, 10);
                    row.setGravity(Gravity.CENTER);

                    TextView msg = new TextView(activity);
                    msg.setText("Data not available. Try logging in again.");
                }
            }
            else{
                rootView = inflater.inflate(R.layout.fragment_course_options, container, false);
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Statistics";
                case 1:
                    return "Course Options";
            }
            return null;
        }
    }
}
