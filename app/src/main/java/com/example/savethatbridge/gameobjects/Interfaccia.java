package com.example.savethatbridge.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;

import java.util.ArrayList;

/**Classe che definisce informazioni utili per il player**/
public class Interfaccia extends GameObject{
    private final Canvas canvas;
    private final Paint paint;
    private static int contatoreTravi=0;
    private static int contatoreLivello=0;
    private static float timer = -1;
    public static ArrayList<Float> score= new ArrayList<Float>(2);

    public Interfaccia(GameWorld gameWorld) {
        super(gameWorld);

        this.canvas = new Canvas(gameWorld.getBitmapBuffer());
        this.paint = new Paint();

        //Definizione di un corpo statico senza fixture
        BodyDef bodyDef = new BodyDef();
        bodyDef.setType(BodyType.staticBody);

        //creazione corpo e definizione dello user data
        this.body = gameWorld.getWorld().createBody(bodyDef);
        this.body.setUserData(this);

        bodyDef.delete();

        //Settaggio colori, grandezza e allinemaneto delle texture
        if(GameWorld.getLivello()==1)
            this.paint.setColor(Color.BLACK);
        else if(GameWorld.getLivello()==2)
            this.paint.setColor(Color.YELLOW);
        else
            this.paint.setColor(Color.WHITE);

        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.paint.setTextSize(20);
        this.paint.setTextAlign(Paint.Align.CENTER);
    }

    public static void setContatoreLivello(int livello){ contatoreLivello=livello;}
    public static void setContatoreTravi(int contatore){ contatoreTravi=contatore;}
    public static void decrementaContatoreTravi(){ contatoreTravi--;}
    public static void setTimer(float t){timer=t;}
    public static void decrementaTimer(){
        if(timer>0)
            timer--;
    }

    public static float getTimer(){ return timer;}
    public synchronized static void setScore(int index,float timer){ score.set(index,score.get(index)+timer);}

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        if(GameWorld.getLivello()<=2) {
            this.canvas.drawText("Travi rimanenti : " + contatoreTravi, this.gw.getScreenSize().getxMax() / 28, this.gw.getScreenSize().getyMax() / 28, paint);
            this.canvas.drawText("Livello : " + contatoreLivello, this.gw.getScreenSize().getxMax() / 8, this.gw.getScreenSize().getyMax() / 28, paint);
            this.canvas.drawText("Tempo rimamente : " + ((timer >= 0) ? timer + "s" : "0"), (float) (this.gw.getScreenSize().getxMax() / 4.7f), this.gw.getScreenSize().getyMax() / 28, paint);
            this.canvas.drawText("Score : " + score.get(contatoreLivello-1), this.gw.getScreenSize().getxMax() / 38, this.gw.getScreenSize().getyMax() / 18, paint);
        }
        else{
            this.canvas.drawText("Score 1° livello: " + score.get(0), this.gw.getScreenSize().getxMax() / 24, this.gw.getScreenSize().getyMax()/2.8f, paint);
            this.canvas.drawText("Score 2° livello: " + score.get(1), this.gw.getScreenSize().getxMax() / 24, this.gw.getScreenSize().getyMax()/2.6f , paint);
        }
        this.canvas.restore();
    }
}
