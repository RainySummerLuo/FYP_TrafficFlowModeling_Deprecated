class Trafficlight extends RoadFacility {
    private int redlightPeriod;
    private int greenlightPeriod;
    private int redlight;
    private int greenlight;
    private String greenlightIcon;
    private String redlightIcon;

    @SuppressWarnings("SameParameterValue")
    Trafficlight(int location, int redlightPeriod, int greenlightPeriod, String redlightIcon, String greenlightIcon) {
        super("trafficlight", location, "", false);
        this.redlightPeriod = redlightPeriod;
        this.greenlightPeriod = greenlightPeriod;
        this.redlightIcon = redlightIcon;
        this.greenlightIcon = greenlightIcon;
        this.redlight = redlightPeriod;
        this.greenlight = 0;
    }

    int getRedlightPeriod() {
        return redlightPeriod;
    }

    public void setRedlightPeriod(int redlightPeriod) {
        this.redlightPeriod = redlightPeriod;
    }

    int getGreenlightPeriod() {
        return greenlightPeriod;
    }

    public void setGreenlightPeriod(int greenlightPeriod) {
        this.greenlightPeriod = greenlightPeriod;
    }

    int getRedlight() {
        return redlight;
    }

    void setRedlight(int redlight) {
        this.redlight = redlight;
    }

    int getGreenlight() {
        return greenlight;
    }

    void setGreenlight(int greenlight) {
        this.greenlight = greenlight;
    }

    String getGreenlightIcon() {
        return greenlightIcon;
    }

    public void setGreenlightIcon(String greenlightIcon) {
        this.greenlightIcon = greenlightIcon;
    }

    String getRedlightIcon() {
        return redlightIcon;
    }

    public void setRedlightIcon(String redlightIcon) {
        this.redlightIcon = redlightIcon;
    }
}
