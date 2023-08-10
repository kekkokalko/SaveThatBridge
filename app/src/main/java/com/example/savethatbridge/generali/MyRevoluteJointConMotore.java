package com.example.savethatbridge.generali;

import com.example.savethatbridge.gameobjects.GameWorld;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Joint;
import com.google.fpl.liquidfun.RevoluteJointDef;

/**Joint che definisce un joint rotazione con motore. Esso è applicato alle ruote della vespa con la carrozzeria**/
public class MyRevoluteJointConMotore {
    Joint joint;

    public MyRevoluteJointConMotore(GameWorld gw, Body a, Body b, float xb, float yb, float xa, float ya) {
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.setBodyA(a);
        jointDef.setBodyB(b);
        jointDef.setLocalAnchorA(xa, ya);
        jointDef.setLocalAnchorB(xb, yb);
        jointDef.setCollideConnected(true);
        jointDef.setEnableMotor(true);
        jointDef.setMotorSpeed(20);
        jointDef.setMaxMotorTorque(1500000);
        joint = gw.getWorld().createJoint(jointDef);

        jointDef.delete();
    }

    public Joint getJoint(){ return this.joint;}
}
