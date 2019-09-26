/**
 * @author Laurence
 */
public class Car {
    private int distance;
    private int location;
    private int speed;
    private int acceleration;
    private int deceleration;
    private int a;
    private int time;
    private String icon;
    private boolean stop;

    Car() {
        speed = 0;
        acceleration = 2;
        deceleration = -2;
        time = 0;
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

    public int getDeceleration() {
        return deceleration;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }
}
