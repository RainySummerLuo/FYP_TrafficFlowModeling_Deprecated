public class Car {
    private int distance;
    private int slowDistance;
    private int location;
    private int speed;
    private int acceleration;
    private int time;
    private boolean isSlow;
    private int round;
    private String icon;

    Car(int speed, String icon) {
        this.speed = speed;
        isSlow = false;
        acceleration = 1;
        time = 0;
        round = 0;
        this.icon = icon;
    }

    int getDistance() {
        return distance;
    }

    void setDistance(int distance) {
        this.distance = distance;
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
        this.time = time;
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

    void setRound(int round) {
        this.round = round;
    }

    boolean getSlow() {
        return isSlow;
    }

    void setSlow(boolean slow) {
        this.isSlow = slow;
    }

    public int getRound() {
        return round;
    }

    public int getSlowDistance() {
        return slowDistance;
    }

    public void setSlowDistance(int slowDistance) {
        this.slowDistance = slowDistance;
    }
}
