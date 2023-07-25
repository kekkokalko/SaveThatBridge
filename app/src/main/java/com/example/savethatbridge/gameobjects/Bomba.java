package com.example.savethatbridge.gameobjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.savethatbridge.R;
import com.example.savethatbridge.generali.MyRevoluteJoint;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;

public class Bomba extends GameObject{
    private final Canvas canvas;
    private final Bitmap bitmap;
    private final RectF dest= new RectF();
    private final float screenSemiWidth, screenSemiHeight;
    private MyRevoluteJoint joint;
    private MediaPlayer mediaPlayer;
    private final float x, y;
    private Context context;

    public Bomba(Context context,GameWorld gameWorld, float x , float y, MyRevoluteJoint jointDaDistruggere) {
        super(gameWorld);

        this.context=context;
        this.x=x;
        this.y=y;

        this.canvas = new Canvas(gameWorld.getBitmapBuffer());
        this.joint=jointDaDistruggere;
        float dimensione=2.0f;
        this.screenSemiHeight = gameWorld.toPixelsYLength(dimensione) / 2;
        this.screenSemiWidth = gameWorld.toPixelsXLength(dimensione) / 2;

        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.staticBody);

        this.body = gameWorld.getWorld().createBody(bodyDef);
        this.body.setSleepingAllowed(true);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gameWorld.getActivity().getResources(), R.drawable.bomba, options);
        bodyDef.delete();
    }

    public synchronized void esplosione(){
        //Gestione musica
        mediaPlayer= MediaPlayer.create(this.context, R.raw.esplosione);
        mediaPlayer.start();
        //Seleziona il joint da distruggere
        GameWorld.getJointDaDistruggere().add(this.joint.getJoint());
        //Rimuovilo da quelli disponibili sul ponte
        GameWorld.getJoint().remove(this.joint);
        GameWorld.setOggettiVecchiDaDistruggere(false);
        //Aggiunta particelle dall'esplosione (Da completare)
        Particelle particelle= new Particelle(this.gw, this.x,this.y);
        this.gw.aggiungiOggetto(particelle);
        //Annulla il joint
        this.joint = null;
    }
    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.dest.top = y - 2 * this.screenSemiHeight;
        this.dest.bottom = y;
        this.dest.right = x + this.screenSemiWidth;
        this.dest.left = x+7 - this.screenSemiWidth;
        this.canvas.drawBitmap(this.bitmap, null, this.dest, null);
        this.canvas.restore();
    }
}
