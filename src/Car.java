/**
 * @author Laurence
 * @date 2019/09/26
 */

public class Car {
    private int distance;
    private int location;
    private double speed;
    private double acceleration;
    private double deceleration;
    private double a;
    private int time;
    private String icon;
    private boolean stop;

    Car(int distance) {
        this.distance = distance;
        speed = 0;
        acceleration = 1;
        deceleration = -2;
        time = 0;
    }

    int getDistance() {
        return distance;
    }

    void setDistance(int distance) {
        this.distance += distance;
    }

    double getSpeed() {
        return speed;
    }

    void setSpeed(double speed) {
        this.speed = speed;
    }

    double getAcceleration() {
        return acceleration;
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

    void setSlow(boolean stop) {
        this.stop = stop;
    }

    double getDeceleration() {
        return deceleration;
    }

    double getA() {
        return a;
    }

    void setA(double a) {
        this.a = a;
    }

    int getTime() {
        return time;
    }
}
