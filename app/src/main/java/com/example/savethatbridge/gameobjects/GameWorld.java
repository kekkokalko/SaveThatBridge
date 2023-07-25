package com.example.savethatbridge.gameobjects;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.savethatbridge.DragMeImpl.Input;
import com.example.savethatbridge.R;
import com.example.savethatbridge.generali.AndroidFastRenderView;
import com.example.savethatbridge.generali.Box;
import com.example.savethatbridge.generali.Livello;
import com.example.savethatbridge.activities.StartingActivity;
import com.example.savethatbridge.generali.MyRevoluteJoint;
import com.example.savethatbridge.DragMeImpl.TouchHandler;
import com.example.savethatbridge.generali.MyRevoluteJointConMotore;
import com.example.savethatbridge.generali.TouchConsumer;
import com.google.fpl.liquidfun.Body;

import com.google.fpl.liquidfun.ContactListener;
import com.google.fpl.liquidfun.Joint;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.ParticleSystemDef;
import com.google.fpl.liquidfun.World;

import java.util.ArrayList;
import java.util.List;

public class GameWorld {

    private final Box physicalSize, screenSize;
    private final StartingActivity startingActivity;
    private final Canvas myCanvas;
    private final int larghezzaBuffer, altezzaBuffer;
    private final Bitmap frameBuffer;
    private static float lunghezzaPonte;
    private static float altezzaAggancio;
    private Bitmap bitmap;

    private static int level = 0;
    private World world;
    private final RectF dest;
    private final TouchConsumer touchConsumer;
    private TouchHandler touchHandler;
    private Context context;
    private final ContactListener contactListener;
    public MediaPlayer mediaPlayer;

    //GameObjects
    private final ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
    private static GameObject bordo;
    private static Terrorista terrorista;
    private static ArrayList<GameObject> bombe=new ArrayList<GameObject>();
    public static ArrayList<GameObject> trave=new ArrayList<>();
    public static ArrayList<GameObject> ruote= new ArrayList<>(2);
    public static GameObject vespa;
    public static Interfaccia interfaccia;

    //Game Objects per la definizione del ponte
    public static ArrayList<GameObject> agganci = new ArrayList<>();
    public static ArrayList<GameObject> colline= new ArrayList<>();
    public static ArrayList<GameObject> ponte= new ArrayList<>();

    //Particelle
    private final ParticleSystem particleSystem;

    //Joint
    public static ArrayList<MyRevoluteJoint> joint= new ArrayList<>();
    private static final ArrayList<Joint> jointDaDistruggere = new ArrayList<>();
    private static final ArrayList<Body> oggettiDaDistruggere = new ArrayList<>();
    private static final ArrayList<MyRevoluteJointConMotore> jointRuote = new ArrayList<>(2);

    // Parametri per la simulazione
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;
    private static final int PARTICLE_ITERATIONS = 3;

    //Valori
    private static int livello = 1;
    private static int numeroTravi =-1;
    private static boolean costruire = false;
    private static boolean oggettiVecchiDaDistruggere=true;
    private static boolean prontoProssimoLivello=true;
    private static boolean verifica=false;
    public static int timerBomba=5;

    public GameWorld(Context context,Box phisicalSize, Box screenSize, StartingActivity startingActivity) {
        //Settaggio dimensioni visive del gioco
        this.physicalSize = phisicalSize;
        this.screenSize = screenSize;
        this.startingActivity = startingActivity;
        this.context=context;

        //Caricamento dimensioni del frameBuffer
        Resources resorces = this.startingActivity.getResources();
        this.larghezzaBuffer = 600;
        this.altezzaBuffer = 400;
        this.frameBuffer = Bitmap.createBitmap(this.larghezzaBuffer, this.altezzaBuffer, Bitmap.Config.ARGB_8888);

        //Definizione del mondo con il suo Gravity vector
        this.world = new World(0,7);

        //Settaggio canvas
        this.myCanvas = new Canvas(this.frameBuffer);
        this.dest = new RectF();
        this.dest.top = 0;
        this.dest.bottom = 400;
        this.dest.right = 600;
        this.dest.left = 0;

        //Settaggio sistema di particelle
        ParticleSystemDef particleSystemDef = new ParticleSystemDef();
        particleSystemDef.setDestroyByAge(true);
        this.particleSystem = this.world.createParticleSystem(particleSystemDef);
        this.particleSystem.setRadius(0.3f);
        this.particleSystem.setMaxParticleCount(1000);
        particleSystemDef.delete();

        this.contactListener = new ContactListener();
        this.world.setContactListener(this.contactListener);
        this.touchConsumer= new TouchConsumer(this.context,this);

        lunghezzaPonte = 20;
        altezzaAggancio= this.physicalSize.getyMax() / 30;

        mediaPlayer= MediaPlayer.create(this.context, R.raw.vespa);
        mediaPlayer.setLooping(true);
    }

