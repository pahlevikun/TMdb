package com.udacity.themoviestage1;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashLanding();
    }

    private void splashLanding() {
        int SPLASH_TIME_OUT = 3000;

        new Handler().postDelayed(new Thread() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        }, SPLASH_TIME_OUT);
    }


    @Override
    public void onBackPressed() {
    }
}
