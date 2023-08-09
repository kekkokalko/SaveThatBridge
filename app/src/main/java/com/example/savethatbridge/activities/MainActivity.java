package com.example.savethatbridge.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.savethatbridge.R;


/**Attività di apertura del gioco**/
public class MainActivity extends Activity {

    private Button nuova_partita,esci;
    private AlertDialog.Builder exitDialog;
    private ImageButton riconoscimenti;
    public MediaPlayer background_music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Settaggio full-screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Schermo sempre accesso
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        //Gestione audio
        background_music = MediaPlayer.create(this, R.raw.music_background);
        background_music.setLooping(true);
        background_music.start();

        //gestione uscita tramite bottone
        esci= findViewById(R.id.button_Esci);
        esci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog = new AlertDialog.Builder(MainActivity.this);
                exitDialog.setTitle(R.string.title);
                exitDialog.setMessage(R.string.message);
                exitDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        background_music.stop();
                        background_music.release();
                        finish();
                    }
                });
                exitDialog.setNegativeButton("No", null);
                AlertDialog dialogo = exitDialog.create();
                dialogo.show();
            }
        });

        //Passaggio alla pagina dei riconoscimenti
        riconoscimenti=(ImageButton) findViewById(R.id.riconoscimenti);
        riconoscimenti.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                background_music.stop();
                background_music.release();
                Intent myIntent = new Intent(MainActivity.this, RiconoscimentiActivity.class);
                startActivity(myIntent);
                finish();
            }
        });

        //Inizio nuova partita
        nuova_partita= findViewById(R.id.button_Nuova_Partita);
        nuova_partita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                background_music.stop();
                background_music.release();
                Log.d("Message","Inizio nuova partita");
                Intent myIntent = new Intent(MainActivity.this, StartingActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }

    //Gestione uscita tramite tocco prolungato
    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setMessage("Sei sicuro di voler uscire?")
                .setTitle("Attenzione!")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        background_music.stop();
                        background_music.release();
                        finish();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }
}