    //Getters
    public Box getPhysicalSize() {
        return this.physicalSize;
    }
    public Box getScreenSize(){ return this.screenSize;}
    public StartingActivity getActivity() {
        return this.startingActivity;
    }
    public Bitmap getBitmapBuffer() {
        return this.frameBuffer;
    }
    public World getWorld() {
        return this.world;
    }
    public static float getLunghezzaPonte() {
        return lunghezzaPonte;
    }
    public static float getAltezzaAggancio(){ return altezzaAggancio;}
    public static ArrayList<GameObject> getBombe(){ return GameWorld.bombe;}
    public static Terrorista getTerrorista(){ return GameWorld.terrorista;}
    public static int getNumeroTravi() {return GameWorld.numeroTravi;}
    public static ArrayList<MyRevoluteJoint> getJoint(){ return joint;}
    public static ArrayList<Joint> getJointDaDistruggere() {
        return jointDaDistruggere;
    }
    public static synchronized boolean getOggettiVecchiDaDistruggere(){ return oggettiVecchiDaDistruggere;}
    public ParticleSystem getParticleSystem() {return this.particleSystem;}
    public static boolean getProntoProssimoLivello(){ return prontoProssimoLivello;}
    public static int getLivello(){ return livello;}

    //Setters
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public float setWorldToFrameX(float x) {return (x - this.physicalSize.getxMin()) / this.physicalSize.getWidth() * this.larghezzaBuffer;}
    public static void setBordo(GameObject bordo){ GameWorld.bordo=bordo;}
    public float setWorldToFrameY(float y) {return (y - this.physicalSize.getyMin()) / this.physicalSize.getHeight() * this.altezzaBuffer;}
    public static void setTerrorista(Terrorista terrorista) {
        GameWorld.terrorista = terrorista;
    }
    public static void setNumeroTravi(int numeroTravi) {GameWorld.numeroTravi=numeroTravi;}
    public static void setCostruire(boolean costruire){ GameWorld.costruire=costruire;}
    public void setTouchHandler(TouchHandler touchHandler) {
        this.touchHandler = touchHandler;
    }
    public static synchronized void setOggettiVecchiDaDistruggere(boolean b) { oggettiVecchiDaDistruggere=b;}
    public static void aggiungiBomba(GameObject bomba){ GameWorld.getBombe().add(bomba);}

    public synchronized void update(float elapsedTime) {
        world.step(elapsedTime, VELOCITY_ITERATIONS, POSITION_ITERATIONS, PARTICLE_ITERATIONS);
        gestioneDistruzione();

        //Gestione delle collisioni
        gestioneVespa();

        //Aggorna i tocchi avvenuti
        for (Input.TouchEvent event : this.touchHandler.getTouchEvents())
            this.touchConsumer.consumeTouchEvent(event);
    }
    public synchronized void render(){
        this.myCanvas.save();
        this.myCanvas.drawBitmap(this.bitmap, null, dest, null);
        this.myCanvas.restore();
        for (GameObject obj : this.gameObjects)
            obj.draw(this.frameBuffer);
    }

