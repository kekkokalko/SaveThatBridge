package com.example.savethatbridge.generali;

import static com.example.savethatbridge.gameobjects.GameWorld.*;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.savethatbridge.gameobjects.Bomba;
import com.example.savethatbridge.gameobjects.GameWorld;
import com.example.savethatbridge.gameobjects.Interfaccia;

/**Classe che definisce una superificie di tipo vista che implementa il GameLoop del gioco**/
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
            //Controllo che verifica se la superficie è valida oppure no. Questo perchè, all'avvio del Main Loop, Andorid potrebbe non
            //essere ancora pronto a mostrare il contenuto di tale superficie.Se così è saltare alla condizione del while.
            if(!surfaceHolder.getSurface().isValid()) {
                // too soon (busy waiting), this only happens on startup and resume
                continue;
            }
            long currentTime = System.nanoTime();
            //deltaTime è in secondi e misura il tempo trascorso dall'ultimo frame (è tempoCorrente-tempoInizioFrame). Tale tecnica adatta le varie attività
            //alla durata naturale del game loop
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

            //Aggiornamento dello stato della partita
            this.gameWorld.update(deltaTime);
            //Disegno del prossimo frame sul bitmap 'bitmapBackground'
            this.gameWorld.render();

            //Disegna effettivamete sullo schermo quello che render ha preparato, cioè quello che c'è in bitmapBackground
            //Blocca la canvas (sincronizzazione), quello che effettivamente è associata allo schermo
            canvas=surfaceHolder.lockCanvas();
            canvas.getClipBounds(this.dest);
            //Disegno sulla canvas
            canvas.drawBitmap(this.bitmapBackground,null,this.dest,null);
            //Rilascio la canvas => canvas mostrata sullo schermo
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

    /**Metodo usato per la generazione della bomba**/
    private void checkGeneraBomba(){
        //Se il flag è vero, aggiungi la bomba nel mondo
        if(generaBomba){
            this.gameWorld.aggiungiOggetto(GameWorld.getBombe().get(posizione_bomba));
            size=GameWorld.getBombe().size();
            generaBomba=false;
        }
    }

    /**Metodo che elimina il terorrista una volta superato il ponte**/
    private void checkRimozioneTerrorista(){
        if(rimozioneTerrorista){
            this.gameWorld.rimuoviOggetto(getTerrorista());
            rimozioneTerrorista=false;
            //Rimosso il terrorista, il player può giocare
            inizioGioco=true;
        }
    }

    /**Metodo che permette la costruzione delle travi e gestisce il timer**/
    private void checkCostruzioneTravi(float fpsDeltaTime){
        //Se il gioco è iniziato imposta il flag della gestione del timer a vero
        if(inizioGioco){
            decrementaTimer=true;
            inizioGioco=false;
        }
        //Se siamo nel 2° livello, decrementa il timer
        if(decrementaTimer && GameWorld.getLivello()==2 && fpsDeltaTime >1){
            Interfaccia.decrementaTimer();
            Interfaccia.setScore(GameWorld.getLivello()-1,-1);
            //Se il timer si è azzerato il player non può più giocare
            if(Interfaccia.getTimer()==0)
                fineGioco=true;
        }
        //Se il numero di travi è 0 non si può più costruire
        if(GameWorld.getNumeroTravi()==0){
            GameWorld.setNumeroTravi(-1);
            fineGioco=true;
        }
    }

    /**Metodo che gestisce la fine del gioco**/
    private void checkFineGioco(float fpsDeltaTime){
        if(fineGioco){
            if(fpsDeltaTime>1){
                //Fai partire i 5 secondi prima dell'esplosione
                GameWorld.timerBomba--;
                if(GameWorld.getBombe()!=null && GameWorld.timerBomba==0){
                    int index=0,i=0;
                    //Esplosione di tutte le bombe e conseguente rimozione dal mondo
                    do{
                        ((Bomba)GameWorld.getBombe().get(index)).esplosione(i);
                        this.gameWorld.rimuoviOggetto(GameWorld.getBombe().get(index));
                        GameWorld.getBombe().remove(index);
                        i++;
                    }
                    while(index<GameWorld.getBombe().size());
                    GameWorld.setOggettiVecchiDaDistruggere(false);
                    fineGioco=false;
                    verificaLivello=true;
                    GameWorld.timerBomba=5;
                }
            }
            decrementaTimer=false;
        }
    }

    /**Metodo che verifica l'eventuale vittoria del player**/
    private void verificaVincitaLivello(float fpsDeltaTime){
        if(verificaLivello && fpsDeltaTime>1){
            //5 secondi dallo scoppio della/e bomba/e
            checkTimer++;
            if(checkTimer==5){
                tempoDiCheckScaduto=true;
                //Aggiunta della vespa nel mondo
                this.gameWorld.checkLevel();
                verificaLivello=false;
                checkTimer=0;
            }
        }
        //Se la vespa ha superato il ponte => passaggio prossimo livello o vittoria gioco
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

    /**Metodo che gestisce il timer associato alla vespa**/
    private void checkTimer(float fpsDelatTime){
        if(tempoDiCheckScaduto && fpsDelatTime>1){
            //La vespa non deve impiegare più di 10 secondi per attraversare il ponte
            tempoDiGioco++;
            //Aggiornamento dello score su interfaccia
            Interfaccia.setScore(GameWorld.getLivello()-1,-1);
            if(tempoDiGioco==10){
                tempoDiCheckScaduto=false;
                verificaVittoria=true;
                //Veifica posizione della vespa
                this.gameWorld.gestioneVespa();
                this.gameWorld.mediaPlayer.pause();
            }
        }
    }

    /**Metodo che lancia effettivamente il prossimo livello**/
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
