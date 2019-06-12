public class Car {
    private int distance;
    private int location;
    private int speed;
    private int acceleration;
    private int time;
    private int slowPeriod;//The time of every slow down
    private int round;
    private String icon;

    Car(int speed, String icon) {
        // this.maxSpeed = speed;
        this.speed = speed;
        slowPeriod = 0;
        distance = 0;
        acceleration = 0;
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

    public int getRound() {
        return round;
    }

    void setRound(int round) {
        this.round = round;
    }

    int getSlowPeriod() {
        return slowPeriod;
    }

    void setSlowPeriod(int slowPeriod) {
        this.slowPeriod = slowPeriod;
    }
}
