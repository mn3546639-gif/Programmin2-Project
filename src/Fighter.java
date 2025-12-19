import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public abstract class Fighter {

    private String name;
    private String displayName;

    private Rectangle sprite;
    private double x;
    private double y;

    private int health;

    private ArrayList<Weapon> weapons;
    private int weaponIndex;

    private long lastShotTime;

    public Fighter(String name, double startX, double startY, Color color) {
        this.name = name;
        this.displayName = name;

        this.x = startX;
        this.y = startY;

        this.health = 100;

        weapons = new ArrayList<>();
        weaponIndex = 0;
        lastShotTime = 0;

        sprite = new Rectangle(40, 40, color);
        sprite.setTranslateX(x);
        sprite.setTranslateY(y);
    }

    public abstract void createDefaultWeapons();

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Node getNode() {
        return sprite;
    }

    public int getHealth() {
        return health;
    }

    public void damage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
    }

    public void move(double dx, double dy, double width, double height) {
        x += dx;
        y += dy;

        if (x < 0) x = 0;
        if (x > width - sprite.getWidth()) x = width - sprite.getWidth();

        if (y < 0) y = 0;
        if (y > height - sprite.getHeight()) y = height - sprite.getHeight();

        sprite.setTranslateX(x);
        sprite.setTranslateY(y);

       
        if (dx > 0) sprite.setRotate(0);
        else if (dx < 0) sprite.setRotate(180);
    }

    public void addWeapon(Weapon w) {
        weapons.add(w);
    }

    public void nextWeapon() {
        if (weapons.isEmpty()) return;
        weaponIndex++;
        if (weaponIndex >= weapons.size()) weaponIndex = 0;
    }

    public Weapon getCurrentWeapon() {
        if (weapons.isEmpty()) return null;
        return weapons.get(weaponIndex);
    }

    public long getLastShotTime() {
        return lastShotTime;
    }

    public void setLastShotTime(long lastShotTime) {
        this.lastShotTime = lastShotTime;
    }

    public double getCenterX() {
        return x + sprite.getWidth() / 2.0;
    }

    public double getCenterY() {
        return y + sprite.getHeight() / 2.0;
    }
}