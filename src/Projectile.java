import javafx.scene.shape.Circle;

public class Projectile {

    private Circle shape;
    private double speedX;
    private Fighter owner;
    private Weapon weapon;

    public Projectile(double x, double y, double speedX, Weapon weapon, Fighter owner) {
        this.speedX = speedX;
        this.weapon = weapon;
        this.owner = owner;

        shape = new Circle(weapon.getProjectileRadius());
        shape.setFill(weapon.getProjectileColor());
        shape.setTranslateX(x);
        shape.setTranslateY(y);
    }

    public Circle getShape() {
        return shape;
    }

    public Fighter getOwner() {
        return owner;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void update() {
        shape.setTranslateX(shape.getTranslateX() + speedX);
    }

    public boolean isOffScreen(double width) {
        return shape.getTranslateX() < 0 || shape.getTranslateX() > width;
    }
}