package com.udacity.themoviestage1;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private Button btCinema, btMovie, btFav, btAbout;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btCinema = (Button) findViewById(R.id.buttonCinema);
        btMovie = (Button) findViewById(R.id.buttonMovie);
        btFav = (Button) findViewById(R.id.buttonFavorite);
        btAbout = (Button) findViewById(R.id.buttonAbout);
        textView = (TextView) findViewById(R.id.textview);

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/hollywood.ttf");
        textView.setTypeface(myTypeface);

        btMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MenuActivity.this);
                alert.setTitle("Information");
                alert.setMessage("Nama~\n30 June 2017\nAssociate Android Developer Fast Track");
                alert.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        });
                alert.show();
            }
        });

        btFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, FavActivity.class);
                startActivity(intent);
            }
        });

        btCinema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, CinemaMapActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Double click for exit!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
