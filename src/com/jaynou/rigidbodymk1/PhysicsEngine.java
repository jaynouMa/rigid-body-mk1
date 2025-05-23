package com.jaynou.rigidbodymk1;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {
    private List<RigidBody> bodies = new ArrayList<>();
    private Vector3 gravity = new Vector3(0, -9.81, 0);

    public void addBody(RigidBody body) {
        bodies.add(body);
    }

    public List<RigidBody> getBodies() {
        return bodies;
    }

    public void simulate(double dt) {
        for (RigidBody body : bodies) {
            if (!body.isStatic && body.position.y > 0) {
                body.applyForce(gravity.multiply(body.mass), dt);
            }
        }
        for (RigidBody body : bodies) {
            body.integrate(dt);
            if (!body.isStatic && body.position.y < 0.5) {
                body.velocity.y *= -0.5;
                body.position.y = 0.5;
            }
        }

        // Basic cube collision (AABB)
        for (int i = 0; i < bodies.size(); i++) {
            RigidBody a = bodies.get(i);
            if (a.isStatic) continue;
            for (int j = i + 1; j < bodies.size(); j++) {
                RigidBody b = bodies.get(j);
                if (b.isStatic) continue;

                if (isColliding(a, b)) {
                    resolveCollision(a, b);
                }
            }
        }
    }

    private boolean isColliding(RigidBody a, RigidBody b) {
        double halfSize = a.size / 2.0;
        return Math.abs(a.position.x - b.position.x) < a.size &&
                Math.abs(a.position.y - b.position.y) < a.size &&
                Math.abs(a.position.z - b.position.z) < a.size;
    }

    private void resolveCollision(RigidBody a, RigidBody b) {
        Vector3 delta = b.position.subtract(a.position);
        Vector3 normal = new Vector3(
                Math.signum(delta.x),
                Math.signum(delta.y),
                Math.signum(delta.z)
        );
        Vector3 response = normal.multiply(0.1);
        a.velocity = a.velocity.subtract(response);
        b.velocity = b.velocity.add(response);
    }
}
