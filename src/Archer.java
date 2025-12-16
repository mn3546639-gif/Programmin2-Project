import javafx.scene.paint.Color;

public class Archer extends Fighter {

    public Archer(double startX, double startY) {
        super("Archer", startX, startY, Color.GREEN);
        createDefaultWeapons();
    }

    @Override
    public void createDefaultWeapons() {
        addWeapon(new Weapon("Arrow", 7, 10.0, 220, Color.LIME, 3));
        addWeapon(new Weapon("Heavy Arrow", 15, 6.0, 700, Color.DARKGREEN, 7));
    }
}