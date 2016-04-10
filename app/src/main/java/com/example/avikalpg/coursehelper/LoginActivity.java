package com.example.avikalpg.coursehelper;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
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
    String cookie = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inp_rollno = (EditText) findViewById(R.id.inpLoginRoll);
        inp_password = (EditText) findViewById(R.id.inpLoginPass);
        btn_submit = (Button) findViewById(R.id.btnLoginSubmit);
        txt_message = (TextView) findViewById(R.id.txtLoginMsg);
        db = openOrCreateDatabase("personal_courses", MODE_PRIVATE, null);

        pDialog = new ProgressDialog(this);
        req_queue = Volley.newRequestQueue(this);
    }

    private boolean createTablePersonalCourses(){
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS personal_courses(code VARCHAR PRIMARY KEY,type VARCHAR,title VARCHAR,credits INTEGER,grade VARCHAR);");
            Cursor cursor = db.rawQuery("SELECT * FROM personal_courses", null);
            int count = cursor.getCount();
            cursor.close();
            if (count > 0){
                return  true;
            }
        }
        catch (SQLException e){
            Log.e("COURSEHELPER", "unexpected SQL error.", e);
        }
        return  false;
    }

    private void getUserInfo() {
        StringRequest infoRequest = new StringRequest(Request.Method.GET, info_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains(inp_rollno.getText().toString())){
                            org.jsoup.nodes.Document doc = Jsoup.parse(response);
                            String name = doc.getElementsByTag("h3").first().child(0).text();
                            name = name.substring(name.indexOf('.')+2,name.indexOf("--"));
                            String roll_no = doc.getElementsByTag("tr").get(0).child(1).text();
                            String prog = doc.getElementsByTag("tr").get(0).child(3).text();
                            String dept = doc.getElementsByTag("tr").get(1).child(1).text();
                            String username = doc.getElementsByAttributeValue("name","EMAIL").val();
                            username = username.substring(0,username.indexOf('@'));

                            SharedPreferences shared_pref = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared_pref.edit();
                            editor.putString("rollno", roll_no);
                            editor.putString("pswd",inp_password.getText().toString());
                            editor.putString("name",name);
                            editor.putString("prog",prog);
                            editor.putString("dept",dept);
                            editor.putString("uname",username);
                            editor.commit();

                            pDialog.hide();
                        }
                        else {
                            txt_message.setText("Login Failed. Some error occurred.");
                            pDialog.hide();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txt_message.setText("Login Failed. Some error occurred. Error : "+error.toString());
                        pDialog.hide();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Cookie",cookie);
                params.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko");
                params.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                return params;
            }
        };
        req_queue.add(infoRequest);
    }

    /*
     * This function is not really required. Delete it at will
     */
    private String checkPersonalCoursesTable(){
        String ret = "";
        Cursor cursor = db.rawQuery("SELECT * FROM personal_courses", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            for (int x = 0; x < 5; x++){
                // TODO: Replace the hard coded value 5 with variable
                ret += cursor.getString(x)+" ";
            }
            ret += "\n";
            cursor.moveToNext();
        }
        cursor.close();
        return ret;
    }

    private void getTranscript() {
        StringRequest transcriptRequest = new StringRequest(Request.Method.GET, transcript_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains(inp_rollno.getText().toString())){
                            org.jsoup.nodes.Document doc = Jsoup.parse(response);
                            Elements all_rows = doc.getElementsByTag("tr");
                            createTablePersonalCourses();
                            for (int position = 0; position < all_rows.size(); position += 1){
                                String row_id = all_rows.get(position).child(0).text();
                                if (row_id.matches("[A-Z]+\\d{3}[A-Z]*")) {
                                    Element row = all_rows.get(position);
                                    String values = "";
                                    for (int child_no = 0; child_no < 5; child_no++){
                                        // TODO: Change the above hard-coded value (5) to variable
                                        values +=  "'" + row.child(child_no).text() + "',";
                                    }
                                    try {
                                        values = values.substring(0, values.length()-1);
                                        Cursor cursor = db.rawQuery("SELECT * FROM personal_courses WHERE code = '"+row_id+"'", null);
                                        int count = cursor.getCount();
                                        cursor.close();
                                        if (count > 0){
                                            db.execSQL("UPDATE personal_courses SET grade='" + row.child(4).text() + "' WHERE code='"+row_id+"'");
                                        }
                                        else {
                                            db.execSQL("INSERT INTO personal_courses(code,type,title,credits,grade) VALUES(" + values + ")");
                                        }
                                    }
                                    catch (SQLException e){
                                        Log.e("COURSEHELPER", "unexpected SQL exception while inserting course", e);
                                    }
                                }
                            }
                            txt_message.setText(checkPersonalCoursesTable());

//                            SharedPreferences shared_pref = getSharedPreferences("Transcript", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = shared_pref.edit();
//                            editor.putString("rollno", roll_no);
//                            editor.commit();

                            pDialog.hide();
                        }
                        else {
                            txt_message.setText("Transcript Download Failed. Some error occurred.");
                            pDialog.hide();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txt_message.setText("Downloading Transcript Failed. Some error occurred. Error : "+error.toString());
                        pDialog.hide();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Cookie",cookie);
                params.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko");
                params.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                return params;
            }
        };
        StringRequest currentsemRequest = new StringRequest(Request.Method.GET, currentsem_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains(inp_rollno.getText().toString())){
//                            txt_message.setText(response);
//                            org.jsoup.nodes.Document doc = Jsoup.parse(response);
//                            String roll_no = doc.getElementsByTag("tr").get(0).child(1).text();

//                            SharedPreferences shared_pref = getSharedPreferences("Transcript", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = shared_pref.edit();
//                            editor.putString("rollno", roll_no);
//                            editor.commit();

                            pDialog.hide();
                        }
                        else {
                            txt_message.setText("Current Sem Courses' Download Failed. Some error occurred.");
                            pDialog.hide();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txt_message.setText("Downloading Current Sem Courses Failed. Some error occurred. Error : "+error.toString());
                        pDialog.hide();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Cookie",cookie);
                params.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko");
                params.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                return params;
            }
        };
        req_queue.add(transcriptRequest);
        req_queue.add(currentsemRequest);
    }

    public void submitLogin(View view){
        if(!TextUtils.isEmpty(inp_rollno.getText().toString()) && !TextUtils.isEmpty(inp_password.getText().toString())){
            pDialog.setMessage("Loading...");
            pDialog.show();

            StringRequest cookieRequest = new StringRequest(Request.Method.GET, login_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (!TextUtils.isEmpty(cookie)){
                                StringRequest loginRequest = new StringRequest(Request.Method.POST, login_url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                if(!response.contains("INVALID USERNAME/PASSWORD")){
                                                    txt_message.setText("Login Successful.");
                                                    getUserInfo();
                                                    getTranscript();
                                                }
                                                else {
                                                    txt_message.setText("Invalid Credentials.");
                                                    pDialog.hide();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                txt_message.setText("Login Failed. Some error occurred. Error : "+error.toString());
                                                pDialog.hide();
                                            }
                                        }
                                ){
                                    @Override
                                    protected Map<String,String> getParams(){
                                        Map<String,String> params = new HashMap<String, String>();
                                        params.put("LoginID", inp_rollno.getText().toString());
                                        params.put("Password", inp_password.getText().toString());
                                        params.put("loginForm", "Login");
                                        return params;
                                    }

                                    @Override
                                    public Map<String, String> getHeaders() {
                                        Map<String,String> params = new HashMap<String, String>();
                                        params.put("Cookie",cookie);
                                        params.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko");
                                        params.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                                        return params;
                                    }
                                };
                                req_queue.add(loginRequest);
                            }
                            else {
                                txt_message.setText("Some error occurred. Please try again.");
                                pDialog.hide();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            txt_message.setText("Some error occurred. Error : "+error.toString());
                            pDialog.hide();
                        }
                    }){
                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            cookie = response.headers.get("Set-Cookie");
                            return  super.parseNetworkResponse(response);
                        }
                    };
            req_queue.add(cookieRequest);
        } else {
            txt_message.setText("Please enter something.");
        }
    }
}
