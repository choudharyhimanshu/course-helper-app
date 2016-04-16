package com.example.avikalpg.coursehelper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
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
import java.util.Objects;

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
    int offset = 0;

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

            SQLiteDatabase db = activity.openOrCreateDatabase("coursehelper", MODE_PRIVATE, null);

            class Course {
                public String code;
                public String title;
                public String type;
                public int credits;
                public String grade;
            }

            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.fragment_personal_template, container, false);
                TextView txt_msg_stats = (TextView)rootView.findViewById(R.id.txtStatsMsg);

                SharedPreferences shared_pref = activity.getSharedPreferences("DegreeTemplate", MODE_PRIVATE);

                TableLayout table_credits = (TableLayout) rootView.findViewById(R.id.tableCreditsStats);

                TableLayout.LayoutParams row_params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
                TableRow.LayoutParams col_type_params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,0.3f);
                TableRow.LayoutParams col_required_params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,0.35f);
                TableRow.LayoutParams col_completed_params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,0.35f);

                if (shared_pref.contains("dept")){

                    List<Course> Comp = new ArrayList<>();
                    List<Course> DE = new ArrayList<>();
                    List<Course> OE = new ArrayList<>();
                    List<Course> SO = new ArrayList<>();
                    List<Course> HSS1 = new ArrayList<>();
                    List<Course> HSS2 = new ArrayList<>();
                    List<Course> UGP1 = new ArrayList<>();
                    List<Course> UGP2 = new ArrayList<>();
                    List<Course> backlogs = new ArrayList<>();

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

                            if (!course.grade.equals("F") && !course.grade.equals("E")){
                                if (course.type.equals("COMPULSORY")){
                                    if(course.code.matches("[A-Z]SO\\d{3}[A-Z]?")){
                                        course.type = "SO";
                                        SO.add(course);
                                    }
                                    else {
                                        course.type = "COMPULSORY";
                                        Comp.add(course);
                                    }
                                }
                                else if (course.type.contains("HSS")){
                                    if (course.code.matches("[A-Z]+1\\d{2}[A-Z]?")){
                                        course.type = "HSS I";
                                        HSS1.add(course);
                                    }
                                    else {
                                        course.type = "HSS II";
                                        HSS2.add(course);
                                    }

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
                    }
                    catch (Exception e){
                        txt_msg_stats.setText(e.toString());
                    }

                    TableLayout remaining_credits = (TableLayout) rootView.findViewById(R.id.tableRemainCredits);
                    TableRow.LayoutParams remain_type = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,.8f);
                    TableRow.LayoutParams remain_amount = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,.2f);
                    TableLayout courselist = (TableLayout) rootView.findViewById(R.id.tableCourseList);
                    TableLayout.LayoutParams courselist_row_params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
                    TableRow.LayoutParams col_courselist_code = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,0.25f);
                    TableRow.LayoutParams col_courselist_title = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,0.65f);
                    TableRow.LayoutParams col_courselist_credits = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,0.05f);
                    TableRow.LayoutParams col_courselist_grade = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,0.05f);
                    TableRow.LayoutParams col_courselist_heading = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);

                    int i,j,count_baskets=9;

                    String[] headings = {"Compulsory","Department Elective","Open Elective","SO/ESO","HSS-I","HSS-II","UGP-I","UGP-II","Backlogs"};
                    List<Course>[] baskets = new ArrayList[9];
                    int[] credits_sum = new int[9];
                    int[] degree_template = new int[9];

                    baskets[0] = Comp;
                    baskets[1] = DE;
                    baskets[2] = OE;
                    baskets[3] = SO;
                    baskets[4] = HSS1;
                    baskets[5] = HSS2;
                    baskets[6] = UGP1;
                    baskets[7] = UGP2;
                    baskets[8] = backlogs;

                    degree_template[0] = shared_pref.getInt("IC",0) + shared_pref.getInt("DC",0);
                    degree_template[1] = shared_pref.getInt("DE",0);
                    degree_template[2] = shared_pref.getInt("OE",0);
                    degree_template[3] = shared_pref.getInt("SO",0);
                    degree_template[4] = shared_pref.getInt("HSS1",0);
                    degree_template[5] = shared_pref.getInt("HSS2",0);
                    degree_template[6] = shared_pref.getInt("UGP1",0);
                    degree_template[7] = shared_pref.getInt("UGP2",0);

                    for (i=0;i<count_baskets;i++){
                        TableRow courselist_row = new TableRow(activity);
                        courselist_row.setLayoutParams(courselist_row_params);
                        courselist_row.setPadding(0,5,0,5);

                        TextView heading = new TextView(activity);
                        heading.setLayoutParams(col_courselist_heading);
                        heading.setText(headings[i]);
                        heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        heading.setTextColor(Color.parseColor("#2980b9"));
                        heading.setGravity(Gravity.CENTER);
                        courselist_row.addView(heading);

                        courselist.addView(courselist_row);

                        for (j=0; j<baskets[i].size();j++){
                            TableRow courselist_temp_row = new TableRow(activity);
                            courselist_temp_row.setLayoutParams(courselist_row_params);
                            courselist_temp_row.setPadding(0, 5, 0, 5);

                            TextView col_code = new TextView(activity);
                            col_code.setLayoutParams(col_courselist_code);
                            col_code.setText(baskets[i].get(j).code);
                            courselist_temp_row.addView(col_code);

                            TextView col_title = new TextView(activity);
                            col_title.setLayoutParams(col_courselist_title);
                            col_title.setText(baskets[i].get(j).title);
                            courselist_temp_row.addView(col_title);

                            TextView col_credits = new TextView(activity);
                            col_credits.setLayoutParams(col_courselist_credits);
                            col_credits.setGravity(Gravity.CENTER);
                            col_credits.setText("" + baskets[i].get(j).credits);
                            courselist_temp_row.addView(col_credits);

                            TextView col_grade = new TextView(activity);
                            col_grade.setLayoutParams(col_courselist_grade);
                            col_grade.setGravity(Gravity.CENTER);
                            col_grade.setText(baskets[i].get(j).grade);
                            courselist_temp_row.addView(col_grade);

                            courselist.addView(courselist_temp_row);

                            credits_sum[i] += baskets[i].get(j).credits;
                        }
                    }

                    TableRow table_heading = new TableRow(activity);
                    table_heading.setLayoutParams(row_params);
                    table_heading.setPadding(0, 5, 0, 5);

                    TextView heading_type = new TextView(activity);
                    heading_type.setLayoutParams(col_type_params);
                    heading_type.setText("Course Type");
                    heading_type.setTypeface(null, Typeface.BOLD);
                    table_heading.addView(heading_type);
                    TextView heading_req = new TextView(activity);
                    heading_req.setLayoutParams(col_required_params);
                    heading_req.setText("Min Required");
                    heading_req.setGravity(Gravity.CENTER);
                    heading_req.setTypeface(null, Typeface.BOLD);
                    table_heading.addView(heading_req);
                    TextView heading_done = new TextView(activity);
                    heading_done.setLayoutParams(col_completed_params);
                    heading_done.setText("Completed");
                    heading_done.setGravity(Gravity.CENTER);
                    heading_done.setTypeface(null, Typeface.BOLD);
                    table_heading.addView(heading_done);

                    table_credits.addView(table_heading);

                    int total_req=0,total_done=0,total_remains=0;

                    for (i=0; i<count_baskets-1;i++){
                        total_req += degree_template[i];
                        total_done += credits_sum[i];

                        TableRow row = new TableRow(activity);
                        row.setLayoutParams(row_params);
                        row.setPadding(0, 5, 0, 5);

                        TextView col_type = new TextView(activity);
                        col_type.setLayoutParams(col_type_params);
                        col_type.setText(headings[i]);
                        row.addView(col_type);

                        TextView col_req = new TextView(activity);
                        col_req.setLayoutParams(col_required_params);
                        col_req.setText(String.valueOf(degree_template[i]));
                        col_req.setGravity(Gravity.CENTER);
                        row.addView(col_req);

                        TextView col_done = new TextView(activity);
                        col_done.setLayoutParams(col_completed_params);
                        col_done.setText(String.valueOf(credits_sum[i]));
                        col_done.setGravity(Gravity.CENTER);
                        row.addView(col_done);

                        table_credits.addView(row);

                        if (degree_template[i] - credits_sum[i] > 0){
                            total_remains += degree_template[i] - credits_sum[i];

                            TableRow row1 = new TableRow(activity);
                            row1.setLayoutParams(row_params);
                            row1.setPadding(0, 5, 0, 5);

                            TextView rem_type = new TextView(activity);
                            rem_type.setLayoutParams(remain_type);
                            rem_type.setText(headings[i]);
                            row1.addView(rem_type);

                            TextView rem_amount = new TextView(activity);
                            rem_amount.setLayoutParams(remain_amount);
                            rem_amount.setGravity(Gravity.CENTER);
                            rem_amount.setText(String.valueOf(degree_template[i] - credits_sum[i]));
                            row1.addView(rem_amount);

                            remaining_credits.addView(row1);
                        }
                    }

                    TableRow tot_stats = new TableRow(activity);
                    tot_stats.setLayoutParams(row_params);
                    tot_stats.setPadding(0, 5, 0, 5);

                    TextView col_type = new TextView(activity);
                    col_type.setLayoutParams(col_type_params);
                    col_type.setText("Total");
                    tot_stats.addView(col_type);

                    TextView col_req = new TextView(activity);
                    col_req.setLayoutParams(col_required_params);
                    col_req.setText(String.valueOf(total_req));
                    col_req.setGravity(Gravity.CENTER);
                    tot_stats.addView(col_req);

                    TextView col_done = new TextView(activity);
                    col_done.setLayoutParams(col_completed_params);
                    col_done.setText(String.valueOf(total_done));
                    col_done.setGravity(Gravity.CENTER);
                    tot_stats.addView(col_done);

                    table_credits.addView(tot_stats);

                    TableRow tot_remains = new TableRow(activity);
                    tot_remains.setLayoutParams(row_params);
                    tot_remains.setPadding(0, 5, 0, 5);

                    TextView rem_type = new TextView(activity);
                    rem_type.setLayoutParams(remain_type);
                    rem_type.setText("Total");
                    tot_remains.addView(rem_type);

                    TextView rem_amount = new TextView(activity);
                    rem_amount.setLayoutParams(remain_amount);
                    rem_amount.setGravity(Gravity.CENTER);
                    rem_amount.setText(String.valueOf(total_remains));
                    tot_remains.addView(rem_amount);

                    remaining_credits.addView(tot_remains);
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
                TextView txt_msg = (TextView) rootView.findViewById(R.id.textView11);
                TableLayout table_results = (TableLayout) rootView.findViewById(R.id.tableOut);

                // Creating a list of course codes corresponding to the completed courses
                List<String> completed_prereq = new ArrayList<>();
                Cursor cursor = db.rawQuery("SELECT code FROM personal_courses;", null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){
                    completed_prereq.add(cursor.getString(0));
                    cursor.moveToNext();
                }

                cursor = db.rawQuery("SELECT c.code,c.title,c.instructor,c.credits,c.schedule,c.instr_mail,c.prereq,c.instr_notes FROM courses AS c LEFT JOIN personal_courses AS p ON c.code = p.code WHERE p.code IS NULL;", null);
                cursor.moveToFirst();
                int count = cursor.getCount();

                if (count > 0){
                    int print_count = 0;
                    while (!cursor.isAfterLast()){
                        // checking if the pre-requisites are satisfied
                        boolean allowed = true;
                        String temp = "";
                        String[] prereq_array = cursor.getString(6).split("[^A-Z\\d]+");
                        for (String aPrereq_array : prereq_array) {
                            //if prereq_array[i] not in completed_prereq then allowed = false
                            temp += ": :" + aPrereq_array;
                            if ((!completed_prereq.contains(aPrereq_array)) && (!aPrereq_array.equals("")))
                                allowed = false;
                        }
//                        Log.e("CHECK_ERROR", "length: " + prereq_array.length+ "; prereqs: " +temp);
                        if (!allowed) {
                            cursor.moveToNext();
                            continue;
                        }

                        TableRow row = new TableRow(activity);
                        row.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        row.setPadding(0, 20, 0, 20);
                        row.setClickable(true);
                        if (count%2 == 0) {
                            row.setBackgroundColor(Color.parseColor("#ffffff"));
                        }

                        LinearLayout left_panel = new LinearLayout(activity);
                        left_panel.setOrientation(LinearLayout.VERTICAL);
                        left_panel.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f));

                        TextView code = new TextView(activity);
                        code.setText(cursor.getString(0));
                        code.setTextColor(Color.parseColor("#16a085"));
                        code.setGravity(Gravity.CENTER);
                        code.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        left_panel.addView(code);

                        TextView credits = new TextView(activity);
                        credits.setText(cursor.getString(3));
                        credits.setGravity(Gravity.CENTER);
                        credits.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        left_panel.addView(credits);

                        row.addView(left_panel);

                        LinearLayout right_panel = new LinearLayout(activity);
                        right_panel.setOrientation(LinearLayout.VERTICAL);
                        right_panel.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.8f));

                        TextView title = new TextView(activity);
                        title.setText(cursor.getString(1));
                        title.setTextColor(Color.parseColor("#34495e"));
                        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        right_panel.addView(title);

                        TextView instructor = new TextView(activity);
                        instructor.setText("Instructor : " + cursor.getString(2) + "(" + cursor.getString(5) + ")");
                        instructor.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        right_panel.addView(instructor);

                        TextView instr_notes = new TextView(activity);
                        instr_notes.setText("Instructor Notes : " + cursor.getString(7));
                        instr_notes.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        right_panel.addView(instr_notes);

                        TextView prereq = new TextView(activity);
                        prereq.setText("Pre-req : " + cursor.getString(6));
                        prereq.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        right_panel.addView(prereq);

                        TextView schedule = new TextView(activity);
                        schedule.setText(cursor.getString(4));
                        schedule.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        right_panel.addView(schedule);

                        row.addView(right_panel);

                        table_results.addView(row);
                        cursor.moveToNext();

                        print_count++;
                        if (print_count >= 20) break;
                    }
                    txt_msg.setText("Displaying " + print_count + " results");
                }
                else {
                    txt_msg.setText("Somehow you have done ALL courses offered in the next semester!!");
                }
                cursor.close();
            }
            return rootView;
        }
    }

    public void moreResults (View view){
        offset += 20;
        SQLiteDatabase db = this.openOrCreateDatabase("coursehelper", MODE_PRIVATE, null);
        TextView txt_msg = (TextView) findViewById(R.id.textView11);
        TableLayout table_results = (TableLayout) findViewById(R.id.tableOut);

        // Creating a list of course codes corresponding to the completed courses
        List<String> completed_prereq = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT code FROM personal_courses;", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            completed_prereq.add(cursor.getString(0));
            cursor.moveToNext();
        }

        cursor = db.rawQuery("SELECT c.code,c.title,c.instructor,c.credits,c.schedule,c.instr_mail,c.prereq,c.instr_notes FROM courses AS c LEFT JOIN personal_courses AS p ON c.code = p.code WHERE p.code IS NULL;", null);
        cursor.moveToFirst();
        cursor.move(offset);
        int count = cursor.getCount();

        if (count > 0){
            int print_count = 0;
            while (!cursor.isAfterLast()){
                // checking if the pre-requisites are satisfied
                boolean allowed = true;
//                String temp = "";
                String[] prereq_array = cursor.getString(6).split("[^A-Z\\d]+");
                for (String aPrereq_array : prereq_array) {
                    //if prereq_array[i] not in completed_prereq then allowed = false
//                    temp += ": :"+prereq_array[i];
                    if ((!completed_prereq.contains(aPrereq_array)) && (!aPrereq_array.equals("")))
                        allowed = false;
                }
//                        Log.e("CHECK_ERROR", "length: " + prereq_array.length+ "; prereqs: " +temp);
                if (!allowed) {
                    cursor.moveToNext();
                    continue;
                }

                TableRow row = new TableRow(this);
                row.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                row.setPadding(0, 20, 0, 20);
                row.setClickable(true);
                if (count%2 == 0) {
                    row.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                LinearLayout left_panel = new LinearLayout(this);
                left_panel.setOrientation(LinearLayout.VERTICAL);
                left_panel.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f));

                TextView code = new TextView(this);
                code.setText(cursor.getString(0));
                code.setTextColor(Color.parseColor("#16a085"));
                code.setGravity(Gravity.CENTER);
                code.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                left_panel.addView(code);

                TextView credits = new TextView(this);
                credits.setText(cursor.getString(3));
                credits.setGravity(Gravity.CENTER);
                credits.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                left_panel.addView(credits);

                row.addView(left_panel);

                LinearLayout right_panel = new LinearLayout(this);
                right_panel.setOrientation(LinearLayout.VERTICAL);
                right_panel.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.8f));

                TextView title = new TextView(this);
                title.setText(cursor.getString(1));
                title.setTextColor(Color.parseColor("#34495e"));
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                right_panel.addView(title);

                TextView instructor = new TextView(this);
                instructor.setText("Instructor : " + cursor.getString(2) + "(" + cursor.getString(5) + ")");
                instructor.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                right_panel.addView(instructor);

                TextView instr_notes = new TextView(this);
                instr_notes.setText("Instructor Notes : " + cursor.getString(7));
                instr_notes.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                right_panel.addView(instr_notes);

                TextView prereq = new TextView(this);
                prereq.setText("Pre-req : " + cursor.getString(6));
                prereq.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                right_panel.addView(prereq);

                TextView schedule = new TextView(this);
                schedule.setText(cursor.getString(4));
                schedule.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                right_panel.addView(schedule);

                row.addView(right_panel);

                table_results.addView(row);
                cursor.moveToNext();

                print_count++;
                if (print_count >= 20) break;
            }
            txt_msg.setText("Displaying " + (print_count + offset) + " results");
        }
        else {
            txt_msg.setText("Somehow you have done ALL courses offered in the next semester!!");
        }
        cursor.close();
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
