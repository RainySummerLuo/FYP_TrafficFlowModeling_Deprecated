/**
 * @author Laurence
 * @date 2019/09/26
 */

class Car {
    private int distance;
    private int location;
    private double speed;
    private double acceleration;
    private double deceleration;
    private double a;
    private int time;
    private String icon;
    private boolean slow;
    private boolean changeLane;

    Car(int distance) {
        this.distance = distance;
        this.changeLane = false;
        speed = 0;
        acceleration = 1;
        deceleration = -2;
        time = 1;
        icon = "→";
    }

    Car(int distance, String icon) {
        this(distance);
        this.icon = icon;
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

    boolean isSlow() {
        return slow;
    }

    void setSlow(boolean stop) {
        this.slow = stop;
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

    void setTime(int time) {
        this.time += time;
    }

    public boolean isChangeLane() {
        return changeLane;
    }

    public void setChangeLane(boolean changeLane) {
        this.changeLane = changeLane;
    }
}
