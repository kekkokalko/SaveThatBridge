package com.example.savethatbridge.gameobjects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.example.savethatbridge.R;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.PolygonShape;

import java.util.logging.Level;

public class Collina extends GameObject{
    private static int istanza = 0;
    private float larghezzaShape;
    private final float altezzaShape;
    private final Canvas canvas;
    private final RectF dest = new RectF();
    private final Bitmap bitmap;
    private final float xMinRoad;

    public Collina(GameWorld gw, float xMin, float xMax, float yMin, float yMax) {
        super(gw);
        istanza++;
        this.canvas=new Canvas(gw.getBitmapBuffer());
        this.larghezzaShape= Math.abs(xMax - xMin);
        this.altezzaShape = Math.abs(yMax - yMin);
        this.xMinRoad = xMin;

        //Definizione del body: posizione e tipo
        BodyDef bodyDef= new BodyDef();
        bodyDef.setPosition(xMin+larghezzaShape/2,yMin+altezzaShape/2);

        //Un body
        this.body=gw.getWorld().createBody(bodyDef);
        this.body.setType(BodyType.staticBody);
        this.name="Collina"+istanza;

        //Shape della collina
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(this.larghezzaShape / 2, this.altezzaShape / 2);
        this.body.createFixture(polygonShape, 0);

        //Settaggio dell'immagine della collina
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        if(GameWorld.getLivello()==1)
            this.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.road1, o);
        else
            this.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.road2, o);

        this.dest.top= 200; this.dest.bottom=400;
        if(xMinRoad>0){
            this.dest.left = 500;
            this.dest.right = 700;
        }
        else {
            this.dest.left = 0;
            this.dest.right = 100;
        }

        bodyDef.delete();
        polygonShape.delete();
    }

    public float getWidth() {
        return this.larghezzaShape;
    }

    public float getHeight() {
        return this.altezzaShape;
    }


    /** Disegna la collina**/
    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.drawBitmap(this.bitmap, null, this.dest, null);
        this.canvas.restore();
    }
}
