package com.hiseanvaldez.fireloq;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activity_Splash extends AppCompatActivity {
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }
                catch (InterruptedException e){
                    e.printStackTrace ();
                }
                finally {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if(currentUser != null){
                        startActivity(new Intent(Activity_Splash.this, Activity_Main.class));
                        finish();
                    }
                    else {
                        startActivity(new Intent(Activity_Splash.this, Activity_Login.class));
                        finish();
                    }
                }
            }
        };
        timer.start ();
    }
}