    //Funzioni per convertire coordinate dello schermo e quelle fisiche del gioco e viceversa
    public float toPixelsXLength(float x) {return x / this.physicalSize.getWidth() * this.larghezzaBuffer;}
    public float toPixelsYLength(float y) {return y / this.physicalSize.getHeight() * this.altezzaBuffer;}
    public float setScreenToWorldX(float x) {return this.physicalSize.getxMin() + x * (this.physicalSize.getWidth() / this.screenSize.getWidth());}
    public float setScreenToWorldY(float y) {return this.physicalSize.getyMin() + y * (this.physicalSize.getHeight() / this.screenSize.getHeight());}

    //Funzioni specifiche per il GameWorld
    /**Aggiungere un nuovo GameObject**/
    public synchronized GameObject aggiungiOggetto(GameObject obj)
    {
        this.gameObjects.add(obj);
        return obj;
    }

    /**Rimuovi un Gameobject**/
    public synchronized void rimuoviOggetto(GameObject obj){
        if(obj!=null) {
            oggettiDaDistruggere.add(obj.body);
            while (gameObjects.remove(obj))
                this.gameObjects.remove(obj);
        }
    }

    private void rimuoviListeDiOggetti(ArrayList<GameObject> oggetti) {
        for (GameObject oggetto : oggetti)
            this.rimuoviOggetto(oggetto);
    }

    public void rimuoviVecchiOggetti() {
        rimuoviListeDiOggetti(colline);
        rimuoviListeDiOggetti(ponte);
        rimuoviListeDiOggetti(agganci);
        rimuoviListeDiOggetti(trave);
        rimuoviOggetto(vespa);
        rimuoviListeDiOggetti(ruote);
        aggiungiJointDaDistruggere(joint);
        rimuoviOggetto(bordo);
        rimuoviOggetto(interfaccia);
        setOggettiVecchiDaDistruggere(false);
        prontoProssimoLivello=false;
    }

    private void aggiungiJointDaDistruggere(ArrayList<MyRevoluteJoint> joints) {
        for (MyRevoluteJoint joint : joints)
            jointDaDistruggere.add(joint.getJoint());
    }

    private void gestioneDistruzione(){
        if(!getOggettiVecchiDaDistruggere()){
            distruggiJoint(jointDaDistruggere);
            distruggiBodies(oggettiDaDistruggere);
            setOggettiVecchiDaDistruggere(true);
            if(!prontoProssimoLivello)
                prontoProssimoLivello=true;
        }
    }

    public void gestioneVespa(){
        if(!ruote.isEmpty() && vespa!=null){
            if(!verifica) {
                if (this.ruote.get(0).body.getPositionX() > this.getPhysicalSize().getxMax() - 2 || this.ruote.get(1).body.getPositionX() > this.getPhysicalSize().getxMax() - 2 || this.vespa.body.getPositionX() > this.getPhysicalSize().getxMax() - 2) {
                    verifica = true;
                    Log.d("Messagge", "La vespa ha superato il ponte");
                    AndroidFastRenderView.vittoria = true;
                    AndroidFastRenderView.verificaVittoria = true;
                    setOggettiVecchiDaDistruggere(false);
                    mediaPlayer.pause();
                }
                else if (this.ruote.get(0).body.getPositionY() > this.getPhysicalSize().getyMax() - 2 || this.ruote.get(1).body.getPositionY() > this.getPhysicalSize().getyMax() - 2 || this.vespa.body.getPositionY() > this.getPhysicalSize().getyMax() - 2) {
                    verifica = true;
                    Log.d("Messagge", "La vespa è caduta giù dal ponte");
                    AndroidFastRenderView.verificaVittoria = true;
                    setOggettiVecchiDaDistruggere(false);
                    mediaPlayer.pause();
                }
            }
        }
    }

    public static synchronized void incrementaLivello(){ livello++;}
    private void distruggiJoint(List<Joint> joints) {
        for (Joint joint : joints)
            this.world.destroyJoint(joint);
        joints.clear();
    }

    private void distruggiBodies(List<Body> oggettiDaDistruggere) {
        for (Body body : oggettiDaDistruggere)
            this.world.destroyBody(body);
        oggettiDaDistruggere.clear();
    }

