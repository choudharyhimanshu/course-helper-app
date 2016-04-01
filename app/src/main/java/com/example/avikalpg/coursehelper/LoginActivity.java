package com.example.avikalpg.coursehelper;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button btn_submit;
    private EditText inp_rollno, inp_password;
    private TextView txt_message;
    ProgressDialog pDialog;
    RequestQueue req_queue;

    String login_url = "http://oars.cc.iitk.ac.in:6060/login.asp";
    String info_url = "http://oars.cc.iitk.ac.in:6060/Student/Default.asp?menu=91";
    String transcript_url = "http://oars.cc.iitk.ac.in:6060/Student/Transcript.asp";
    String cookie = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inp_rollno = (EditText) findViewById(R.id.inpLoginRoll);
        inp_password = (EditText) findViewById(R.id.inpLoginPass);
        btn_submit = (Button) findViewById(R.id.btnLoginSubmit);
        txt_message = (TextView) findViewById(R.id.txtLoginMsg);

        pDialog = new ProgressDialog(this);
        req_queue = Volley.newRequestQueue(this);
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
                                                    StringRequest infoRequest = new StringRequest(Request.Method.GET, info_url,
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    if (response.contains(inp_rollno.getText().toString())){
                                                                        txt_message.setText("Login Successful.");
                                                                        txt_message.append("\r\n");
                                                                        txt_message.append(response);
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
