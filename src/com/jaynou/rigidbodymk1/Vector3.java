package com.jaynou.rigidbodymk1;

public class Vector3 {
    public double x, y, z;

    public Vector3(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 subtract(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    public Vector3 multiply(double scalar) {
        return new Vector3(x * scalar, y * scalar, z * scalar);
    }
}