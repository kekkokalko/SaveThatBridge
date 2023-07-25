package com.example.savethatbridge.generali;

public class Box {
    private final float xMin, yMin, xMax, yMax, height, width;

    public Box(float xMin, float yMin, float xMax, float yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.height = yMax - yMin;
        this.width = xMax - xMin;
    }

    // getters
    public float getxMin() {
        return this.xMin;
    }

    public float getyMin() {
        return this.yMin;
    }

    public float getxMax() {
        return this.xMax;
    }

    public float getyMax() {
        return this.yMax;
    }

    public float getHeight() {
        return this.height;
    }

    public float getWidth() {
        return this.width;
    }
}
