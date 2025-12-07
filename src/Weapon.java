import javafx.scene.paint.Color;

public class Weapon {

    private String name;
    private int damage;
    private double projectileSpeed;
    private long cooldownMs;
    private Color projectileColor;
    private double projectileRadius;

    public Weapon(String name, int damage, double projectileSpeed,
                  long cooldownMs, Color projectileColor, double projectileRadius) {
        this.name = name;
        this.damage = damage;
        this.projectileSpeed = projectileSpeed;
        this.cooldownMs = cooldownMs;
        this.projectileColor = projectileColor;
        this.projectileRadius = projectileRadius;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public double getProjectileSpeed() {
        return projectileSpeed;
    }

    public long getCooldownMs() {
        return cooldownMs;
    }

    public Color getProjectileColor() {
        return projectileColor;
    }

    public double getProjectileRadius() {
        return projectileRadius;
    }
}