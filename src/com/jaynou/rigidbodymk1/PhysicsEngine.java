package com.jaynou.rigidbodymk1;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {
    private ArrayList<RigidBody> bodies = new ArrayList<>();
    private Vector3 gravity = new Vector3(0, -9.81, 0);

    public void addBody(RigidBody body){
        bodies.add(body);
    }

    public List<RigidBody> getBodies(){
        return bodies;
    }

    public void simulate(double dt) {
        for (RigidBody body : bodies){
            if (!body.isStatic && body.position.y > 0) {
                body.applyForce(gravity.multiply(body.mass), dt);
            }
        }

        for (RigidBody body : bodies){
            body.integrate(dt);

            if (!body.isStatic && body.position.y < 0.5) {
                body.velocity.y *= -0.5;
                body.position.y = 0.5;
            }
        }

        for (int i = 0; i < bodies.size(); i++){
            RigidBody a = bodies.get(i);
            if (!a.isStatic) {
            for (int j = i + 1; j < bodies.size(); j++) {
                RigidBody b = bodies.get(j);
                if (b.isStatic) continue;

                if (checkAABBCollision(a, b)) {
                    resolveCollision(a, b);
                }
            }
            }
        }
    }

    private boolean checkAABBCollision(RigidBody a, RigidBody b) {
        Vector3 aMin = a.position.subtract(a.size.multiply(0.5));
        Vector3 aMax = a.position.add(a.size.multiply(0.5));
        Vector3 bMin = b.position.subtract(b.size.multiply(0.5));
        Vector3 bMax = b.position.add(b.size.multiply(0.5));

        return (aMin.x <= bMax.x && aMax.x >= bMin.x) &&
                (aMin.y <= bMax.y && aMax.y >= bMin.y) &&
                (aMin.z <= bMax.z && aMax.z >= bMin.z);
    }

    private void resolveCollision(RigidBody a, RigidBody b){

        Vector3 n = b.position.subtract(a.position);

        double mag = Math.sqrt(n.x * n.x + n.y * n.y + n.z * n.z);
        if (mag == 0) return; // Avoid divide by zero
        n = new Vector3(n.x / mag, n.y / mag, n.z / mag);

        Vector3 rv = b.velocity.subtract(a.velocity);

        double velAlongNormal = rv.x * n.x + rv.y * n.y + rv.z * n.z;
        if (velAlongNormal > 0) return; // They're separating

        double invMassA = 1.0 / a.mass;
        double invMassB = 1.0 / b.mass;
        double j = -0.5 * velAlongNormal / (invMassA + invMassB);

        Vector3 impulse = new Vector3(n.x * j, n.y * j, n.z * j);
        a.velocity = b.velocity.subtract(impulse.multiply(invMassA));
        b.velocity = a.velocity.add(impulse.multiply(invMassB));
    }
}
