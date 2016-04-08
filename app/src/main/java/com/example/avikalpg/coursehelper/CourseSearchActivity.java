package com.example.avikalpg.coursehelper;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.print.PrintAttributes;
import android.support.annotation.DimenRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CourseSearchActivity extends AppCompatActivity {

    private TableLayout table_results;
    private EditText inp_search_query;
    private TextView txt_msg;
    private Spinner inp_search_dept;
    private Spinner inp_search_fields;
    private Button btn_search_submit;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);

        table_results = (TableLayout) findViewById(R.id.tableSearchResults);
        inp_search_query = (EditText) findViewById(R.id.inpSearchQuery);
        inp_search_dept = (Spinner) findViewById(R.id.inpSearchDept);
        inp_search_fields = (Spinner) findViewById(R.id.inpSearchFields);
        txt_msg = (TextView) findViewById(R.id.txtSearchMsg);
        btn_search_submit = (Button) findViewById(R.id.btnSearchSubmit);
        db = openOrCreateDatabase("coursehelper", MODE_PRIVATE, null);

        if (haveCourseData()){
            setColumns();
            doSearch();

            btn_search_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doSearch();
                }
            });
        }
        else {
            txt_msg.setText("You do not have course data. Please connect to internet once.");
        }
    }

    private boolean haveCourseData(){
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS courses(code VARCHAR PRIMARY KEY,title VARCHAR,instructor VARCHAR,instr_mail VARCHAR,credits INTEGER,credits_distrb VARCHAR,prereq VARCHAR,schedule VARCHAR,dept VARCHAR,instr_notes VARCHAR);");
            Cursor cursor = db.rawQuery("SELECT * FROM courses", null);
            int count = cursor.getCount();
            cursor.close();
            if (count > 0){
                return  true;
            }
        }
        catch (SQLException e){
            Log.e("COURSEHELPER", "unexpected SQL error.",e);
        }
        return  false;
    }

    private void setColumns(){
        try {
            List<String> list = new ArrayList<String>();
            list.add("All");
            Cursor cursor = db.rawQuery("SELECT DISTINCT dept FROM courses", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();
            ArrayAdapter<String> list_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,list);
            inp_search_dept.setAdapter(list_adapter);
        }
        catch (SQLException e){
            Log.e("COURSEHELPER", "unexpected SQL error.",e);
        }
    }

    public void doSearch(){
        if (inp_search_dept.getSelectedItem().toString().equals("All") && TextUtils.isEmpty(inp_search_query.getText().toString()) && table_results.getChildCount() > 0){
            return;
        }
        table_results.removeAllViewsInLayout();
        Boolean flag = false;
        String query = "SELECT code,title,instructor,credits,schedule FROM courses";
        if (!inp_search_dept.getSelectedItem().toString().equals("All")){
            query += " WHERE dept='"+inp_search_dept.getSelectedItem().toString()+"'";
            flag = true;
        }
        if (!TextUtils.isEmpty(inp_search_query.getText().toString())) {
            if (flag){
                query += " AND (";
            }
            else{
                query += " WHERE (";
            }
            String search_query = inp_search_query.getText().toString();
            Log.e("QUERYFIELDS",inp_search_fields.getSelectedItem().toString());
            if (inp_search_fields.getSelectedItem().toString().equals("All")){
                Log.e("ALLFIELDS","Here");
                query += "code LIKE '%"+search_query+"%' OR title LIKE '%"+search_query+"%' OR instructor LIKE '%"+search_query+"%' OR instr_notes LIKE '%"+search_query+"%')";
            }
            else if (inp_search_fields.getSelectedItem().toString().equals("Code")){
                query += "code LIKE '%"+search_query+"%')";
            }
            else if (inp_search_fields.getSelectedItem().toString().equals("Title")){
                query += "title LIKE '%"+search_query+"%')";
            }
            else if (inp_search_fields.getSelectedItem().toString().equals("Instructor")){
                query += "instructor LIKE '%"+search_query+"%')";
            }
            else if (inp_search_fields.getSelectedItem().toString().equals("Instructor Notes")){
                query += "instr_notes LIKE '%"+search_query+"%')";
            }
        }
        query += " LIMIT 50";
        Log.e("QUERY",query);
        try {
            int count = 1;
            Cursor cursor = db.rawQuery(query,null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                count++;

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
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                right_panel.addView(title);

                TextView instructor = new TextView(this);
                instructor.setText("Instructor : " + cursor.getString(2));
                instructor.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                right_panel.addView(instructor);

                TextView schedule = new TextView(this);
                schedule.setText(cursor.getString(4));
                schedule.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                right_panel.addView(schedule);

                row.addView(right_panel);

                table_results.addView(row);
                cursor.moveToNext();
            }
            txt_msg.setText("Total courses : " +cursor.getCount());
        }
        catch (Exception e){
            txt_msg.setText("Some error occurred. Error : "+e.toString());
        }
    }
}
