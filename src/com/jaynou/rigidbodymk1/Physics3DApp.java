package com.jaynou.rigidbodymk1;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;

public class Physics3DApp extends Application {
    private PhysicsEngine engine = new PhysicsEngine();
    private List<Box> bodyMeshes = new ArrayList<>();
    private Group root = new Group();

    @Override
    public void start(Stage primaryStage) {
        // Add initial bodies
        for (int i = 0; i < 5; i++) {
            addCube(new Vector3(i * 2 - 4, 5 + i, 0));
        }

        // Add ground plane
        RigidBody ground = new RigidBody(new Vector3(0, 0, 0), 0, true);
        engine.addBody(ground);
        Box groundBox = new Box(20, 1, 20);
        groundBox.setTranslateY(0);
        groundBox.setMaterial(new PhongMaterial(Color.DARKGREEN));
        root.getChildren().add(groundBox);

        // Camera setup
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-28);
        camera.setTranslateY(-15);
        camera.setTranslateX(-9);
        camera.setNearClip(0.1);
        camera.setFarClip(1000);
        Rotate rotateX = new Rotate(-25, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(16, Rotate.Y_AXIS);// Look downward
          // Turn toward center
        camera.getTransforms().addAll(rotateX,rotateY);

        SubScene subScene = new SubScene(root, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);
        subScene.setFill(Color.LIGHTGRAY);

        Group mainRoot = new Group();
        mainRoot.getChildren().add(subScene);

        Scene scene = new Scene(mainRoot);
        primaryStage.setTitle("JavaFX 3D Rigid Body Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();

        AnimationTimer timer = new AnimationTimer() {
            long lastTime = System.nanoTime();
            @Override
            public void handle(long now) {
                double dt = (now - lastTime) / 1e9;
                lastTime = now;

                engine.simulate(dt);

                int meshIndex = 0;
                for (RigidBody body : engine.getBodies()) {
                    if (!body.isStatic) {
                        Box box = bodyMeshes.get(meshIndex++);
                        box.setTranslateX(body.position.x);
                        box.setTranslateY(-body.position.y);
                        box.setTranslateZ(body.position.z);
                    }
                }
            }
        };
        timer.start();
    }

    private void addCube(Vector3 position) {
        RigidBody body = new RigidBody(position, 1.0, false);
        engine.addBody(body);

        Box box = new Box(1, 1, 1);
        box.setMaterial(new PhongMaterial(Color.hsb(Math.random() * 360, 1.0, 1.0)));
        root.getChildren().add(box);
        bodyMeshes.add(box);
    }
}


