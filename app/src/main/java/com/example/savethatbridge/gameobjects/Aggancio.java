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

public class Aggancio extends GameObject{

    private static final float larghezza=1.0f;
    private static final float altezza=1.0f;
    private final Bitmap bitmap;
    private final Canvas canvas;
    private final RectF rect=new RectF();
    private final float screen_semi_width, screen_semi_height;

    public Aggancio(GameWorld gameWorld,float x, float y){
        super(gameWorld);

        this.canvas=new Canvas(gameWorld.getBitmapBuffer());
        this.screen_semi_width = gw.toPixelsXLength(larghezza)/2;
        this.screen_semi_height = gw.toPixelsYLength(altezza)/2;

        BodyDef bodydef=new BodyDef();
        bodydef.setPosition(x,y);
        bodydef.setType(BodyType.staticBody);


        this.body=gw.getWorld().createBody(bodydef);
        this.body.setUserData(this);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(larghezza , altezza);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(polygonShape);
        fixtureDef.setFriction(0f);
        this.body.createFixture(fixtureDef);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.aggancio, options);
        bodydef.delete();
        polygonShape.delete();
    }

    public static float getLarghezza(){return larghezza;}
    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        canvas.save();
        rect.left = x - screen_semi_width;
        rect.bottom = y + screen_semi_height;
        rect.right = x + screen_semi_width;
        rect.top = y - screen_semi_height;
        this.canvas.drawBitmap(this.bitmap, null, this.rect, null);
        this.canvas.restore();
    }
}
