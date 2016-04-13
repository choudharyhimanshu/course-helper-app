package com.example.avikalpg.coursehelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String url_oa_photo = "http://oa.cc.iitk.ac.in:8181/Oa/Jsp/Photo/";

    private TextView txt_nav_name;
    private TextView txt_nav_rollno;
    private ImageView img_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        txt_nav_name = (TextView) headerView.findViewById(R.id.txtNavName);
        txt_nav_rollno = (TextView) headerView.findViewById(R.id.txtNavRollno);
        img_user = (ImageView) headerView.findViewById(R.id.imgUser);

        SharedPreferences shared_pref = getSharedPreferences("UserData", MODE_PRIVATE);
        if (shared_pref.contains("rollno")){
            txt_nav_name.setText(shared_pref.getString("name", "Name"));
            txt_nav_rollno.setText(shared_pref.getString("rollno","Roll No"));
            try {
                Picasso.with(this).load(url_oa_photo+shared_pref.getString("rollno","")+"_0.jpg").into(img_user);
            }
            catch (Exception e){
                Log.e("PICASSO",e.toString());
            }
        }

        Intent intent = new Intent(this, CourseService.class);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_course_search) {
            Intent myIntent = new Intent(this, CourseSearchActivity.class);
            this.startActivity(myIntent);
        } else if (id == R.id.nav_dept_template) {

        } else if (id == R.id.nav_personal) {

        } else if (id == R.id.nav_logout){
            SharedPreferences shared_pref = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = shared_pref.edit();
            editor.clear();
            editor.commit();
            SQLiteDatabase db = openOrCreateDatabase("coursehelper", MODE_PRIVATE, null);
            try {
                db.execSQL("DELETE FROM personal_courses");
            }
            catch (Exception e){
                Log.e("LOGOUT", e.toString());
            }
            txt_nav_name.setText(null);
            txt_nav_rollno.setText(null);
            img_user.setImageResource(0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void personalTemplate(View view){
        SharedPreferences shared_pref = getSharedPreferences("UserData", MODE_PRIVATE);
        if (shared_pref.contains("rollno")){
            Intent myIntent = new Intent(this, PersonalTemplate.class);
            this.startActivity(myIntent);
        }
        else{
            Intent myIntent = new Intent(this, LoginActivity.class);
            this.startActivity(myIntent);
        }
    }

    public void gotoLogin(View view){
        Intent myIntent = new Intent(this, LoginActivity.class);
//            myIntent.putExtra("key", value); //Optional parameters
        this.startActivity(myIntent);
    }

    public void gotoCourseSearch(View view){
        Intent myIntent = new Intent(this, CourseSearchActivity.class);
        this.startActivity(myIntent);
    }
}
