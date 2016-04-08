package com.example.avikalpg.coursehelper;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CourseService extends Service {

    private static final long COURSES_UPDATE_INTERVAL = 10800*1000; // 3 hours
    private Handler mHandler;
    private SQLiteDatabase db;
    private RequestQueue req_queue;


    private String get_courses_url = "http://192.168.0.105:8000/api/courses/";
    private String update_courses_url = "http://192.168.0.105:8000/api/courses/updates";

    public CourseService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate(Bundle bundle) {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler = new Handler();
        db = openOrCreateDatabase("coursehelper", MODE_PRIVATE, null);
        req_queue = Volley.newRequestQueue(this);

        if (!haveCourseData()){
            getCourseList();
        }
        //updateCourseList.run();
        return  START_STICKY;
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

    private void getCourseList(){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, get_courses_url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                JSONArray courses = response.getJSONObject("data").getJSONArray("courses");
                                for (int i=0; i < courses.length();i++){
                                    JSONObject course = courses.getJSONObject(i);
                                    String values = String.format("'%s','%s','%s','%s',%d,'%s','%s','%s','%s','%s'",course.getString("code"),course.getString("title"),course.getString("instructor"),course.getString("instr_mail" ),course.getInt("credits" ),course.getString("credits_distrb"),course.getString("prereq" ),course.getString("schedule").replaceAll("(\\r|\\n)", "").trim(),course.getString("dept"),course.getString("instr_notes"));
                                    try {
                                        db.execSQL("INSERT INTO courses(code,title,instructor,instr_mail,credits,credits_distrb,prereq,schedule,dept,instr_notes) VALUES(" + values + ")");
                                    }
                                    catch (SQLException e){
                                        Log.e("COURSEHELPER", "unexpected SQL exception while inserting course", e);
                                    }
                                }
                            }
                        }
                        catch (JSONException e){
                            Log.e("COURSEHELPER", "unexpected JSON exception", e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("COURSEHELPER", "unexpected request exception. Error : "+error.toString(), error);
                    }
                });
        try {
            req_queue.add(jsObjRequest);
        }
        catch (Exception e) {
            Log.e("COURSEHELPER", "unexpected Request Queue exception", e);
        }
    }

    Runnable updateCourseList = new Runnable() {
        @Override
        public void run() {
            try {
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, update_courses_url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub

                            }
                        });
                req_queue.add(jsObjRequest);
            } finally {
                mHandler.postDelayed(updateCourseList, COURSES_UPDATE_INTERVAL);
            }
        }
    };
}
