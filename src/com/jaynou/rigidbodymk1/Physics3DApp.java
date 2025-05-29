package com.jaynou.rigidbodymk1;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Physics3DApp extends Application {
    private PhysicsEngine engine = new PhysicsEngine();
    private List<Shape3D> bodyMeshes = new ArrayList<>();
    private Group root = new Group();


    @Override
    public void start(Stage primaryStage) {
        for (int i = 0; i < 5; i++) {
            addCube(new Vector3(((Math.random() * 20) - 10), 5 + i, ((Math.random() * 20) - 10)));
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
        Rotate rotateX = new Rotate(-25, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(16, Rotate.Y_AXIS);
        camera.getTransforms().addAll(rotateX, rotateY);

        SubScene subScene = new SubScene(root, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);

        // Spawn Button
        TextField xField = new TextField("0");
        TextField yField = new TextField("10");
        TextField zField = new TextField("0");
        Button spawnButton = new Button("Spawn Cube");
        Button spawnSphere = new Button("Spawn Sphere");

        HBox controls = new HBox(10, new Label("X:"), xField, new Label("Y:"), yField, new Label("Z:"), zField, spawnButton, spawnSphere);
        controls.setPadding(new Insets(10));
        controls.setStyle("-fx-background-color: rgba(255,255,255,0);");

        // Action for spawn button
        spawnButton.setOnAction(e -> {
            try {
                double x = Double.parseDouble(xField.getText());
                double y = Double.parseDouble(yField.getText());
                double z = Double.parseDouble(zField.getText());
                addCube(new Vector3(x, y, z));
            } catch (NumberFormatException ex) {
                System.out.println("Put in a number");
            }
        });

        spawnSphere.setOnAction(e -> {
            try {
                double x = Double.parseDouble(xField.getText());
                double y = Double.parseDouble(yField.getText());
                double z = Double.parseDouble(zField.getText());
                addSphere(new Vector3(x, y, z));
            } catch (NumberFormatException ex) {
                System.out.println("Put in a number");
            }
        });

        // Layout
        StackPane layout = new StackPane();
        layout.getChildren().addAll(subScene, controls);
        StackPane.setMargin(controls, new Insets(10));
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setTitle("apcsa final project");
        primaryStage.setScene(scene);
        primaryStage.show();

        // ticks
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
                        Shape3D box = bodyMeshes.get(meshIndex++);
                        box.setTranslateX(body.position.x);
                        box.setTranslateY(-body.position.y);
                        box.setTranslateZ(body.position.z);
                    }
                }
            }
        };
        timer.start();
    }

    private void addCube(Vector3 position){
        RigidBody body = new RigidBody(position, 1.0, false);
        engine.addBody(body);

        Box box = new Box(1, 1, 1);
        box.setMaterial(new PhongMaterial(Color.hsb(Math.random() * 360, 1.0, 1.0)));
        root.getChildren().add(box);
        bodyMeshes.add(box);
    }

    private void addSphere(Vector3 position){
        RigidBody body = new RigidBody(position, 1.0, false);
        engine.addBody(body);

        Sphere sphere = new Sphere();
        sphere.setMaterial(new PhongMaterial(Color.hsb(Math.random() * 360, 1.0, 1.0)));
        root.getChildren().add(sphere);
        bodyMeshes.add(sphere);
    }
}


