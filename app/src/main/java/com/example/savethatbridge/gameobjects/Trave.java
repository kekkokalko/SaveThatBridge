package com.example.savethatbridge.gameobjects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.example.savethatbridge.R;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;

/**Classe che definisce la trave di supporto**/
public class Trave extends GameObject{
    private final Canvas canvas;
    private final Bitmap bitmap;
    private final float screenSemiWidth,screenSemiHeight;
    private final RectF dest= new RectF();

    public Trave(GameWorld gameWorld,float x, float y, float larghezza, float altezza, float angolo) {
        super(gameWorld);

        this.canvas=new Canvas(gameWorld.getBitmapBuffer());
        this.screenSemiHeight = gw.toPixelsYLength(altezza) / 2;
        this.screenSemiWidth = gw.toPixelsXLength(larghezza) / 2;

        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.staticBody);

        this.body = gameWorld.getWorld().createBody(bodyDef);
        this.body.setSleepingAllowed(false);
        this.body.setTransform(x, y, angolo);
        this.body.setUserData(this);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(larghezza / 2, altezza / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(polygonShape);
        fixtureDef.setFriction(0.1f);
        fixtureDef.setRestitution(0.4f);
        fixtureDef.setDensity(7f);
        this.body.createFixture(fixtureDef);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.aggancio, options);

        fixtureDef.delete();
        bodyDef.delete();
        polygonShape.delete();
    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.dest.top = y - this.screenSemiHeight;
        this.dest.bottom = y + this.screenSemiHeight;
        this.dest.right = x + this.screenSemiWidth;
        this.dest.left = x - this.screenSemiWidth;
        this.canvas.drawBitmap(this.bitmap, null, this.dest, null);
        this.canvas.restore();
    }
}
