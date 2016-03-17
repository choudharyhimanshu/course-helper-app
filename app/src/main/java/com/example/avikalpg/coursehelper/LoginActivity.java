package com.example.avikalpg.coursehelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private Button btn_submit;
    private EditText inp_rollno, inp_password;
    private TextView txt_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inp_rollno = (EditText) findViewById(R.id.inpLoginRoll);
        inp_password = (EditText) findViewById(R.id.inpLoginPass);
        btn_submit = (Button) findViewById(R.id.btnLoginSubmit);
        txt_message = (TextView) findViewById(R.id.txtLoginMsg);
    }

    public void submitLogin(View view){
        if(!TextUtils.isEmpty(inp_rollno.getText().toString()) && !TextUtils.isEmpty(inp_password.getText().toString())){
            txt_message.setText("Please wait..");
        }
        else {
            txt_message.setText("Please enter something.");
        }
    }
}
