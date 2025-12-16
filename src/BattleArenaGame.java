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

    private final double WINDOW_W = 800;
    private final double WINDOW_H = 400;

    // IMPORTANT: arena is smaller because top+bottom UI take space
    private final double ARENA_W = 800;
    private final double ARENA_H = 320;

    private Fighter player1, player2;

    private boolean p1Up, p1Down, p1Left, p1Right, p1Shoot, p1Switch;
    private boolean p2Up, p2Down, p2Left, p2Right, p2Shoot, p2Switch;

    private Pane arena;
    private final List<Projectile> projectiles = new ArrayList<>();

    private final Label p1HealthLabel = new Label();
    private final Label p2HealthLabel = new Label();
    private final ProgressBar p1HealthBar = new ProgressBar(1.0);
    private final ProgressBar p2HealthBar = new ProgressBar(1.0);
    private final Label p1WeaponLabel = new Label();
    private final Label p2WeaponLabel = new Label();
    private final Label winnerLabel = new Label();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Battle Arena Game");
        stage.setScene(createSelectionScene(stage));
        stage.show();
    }

    private Scene createSelectionScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #202020;");

        Label title = new Label("Battle Arena - Character Selection");
        title.setFont(Font.font(22));
        title.setTextFill(Color.CYAN);

        ComboBox<String> p1Choice = new ComboBox<>();
        ComboBox<String> p2Choice = new ComboBox<>();

        p1Choice.getItems().addAll("M.Nader", "A.Khaled", "Mahmoud", "Youssef");
        p2Choice.getItems().addAll("M.Nader", "A.Khaled", "Mahmoud", "Youssef");

        p1Choice.setValue("M.Nader");
        p2Choice.setValue("A.Khaled");

        VBox p1Box = new VBox(5, new Label("Player 1"), p1Choice);
        VBox p2Box = new VBox(5, new Label("Player 2"), p2Choice);
        p1Box.setAlignment(Pos.CENTER);
        p2Box.setAlignment(Pos.CENTER);

        HBox playersBox = new HBox(40, p1Box, p2Box);
        playersBox.setAlignment(Pos.CENTER);

        Button startBtn = new Button("Start Game");
        startBtn.setOnAction(e -> {
            player1 = createFighter(p1Choice.getValue(), 80, ARENA_H / 2 - 20);
            player2 = createFighter(p2Choice.getValue(), ARENA_W - 120, ARENA_H / 2 - 20);

            player1.setDisplayName(p1Choice.getValue());
            player2.setDisplayName(p2Choice.getValue());

            Scene game = createGameScene();
            stage.setScene(game);

            // IMPORTANT: ensure the scene receives key events
            game.getRoot().requestFocus();
        });

        root.setTop(title);
        root.setCenter(playersBox);
        root.setBottom(startBtn);

        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setAlignment(startBtn, Pos.CENTER);
        BorderPane.setMargin(startBtn, new Insets(15));

        return new Scene(root, WINDOW_W, WINDOW_H);
    }

    private Fighter createFighter(String name, double x, double y) {
        switch (name) {
            case "Mahmoud":
                return new Mage(x, y);
            case "A.Khaled":
                return new Archer(x, y);
            default:
                return new Warrior(x, y);
        }
    }

    private Scene createGameScene() {
        arena = new Pane();
        arena.setPrefSize(ARENA_W, ARENA_H);
        arena.setMinSize(ARENA_W, ARENA_H);
        arena.setMaxSize(ARENA_W, ARENA_H);
        arena.setStyle("-fx-background-color: #101010;");

        Line midLine = new Line(ARENA_W / 2, 0, ARENA_W / 2, ARENA_H);
        midLine.setStroke(Color.GRAY);

        arena.getChildren().addAll(midLine, player1.getNode(), player2.getNode());

        p1HealthBar.setPrefWidth(150);
        p2HealthBar.setPrefWidth(150);

        VBox leftHUD = new VBox(2, p1HealthLabel, p1HealthBar);
        VBox rightHUD = new VBox(2, p2HealthLabel, p2HealthBar);
        leftHUD.setAlignment(Pos.CENTER_LEFT);
        rightHUD.setAlignment(Pos.CENTER_RIGHT);

        p1WeaponLabel.setTextFill(Color.ORANGE);
        p2WeaponLabel.setTextFill(Color.ORANGE);

        HBox topBar = new HBox(30, leftHUD, p1WeaponLabel, p2WeaponLabel, rightHUD);
        topBar.setPadding(new Insets(5));
        topBar.setAlignment(Pos.CENTER);
        topBar.setStyle("-fx-background-color: #262626;");

        winnerLabel.setFont(Font.font(24));
        winnerLabel.setTextFill(Color.LAWNGREEN);
        winnerLabel.setVisible(false);

        Label controls = new Label("P1: WASD + F shoot + R switch | P2: Arrows + L shoot + P switch");
        controls.setTextFill(Color.SILVER);

        VBox bottom = new VBox(5, winnerLabel, controls);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(5));
        bottom.setStyle("-fx-background-color: #202020;");

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(arena);
        root.setBottom(bottom);

        Scene scene = new Scene(root, WINDOW_W, WINDOW_H);

        setupKeys(scene);
        updateHUD();
        startLoop();

        // Clicking anywhere restores focus (prevents stuck keys sometimes)
        root.setOnMouseClicked(e -> root.requestFocus());

        return scene;
    }

    private void setupKeys(Scene scene) {
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

    private void startLoop() {
        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (player1.getHealth() <= 0 || player2.getHealth() <= 0) {
                    stop();
                    return;
                }

                double s = 3;

                // Clamp to ARENA size only
                double boundW = ARENA_W;
                double boundH = ARENA_H;

                if (p1Up) player1.move(0, -s, boundW, boundH);
                if (p1Down) player1.move(0, s, boundW, boundH);
                if (p1Left) player1.move(-s, 0, boundW, boundH);
                if (p1Right) player1.move(s, 0, boundW, boundH);

                if (p2Up) player2.move(0, -s, boundW, boundH);
                if (p2Down) player2.move(0, s, boundW, boundH);
                if (p2Left) player2.move(-s, 0, boundW, boundH);
                if (p2Right) player2.move(s, 0, boundW, boundH);

                if (p1Switch) { player1.nextWeapon(); p1Switch = false; updateHUD(); }
                if (p2Switch) { player2.nextWeapon(); p2Switch = false; updateHUD(); }

                shoot(player1, p1Shoot, 1);
                shoot(player2, p2Shoot, -1);

                Iterator<Projectile> it = projectiles.iterator();
                while (it.hasNext()) {
                    Projectile p = it.next();
                    p.update();

                    Fighter target = (p.getOwner() == player1) ? player2 : player1;

                    if (p.getShape().getBoundsInParent().intersects(target.getNode().getBoundsInParent())) {
                        target.damage(p.getWeapon().getDamage());
                        arena.getChildren().remove(p.getShape());
                        it.remove();
                        updateHUD();
                        checkWinner();
                        continue;
                    }

                    if (p.isOffScreen(boundW)) {
                        arena.getChildren().remove(p.getShape());
                        it.remove();
                    }
                }
            }
        };
        loop.start();
    }

    private void shoot(Fighter f, boolean shoot, int dir) {
        if (!shoot) return;

        Weapon w = f.getCurrentWeapon();
        if (w == null) return;

        long now = System.currentTimeMillis();
        if (now - f.getLastShotTime() < w.getCooldownMs()) return;

        f.setLastShotTime(now);

        Projectile p = new Projectile(
                f.getCenterX(),
                f.getCenterY(),
                w.getProjectileSpeed() * dir,
                w,
                f
        );

        arena.getChildren().add(p.getShape());
        projectiles.add(p);
    }

    private void updateHUD() {
        p1HealthLabel.setText(player1.getDisplayName() + " Health");
        p2HealthLabel.setText(player2.getDisplayName() + " Health");

        p1HealthBar.setProgress(Math.max(0, player1.getHealth()) / 100.0);
        p2HealthBar.setProgress(Math.max(0, player2.getHealth()) / 100.0);

        Weapon w1 = player1.getCurrentWeapon();
        Weapon w2 = player2.getCurrentWeapon();

        p1WeaponLabel.setText(player1.getDisplayName() + " Weapon: " + (w1 == null ? "None" : w1.getName()));
        p2WeaponLabel.setText(player2.getDisplayName() + " Weapon: " + (w2 == null ? "None" : w2.getName()));
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

    public static void main(String[] args) {
        launch(args);
    }
}