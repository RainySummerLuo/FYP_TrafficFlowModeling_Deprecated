import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

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
        // new Alert(Alert.AlertType.CONFIRMATION);
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
            if (facility.getName().equals("roadblock")) {
                facility.setEnable(!facility.isEnable());
            }
        }
    }

    public void slowDown() {

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
            if (facility.isEnable() && !facility.getName().equals("monitor")) {
                facilityMapEnable.put(facility.getLocation(), facility);
            }
        }

        carJudgement(carsLocMap, facilityMapEnable);
        carMovement(carsLocMap);

        Monitor(carsLocMap);

        guiRoadText(carsLocMap, facilityMap);
        guiInfoText();
    }

    private void carJudgement(TreeMap<Integer, Car> carsLocMap, TreeMap<Integer, RoadFacility> facilityMap) {
        for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
            int currDistance = entry.getKey();
            Car car = entry.getValue();

            int safeDistance = Road.carDistance;
            // if (Road.maxSpeed > Road.carDistance) safeDistance = Road.maxSpeed;
            // else safeDistance = Road.carDistance;

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

    private void guiRoadText(TreeMap<Integer, Car> carsLocMap, TreeMap<Integer, RoadFacility> facilityMap) {
        /* Roadside '==='s */
        StringBuilder strRoadLane = new StringBuilder();
        for (int i = 0; i <= Gui.roadLength + 1; i++) {
            strRoadLane.append("=");
        }
        roadLabelUp.setText(String.valueOf(strRoadLane));
        roadLabelDown.setText(String.valueOf(strRoadLane));

        /* Roadblock and Traffic light */
        StringBuilder strRoad = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            for (int j = 1; j < Gui.roadLength + 1; j++) {
                if (i < 4) {
                    if (facilityMap.containsKey(j)) {
                        RoadFacility facility = facilityMap.get(j);
                        if (facility.getName().equals("trafficlight")) {
                            Trafficlight trafficlight = (Trafficlight) facility;
                            if (trafficlight.isEnable()) {
                                strRoad.append(trafficlight.getRedlightIcon());
                            } else {
                                strRoad.append(trafficlight.getGreenlightIcon());
                            }
                        } else if (facility.getName().equals("roadblock")) {
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

        /* Cars on the road */
        StringBuilder strCar = new StringBuilder();
        for (
                int i = 1;
                i <= Gui.roadLength; i++) {
            if (carsLocMap.containsKey(i)) {
                String icon = carsLocMap.get(i).getIcon();
                strCar.append(icon);
            } else {
                strCar.append(" ");
            }
        }
        laneLabel.setText(String.valueOf(strCar));
    }

    private void guiInfoText() {
        Monitor monitor = null;
        Trafficlight trafficlight = null;
        for (RoadFacility facility : Gui.roadFacilities) {
            if (facility.getName().equals("monitor")) {
                monitor = (Monitor) facility;
            } else if (facility.getName().equals("trafficlight")) {
                trafficlight = (Trafficlight) facility;
            }
        }
        StringBuilder strInfo = new StringBuilder();

        int time = Gui.cars.get(0).getTime();
        strInfo.append("Time: ").append(time);

        assert monitor != null;
        int carNum = monitor.getCarNum();
        strInfo.append(" | Count: ").append(carNum);
        int density = (carNum / Gui.periodSecond * 3600) / time;
        strInfo.append(" Flow: ").append(density).append("veh/h");

        String roadblock;

        for (RoadFacility facility : Gui.roadFacilities) {
            if (facility.getName().equals("roadblock")) {
                if (facility.isEnable()) {
                    roadblock = " | Roadblock " + facility.getLocation() + " | ";
                } else {
                    roadblock = " | No Roadblock | ";
                }
                strInfo.append(roadblock);
            }
        }

        String trafficlightStatus = "";
        assert trafficlight != null;
        if (trafficlight.isEnable()) {
            trafficlightStatus = "Red Light " + trafficlight.getGreenlight() + "  ";
            trafficlight.setGreenlight(trafficlight.getGreenlight() - 1);
            if (trafficlight.getGreenlight() == 0) {
                trafficlight.setRedlight(trafficlight.getRedlightPeriod());
                trafficlight.setEnable(false);
            }
        } else if (!trafficlight.isEnable()) {
            trafficlightStatus = "Green Light " + trafficlight.getRedlight() + "  ";
            trafficlight.setRedlight(trafficlight.getRedlight() - 1);
            if (trafficlight.getRedlight() == 0) {
                trafficlight.setGreenlight(trafficlight.getGreenlightPeriod());
                trafficlight.setEnable(true);
            }
        }
        strInfo.append(trafficlightStatus);
        infoLabel.setText(String.valueOf(strInfo));
    }

    private void carMovement(TreeMap<Integer, Car> carsLocMap) {
        for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
            Car car = entry.getValue();
            /* Car's Advancement */
            int speed = car.getSpeed();
            int time = Gui.periodSecond;
            car.setDistance(speed * time);
            car.setLocation(car.getDistance() % Gui.roadLength);
            car.setTime(time);
            for (int i = 1; i < car.getSpeed(); i++) {
                for (RoadFacility facility : Gui.roadFacilities) {
                    if (facility.getName().equals("monitor")) {
                        if (car.getLocation() + i == facility.getLocation()) {
                            Monitor monitor = (Monitor) facility;
                            monitor.setCarNum(monitor.getCarNum() + 1);
                        }
                    }
                }
            }
        }
    }

    private void Monitor(TreeMap<Integer, Car> carsLocMap) {
        for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
            Car car = entry.getValue();
            for (RoadFacility facility : Gui.roadFacilities) {
                if (facility.getName().equals("monitor")) {
                    if (car.getLocation() + car.getSpeed() == facility.getLocation()) {
                        Monitor monitor = (Monitor) facility;
                        monitor.setCarNum(monitor.getCarNum() + 1);
                    }
                }
            }
        }
    }
}