package com.jaynou.rigidbodymk1;

public class RigidBody {
    public Vector3 position;
    public Vector3 velocity;
    public Vector3 size;
    public double mass;
    public boolean isStatic;


    public RigidBody(Vector3 pos, double mass, boolean isStatic) {
        this.position = pos;
        this.velocity = new Vector3(0, 0, 0);
        this.mass = mass;
        this.isStatic = isStatic;
        this.size = new Vector3(1, 1, 1);
    }

    public void applyForce(Vector3 force, double dt) {
        if (!isStatic) {
            Vector3 acceleration = force.multiply(1.0 / mass);
            velocity = velocity.add(acceleration.multiply(dt));
        }
    }

    public void integrate(double dt) {
        if (!isStatic) {
            position = position.add(velocity.multiply(dt));
        }
    }
}

