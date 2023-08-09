package com.example.savethatbridge.gameobjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;

import com.example.savethatbridge.R;
import com.example.savethatbridge.generali.AndroidFastRenderView;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.Vec2;

/**Classe che definisce il terrorista del gioco**/
public class Terrorista extends GameObject{
    private final Canvas canvas;
    private final Bitmap bitmap;
    private RectF rect= new RectF();
    private final float screenSemiWidth, screenSemiHeight;
    private int timer=0;
    private int sprite=1;
    private final Rect src;
    private MediaPlayer mediaPlayer;
    private Context context;

    public Terrorista(Context context,GameWorld gw, float x, float y){
        super(gw);
        //grandezza visiva
        int altezza=3;
        int larghezza=3;
        this.context=context;

        this.canvas = new Canvas(gw.getBitmapBuffer());
        this.src = new Rect(0, 0, 686, 1305);
        this.screenSemiHeight = gw.toPixelsYLength(altezza) / 2;
        this.screenSemiWidth = gw.toPixelsXLength(larghezza) / 2;

        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.kinematicBody);
        //definizione della velocità del corpo
        bodyDef.setLinearVelocity(new Vec2((float) 5, 0));

        this.body = gw.getWorld().createBody(bodyDef);
        this.body.setSleepingAllowed(false);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.terrorista_spritesheet, options);

        mediaPlayer= MediaPlayer.create(this.context, R.raw.risata);
        mediaPlayer.setLooping(false);

        bodyDef.delete();
    }
    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        mediaPlayer.start();
        this.timer++;
        //Se hai visto tutte le animazioni torna alla prima
        if(this.timer==8){
            aggiornaAnimazione();
            this.timer=0;
        }

        controlloBomba();

        //se lo sprite è arrivato all'estrema destra dello schermo, rimuovilo
        if (this.body.getPositionX() > this.gw.getPhysicalSize().getxMax()-4) {
            AndroidFastRenderView.setRimozioneTerrorista(true);
        }

        this.canvas.save();
        this.rect.top = y - this.screenSemiHeight;
        this.rect.bottom = y + this.screenSemiHeight;
        this.rect.right = x + this.screenSemiWidth;
        this.rect.left = x - this.screenSemiWidth;
        this.canvas.drawBitmap(this.bitmap, this.src, this.rect, null);
        this.canvas.restore();
    }

    private void aggiornaAnimazione() {
        this.src.left = this.sprite * 686;
        this.src.right = this.sprite * 686 + 686;
        this.sprite = (this.sprite + 1) % 8;
    }

    /**Metodo che controlla quando disegnare le bombe a seconda della posizione del terrorista**/
    private void controlloBomba(){
        for(int i=0;i<GameWorld.getBombe().size();i++)
            if(this.body.getPositionX()>GameWorld.getBombe().get(i).body.getPositionX()){
                AndroidFastRenderView.setGeneraBomba(true);
                AndroidFastRenderView.posizione_bomba=i;
            }
    }
}
