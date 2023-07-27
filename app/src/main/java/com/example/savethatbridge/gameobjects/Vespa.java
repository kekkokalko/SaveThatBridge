package com.example.savethatbridge.gameobjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;

import com.example.savethatbridge.R;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;

public class Vespa extends GameObject{
    static final float larghezza = 2.5f, altezza = 1.5f;
    private static float screen_semi_width, screen_semi_height;

    private final Canvas canvas;
    private final Paint paint = new Paint();
    private final RectF dest = new RectF();
    private final Bitmap bitmap;
    private Context context;
    private static MediaPlayer mediaPlayer;

    public Vespa(Context context, GameWorld gameWorld, float x, float y) {
        super(gameWorld);

        this.canvas = new Canvas(gameWorld.getBitmapBuffer());
        screen_semi_width = gw.toPixelsXLength(larghezza) / 2;
        screen_semi_height = gw.toPixelsYLength(altezza) / 2;
        this.context=context;

        BodyDef bdef = new BodyDef();
        bdef.setPosition(x, y);
        bdef.setType(BodyType.dynamicBody);

        this.body = gameWorld.getWorld().createBody(bdef);
        this.body.setSleepingAllowed(false);
        this.name = "Voiture";
        this.body.setUserData(this);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(larghezza / 2, altezza / 2);

        FixtureDef fixturedef = new FixtureDef();
        fixturedef.setShape(polygonShape);
        fixturedef.setFriction(0);
        fixturedef.setRestitution(0);
        fixturedef.setDensity(7);
        this.body.createFixture(fixturedef);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.civile, options);
        mediaPlayer=MediaPlayer.create(this.context,R.raw.caduta);

        // clean up native objects
        fixturedef.delete();
        bdef.delete();
        polygonShape.delete();
    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);

        if(this.body.getPositionY()>=0.5f)
            mediaPlayer.start();
        this.dest.left = x - screen_semi_width;
        this.dest.bottom = y + screen_semi_height;
        this.dest.right = x + screen_semi_width;
        this.dest.top = y - screen_semi_height;
        // Simple box
        this.canvas.drawBitmap(this.bitmap, null, this.dest, null);
        this.canvas.restore();
    }
}
