package com.hiseanvaldez.fireloq;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Activity_Main extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private BottomNavigationView bottomNav;
    private NavigationView sideNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sideNav = findViewById(R.id.nav_navigationView);
        sideNav.setNavigationItemSelectedListener(sideNavListener);

        bottomNav = findViewById(R.id.nav_bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(bottomNavListener);

        if (savedInstanceState == null) {
            sideNav.setCheckedItem(R.id.nav_home);
            bottomNav.setSelectedItemId(R.id.nav_home);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_Home()).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public FirebaseAuth getMAuth() {
        return mAuth;
    }

    private NavigationView.OnNavigationItemSelectedListener sideNavListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.nav_profile:
                    sideNav.setCheckedItem(R.id.nav_profile);
                    selectedFragment = new Fragment_Profile();
                    break;
                case R.id.nav_logout:
                    new Firestore_WriteLog(mAuth, "Log Out");
                    mAuth.signOut();
                    Toast.makeText(Activity_Main.this, "Signing out...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Activity_Main.this, Activity_Login.class));
                    finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            } else {
                return false;
            }
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new Fragment_Home();
                    break;
                case R.id.nav_log:
                    selectedFragment = new Fragment_Logs();
                    break;
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            } else {
                return false;
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
