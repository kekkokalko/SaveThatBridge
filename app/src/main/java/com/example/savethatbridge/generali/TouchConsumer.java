package com.example.savethatbridge.generali;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.savethatbridge.DragMeImpl.Input;
import com.example.savethatbridge.R;
import com.example.savethatbridge.gameobjects.Aggancio;
import com.example.savethatbridge.gameobjects.GameObject;
import com.example.savethatbridge.gameobjects.GameWorld;
import com.example.savethatbridge.gameobjects.Ponte;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.MouseJoint;
import com.google.fpl.liquidfun.QueryCallback;

/**Classe che definisce l'azione da intraprendere al tocco sullo schermo**/
public class TouchConsumer {
    private GameObject oggetto;
    private final GameWorld gameWorld;
    private MouseJoint mouseJoint;
    private int activePointerID;
    private Fixture touchedFixture;
    private final QueryCallback touchQueryCallback = new TouchQueryCallback();
    private final static float POINTER_SIZE = 0.5f;
    private MediaPlayer mediaPlayer;
    private Context context;
    
    public TouchConsumer(Context context,GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.oggetto = null;
        this.context=context;
    }

    public void consumeTouchEvent(Input.TouchEvent event) {
        if(event.type==Input.TouchEvent.TOUCH_DOWN)
            consumeTouchDown(event);
    }

    private class TouchQueryCallback extends QueryCallback {
        public boolean reportFixture(Fixture fixture) {
            touchedFixture = fixture;
            return true;
        }
    }
    private void consumeTouchDown(Input.TouchEvent event){
        int pointerId = event.pointer;

        if (this.mouseJoint != null) return;

        float x = this.gameWorld.setScreenToWorldX(event.x);
        float y = this.gameWorld.setScreenToWorldY(event.y);

        Log.d("MultiTouchHandler", "touch down at " + x + ", " + y);

        this.touchedFixture = null;
        this.gameWorld.getWorld().queryAABB(this.touchQueryCallback, x - POINTER_SIZE, y - POINTER_SIZE, x + POINTER_SIZE, y + POINTER_SIZE);
        if (this.touchedFixture == null) return;

        Body touchedBody = this.touchedFixture.getBody();
        Object userData = touchedBody.getUserData();
        if(userData==null) return;

        GameObject oggettoToccato = (GameObject) userData;
        this.activePointerID = pointerId;
        Log.d("MultiTouchHandler", "touched game object " + oggettoToccato.name);
        mediaPlayer= MediaPlayer.create(this.context, R.raw.costruzione);
        //Se l'oggetto toccatto è un aggancio del ponte o di supporto
        if(oggettoToccato instanceof Aggancio || oggettoToccato instanceof Ponte)
            gestioneToccoAgganci(oggettoToccato);
    }

    private void gestioneToccoAgganci(GameObject oggettoToccato) {
        //Se è stato toccato un aggancio del ponte
        if(oggettoToccato instanceof Ponte){
            //Se il vecchio oggetto toccato è un aggancio di rafforzamento
            if(this.oggetto != null && this.oggetto instanceof Aggancio){
                this.gameWorld.aggiuntaTrave(this.oggetto,oggettoToccato);
                mediaPlayer.start();
                this.oggetto=null;
            }
            else
                this.oggetto=oggettoToccato;
        }
        //Se è stato toccato un aggancio di rafforzamento
        else if(oggettoToccato instanceof Aggancio){
            //Se il vecchio oggetto toccato è un aggancio del ponte
            if(this.oggetto!=null && this.oggetto instanceof Ponte){
                this.gameWorld.aggiuntaTrave(oggettoToccato,this.oggetto);
                mediaPlayer.start();
                this.oggetto=null;
            }
            else
                this.oggetto = oggettoToccato;
        }
    }
}
