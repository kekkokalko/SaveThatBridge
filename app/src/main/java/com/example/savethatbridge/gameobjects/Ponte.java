package com.example.savethatbridge.gameobjects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.savethatbridge.R;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;

/**Classe che definisce il ponte del gioco**/
public class Ponte extends GameObject {

    private final Canvas canvas;
    private final Bitmap bitmap;
    private final RectF rect=new RectF();

    private float screen_semi_width;
    private float screen_semi_height;

    public Ponte(GameWorld gw, float x, float y, float larghezza, float altezza)
    {
        super(gw);

        this.canvas=new Canvas(gw.getBitmapBuffer());
        this.screen_semi_width = gw.toPixelsXLength(larghezza)/ 2;
        this.screen_semi_height=gw.toPixelsYLength(altezza)/2;

        //Definiizione del body, posizione e tipo
        BodyDef bodydef=new BodyDef();
        bodydef.setPosition((x+larghezza/2),y);
        bodydef.setType(BodyType.dynamicBody);

        //Creazione del body, disattivazione dello sleeping mode e settaggio user data
        this.body=gw.getWorld().createBody(bodydef);
        this.body.setSleepingAllowed(false);
        this.body.setUserData(this);

        //Settaggio shape
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(larghezza / 2, altezza / 2);

        //Settaggio fixture e caratteristiche conseguenti
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(polygonShape);
        fixtureDef.setFriction(0.1f);
        fixtureDef.setRestitution(0.4f);
        fixtureDef.setDensity(3f);
        this.body.createFixture(fixtureDef);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.box, options);
        bodydef.delete();
        polygonShape.delete();
    }


    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.rect.left = x - screen_semi_width;
        this.rect.bottom = y + screen_semi_height;
        this.rect.right = x + screen_semi_width;
        this.rect.top = y - screen_semi_height;
        //Disegno dello sprite
        this.canvas.drawBitmap(this.bitmap, null, this.rect, null);
        Paint paint = new Paint();
        paint.setARGB(255,92, 51, 23);
        this.canvas.drawCircle(x, y, this.gw.toPixelsXLength(Aggancio.getLarghezza() - 0.1f) / 2,paint);
        this.canvas.restore();

    }

}
