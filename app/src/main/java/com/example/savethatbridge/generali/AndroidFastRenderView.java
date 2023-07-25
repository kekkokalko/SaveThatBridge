package com.example.savethatbridge.generali;

import static com.example.savethatbridge.gameobjects.GameWorld.*;

import android.app.GameManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.savethatbridge.gameobjects.Bomba;
import com.example.savethatbridge.gameobjects.GameObject;
import com.example.savethatbridge.gameobjects.GameWorld;
import com.example.savethatbridge.gameobjects.Interfaccia;
import com.example.savethatbridge.gameobjects.Ponte;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.World;

import java.util.concurrent.TimeUnit;

public class AndroidFastRenderView extends SurfaceView implements Runnable {

    private Thread myThread=null;
    private volatile boolean running=false;
    private GameWorld gameWorld;
    private SurfaceHolder surfaceHolder;
    private Bitmap bitmapBackground;
    private Canvas canvas;
    private final Rect dest = new Rect();
    private static int size=-1;

    //flag utili
    public static boolean generaBomba = false;
    public static boolean rimozioneTerrorista=false;
    public static boolean inizioGioco=false;
    public static boolean decrementaTimer=false;
    public static boolean fineGioco=false;
    public static boolean verificaLivello=false;
    public static int checkTimer=0;
    public static boolean tempoDiCheckScaduto=false;
    public static boolean verificaVittoria=false;
    public static boolean vittoria=false;
    public static int tempoDiGioco=0;
    public static boolean prossimoLivello=false;
    public static int posizione_bomba=-1;

    public AndroidFastRenderView(Context context, GameWorld gameWorld) {
        super(context);
        this.gameWorld=gameWorld;
        this.bitmapBackground=gameWorld.getBitmapBuffer();
        this.surfaceHolder=getHolder();
    }

    //Inizia il game loop attraverso l'uso di un thread separato
    public void resume() {
        running = true;
        myThread = new Thread(this);
        myThread.start();
    }

    //Ferma il game loop e aspetta che termina
    public void pause() {
        running = false;
        while(true) {
            try {
                myThread.join();
                break;
            } catch (InterruptedException e) {
                // just retry
            }
        }
    }

    @Override
    public void run() {
        long startTime = System.nanoTime(), fpsTime = startTime, frameCounter = 0;
        /**Parte il game loop **/
        while (running) {
            if(!surfaceHolder.getSurface().isValid()) {
                // too soon (busy waiting), this only happens on startup and resume
                continue;
            }
            long currentTime = System.nanoTime();
            //deltaTime è in secondi
            float deltaTime = (currentTime - startTime) / 1000000000f;
            float fpsDeltaTime = (currentTime - fpsTime) / 1000000000f;
            startTime = currentTime;

            checkGeneraBomba();
            checkRimozioneTerrorista();
            checkCostruzioneTravi(fpsDeltaTime);
            checkFineGioco(fpsDeltaTime);
            verificaVincitaLivello(fpsDeltaTime);
            checkTimer(fpsDeltaTime);
            lanciaNuovoLivello();

            this.gameWorld.update(deltaTime);
            this.gameWorld.render();

            //Disegna il frameBuffer con Canvas sullo schermo
            canvas=surfaceHolder.lockCanvas();
            canvas.getClipBounds(this.dest);
            canvas.drawBitmap(this.bitmapBackground,null,this.dest,null);
            surfaceHolder.unlockCanvasAndPost(canvas);

            //misura gli FPS
            frameCounter++;
            if (fpsDeltaTime > 1) {
                Log.d("FastRenderView", "Current FPS = " + frameCounter);
                frameCounter = 0;
                fpsTime = currentTime;
            }
        }
    }

    private void checkGeneraBomba(){
        if(generaBomba){
            this.gameWorld.aggiungiOggetto(GameWorld.getBombe().get(posizione_bomba));
            size=GameWorld.getBombe().size();
            generaBomba=false;
        }

    }

    private void checkRimozioneTerrorista(){
        if(rimozioneTerrorista){
            this.gameWorld.rimuoviOggetto(getTerrorista());
            rimozioneTerrorista=false;
            inizioGioco=true;
        }
    }

    private void checkCostruzioneTravi(float fpsDeltaTime){
        if(inizioGioco){
            decrementaTimer=true;
            inizioGioco=false;
        }
        if(decrementaTimer && fpsDeltaTime >1){
            Interfaccia.decrementaTimer();
            if(Interfaccia.getTimer()==0)
                fineGioco=true;
        }
        if(GameWorld.getNumeroTravi()==0){
            GameWorld.setNumeroTravi(-1);
            fineGioco=true;
        }
    }

    private void checkFineGioco(float fpsDeltaTime){
        if(fineGioco){
            if(fpsDeltaTime>1){
                GameWorld.timerBomba--;
                if(GameWorld.getBombe()!=null && GameWorld.timerBomba==0){
                    int index=size-1;
                    do{
                        ((Bomba)GameWorld.getBombe().get(index)).esplosione();
                        this.gameWorld.rimuoviOggetto(GameWorld.getBombe().get(index));
                        GameWorld.getBombe().remove(index);
                        index--;
                    }
                    while(index==0);
                    GameWorld.setOggettiVecchiDaDistruggere(false);
                    fineGioco=false;
                    verificaLivello=true;
                    GameWorld.timerBomba=5;
                }
            }
            decrementaTimer=false;
        }
    }

    private void verificaVincitaLivello(float fpsDeltaTime){
        if(verificaLivello && fpsDeltaTime>1){
            checkTimer++;
            if(checkTimer==5){
                tempoDiCheckScaduto=true;
                this.gameWorld.checkLevel();
                verificaLivello=false;
                checkTimer=0;
            }
        }
        if(verificaVittoria){
            if(vittoria) {
                GameWorld.incrementaLivello();
                vittoria=false;
            }
            tempoDiGioco=0;
            tempoDiCheckScaduto=false;
            this.gameWorld.rimuoviVecchiOggetti();
            verificaVittoria=false;
            prossimoLivello=true;
        }
    }

    private void checkTimer(float fpsDelatTime){
        if(tempoDiCheckScaduto && fpsDelatTime>1){
            tempoDiGioco++;
            if(tempoDiGioco==10){
                tempoDiCheckScaduto=false;
                verificaVittoria=true;
                this.gameWorld.gestioneVespa();
                this.gameWorld.mediaPlayer.pause();
            }
        }
    }

    private void lanciaNuovoLivello(){
        if(prossimoLivello && GameWorld.getProntoProssimoLivello()){
            this.gameWorld.setupNewLevel();
            size=GameWorld.getBombe().size();
            prossimoLivello=false;
        }
    }

    public static void setRimozioneTerrorista(boolean rimozioneTerrorista){ AndroidFastRenderView.rimozioneTerrorista=rimozioneTerrorista;}

    public static void setGeneraBomba(boolean generaBomba){AndroidFastRenderView.generaBomba=generaBomba;}
}
