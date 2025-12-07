import javafx.scene.paint.Color;

public class Mage extends Fighter {

    public Mage(double startX, double startY) {
        super("Mage", startX, startY, Color.PURPLE);
        createDefaultWeapons();
    }

    @Override
    public void createDefaultWeapons() {
        addWeapon(new Weapon("Magic Bolt", 10, 6.0, 350, Color.CYAN, 5));
        addWeapon(new Weapon("Fireball", 30, 3.0, 1100, Color.RED, 9));
    }
}