    /**Gestione livelli**/
    public synchronized void setupNewLevel() {
        Livello gameLevel = new Livello(this.context,this);
        if (livello == 1) gameLevel.level1(this);
        else if (livello == 2) gameLevel.level2(this);
        else gameLevel.endLevel(this);
    }

    public synchronized void checkLevel(){
        verifica=false;
        costruire=false;
        ruote.clear();
        jointRuote.clear();
        mediaPlayer.start();
        this.vespa=this.aggiungiOggetto(new Vespa(this.context,this,this.getPhysicalSize().getxMin()+2,-5));
        this.ruote.add(this.aggiungiOggetto(new Ruota(this,this.getPhysicalSize().getxMin()+2+Ruota.larghezza/2,-3)));
        this.ruote.add(this.aggiungiOggetto(new Ruota(this,this.getPhysicalSize().getxMin()+2+Ruota.larghezza/2,-3)));
        this.jointRuote.add(new MyRevoluteJointConMotore(this, vespa.body, ruote.get(0).body,0,0,-Vespa.larghezza/2+Ruota.larghezza/2, Vespa.altezza/2));
        this.jointRuote.add(new MyRevoluteJointConMotore(this, vespa.body, ruote.get(1).body,0,0,Vespa.larghezza/2-Ruota.larghezza/2,Vespa.altezza/2));
    }


    /**Gestione aggiunta di una trave di rafforzamento**/
    public synchronized void aggiuntaTrave(GameObject a, GameObject b){
        //Controllo tipologia di oggetti passati alla funzione
        Aggancio aggancio = a instanceof Aggancio ? (Aggancio) a : (Aggancio) b;
        Ponte ponte = a instanceof Ponte ? (Ponte) a : (Ponte) b;

        //Se il numero di travi a disposizione è 0 o non si può ancora costruire non fare nulla
        if (numeroTravi == 0 || !costruire) return;

        //calcolo della distanza tra i due joint
        float aggancioX=aggancio.body.getPositionX();
        float aggancioY=aggancio.body.getPositionY();
        float larghezzaAggancio=Aggancio.getLarghezza();

        float ponteX=ponte.body.getPositionX();
        float ponteY=ponte.body.getPositionY();

        float diffX=Math.abs(aggancioX-ponteX);
        float diffY=Math.abs(aggancioY-ponteY);
        float larghezza= (float)Math.sqrt(Math.pow(diffX,2)+Math.pow(diffY,2))-larghezzaAggancio/2;

        if (larghezza >= 30) return;

        float x= Math.min(aggancioX,ponteX)+diffX/2;
        float y= Math.min(aggancioY,ponteY)+diffY/2;
        float angolo = (float) ((aggancioX < ponteX) ? Math.PI / 2 + Math.atan(diffX / diffY) : Math.PI / 2 - Math.atan(diffX / diffY));

        Trave rinforzo = new Trave(this, x, y, larghezza, altezzaAggancio, angolo);
        this.aggiungiOggetto(rinforzo);
        trave.add(rinforzo);

        if (aggancioX < rinforzo.body.getPositionX() && aggancioY > rinforzo.body.getPositionY()) {
            joint.add(new MyRevoluteJoint(this, aggancio.body, rinforzo.body, diffX / 2 - larghezzaAggancio / 2, -diffY / 2 + larghezzaAggancio, larghezzaAggancio / 2, 0));
            joint.add(new MyRevoluteJoint(this, ponte.body, rinforzo.body, -diffX / 2 , -diffY / 2 + altezzaAggancio * 3 / 2, 0, altezzaAggancio));
        } else if (aggancioX > rinforzo.body.getPositionX() && aggancioY > rinforzo.body.getPositionY()) {
            joint.add(new MyRevoluteJoint(this, aggancio.body, rinforzo.body, 0, 0, 0, 0));
            joint.add(new MyRevoluteJoint(this, ponte.body, rinforzo.body, 0, 0,0 , 0));
        }
        numeroTravi--;
        Interfaccia.decrementaContatoreTravi();
    }

    @Override
    protected void finalize() {
        this.world.delete();
    }

}
