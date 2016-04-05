package com.example.avikalpg.coursehelper;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CourseSearchActivity extends AppCompatActivity {

    private TableLayout table_results;
    private EditText inp_search_query;
    private TextView txt_msg;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);

        table_results = (TableLayout) findViewById(R.id.tableSearchResults);
        inp_search_query = (EditText) findViewById(R.id.inpSearchQuery);
        txt_msg = (TextView) findViewById(R.id.txtSearchMsg);
        db = openOrCreateDatabase("coursehelper", MODE_PRIVATE, null);

        doSearch();
    }

    public void doSearch(){
        if (TextUtils.isEmpty(inp_search_query.getText().toString())){
            try {
                Cursor cursor = db.rawQuery("SELECT code,title,instructor,schedule FROM courses",null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){
                    TableRow row = new TableRow(this);

                    TextView code = new TextView(this);
                    code.setText(cursor.getString(0));
                    row.addView(code);

                    TextView title = new TextView(this);
                    title.setText(cursor.getString(1));
                    row.addView(title);

                    TextView instructor = new TextView(this);
                    instructor.setText(cursor.getString(2));
                    row.addView(instructor);

                    TextView schedule = new TextView(this);
                    schedule.setText(cursor.getString(3));
                    row.addView(schedule);

                    table_results.addView(row);
                    cursor.moveToNext();
                }
                txt_msg.setText("Total courses : "+cursor.getCount());
            }
            catch (SQLException e){
                txt_msg.setText("Some error occurred. Error : "+e.toString());
            }
        } else {
            txt_msg.setText("You searched for : "+inp_search_query.getText().toString());
        }
    }
}
