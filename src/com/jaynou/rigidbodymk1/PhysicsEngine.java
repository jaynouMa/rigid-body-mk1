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
            for (int j = i + 1; j < bodies.size(); j++) {
                RigidBody b = bodies.get(j);

                if (a == b) continue;

                if (isColliding(a, b)) {
                    resolvePenetration(a, b);
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

    private void resolvePenetration(RigidBody a, RigidBody b) {
        Vector3 delta = b.position.subtract(a.position);

        double overlapX = a.size - Math.abs(delta.x);
        double overlapY = a.size - Math.abs(delta.y);
        double overlapZ = a.size - Math.abs(delta.z);

        double minOverlap = Math.min(overlapX, Math.min(overlapY, overlapZ));
        Vector3 mtv = new Vector3(0, 0, 0);

        if (minOverlap == overlapX) {
            mtv = new Vector3(Math.signum(delta.x), 0, 0).multiply(minOverlap);
        } else if (minOverlap == overlapY) {
            mtv = new Vector3(0, Math.signum(delta.y), 0).multiply(minOverlap);
        } else {
            mtv = new Vector3(0, 0, Math.signum(delta.z)).multiply(minOverlap);
        }

        if (a.isStatic && !b.isStatic) {
            b.position = b.position.add(mtv);
        } else if (!a.isStatic && b.isStatic) {
            a.position = a.position.subtract(mtv);
        } else if (!a.isStatic && !b.isStatic) {
            a.position = a.position.subtract(mtv.multiply(0.5));
            b.position = b.position.add(mtv.multiply(0.5));
        }
    }

    private void resolveCollision(RigidBody a, RigidBody b) {
        Vector3 normal = b.position.subtract(a.position).normalize();
        Vector3 relativeVelocity = b.velocity.subtract(a.velocity);
        double velocityAlongNormal = relativeVelocity.dot(normal);

        if (velocityAlongNormal > 0) return;

        double restitution = 0.5;
        double invMassA = a.isStatic ? 0 : 1.0 / a.mass;
        double invMassB = b.isStatic ? 0 : 1.0 / b.mass;

        double impulseMagnitude = -(1 + restitution) * velocityAlongNormal;
        impulseMagnitude /= (invMassA + invMassB);

        Vector3 impulse = normal.multiply(impulseMagnitude);

        if (!a.isStatic) a.velocity = a.velocity.subtract(impulse.multiply(invMassA));
        if (!b.isStatic) b.velocity = b.velocity.add(impulse.multiply(invMassB));
    }
}
