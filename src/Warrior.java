import javafx.scene.paint.Color;

public class Warrior extends Fighter {

    public Warrior(double startX, double startY) {
        super("Warrior", startX, startY, Color.LIGHTBLUE);
        createDefaultWeapons();
    }

    @Override
    public void createDefaultWeapons() {
        addWeapon(new Weapon("Sword Shot", 8, 7.0, 250, Color.GOLD, 5));
        addWeapon(new Weapon("Axe Blast", 20, 4.0, 900, Color.ORANGE, 8));
    }
}