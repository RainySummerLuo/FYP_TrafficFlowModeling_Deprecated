import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.*;

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
                    //The period of moving each car depends on the
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
        if (Roadblock.location != 0) {
            Roadblock.location = 0;
        } else {
            Roadblock.location = 75;
        }
    }

    public void slowDown() {
        int maxLoc = 0;
        Car maxCar = null;
        for (Car car : Gui.cars) {
            if (car.getLocation() > maxLoc) {
                maxLoc = car.getLocation();
                maxCar = car;
            }
        }
        assert maxCar != null;
        maxCar.setSpeed(maxCar.getSpeed() / 2);
        maxCar.setSlowPeriod(5);
    }

    private void refresh() {
        int distance;
        int time;
        int speed;
        StringBuilder laneStr = new StringBuilder();
        //TreeMap<Integer, Car> carsMap = new TreeMap<>((o1, o2) -> o2 - o1);
        TreeMap<Integer, Car> carsLocMap = new TreeMap<>((o1, o2) -> o2 - o1);
        /*
        for (Car car : Main.cars) {
            carsMap.put(car.getDistance(), car);
        }
        */
        laneStr.append("\n");
        for (int i = 0; i <= Gui.roadLength + 1; i++) {
            laneStr.append("=");
        }
        laneStr.append("\n");
        for (Car car : Gui.cars) {
            carsLocMap.put(car.getLocation(), car);
        }
        /*
        int preDistance = 0;
        Object obj = null;
        obj = carsMap.firstKey();
        Car carInCars = carsMap.get(obj);
        preDistance = carInCars.getLocation();
        */
        StringBuilder strInfo = new StringBuilder();
        String roadblock;
        if (Roadblock.location > 0) {
            roadblock = "Roadblock " + Roadblock.location + " | ";
        } else {
            roadblock = "No Roadblock | ";
        }
        String trafficlight = "";
        if (Trafficlight.redlight > 0) {
            trafficlight = "Red Light " + Trafficlight.redlight + "  ";
            Trafficlight.redlight -= 1;
            if (Trafficlight.redlight == 0) {
                Trafficlight.greenlight = Trafficlight.greenlightPeriod;
            }
        } else if (Trafficlight.greenlight > 0) {
            trafficlight = "Green Light " + Trafficlight.greenlight;
            Trafficlight.greenlight -= 1;
            if (Trafficlight.greenlight == 0) {
                Trafficlight.redlight = Trafficlight.redlightPeriod;
            }
        }
        strInfo.append(roadblock);
        strInfo.append(trafficlight);
        for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
            int currDistance = entry.getKey();
            Car carElement = entry.getValue();
            //Controlling of the time period of decreasing the speed
            if (carElement.getSlowPeriod() == 0) {
                carElement.setSpeed(Road.maxSpeed);
            } else {
                carElement.setSlowPeriod(carElement.getSlowPeriod() - 1);
            }
            int carDistanceEnd = Gui.roadLength - currDistance - carElement.getSpeed();
            if (carDistanceEnd <= Gui.carDistance) {
                int carPeriod = Gui.carDistance - carDistanceEnd;
                for (int i = 1; i <= carDistanceEnd; i++) {
                    if (carsLocMap.containsKey(currDistance + i)) {
                        carElement.setSpeed(0);
                    }
                }
                for (int i = 1; i <= carPeriod; i++) {
                    if (carsLocMap.containsKey(i)) {
                        carElement.setSpeed(0);
                    }
                }
            } else {
                for (int i = 1; i <= Gui.carDistance + carElement.getSpeed(); i++) {
                    if (carsLocMap.containsKey(currDistance + i)) {
                        carElement.setSpeed(0);
                    }
                }
            }

            if (Roadblock.location > 0) {
                if (currDistance + carElement.getSpeed() == Roadblock.location) {
                    carElement.setSpeed(currDistance);
                }
                if (currDistance == Roadblock.location - 1) {
                    carElement.setSpeed(0);
                }
            }
            if (currDistance + 1 == Trafficlight.location && Trafficlight.redlight > 0) {
                if (currDistance + carElement.getSpeed() == Trafficlight.location) {
                    carElement.setSpeed(currDistance);
                }
                if (currDistance == Trafficlight.location - 1) {
                    carElement.setSpeed(0);
                }
            }
        }

        System.out.println();
        StringBuilder strLane = new StringBuilder();
        for (int i = 1; i <= Gui.roadLength; i++) {
            if (carsLocMap.containsKey(i)) {
                String icon = carsLocMap.get(i).getIcon();
                strLane.append(icon);
            } else {
                strLane.append(" ");
            }
        }
        laneStr.append(strLane);
        laneStr.append("\n");
        time = Gui.cars.get(0).getTime();
        strInfo.append(" | Time: ").append(time);
        TreeMap<Integer, Car> carsLocMapRev = new TreeMap<>(Comparator.comparingInt(o -> o));
        for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
            carsLocMapRev.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, Car> entry : carsLocMapRev.entrySet()) {
            /* Car's Status */
            Car car = entry.getValue();

            /* Car's Advancement */
            distance = car.getDistance();
            speed = car.getSpeed();
            int distance_new = distance + (speed * Gui.periodSecond);
            car.setDistance(distance_new);
            car.setLocation(distance_new % Gui.roadLength);
            car.setRound(distance_new / Gui.roadLength);
            car.setTime(car.getTime() + 1);
        }
        laneStr.append(strInfo);
        laneStr.append("\n");
        StringBuilder strRoadLane = new StringBuilder();
        for (int i = 0; i <= Gui.roadLength + 1; i++) {
            laneStr.append("=");
            strRoadLane.append("=");
        }
        StringBuilder strRoadFacility = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            strRoadFacility.append("*");
            for (int j = 1; j < Gui.roadLength + 1; j++) {
                if (j == Roadblock.location) {
                    strRoadFacility.append("|");
                } else if (j == Trafficlight.location) {
                    if (Trafficlight.redlight > 0) {
                        strRoadFacility.append("✦");
                    } else if (Trafficlight.greenlight > 0)  {
                        strRoadFacility.append("✧");
                    }
                } else {
                    strRoadFacility.append(" ");
                }
            }
            strRoadFacility.append("*").append("\n");
        }
        System.out.print(laneStr);
        facilityLabel.setText(String.valueOf(strRoadFacility));
        roadLabelUp.setText(String.valueOf(strRoadLane));
        roadLabelDown.setText(String.valueOf(strRoadLane));
        laneLabel.setText(String.valueOf(strLane));
        infoLabel.setText(String.valueOf(strInfo));
    }
}