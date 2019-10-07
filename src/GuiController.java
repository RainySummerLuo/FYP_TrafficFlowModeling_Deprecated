import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * @author Laurence
 * @date 2019/10/8
 */
public class GuiController implements Initializable {
    @FXML
    private Button clearBtn;

    @FXML
    private Button slowBtn;

    @FXML
    private Label laneLabel;

    @FXML
    private Label roadLabelUp;

    @FXML
    private Label roadLabelDown;

    @FXML
    private Label infoLabel;

    @FXML
    private Label facilityLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //noinspection AlibabaAvoidManuallyCreateThread
        Thread thread = new Thread(() -> {
            Runnable updater = this::refresh;
            while (true) {
                try {
                    //The period of moving each car depends on Gui.periodThread
                    Thread.sleep(Gui.periodThread);
                } catch (InterruptedException ignored) {
                }
                // UI update run on the Application thread
                Platform.runLater(updater);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void roadblock() {
        for (RoadFacility facility : Gui.roadFacilities) {
            if ("roadblock".equals(facility.getName())) {
                facility.setEnable(!facility.isEnable());
            }
        }
    }

    public void slowDown() {
        // TODO: slowDown() Function
    }

    private void refresh() {
        TreeMap<Integer, Car> carsLocMap = new TreeMap<>((o1, o2) -> o2 - o1);
        for (Car car : Gui.cars) {
            carsLocMap.put(car.getLocation(), car);
        }

        TreeMap<Integer, RoadFacility> facilityMap = new TreeMap<>((o1, o2) -> o2 - o1);
        for (RoadFacility facility : Gui.roadFacilities) {
            facilityMap.put(facility.getLocation(), facility);
        }

        TreeMap<Integer, RoadFacility> facilityMapEnable = new TreeMap<>((o1, o2) -> o2 - o1);
        for (RoadFacility facility : Gui.roadFacilities) {
            if (facility.isEnable() && !"monitor".equals(facility.getName())) {
                facilityMapEnable.put(facility.getLocation(), facility);
            }
        }

        carJudgement(carsLocMap, facilityMapEnable);
        carMovement(carsLocMap);

        monitor(carsLocMap);

        guiRoadText(carsLocMap, facilityMap);
        guiInfoText();
    }

    private void carJudgement(TreeMap<Integer, Car> carsLocMap, TreeMap<Integer, RoadFacility> facilityMap) {
        for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
            int currDistance = entry.getKey();
            Car car = entry.getValue();

            int safeDistance = Road.safeDistance;

            car.setSlow(false);

            int carTillEnd = Gui.roadLength - currDistance;
            if (carTillEnd < safeDistance + car.getSpeed()) {
                for (int i = 1; i <= carTillEnd + 1; i++) {
                    if (carsLocMap.containsKey(currDistance + i)) {
                        car.setSlow(true);
                        break;
                    } else if (facilityMap.containsKey(currDistance + i)) {
                        car.setSlow(true);
                        break;
                    }
                }
                for (int i = 1; i <= safeDistance + car.getSpeed() - carTillEnd + 1; i++) {
                    if (carsLocMap.containsKey(i)) {
                        car.setSlow(true);
                        break;
                    } else if (facilityMap.containsKey(currDistance + i)) {
                        car.setSlow(true);
                        break;
                    }
                }
            } else {
                for (int i = 1; i <= safeDistance + car.getSpeed() + 1; i++) {
                    if (carsLocMap.containsKey(currDistance + i)) {
                        car.setSlow(true);
                        break;
                    } else if (facilityMap.containsKey(currDistance + i)) {
                        car.setSlow(true);
                        break;
                    }
                }
            }
            if (car.isStop()) {
                car.setA(car.getDeceleration());
            } else if (car.getSpeed() < Road.maxSpeed) {
                car.setA(car.getAcceleration());
            } else {
                car.setA(0);
            }
        }
    }

    private void carMovement(TreeMap<Integer, Car> carsLocMap) {
        for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
            Car car = entry.getValue();
            /* Car's Advancement */
            double speed = car.getSpeed() + car.getA();
            if (speed < 0) {
                speed = 0;
            }
            car.setSpeed(speed);
            int time = Gui.periodSecond;
            car.setTime(time);
            car.setDistance((int) Math.floor(speed * time));
            car.setLocation(car.getDistance() % Gui.roadLength);
        }
    }

    private void guiRoadText(TreeMap<Integer, Car> carsLocMap, TreeMap<Integer, RoadFacility> facilityMap) {
        StringBuilder strRoadLane = new StringBuilder();
        for (int i = 0; i <= Gui.roadLength + 1; i++) {
            strRoadLane.append("=");
        }
        roadLabelUp.setText(String.valueOf(strRoadLane));
        roadLabelDown.setText(String.valueOf(strRoadLane));

        /* Roadblock and Traffic light */
        /*
        StringBuilder strRoad = new StringBuilder();
        int interval = 5;
        for (int i = 0; i < interval; i++) {
            for (int j = 1; j < Gui.roadLength + 1; j++) {
                if (i < 4) {
                    if (facilityMap.containsKey(j)) {
                        RoadFacility facility = facilityMap.get(j);
                        if ("trafficlight".equals(facility.getName())) {
                            Trafficlight trafficlight = (Trafficlight) facility;
                            if (trafficlight.isEnable()) {
                                strRoad.append(trafficlight.getRedlightIcon());
                            } else {
                                strRoad.append(trafficlight.getGreenlightIcon());
                            }
                        } else if ("roadblock".equals(facility.getName())) {
                            Roadblock roadblock = (Roadblock) facility;
                            if (roadblock.isEnable()) {
                                strRoad.append("|");
                            } else {
                                strRoad.append(" ");
                            }
                        } else {
                            strRoad.append(facilityMap.get(j).getIcon());
                        }
                    } else {
                        strRoad.append(" ");
                    }
                } else {
                    strRoad.append(" ");
                }
            }
            strRoad.append("\n");
        }
        facilityLabel.setText(String.valueOf(strRoad));
        */

        /* Cars on the road */
        StringBuilder strCar = new StringBuilder();
        StringBuilder strRoad = new StringBuilder();
        for (int i = 1; i <= Gui.roadLength; i++) {
            if (facilityMap.containsKey(i)) {
                RoadFacility facility = facilityMap.get(i);
                if ("trafficlight".equals(facility.getName())) {
                    Trafficlight trafficlight = (Trafficlight) facility;
                    if (trafficlight.isEnable()) {
                        strRoad.append(trafficlight.getRedlightIcon());
                    } else {
                        strRoad.append(trafficlight.getGreenlightIcon());
                    }
                } else if ("roadblock".equals(facility.getName())) {
                    Roadblock roadblock = (Roadblock) facility;
                    if (roadblock.isEnable()) {
                        strRoad.append("|");
                    } else {
                        strRoad.append(" ");
                    }
                } else {
                    strRoad.append(facilityMap.get(i).getIcon());
                }
            } else {
                strRoad.append(" ");
            }
            if (carsLocMap.containsKey(i)) {
                String icon = carsLocMap.get(i).getIcon();
                strRoad.append(icon);
            } else {
                strRoad.append(" ");
            }
        }
        laneLabel.setText(String.valueOf(strRoad));
    }

    @SuppressWarnings("AlibabaMethodTooLong")
    private void guiInfoText() {
        Monitor monitor = null;
        Trafficlight trafficlight = null;
        Crosswalk crosswalk = null;
        for (RoadFacility facility : Gui.roadFacilities) {
            switch (facility.getName()) {
                case "monitor":
                    monitor = (Monitor) facility;
                    break;
                case "trafficlight":
                    trafficlight = (Trafficlight) facility;
                    break;
                case "crosswalk":
                    crosswalk = (Crosswalk) facility;
                    break;
                default:
            }
        }
        StringBuilder strInfo = new StringBuilder();

        int time = Gui.cars.get(0).getTime();
        strInfo.append("Time: ").append(time);

        assert monitor != null;
        int carNum = monitor.getCarNum();
        strInfo.append(" | Count: ").append(String.format("%2s", carNum));
        int density = (carNum / Gui.periodSecond * 3600) / time;
        strInfo.append(" Flow: ").append(String.format("%3s", density)).append("veh/h");

        String roadblock = null;

        for (RoadFacility facility : Gui.roadFacilities) {
            if ("roadblock".equals(facility.getName())) {
                if (facility.isEnable()) {
                    roadblock = " | Roadblock " + facility.getLocation();
                } else {
                    roadblock = " | Roadblock ✘";
                }
            }
        }

        String trafficlightStatus = "";
        assert trafficlight != null;
        if (trafficlight.isEnable()) {
            trafficlightStatus = " | Red Light " + String.format("%2s", trafficlight.getGreenlight());
            trafficlight.setGreenlight(trafficlight.getGreenlight() - 1);
            if (trafficlight.getGreenlight() == 0) {
                trafficlight.setRedlight(trafficlight.getRedlightPeriod());
                trafficlight.setEnable(false);
            }
        } else if (!trafficlight.isEnable()) {
            trafficlightStatus = " | Green Light " + String.format("%2s", trafficlight.getRedlight());
            trafficlight.setRedlight(trafficlight.getRedlight() - 1);
            if (trafficlight.getRedlight() == 0) {
                trafficlight.setGreenlight(trafficlight.getGreenlightPeriod());
                trafficlight.setEnable(true);
            }
        }

        String crosswalkStatus;
        assert crosswalk != null;
        Random ran = new Random();
        int i = ran.nextInt(100);
        int possibility = 5;
        if (i <= possibility) {
            crosswalk.newPedestrian();
        }
        if (crosswalk.isEnable()) {
            crosswalkStatus = " | Crosswalk " + crosswalk.getPassTime();
            if (crosswalk.getPassTime() == 0) {
                crosswalk.setEnable(false);
            }
            crosswalk.setPassTime(crosswalk.getPassTime() - 1);
        } else {
            crosswalkStatus = " | Crosswalk ✘";
        }
        strInfo.append(crosswalkStatus);
        strInfo.append(trafficlightStatus);
        strInfo.append(roadblock);
        infoLabel.setText(String.valueOf(strInfo));
    }

    private void monitor(TreeMap<Integer, Car> carsLocMap) {
        for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
            Car car = entry.getValue();
            Monitor monitor = null;
            for (RoadFacility facility : Gui.roadFacilities) {
                if (facility.getName().equalsIgnoreCase("monitor")) {
                    monitor = (Monitor) facility;
                }
            }
            for (int i = 1; i <= car.getSpeed() + Road.safeDistance; i++) {
                assert monitor != null;
                if (car.getLocation() + i == monitor.getLocation()) {
                    monitor.setCarNum(monitor.getCarNum() + 1);
                }
            }
        }
    }
}