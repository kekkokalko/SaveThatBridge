package com.example.savethatbridge.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.FixtureDef;

public class Ruota extends GameObject{
    static final float larghezza = 0.6f, altezza = 0.6f, densità = 0.7f;
    private static float screen_semi_width, screen_semi_height;
    private final Paint paint = new Paint();
    private final Canvas canvas;
    private final RectF dest = new RectF();

    public Ruota(GameWorld gameWorld, float x, float y) {
        super(gameWorld);
        this.canvas = new Canvas(gameWorld.getBitmapBuffer());
        screen_semi_width = gw.toPixelsXLength(larghezza) / 2;
        screen_semi_height = gw.toPixelsYLength(altezza) / 2;

        // a body definition: position and type
        BodyDef bdef = new BodyDef();
        bdef.setPosition(x, y);
        bdef.setType(BodyType.dynamicBody);
        // a body
        this.body = gameWorld.getWorld().createBody(bdef);
        this.body.setSleepingAllowed(false);
        this.body.setUserData(this);

        CircleShape box = new CircleShape();
        box.setRadius(larghezza / 2);
        FixtureDef fixturedef = new FixtureDef();
        fixturedef.setShape(box);
        fixturedef.setFriction(1);       // default 0.2
        fixturedef.setRestitution(0.1f);    // default 0
        fixturedef.setDensity(0.7f);     // default 0
        this.body.createFixture(fixturedef);

        int color = Color.argb(255, 0, 0, 0);
        this.paint.setColor(color);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);

        // clean up native objects
        fixturedef.delete();
        bdef.delete();
        box.delete();
    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.dest.left = x - screen_semi_width;
        this.dest.bottom = y + screen_semi_height;
        this.dest.right = x + screen_semi_width;
        this.dest.top = y - screen_semi_height;
        this.canvas.drawCircle(x, y, screen_semi_width, this.paint);
        this.canvas.restore();
    }
}
