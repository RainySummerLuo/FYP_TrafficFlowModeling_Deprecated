class Monitor extends RoadFacility {
    private int carNum;

    Monitor(int location, String icon) {
        super("monitor", location, icon, true);
        carNum = 0;
    }

    int getCarNum() {
        return carNum;
    }

    void setCarNum(int carNum) {
        this.carNum = carNum;
    }
}
