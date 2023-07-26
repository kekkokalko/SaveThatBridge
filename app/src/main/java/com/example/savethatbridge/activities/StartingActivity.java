package com.example.savethatbridge.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.savethatbridge.DragMeImpl.MultiTouchHandler;
import com.example.savethatbridge.generali.AndroidFastRenderView;
import com.example.savethatbridge.generali.Box;
import com.example.savethatbridge.gameobjects.GameWorld;
import com.example.savethatbridge.R;

public class StartingActivity extends Activity {

    private int xMax, xMin, yMax, yMin;
    private GameWorld gameWorld;
    private AndroidFastRenderView renderView;
    private MediaPlayer backgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Caricamento libreria per simulazione fisica
        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");

        //Settaggio full-screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Schermo sempre accesso
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // caricamento constanti
        Resources resources = this.getResources();
        this.xMax = resources.getInteger(R.integer.worldXMax);
        this.xMin = resources.getInteger(R.integer.worldXMin);
        this.yMax = resources.getInteger(R.integer.worldYMax);
        this.yMin = resources.getInteger(R.integer.worldYMin);

        //settaggio game-world
        setupGameWorld();

        // View
        this.renderView = new AndroidFastRenderView(this,gameWorld);
        setContentView(this.renderView);

        //Audio
        //backgroundMusic = MediaPlayer.create(this, R.raw.game_music_background);
        //backgroundMusic.setLooping(true);

        //Touch
        MultiTouchHandler touch = new MultiTouchHandler(this.renderView, 1, 1);
        this.gameWorld.setTouchHandler(touch);
    }

    private void setupGameWorld() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Box worldSize = new Box(this.xMin, this.yMin, this.xMax, this.yMax);
        Box screenSize = new Box(0, 0, metrics.widthPixels, metrics.heightPixels);
        this.gameWorld = new GameWorld(this,worldSize, screenSize, this);
        this.gameWorld.setupNewLevel();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("Main thread", "pause");
        this.renderView.pause(); // stops the main loop
        this.backgroundMusic.stop();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("Main thread", "stop");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Main thread", "resume");
        this.renderView.resume();
        //this.backgroundMusic.start();
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Sei sicuro di voler terminare la partita?")
                .setTitle("Attenzione!")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onPause();
                        Log.d("Message","Stop");
                        Intent intent= new Intent(StartingActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }
}