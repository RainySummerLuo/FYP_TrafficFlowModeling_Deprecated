class RoadFacility {
    private String name;
    private int location;
    private String icon;
    private boolean enable;
    private int laneIndex;

    RoadFacility(String name, int location, String icon, boolean enable, int laneIndex) {
        this.name = name;
        this.location = location;
        this.icon = icon;
        this.enable = enable;
        this.laneIndex = laneIndex;
    }

    String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    boolean isEnable() {
        return enable;
    }

    void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getLaneIndex() {
        return laneIndex;
    }

    public void setLaneIndex(int laneIndex) {
        this.laneIndex = laneIndex;
    }
}
