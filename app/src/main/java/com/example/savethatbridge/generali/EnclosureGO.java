package com.example.savethatbridge.generali;

import android.graphics.Bitmap;

import com.example.savethatbridge.gameobjects.GameObject;
import com.example.savethatbridge.gameobjects.GameWorld;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.PolygonShape;


public class EnclosureGO extends GameObject {
    private static final float THICKNESS = 0;

    public EnclosureGO(GameWorld gw, float leftX, float rightX, float bottomY, float topY) {
        super(gw);

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();

        // a body, default position is (0,0) and default type is staticBody
        this.body = gw.getWorld().createBody(bodyDef);
        this.name = "EnclosureGO";
        this.body.setUserData(this);
        this.body.setType(BodyType.staticBody);

        // enclosurego's shape
        PolygonShape polygonShape = new PolygonShape();

        // create the top and bottom sides of the enclosure
        polygonShape.setAsBox(rightX - leftX, THICKNESS, leftX + (rightX - leftX) / 2, bottomY, 0);
        this.body.createFixture(polygonShape, 0);
        polygonShape.setAsBox(rightX - leftX, THICKNESS, leftX + (rightX - leftX) / 2, topY, 0);
        this.body.createFixture(polygonShape, 0);

        // create the left and right sides of the enclosure
        polygonShape.setAsBox(THICKNESS, topY - bottomY, leftX, bottomY + (topY - bottomY) / 2, 0);
        this.body.createFixture(polygonShape, 0);
        polygonShape.setAsBox(THICKNESS, topY - bottomY, rightX, bottomY + (topY - bottomY) / 2, 0);
        this.body.createFixture(polygonShape, 0);

        // clean up native objects
        bodyDef.delete();
        polygonShape.delete();
    }

    // draw enclosurego
    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        // this method is empty since EnclosureGO does not need to be drawn
    }
}