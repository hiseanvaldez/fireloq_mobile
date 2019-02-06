package com.hiseanvaldez.fireloq;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activity_Main extends AppCompatActivity {

    private FirebaseAuth mAuth;
    BottomNavigationView bottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        bottomNav = findViewById(R.id.nav_bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Fragment_Home()).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public  FirebaseAuth getMAuth(){
        return mAuth;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            switch(menuItem.getItemId()){
                case R.id.nav_profile:
                    selectedFragment = new Fragment_Profile();
                    break;
                case R.id.nav_home:
                    selectedFragment = new Fragment_Home();
                    break;
                case R.id.nav_log:
                    selectedFragment = new Fragment_Logs();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };
}
