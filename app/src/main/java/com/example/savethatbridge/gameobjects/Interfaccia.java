package com.example.savethatbridge.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;

public class Interfaccia extends GameObject{
    private final Canvas canvas;
    private final Paint paint;
    private static int contatoreTravi=0;
    private static int contatoreLivello=0;
    private static int timer = -1;

    public Interfaccia(GameWorld gameWorld) {
        super(gameWorld);

        this.canvas = new Canvas(gameWorld.getBitmapBuffer());
        this.paint = new Paint();

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        bodyDef.setType(BodyType.staticBody);

        // a body
        this.body = gameWorld.getWorld().createBody(bodyDef);
        this.body.setUserData(this);

        bodyDef.delete();

        if(GameWorld.getLivello()==1)
            this.paint.setColor(Color.BLACK);
        else
            this.paint.setColor(Color.YELLOW);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.paint.setTextSize(20);
        this.paint.setTextAlign(Paint.Align.CENTER);
    }

    public static void setContatoreLivello(int livello){ contatoreLivello=livello;}
    public static void setContatoreTravi(int contatore){ contatoreTravi=contatore;}
    public static void decrementaContatoreTravi(){ contatoreTravi--;}
    public static void setTimer(int t){timer=t;}
    public static void decrementaTimer(){
        if(timer>0)
            timer--;
    }
    public static int getTimer(){ return timer;}

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        if(GameWorld.getLivello()<=2) {
            this.canvas.drawText("Travi rimanenti : " + contatoreTravi, this.gw.getScreenSize().getxMax() / 28, this.gw.getScreenSize().getyMax() / 28, paint);
            this.canvas.drawText("Livello : " + contatoreLivello, this.gw.getScreenSize().getxMax() / 8, this.gw.getScreenSize().getyMax() / 28, paint);
            this.canvas.drawText("Tempo rimamente : " + ((timer >= 0) ? timer + "s" : "0"), (float) (this.gw.getScreenSize().getxMax() / 4.5), this.gw.getScreenSize().getyMax() / 28, paint);
        }
        this.canvas.restore();
    }
}
