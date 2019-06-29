/**
 * @author Laurence
 */
public class Car {
    private int distance;
    private int location;
    private int speed;
    private int acceleration;
    private int time;
    private String icon;
    private boolean stop;
    private boolean leftRight;

    Car(int speed, String icon, boolean leftRight) {
        this.speed = speed;
        // acceleration = 1;
        time = 0;
        // round = 0;
        this.icon = icon;
        this.leftRight = leftRight;
    }

    int getDistance() {
        return distance;
    }

    void setDistance(int distance) {
        this.distance += distance;
    }

    int getSpeed() {
        return speed;
    }

    void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
    }

    int getTime() {
        return time;
    }

    void setTime(int time) {
        this.time += time;
    }

    String getIcon() {
        return icon;
    }

    void setIcon(String icon) {
        this.icon = icon;
    }

    int getLocation() {
        return location;
    }

    void setLocation(int location) {
        this.location = location;
        distance = location;
    }

    boolean isStop() {
        return stop;
    }

    void setStop(boolean stop) {
        this.stop = stop;
    }

    boolean isLeftRight() {
        return leftRight;
    }

    void setLeftRight(boolean leftRight) {
        this.leftRight = leftRight;
    }
}
