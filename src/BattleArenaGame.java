import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BattleArenaGame extends Application {

    private final double WIDTH = 800;
    private final double HEIGHT = 400;

    private Scene selectionScene;
    private Scene gameScene;

    private Fighter player1;
    private Fighter player2;

    private boolean p1Up, p1Down, p1Left, p1Right, p1Shoot, p1Switch;
    private boolean p2Up, p2Down, p2Left, p2Right, p2Shoot, p2Switch;

    private List<Projectile> projectiles = new ArrayList<>();

    private Label p1HealthLabel = new Label();
    private Label p2HealthLabel = new Label();
    private ProgressBar p1HealthBar = new ProgressBar(1.0);
    private ProgressBar p2HealthBar = new ProgressBar(1.0);
    private Label p1WeaponLabel = new Label();
    private Label p2WeaponLabel = new Label();
    private Label winnerLabel = new Label();
    private Label controlsLabel = new Label();

    private AnimationTimer gameLoop;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Battle Arena Game");
        setupSelectionScene(primaryStage);
        primaryStage.setScene(selectionScene);
        primaryStage.show();
    }

    private void setupSelectionScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #202020;");

        Label title = new Label("Battle Arena - Character Selection");
        title.setFont(Font.font(24));
        title.setTextFill(Color.CYAN);

        ComboBox<String> p1Choice = new ComboBox<>();
        p1Choice.getItems().addAll("M.Nader", "A.Khaled", "Mahmoud", "Youssef");
        p1Choice.setValue("M.Nader");

        ComboBox<String> p2Choice = new ComboBox<>();
        p2Choice.getItems().addAll("M.Nader", "A.Khaled", "Mahmoud", "Youssef");
        p2Choice.setValue("A.Khaled");

        Label p1Label = new Label("Player 1");
        p1Label.setTextFill(Color.LIGHTGREEN);

        Label p2Label = new Label("Player 2");
        p2Label.setTextFill(Color.LIGHTGREEN);

        HBox playersBox = new HBox(
                40,
                new VBoxWithLabel(p1Label, p1Choice),
                new VBoxWithLabel(p2Label, p2Choice)
        );
        playersBox.setAlignment(Pos.CENTER);

        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> {
            String name1 = p1Choice.getValue();
            String name2 = p2Choice.getValue();

            player1 = createFighterFromChoice(name1, 100, HEIGHT / 2 - 20);
            player2 = createFighterFromChoice(name2, WIDTH - 140, HEIGHT / 2 - 20);

            player1.setDisplayName(name1);
            player2.setDisplayName(name2);

            setupGameScene(stage);
            stage.setScene(gameScene);
        });

        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(playersBox, new Insets(20));
        BorderPane.setMargin(startButton, new Insets(20));

        root.setTop(title);
        root.setCenter(playersBox);
        root.setBottom(startButton);

        selectionScene = new Scene(root, WIDTH, HEIGHT);
    }

    private Fighter createFighterFromChoice(String choice, double x, double y) {
        switch (choice) {
            case "Mahmoud":
            case "Mage":
                return new Mage(x, y);
            case "A.Khaled":
            case "Archer":
                return new Archer(x, y);
            case "Youssef":
            case "M.Nader":
            case "Warrior":
            default:
                return new Warrior(x, y);
        }
    }

    private void setupGameScene(Stage stage) {
        Pane arena = new Pane();
        arena.setPrefSize(WIDTH, HEIGHT);
        arena.setStyle("-fx-background-color: #101010;");

        Line middleLine = new Line(WIDTH / 2, 0, WIDTH / 2, HEIGHT);
        middleLine.setStroke(Color.DARKGRAY);
        middleLine.setStrokeWidth(2);

        arena.getChildren().add(middleLine);
        arena.getChildren().addAll(player1.getNode(), player2.getNode());

        p1HealthLabel.setText(player1.getDisplayName() + " Health");
        p2HealthLabel.setText(player2.getDisplayName() + " Health");
        p1HealthLabel.setTextFill(Color.CYAN);
        p2HealthLabel.setTextFill(Color.CYAN);
        p1HealthLabel.setFont(Font.font(14));
        p2HealthLabel.setFont(Font.font(14));

        p1WeaponLabel.setTextFill(Color.ORANGE);
        p2WeaponLabel.setTextFill(Color.ORANGE);

        p1HealthBar.setPrefWidth(150);
        p2HealthBar.setPrefWidth(150);

        VBox p1HealthBox = new VBox(2, p1HealthLabel, p1HealthBar);
        p1HealthBox.setAlignment(Pos.CENTER_LEFT);
        VBox p2HealthBox = new VBox(2, p2HealthLabel, p2HealthBar);
        p2HealthBox.setAlignment(Pos.CENTER_RIGHT);

        HBox infoBar = new HBox(40, p1HealthBox, p1WeaponLabel, p2WeaponLabel, p2HealthBox);
        infoBar.setAlignment(Pos.CENTER);
        infoBar.setPadding(new Insets(5));
        infoBar.setStyle("-fx-background-color: #262626;");

        winnerLabel.setFont(Font.font(28));
        winnerLabel.setTextFill(Color.LAWNGREEN);
        winnerLabel.setVisible(false);

        controlsLabel.setTextFill(Color.SILVER);
        controlsLabel.setText("P1: WASD move, F shoot, R switch weapon   |   P2: Arrows move, L shoot, P switch weapon");

        VBox bottomBox = new VBox(5, winnerLabel, controlsLabel);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(5));

        BorderPane root = new BorderPane();
        root.setTop(infoBar);
        root.setCenter(arena);
        root.setBottom(bottomBox);

        gameScene = new Scene(root, WIDTH, HEIGHT);

        updateHUD();
        setupInputHandlers(gameScene);
        startGameLoop(arena);
    }

    private void setupInputHandlers(Scene scene) {
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W: p1Up = true; break;
                case S: p1Down = true; break;
                case A: p1Left = true; break;
                case D: p1Right = true; break;
                case F: p1Shoot = true; break;
                case R: p1Switch = true; break;

                case UP: p2Up = true; break;
                case DOWN: p2Down = true; break;
                case LEFT: p2Left = true; break;
                case RIGHT: p2Right = true; break;
                case L: p2Shoot = true; break;
                case P: p2Switch = true; break;
            }
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W: p1Up = false; break;
                case S: p1Down = false; break;
                case A: p1Left = false; break;
                case D: p1Right = false; break;
                case F: p1Shoot = false; break;
                case R: p1Switch = false; break;

                case UP: p2Up = false; break;
                case DOWN: p2Down = false; break;
                case LEFT: p2Left = false; break;
                case RIGHT: p2Right = false; break;
                case L: p2Shoot = false; break;
                case P: p2Switch = false; break;
            }
        });
    }

    private void startGameLoop(Pane arena) {
        final double speed = 3.0;

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (player1.getHealth() <= 0 || player2.getHealth() <= 0) {
                    stop();
                    return;
                }

                double dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;

                if (p1Up) dy1 -= speed;
                if (p1Down) dy1 += speed;
                if (p1Left) dx1 -= speed;
                if (p1Right) dx1 += speed;

                if (p2Up) dy2 -= speed;
                if (p2Down) dy2 += speed;
                if (p2Left) dx2 -= speed;
                if (p2Right) dx2 += speed;

                player1.move(dx1, dy1, WIDTH, HEIGHT);
                player2.move(dx2, dy2, WIDTH, HEIGHT);

                if (p1Switch) {
                    player1.nextWeapon();
                    p1Switch = false;
                    updateHUD();
                }
                if (p2Switch) {
                    player2.nextWeapon();
                    p2Switch = false;
                    updateHUD();
                }

                handleShooting(player1, p1Shoot, 1);
                handleShooting(player2, p2Shoot, -1);

                Iterator<Projectile> it = projectiles.iterator();
                while (it.hasNext()) {
                    Projectile p = it.next();
                    p.update();

                    Fighter target = (p.getOwner() == player1) ? player2 : player1;

                    if (p.getShape().getBoundsInParent()
                            .intersects(target.getNode().getBoundsInParent())) {

                        target.damage(p.getWeapon().getDamage());
                        arena.getChildren().remove(p.getShape());
                        it.remove();
                        updateHUD();
                        checkWinner();
                        continue;
                    }

                    if (p.isOffScreen(WIDTH)) {
                        arena.getChildren().remove(p.getShape());
                        it.remove();
                    }
                }
            }
        };

        gameLoop.start();
    }

    private void handleShooting(Fighter fighter, boolean shootPressed, int direction) {
        if (!shootPressed) return;

        Weapon w = fighter.getCurrentWeapon();
        if (w == null) return;

        long nowMs = System.currentTimeMillis();
        if (nowMs - fighter.getLastShotTime() < w.getCooldownMs()) return;

        fighter.setLastShotTime(nowMs);

        double speedX = w.getProjectileSpeed() * direction;
        Projectile proj = new Projectile(
                fighter.getCenterX(),
                fighter.getCenterY(),
                speedX,
                w,
                fighter
        );

        BorderPane root = (BorderPane) gameScene.getRoot();
        Pane arena = (Pane) root.getCenter();
        arena.getChildren().add(proj.getShape());
        projectiles.add(proj);
    }

    private void updateHUD() {
        double p1Progress = player1.getHealth() / 100.0;
        double p2Progress = player2.getHealth() / 100.0;
        if (p1Progress < 0) p1Progress = 0;
        if (p2Progress < 0) p2Progress = 0;
        p1HealthBar.setProgress(p1Progress);
        p2HealthBar.setProgress(p2Progress);

        Weapon w1 = player1.getCurrentWeapon();
        Weapon w2 = player2.getCurrentWeapon();

        if (w1 != null) {
            p1WeaponLabel.setText(player1.getDisplayName() + " Weapon: " + w1.getName());
        } else {
            p1WeaponLabel.setText(player1.getDisplayName() + " Weapon: None");
        }

        if (w2 != null) {
            p2WeaponLabel.setText(player2.getDisplayName() + " Weapon: " + w2.getName());
        } else {
            p2WeaponLabel.setText(player2.getDisplayName() + " Weapon: None");
        }
    }

    private void checkWinner() {
        if (player1.getHealth() <= 0) {
            winnerLabel.setText(player2.getDisplayName() + " Wins!");
            winnerLabel.setVisible(true);
        } else if (player2.getHealth() <= 0) {
            winnerLabel.setText(player1.getDisplayName() + " Wins!");
            winnerLabel.setVisible(true);
        }
    }

    private static class VBoxWithLabel extends VBox {
        public VBoxWithLabel(Label label, javafx.scene.Node node) {
            super(5, label, node);
            setAlignment(Pos.CENTER);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}