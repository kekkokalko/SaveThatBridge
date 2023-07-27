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

public class Ponte extends GameObject {

    private final Canvas canvas;
    private final Bitmap bitmap;
    private final RectF rect=new RectF();

    private float screen_semi_width;
    private float screen_semi_height;
    private float larghezza;

    public Ponte(GameWorld gw, float x, float y, float larghezza, float altezza)
    {
        super(gw);

        this.canvas=new Canvas(gw.getBitmapBuffer());
        this.screen_semi_width = gw.toPixelsXLength(larghezza)/ 2;
        this.screen_semi_height=gw.toPixelsYLength(altezza)/2;

        BodyDef bodydef=new BodyDef();
        bodydef.setPosition((x+larghezza/2),y);
        bodydef.setType(BodyType.dynamicBody);

        this.body=gw.getWorld().createBody(bodydef);
        this.body.setSleepingAllowed(false);
        this.body.setUserData(this);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(larghezza / 2, altezza / 2);

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
        // Sprite
        this.canvas.drawBitmap(this.bitmap, null, this.rect, null);
        Paint paint = new Paint();
        paint.setARGB(255,92, 51, 23);
        this.canvas.drawCircle(x, y, this.gw.toPixelsXLength(Aggancio.getLarghezza() - 0.1f) / 2,paint);
        this.canvas.restore();

    }

}
