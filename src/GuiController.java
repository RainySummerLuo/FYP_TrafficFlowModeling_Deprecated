import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.*;

/**
 * @author Laurence
 */
public class GuiController implements Initializable {
    @FXML
    private Button clearBtn;

    @FXML
    private Button slowBtn;

    @FXML
    private Label laneLabelUp;

    @FXML
    private Label laneLabelDown;

    @FXML
    private Label roadLabelUp;

    @FXML
    private Label roadLabelDown;

    @FXML
    private Label roadLabelMiddle;

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
                // UI update is run on the Application thread
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

    }

    private void refresh() {
        TreeMap<Integer, Car> carsLocMap = new TreeMap<>((o1, o2) -> o2 - o1);
        TreeMap<Integer, Car> carsRevLocMap = new TreeMap<>((o1, o2) -> o2 - o1);
        ArrayList<TreeMap<Integer, Car>> carMapList = new ArrayList<>();
        for (Car car : Gui.cars) {
            if (car.isLeftRight()) {
                carsLocMap.put(car.getLocation(), car);
            } else {
                carsRevLocMap.put(car.getLocation(), car);
            }
        }
        carMapList.add(carsLocMap);
        carMapList.add(carsRevLocMap);

        TreeMap<Integer, RoadFacility> facilityMap = new TreeMap<>((o1, o2) -> o2 - o1);
        for (RoadFacility facility : Gui.roadFacilities) {
            facilityMap.put(facility.getLocation(), facility);
        }

        TreeMap<Integer, RoadFacility> facilityEnabledMap = new TreeMap<>((o1, o2) -> o2 - o1);
        for (RoadFacility facility : Gui.roadFacilities) {
            if (facility.isEnable() && !"monitor".equals(facility.getName())) {
                facilityEnabledMap.put(facility.getLocation(), facility);
            }
        }

        carJudgement(carMapList, facilityEnabledMap);
        carMovement(carMapList);

        monitor(carsLocMap);

        guiRoadText(carsLocMap, carsRevLocMap, facilityMap);
        guiInfoText();
    }

    private void carJudgement(ArrayList<TreeMap<Integer, Car>> carMapList, TreeMap<Integer, RoadFacility> facilityMap) {
        for (TreeMap<Integer, Car> carsLocMap : carMapList) {
            for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
                int currDistance = entry.getKey();
                Car car = entry.getValue();

                int safeDistance = Road.carDistance;

                car.setStop(false);

                int carTillEnd = Gui.roadLength - currDistance;
                if (carTillEnd < safeDistance) {
                    for (int i = 1; i <= carTillEnd; i++) {
                        if (carsLocMap.containsKey(currDistance + i)) {
                            car.setStop(true);
                            break;
                        } else if (facilityMap.containsKey(currDistance + i)) {
                            car.setStop(true);
                            break;
                        }
                    }
                    for (int i = 1; i <= safeDistance - carTillEnd + 1; i++) {
                        if (carsLocMap.containsKey(i)) {
                            car.setStop(true);
                            break;
                        } else if (facilityMap.containsKey(currDistance + i)) {
                            car.setStop(true);
                            break;
                        }
                    }
                } else {
                    for (int i = 1; i < safeDistance + 1; i++) {
                        if (carsLocMap.containsKey(currDistance + i)) {
                            car.setStop(true);
                            break;
                        } else if (facilityMap.containsKey(currDistance + i)) {
                            car.setStop(true);
                            break;
                        }
                    }
                }
                if (car.isStop()) {
                    car.setSpeed(0);
                } else {
                    car.setSpeed(Road.maxSpeed);
                }
            }
        }
    }

    private void guiRoadText(TreeMap<Integer, Car> carsLocMap, TreeMap<Integer, Car> carsRevLocMap, TreeMap<Integer, RoadFacility> facilityMap) {
        /* Roadside '==='s */
        StringBuilder strRoadLane = new StringBuilder();
        StringBuilder strRoadMiddleLane = new StringBuilder();
        for (int i = 0; i <= Gui.roadLength + 1; i++) {
            strRoadLane.append("=");
            strRoadMiddleLane.append('-');
        }
        roadLabelUp.setText(String.valueOf(strRoadLane));
        roadLabelDown.setText(String.valueOf(strRoadLane));
        roadLabelMiddle.setText(String.valueOf(strRoadMiddleLane));

        /* Roadblock and Traffic light */
        StringBuilder strRoad = new StringBuilder();
        int interval = 5;
        for (int i = 0; i < interval; i++) {
            for (int j = 1; j < Gui.roadLength + 1; j++) {

                if (i == 2) {
                    continue;
                }
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
            }
            strRoad.append("\n");
        }
        facilityLabel.setText(String.valueOf(strRoad));

        /* Cars on the road */
        StringBuilder strCar = new StringBuilder();
        for (int i = 1; i <= Gui.roadLength; i++) {
            if (carsLocMap.containsKey(i)) {
                String icon = carsLocMap.get(i).getIcon();
                strCar.append(icon);
            } else {
                strCar.append(" ");
            }
        }
        laneLabelDown.setText(String.valueOf(strCar));
        StringBuilder strCarRev = new StringBuilder();
        for (int i = 1; i <= Gui.roadLength; i++) {
            if (carsRevLocMap.containsKey(i)) {
                String icon = carsRevLocMap.get(i).getIcon();
                strCarRev.append(icon);
            } else {
                strCarRev.append(" ");
            }
        }
        laneLabelUp.setText(String.valueOf(strCarRev));
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
            trafficlightStatus = " | Red  Light " + String.format("%2s", trafficlight.getGreenlight());
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

    private void carMovement(ArrayList<TreeMap<Integer, Car>> carMapList) {
        for (TreeMap<Integer, Car> carsLocMap : carMapList) {
            for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
                Car car = entry.getValue();
                /* Car's Advancement */
                int time = Gui.periodSecond;
                int speed = car.getSpeed();
                car.setDistance(speed * time);
                if (car.isLeftRight()) {
                    car.setLocation(car.getDistance() % Gui.roadLength);
                } else  {
                    car.setLocation(Gui.roadLength - (car.getDistance() % Gui.roadLength));
                }
                car.setTime(time);
                for (int i = 1; i < car.getSpeed(); i++) {
                    for (RoadFacility facility : Gui.roadFacilities) {
                        if ("monitor".equals(facility.getName())) {
                            if (car.getLocation() + i == facility.getLocation()) {
                                Monitor monitor = (Monitor) facility;
                                monitor.setCarNum(monitor.getCarNum() + 1);
                            }
                        }
                    }
                }
            }
        }
    }

    private void monitor(TreeMap<Integer, Car> carsLocMap) {
        for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
            Car car = entry.getValue();
            for (RoadFacility facility : Gui.roadFacilities) {
                if ("monitor".equals(facility.getName())) {
                    if (car.getLocation() + car.getSpeed() == facility.getLocation()) {
                        Monitor monitor = (Monitor) facility;
                        monitor.setCarNum(monitor.getCarNum() + 1);
                    }
                }
            }
        }
    }
}