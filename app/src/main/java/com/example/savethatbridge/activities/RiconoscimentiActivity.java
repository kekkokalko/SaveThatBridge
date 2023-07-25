package com.example.savethatbridge.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.savethatbridge.R;


public class RiconoscimentiActivity extends Activity {

    private Button github,instagram,linkedin,github1,researchgate;
    private ImageButton indietro;
    private TextView testo1,testo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Settaggio full-screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Schermo sempre accesso
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_riconoscimenti);

        //Gestione multicolor testi
        testo1=findViewById(R.id.textView);
        String text= "<font color=#FF0000>Designer:</font> <font color=#00000>Francesco Calcopietro</font>";
        testo1.setText(Html.fromHtml(text));
        testo2=findViewById(R.id.textView2);
        String text1= "<font color=#FF0000>Supervisor:</font> <font color=#00000>Prof. Marco Faella</font>";
        testo2.setText(Html.fromHtml(text1));

        //Gestione URL tramite bottoni
        github=findViewById(R.id.Github);
        instagram=findViewById(R.id.Instagram);
        linkedin=findViewById(R.id.Linkedin);
        github1=findViewById(R.id.Github1);
        researchgate=findViewById(R.id.researchgate);
        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://github.com/kekkokalko");
            }
        });
        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://www.linkedin.com/in/francesco-calcopietro-794404227/");
            }
        });
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://www.instagram.com/kekkokalko_/");
            }
        });
        github1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://github.com/mfaella");
            }
        });
        researchgate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://www.researchgate.net/profile/Marco-Faella");
            }
        });

        //gestione ritorno main activity
        indietro=(ImageButton) findViewById(R.id.indietro);
        indietro.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(RiconoscimentiActivity.this, MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
    private void gotoUrl(String s) {
        Uri uri=Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(RiconoscimentiActivity.this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }
}