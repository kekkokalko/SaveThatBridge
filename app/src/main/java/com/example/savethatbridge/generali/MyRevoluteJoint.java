package com.example.savethatbridge.generali;

import com.example.savethatbridge.gameobjects.GameWorld;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Joint;
import com.google.fpl.liquidfun.RevoluteJointDef;

public class MyRevoluteJoint {
    private final Joint joint;

    public MyRevoluteJoint(GameWorld gw, Body a, Body b, float xb, float yb, float xa, float ya) {
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.setBodyA(a);
        jointDef.setBodyB(b);
        jointDef.setLocalAnchorA(xa, ya);
        jointDef.setLocalAnchorB(xb, yb);
        jointDef.setCollideConnected(false);
        jointDef.setEnableMotor(false);
        this.joint = gw.getWorld().createJoint(jointDef);
        jointDef.delete();
    }

    // getters
    public Joint getJoint() {
        return this.joint;
    }
}