package com.example.avikalpg.coursehelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button btn_submit;
    private EditText inp_rollno, inp_password;
    private TextView txt_message;
    ProgressDialog pDialog;
    RequestQueue req_queue;
    private SQLiteDatabase db;

    String login_url = "http://oars.cc.iitk.ac.in:6060/login.asp";
    String info_url = "http://oars.cc.iitk.ac.in:6060/Student/Default.asp?menu=91";
    String transcript_url = "http://oars.cc.iitk.ac.in:6060/Student/Transcript.asp";
    String currentsem_url = "http://oars.cc.iitk.ac.in:6060/Student/Afteradd_dropStatus.asp";
    String degree_template_url = "http://52.25.208.96/api/degree-template/";
    String send_courses_url = "http://52.25.208.96/api/update-user-courses/";
    String cookie = "";
    String serverCookie = "";
    String csrftoken = "";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inp_rollno = (EditText) findViewById(R.id.inpLoginRoll);
        inp_password = (EditText) findViewById(R.id.inpLoginPass);
        btn_submit = (Button) findViewById(R.id.btnLoginSubmit);
        txt_message = (TextView) findViewById(R.id.txtLoginMsg);
        db = openOrCreateDatabase("coursehelper", MODE_PRIVATE, null);

        pDialog = new ProgressDialog(this);
        req_queue = Volley.newRequestQueue(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        txt_message.setText("DISCLAIMER: OARS Login does not work between 12 midnight and 6 A.M. IST");
    }

    private boolean createTablePersonalCourses() {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS personal_courses(code VARCHAR PRIMARY KEY,type VARCHAR,title VARCHAR,credits INTEGER,grade VARCHAR);");
            Cursor cursor = db.rawQuery("SELECT * FROM personal_courses", null);
            int count = cursor.getCount();
            cursor.close();
            if (count > 0) {
                return true;
            }
        } catch (SQLException e) {
            Log.e("COURSEHELPER", "unexpected SQL error.", e);
        }
        return false;
    }

    private void getUserInfo() {
        StringRequest infoRequest = new StringRequest(Request.Method.GET, info_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains(inp_rollno.getText().toString())) {
                            Document doc = Jsoup.parse(response);
                            String name = doc.getElementsByTag("h3").first().child(0).text();
                            name = name.substring(name.indexOf('.') + 2, name.indexOf("--"));
                            String roll_no = doc.getElementsByTag("tr").get(0).child(1).text();
                            String prog = doc.getElementsByTag("tr").get(0).child(3).text();
                            String dept = doc.getElementsByTag("tr").get(1).child(1).text();
                            String username = doc.getElementsByAttributeValue("name", "EMAIL").val();
                            username = username.substring(0, username.indexOf('@'));

                            SharedPreferences shared_pref = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared_pref.edit();
                            editor.putString("rollno", roll_no);
                            editor.putString("pswd", inp_password.getText().toString());
                            editor.putString("name", name);
                            editor.putString("prog", prog);
                            editor.putString("dept", dept);
                            editor.putString("uname", username);
                            editor.commit();

                            getDegreeTemplate(dept);

//                            pDialog.hide();
                        } else {
                            txt_message.setText("Login Failed. Some error occurred.");
                            pDialog.hide();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txt_message.setText("Login Failed. Some error occurred. Error : " + error.toString());
                        pDialog.hide();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Cookie", cookie);
                params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko");
                params.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                return params;
            }
        };
        req_queue.add(infoRequest);
    }

    private void getDegreeTemplate(String dept) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, degree_template_url + dept, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray courses = response.getJSONObject("data").getJSONArray("template");
                                for (int i = 0; i < courses.length(); i++) {
                                    JSONObject course = courses.getJSONObject(i);

                                    SharedPreferences shared_pref = getSharedPreferences("DegreeTemplate", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = shared_pref.edit();
                                    editor.putString("dept", course.getString("dept"));
                                    editor.putString("dept_name", course.getString("dept_name"));
                                    editor.putInt("IC", course.getInt("IC"));
                                    editor.putInt("DC", course.getInt("DC"));
                                    editor.putInt("UGP1", course.getInt("UGP1"));
                                    editor.putInt("UGP2", course.getInt("UGP2"));
                                    editor.putInt("DE", course.getInt("DE"));
                                    editor.putInt("OE", course.getInt("OE"));
                                    editor.putInt("SO", course.getInt("SO"));
                                    editor.putInt("HSS1", course.getInt("HSS1"));
                                    editor.putInt("HSS2", course.getInt("HSS2"));
                                    editor.putInt("total", course.getInt("total"));
                                    editor.commit();
                                }
                            } else {
                                Log.e("GETTEMPLATE", "Response unsuccessful.");
                            }
                        } catch (JSONException e) {
                            Log.e("COURSEHELPER", "unexpected JSON exception", e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("COURSEHELPER", "unexpected request exception. Error : " + error.toString(), error);
                    }
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                serverCookie = response.headers.get("Set-Cookie");
//                serverCookie = serverCookie.replaceFirst("csrftoken","csrfmiddlewaretoken");
                String[] cookieArray = serverCookie.split(";");
                for (String aCookieArray : cookieArray) {
                    if (aCookieArray.contains("csrftoken")) {
                        csrftoken = aCookieArray.split("=")[1];
                    }
                }
                return super.parseNetworkResponse(response);
            }
        };
        try {
            req_queue.add(jsObjRequest);
        } catch (Exception e) {
            Log.e("COURSEHELPER", "unexpected Request Queue exception", e);
        }
    }

    private void getTranscript() {
        StringRequest transcriptRequest = new StringRequest(Request.Method.GET, transcript_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains(inp_rollno.getText().toString())) {
                            Document doc = Jsoup.parse(response);
                            Elements all_rows = doc.getElementsByTag("tr");
                            createTablePersonalCourses();
                            for (int position = 0; position < all_rows.size(); position += 1) {
                                String row_id = all_rows.get(position).child(0).text();
                                if (row_id.matches("[A-Z]+\\d{3}[A-Z]*")) {
                                    Element row = all_rows.get(position);
                                    String values = "";
                                    for (int child_no = 0; child_no < 5; child_no++) {
                                        // TODO: Change the above hard-coded value (5) to variable
                                        values += "'" + row.child(child_no).text() + "',";
                                    }
                                    try {
                                        values = values.substring(0, values.length() - 1);
                                        Cursor cursor = db.rawQuery("SELECT * FROM personal_courses WHERE code = '" + row_id + "'", null);
                                        int count = cursor.getCount();
                                        cursor.close();
                                        if (count > 0) {
                                            db.execSQL("UPDATE personal_courses SET grade='" + row.child(4).text() + "' WHERE code='" + row_id + "'");
                                        } else {
                                            db.execSQL("INSERT INTO personal_courses(code,type,title,credits,grade) VALUES(" + values + ")");
                                        }
                                    } catch (SQLException e) {
                                        Log.e("COURSEHELPER", "unexpected SQL exception while inserting course", e);
                                    }
                                }
                            }
//                            pDialog.hide();
                        } else {
                            txt_message.setText("Transcript Download Failed. Some error occurred.");
                            pDialog.hide();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txt_message.setText("Downloading Transcript Failed. Some error occurred. Error : " + error.toString());
                        pDialog.hide();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Cookie", cookie);
                params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko");
                params.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                return params;
            }
        };
        StringRequest currentsemRequest = new StringRequest(Request.Method.GET, currentsem_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains(inp_rollno.getText().toString())) {
                            Document doc = Jsoup.parse(response);
                            Elements all_rows = doc.getElementsByTag("tr");
                            for (int position = 0; position < all_rows.size(); position += 1) {
                                String row_id = all_rows.get(position).child(0).text();
                                if (row_id.matches("[A-Z]+\\d{3}[A-Z]*")) {
                                    Element row = all_rows.get(position);
                                    String values = "";
                                    for (int child_no = 0; child_no < 7; child_no++) {
                                        // TODO: Change the above hard-coded value (7) to variable
                                        if ((child_no < 3) || (child_no == 6))
                                            values += "'" + row.child(child_no).text() + "',";
                                    }
                                    try {
                                        values = values + "'I'";
                                        Cursor cursor = db.rawQuery("SELECT * FROM personal_courses WHERE code = '" + row_id + "'", null);
                                        int count = cursor.getCount();
                                        cursor.close();
                                        if (count > 0) {
                                            db.execSQL("UPDATE personal_courses SET grade='I' WHERE code='" + row_id + "'");
                                        } else {
                                            db.execSQL("INSERT INTO personal_courses(code,title,credits,type,grade) VALUES(" + values + ")");
                                        }
                                    } catch (SQLException e) {
                                        Log.e("COURSEHELPER", "unexpected SQL exception while inserting course", e);
                                    }

                                    // sending the list of all courses to server
                                    try {
                                        sendCoursesToServer();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            pDialog.hide();
                        } else {
                            txt_message.setText("Current Sem Courses' Download Failed. Some error occurred.");
                            pDialog.hide();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txt_message.setText("Downloading Current Sem Courses Failed. Some error occurred. Error : " + error.toString());
                        pDialog.hide();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Cookie", cookie);
                params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko");
                params.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                return params;
            }
        };
        req_queue.add(transcriptRequest);
        req_queue.add(currentsemRequest);
        gotoPersonalTemplate();
    }

    private boolean sendCoursesToServer() throws JSONException {
        final SharedPreferences shared_pref = getSharedPreferences("UserData", MODE_PRIVATE);
        // sending the HTTP request
        StringRequest sendCoursesRequest = new StringRequest(Request.Method.POST, send_courses_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        txt_message.setText(response);
                        pDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txt_message.setText("Sending User Courses Failed. Some error occurred. Error : " + error.toString()+"\n" + serverCookie+"\n"+csrftoken);
                        pDialog.hide();
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Cookie", serverCookie);
                params.put("X-CSRFToken", csrftoken);
                params.put("Referer", degree_template_url);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                // Creating a list of course codes corresponding to the completed courses
                List<String> completedCourses = new ArrayList<>();
                Cursor cursor = db.rawQuery("SELECT code FROM personal_courses;", null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    completedCourses.add(cursor.getString(0));
                    cursor.moveToNext();
                }
                cursor.close();
                JSONArray cCourses = new JSONArray(completedCourses);

                Map<String, String> params = new HashMap<>();
                params.put("roll_no", shared_pref.getString("rollno", "NoRollNo"));
                params.put("dept", shared_pref.getString("dept", "NoDept"));
                params.put("courses", cCourses.toString());
                return params;
            }
        };
        req_queue.add(sendCoursesRequest);
        return true;
    }

    public void submitLogin(View view) {
        if (!TextUtils.isEmpty(inp_rollno.getText().toString()) && !TextUtils.isEmpty(inp_password.getText().toString())) {
            pDialog.setMessage("Loading...");
            pDialog.show();

            StringRequest cookieRequest = new StringRequest(Request.Method.GET, login_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (!TextUtils.isEmpty(cookie)) {
                                StringRequest loginRequest = new StringRequest(Request.Method.POST, login_url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                if (!response.contains("INVALID USERNAME/PASSWORD")) {
                                                    txt_message.setText("Login Successful.");
                                                    pDialog.setMessage("Login Successful. Fetching data ..");
                                                    getUserInfo();
                                                    getTranscript();
                                                } else {
                                                    txt_message.setText("Invalid Credentials.");
                                                    pDialog.hide();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                txt_message.setText("Login Failed. Some error occurred. Error : " + error.toString());
                                                pDialog.hide();
                                            }
                                        }
                                ) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("LoginID", inp_rollno.getText().toString());
                                        params.put("Password", inp_password.getText().toString());
                                        params.put("loginForm", "Login");
                                        return params;
                                    }

                                    @Override
                                    public Map<String, String> getHeaders() {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("Cookie", cookie);
                                        params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko");
                                        params.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                                        return params;
                                    }
                                };
                                req_queue.add(loginRequest);
                            } else {
                                txt_message.setText("Some error occurred. Please try again.");
                                pDialog.hide();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    txt_message.setText("Some error occurred. Error : " + error.toString());
                    pDialog.hide();
                }
            }) {
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    cookie = response.headers.get("Set-Cookie");
                    return super.parseNetworkResponse(response);
                }
            };
            req_queue.add(cookieRequest);
        } else {
            txt_message.setText("Please enter something.");
        }
    }

    private void gotoPersonalTemplate() {
        SharedPreferences shared_pref = getSharedPreferences("UserData", MODE_PRIVATE);
        if (shared_pref.contains("rollno")) {
            Intent myIntent = new Intent(this, PersonalTemplate.class);
            this.startActivity(myIntent);
        } else {
            Intent myIntent = new Intent(this, MainActivity.class);
            this.startActivity(myIntent);
        }
    }
}
