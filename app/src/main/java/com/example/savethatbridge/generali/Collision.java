package com.example.savethatbridge.generali;

import com.example.savethatbridge.gameobjects.GameObject;

public class Collision {
    private final GameObject a,b;

    public Collision(GameObject a, GameObject b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public int hashCode() {
        return a.hashCode() ^ b.hashCode();
    }

    public GameObject getA() {
        return this.a;
    }

    public GameObject getB() {
        return this.b;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Collision))
            return false;
        Collision otherCollision = (Collision) other;
        return (a.equals(otherCollision.a) && b.equals(otherCollision.b)) ||
                (a.equals(otherCollision.b) && b.equals(otherCollision.a));
    }

}
