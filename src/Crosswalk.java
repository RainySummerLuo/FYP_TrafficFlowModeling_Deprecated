class Crosswalk extends RoadFacility {
    private int passPeriod;
    private int passTime;

    @SuppressWarnings("SameParameterValue")
    Crosswalk(int location, String icon, int passPeriod, int laneIndex) {
        super("crosswalk", location, icon, false, laneIndex);
        this.passPeriod = passPeriod;
        passTime = 0;
    }

    int getPassPeriod() {
        return passPeriod;
    }

    public void setPassPeriod(int passPeriod) {
        this.passPeriod = passPeriod;
    }

    int getPassTime() {
        return passTime;
    }

    void setPassTime(int passTime) {
        this.passTime = passTime;
    }

    void newPedestrian() {
        super.setEnable(true);
        passTime = passPeriod;
    }
}
