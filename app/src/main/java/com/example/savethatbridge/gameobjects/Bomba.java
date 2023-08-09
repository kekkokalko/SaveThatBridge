package com.example.savethatbridge.gameobjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.media.MediaPlayer;

import com.example.savethatbridge.R;
import com.example.savethatbridge.generali.MyRevoluteJoint;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;

/**Classe che definisce una bomba**/
public class Bomba extends GameObject{
    private final Canvas canvas;
    private final Bitmap bitmap;
    private final RectF dest= new RectF();
    private final float screenSemiWidth, screenSemiHeight;
    private MyRevoluteJoint joint;
    private MediaPlayer mediaPlayer;
    private final float x, y;
    private Context context;
    private static Particelle p;

    public Bomba(Context context,GameWorld gameWorld, float x , float y, MyRevoluteJoint jointDaDistruggere) {
        super(gameWorld);

        this.context=context;
        this.x=x-1;
        this.y=y;

        this.canvas = new Canvas(gameWorld.getBitmapBuffer());
        this.joint=jointDaDistruggere;
        float dimensione=2.0f;
        this.screenSemiHeight = gameWorld.toPixelsYLength(dimensione) / 2;
        this.screenSemiWidth = gameWorld.toPixelsXLength(dimensione) / 2;

        //Deinizione di un body
        BodyDef bodyDef=new BodyDef();
        //definizione della posizione
        bodyDef.setPosition(x,y);
        //definizione della tipologia di body
        bodyDef.setType(BodyType.staticBody);

        //Creazione del corpo e del suo user data
        this.body = gameWorld.getWorld().createBody(bodyDef);
        this.body.setSleepingAllowed(true);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gameWorld.getActivity().getResources(), R.drawable.bomba, options);
        bodyDef.delete();
    }

    /**Metodo di gestione dell'esplosione della bomba**/
    public synchronized void esplosione(int i){
        //Gestione musica
        mediaPlayer= MediaPlayer.create(this.context, R.raw.esplosione);
        mediaPlayer.start();
        //Seleziona il joint da distruggere e aggiunta nella lista di quelli da eliminare
        GameWorld.getJointDaDistruggere().add(this.joint.getJoint());
        //Rimuovilo da quelli disponibili sul ponte
        GameWorld.getJoint().remove(this.joint);
        //Permetti la distruzione dei corpi selezioni
        GameWorld.setOggettiVecchiDaDistruggere(false);
        //Istanzia particelle dall'esplosione
        p= new Particelle(this.gw, i, this.x,this.y);
        //Aggiunta particelle nel mondo
        this.gw.aggiungiOggetto(p);
        //Annulla il joint
        this.joint = null;
    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        //Salvataggio della canvas e modifiche conseguenti
        this.canvas.save();
        this.dest.top = y - 2 * this.screenSemiHeight;
        this.dest.bottom = y;
        this.dest.right = x + this.screenSemiWidth;
        this.dest.left = x+7 - this.screenSemiWidth;
        this.canvas.drawBitmap(this.bitmap, null, this.dest, null);
        this.canvas.restore();
    }
}
