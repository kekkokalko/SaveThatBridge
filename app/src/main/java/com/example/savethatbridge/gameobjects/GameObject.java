package com.example.savethatbridge.gameobjects;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.example.savethatbridge.generali.Box;
import com.google.fpl.liquidfun.Body;

/**Classe GameObject, definisce un oggetto nel mondo**/
public abstract class GameObject {
    public Body body;
    public String name;
    protected GameWorld gw;

    public GameObject(GameWorld gw) {
        this.gw = gw;
    }

    /**Metodo concreto per il disegno di un oggetto. Richiama draw astratto (Pattern Template method). Questo perchè c'è un pezzo di metodo che è unico per tutti e un altro che è
     soggettivo per ogni oggetto delle varie sottoclassi.**/
    public boolean draw(Bitmap buffer) {
        if (this.body != null) {
            // Physical position of the center
            float x = this.body.getPositionX();
            float y = this.body.getPositionY();
            float angle = this.body.getAngle();

            // Cropping
            Box view = this.gw.getPhysicalSize();
            if (x > view.getxMin() && x < view.getxMax() && y > view.getyMin() && y < view.getyMax()) {
                // Screen position
                float screen_x = this.gw.setWorldToFrameX(x);
                float screen_y = this.gw.setWorldToFrameY(y);
                //richiamo del metodo astratto. Permette il disegno delle specifiche carattaristiche degli oggetti specifici
                this.draw(buffer, screen_x, screen_y, angle);
                return true;
            } else return false;
        } else {
            this.draw(buffer, 0, 0, 0);
            return true;
        }
    }

    public abstract void draw(Bitmap buf, float x, float y, float angle);

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}