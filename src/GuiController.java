import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.*;

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

    private boolean crosswalk1 = false;
    private boolean crosswalk2 = false;
    private boolean crosswalkNewP1 = false;
    private boolean crosswalkNewP2 = false;

    private StringBuilder strInfo;

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

    @SuppressWarnings("EmptyMethod")
    public void slowDown() {
        // TODO: slowDown() Function
    }

    private void refresh() {
        TreeMap<Integer, Car> carsLocMap1 = new TreeMap<>((o1, o2) -> o2 - o1);
        TreeMap<Integer, Car> carsLocMap2 = new TreeMap<>((o1, o2) -> o2 - o1);
        // ArrayList<TreeMap<Integer, Car>> carMaps = new ArrayList<>();
        /*
        for (ArrayList<Car> carsLane: Gui.carLanes) {
            for (Car car : carsLane) {
                carsLocMap1.put(car.getLocation(), car);
            }
        }
        */
        for (Car car : Gui.carLanes.get(0)) {
            carsLocMap1.put(car.getLocation(), car);
        }
        for (Car car : Gui.carLanes.get(1)) {
            carsLocMap2.put(car.getLocation(), car);
        }
        ArrayList<TreeMap<Integer, Car>> carMaps = new ArrayList<>();
        carMaps.add(carsLocMap1);
        carMaps.add(carsLocMap2);

        TreeMap<Integer, RoadFacility> facilityMap1 = new TreeMap<>((o1, o2) -> o2 - o1);
        TreeMap<Integer, RoadFacility> facilityMap2 = new TreeMap<>((o1, o2) -> o2 - o1);
        for (RoadFacility facility : Gui.roadFacilities) {
            if (facility.getLaneIndex() == 0) {
                facilityMap1.put(facility.getLocation(), facility);
                facilityMap2.put(facility.getLocation(), facility);
            } else if (facility.getLaneIndex() == 1) {
                facilityMap1.put(facility.getLocation(), facility);
            } else if (facility.getLaneIndex() == 2) {
                facilityMap2.put(facility.getLocation(), facility);
            }
        }

        StringBuilder strRoad = new StringBuilder();
        strInfo = new StringBuilder();
        int laneIndex = 1;
        for (TreeMap<Integer, Car> carsMap : carMaps) {
            carJudgement(carsMap, facilityMap1, facilityMap2, laneIndex);
            carMovement(carsMap);
            monitor(carsMap);
            strRoad.append(guiRoadText(carsMap, facilityMap1, facilityMap2, laneIndex));
            strRoad.append("\n");
            guiInfoText(facilityMap1, facilityMap2, laneIndex);
            laneIndex++;
        }
        laneLabel.setText(String.valueOf(strRoad));
        infoLabel.setText(String.valueOf(strInfo));
    }

    private void laneChanging(TreeMap<Integer, Car> carsLocMap, TreeMap<Integer, RoadFacility> facilityMap1, TreeMap<Integer, RoadFacility> facilityMap2, int laneIndex) {
        for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
            int location = entry.getKey();
            Car car = entry.getValue();

            int safeDistance = Road.safeDistance;

            int threshold = 50;
            Random ran = new Random();
            int ranNum = ran.nextInt(100);
            if (ranNum > threshold) {
                return;
            }

            int carBeforeLoc = 0, carAfterLoc = 0;

            car.setChangeLane(true);

            TreeMap<Integer, RoadFacility> facilityMap;
            if (laneIndex == 1) {
                facilityMap = facilityMap2;
            } else {
                facilityMap = facilityMap1;
            }

            for (int i = 1; i < location; i++) {
                if (carsLocMap.containsKey(location - i)) {
                    carBeforeLoc = location - i;
                    break;
                }
            }

            if (carBeforeLoc == 0) {
                for (int i = 0; i < Gui.roadLength - location; i++) {
                    if (carsLocMap.containsKey(Gui.roadLength - i)) {
                        carBeforeLoc = Gui.roadLength - i;
                        break;
                    }
                }
            }

            for (int i = 1; i <= Gui.roadLength - location; i++) {
                if (carsLocMap.containsKey(i)) {
                    carAfterLoc = i;
                    break;
                }
            }

            if (carAfterLoc == 0) {
                for (int i = 1; i < location; i++) {
                    if (carsLocMap.containsKey(i)) {
                        carAfterLoc = i;
                        break;
                    }
                }
            }

            if (carAfterLoc - location < Road.safeDistance) {
                return;
            }

            int carFromStart = safeDistance - Road.maxSpeed - location;
            if (carFromStart > 0) {
                for (int i = 1; i < location; i++) {
                    if (carsLocMap.containsKey(i)) {
                        car.setChangeLane(false);
                        break;
                    } else if (facilityMap.containsKey(i)) {
                        if (facilityMap.get(i).isEnable()) {
                            car.setChangeLane(false);
                            break;
                        }
                    }
                }
                for (int i = Gui.roadLength; i >= safeDistance + Road.maxSpeed - location; i--) {
                    if (carsLocMap.containsKey(i)) {
                        car.setChangeLane(false);
                        break;
                    } else if (facilityMap.containsKey(i)) {
                        if (facilityMap.get(i).isEnable()) {
                            car.setChangeLane(false);
                            break;
                        }
                    }
                }
            } else {
                for (int i = 1; i < location; i++) {
                    if (carsLocMap.containsKey(i)) {
                        car.setChangeLane(false);
                        break;
                    } else if (facilityMap.containsKey(i)) {
                        if (facilityMap.get(i).isEnable()) {
                            car.setChangeLane(false);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void carJudgement(TreeMap<Integer, Car> carsLocMap, TreeMap<Integer, RoadFacility> facilityMap1, TreeMap<Integer, RoadFacility> facilityMap2, int laneIndex) {
        for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
            int currDistance = entry.getKey();
            Car car = entry.getValue();

            int safeDistance = Road.safeDistance;

            car.setSlow(false);

            TreeMap<Integer, RoadFacility> facilityMap;
            if (laneIndex == 1) {
                facilityMap = facilityMap1;
            } else {
                facilityMap = facilityMap2;
            }
            /*
            TreeMap<Integer, RoadFacility> facilityMap = null;
            for(Integer location : facilityMap.keySet()){
                RoadFacility facility = facilityMap.get(location);
                if (facility.isEnable()) {
                    assert facilityMap != null;
                    facilityMap.put(location, facility);
                }
            }
            */

            int carTillEnd = Gui.roadLength - currDistance;
            if (carTillEnd < safeDistance + car.getSpeed()) {
                for (int i = 1; i <= carTillEnd + 1; i++) {
                    if (carsLocMap.containsKey(currDistance + i)) {
                        car.setSlow(true);
                        break;
                    } else if (facilityMap.containsKey(currDistance + i)) {
                        if (facilityMap.get(currDistance + i).isEnable()) {
                            car.setSlow(true);
                            break;
                        }
                    }
                }
                for (int i = 1; i <= safeDistance + car.getSpeed() - carTillEnd + 1; i++) {
                    if (carsLocMap.containsKey(i)) {
                        car.setSlow(true);
                        break;
                    } else if (facilityMap.containsKey(currDistance + i)) {
                        if (facilityMap.get(currDistance + i).isEnable()) {
                            car.setSlow(true);
                            break;
                        }
                    }
                }
            } else {
                for (int i = 1; i <= safeDistance + car.getSpeed() + 1; i++) {
                    if (carsLocMap.containsKey(currDistance + i)) {
                        car.setSlow(true);
                        break;
                    } else if (facilityMap.containsKey(currDistance + i)) {
                        if (facilityMap.get(currDistance + i).isEnable()) {
                            car.setSlow(true);
                            break;
                        }
                    }
                }
            }
            if (car.isSlow()) {
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

    private StringBuilder guiRoadText(TreeMap<Integer, Car> carsLocMap, TreeMap<Integer, RoadFacility> facilityMap1, TreeMap<Integer, RoadFacility> facilityMap2, int laneIndex) {
        StringBuilder strRoadLane = new StringBuilder();
        for (int i = 0; i <= Gui.roadLength - 1; i++) {
            strRoadLane.append("——");
        }
        roadLabelUp.setText(String.valueOf(strRoadLane));
        roadLabelDown.setText(String.valueOf(strRoadLane));

        TreeMap<Integer, RoadFacility> facilityMap;
        if (laneIndex == 1) {
            facilityMap = facilityMap1;
        } else {
            facilityMap = facilityMap2;
        }

        /* Cars on the road */
        StringBuilder strRoad = new StringBuilder();
        for (int i = 1; i <= Gui.roadLength; i++) {
            if (i == 1 || i == Gui.roadLength) {
                strRoad.append("|");
            } else {
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
                    } else if ("crosswalk".equals(facility.getName())) {
                        Crosswalk crosswalk = (Crosswalk) facility;
                        if (crosswalk.isEnable()) {
                            strRoad.append(crosswalk.getIcon());
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
        }
        return strRoad;
    }

    @SuppressWarnings({"AlibabaMethodTooLong", "DuplicatedCode"})
    private void guiInfoText(TreeMap<Integer, RoadFacility> facilityMap1, TreeMap<Integer, RoadFacility> facilityMap2, int laneIndex) {
        Monitor monitor = null;
        Trafficlight trafficlight = null;
        Crosswalk crosswalk = null;
        if (laneIndex == 1) {
            for (RoadFacility facility : facilityMap1.values()) {
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
            int time = Gui.carLanes.get(0).get(0).getTime();
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
            if (crosswalk1) {
                crosswalk.newPedestrian();
                crosswalk1 = false;
                crosswalkNewP1 = false;
                crosswalk.setIcon("⇑");
            }
            if (i <= possibility) {
                crosswalk.newPedestrian();
                crosswalkNewP1 = true;
                crosswalk.setIcon("⇓");
            }
            if (crosswalk.isEnable()) {
                crosswalkStatus = " | Crosswalk① " + crosswalk.getPassTime();
                if (crosswalk.getPassTime() == 0) {
                    crosswalk.setEnable(false);
                    crosswalk2 = crosswalkNewP1;
                }
                crosswalk.setPassTime(crosswalk.getPassTime() - 1);
            } else {
                crosswalkStatus = " | Crosswalk① ✘";
            }
            strInfo.append(trafficlightStatus);
            strInfo.append(roadblock);
            strInfo.append(crosswalkStatus);
        } else if (laneIndex == 2) {
            for (RoadFacility facility : facilityMap2.values()) {
                if ("crosswalk".equals(facility.getName())) {
                    crosswalk = (Crosswalk) facility;
                }
            }
            String crosswalkStatus;
            assert false;
            Random ran = new Random();
            int i = ran.nextInt(100);
            int possibility = 5;
            if (crosswalk2) {
                crosswalk.newPedestrian();
                crosswalk2 = false;
                crosswalkNewP2 = false;
                crosswalk.setIcon("⇓");
            }
            if (i <= possibility) {
                crosswalk.newPedestrian();
                crosswalkNewP2 = true;
                crosswalk.setIcon("⇑");
            }
            if (crosswalk.isEnable()) {
                crosswalkStatus = " | Crosswalk② " + crosswalk.getPassTime();
                if (crosswalk.getPassTime() == 0) {
                    crosswalk.setEnable(false);
                    crosswalk1 = crosswalkNewP2;
                }
                crosswalk.setPassTime(crosswalk.getPassTime() - 1);
            } else {
                crosswalkStatus = " | Crosswalk② ✘";
            }
            strInfo.append(crosswalkStatus);
        }
    }

    private void monitor(TreeMap<Integer, Car> carsLocMap) {
        for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
            Car car = entry.getValue();
            Monitor monitor = null;
            for (RoadFacility facility : Gui.roadFacilities) {
                if ("monitor".equalsIgnoreCase(facility.getName())) {
                    monitor = (Monitor) facility;
                }
            }
            for (int i = 1; i <= car.getSpeed() + Road.safeDistance; i++) {
                assert monitor != null;
                if (car.getLocation() + i == monitor.getLocation()) {
                    monitor.setCarNum(monitor.getCarNum() + 1);
                    return;
                }
            }
        }
    }
}