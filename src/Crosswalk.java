class Crosswalk extends RoadFacility {
    private int passPeriod;
    private int passTime;

    Crosswalk(int location, String icon, int passPeriod) {
        super("crosswalk", location, icon, false);
        this.passPeriod = passPeriod;
        passTime = 0;
    }

    public int getPassPeriod() {
        return passPeriod;
    }

    public void setPassPeriod(int passPeriod) {
        this.passPeriod = passPeriod;
    }

    public int getPassTime() {
        return passTime;
    }

    public void setPassTime(int passTime) {
        this.passTime = passTime;
    }
}
