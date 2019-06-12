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
        if (Roadblock.location != 0) {
            Roadblock.location = 0;
        } else {
            Roadblock.location = 75;
        }
    }

    public void slowDown() {

    }

    private void refresh() {
        TreeMap<Integer, Car> carsLocMap = new TreeMap<>((o1, o2) -> o2 - o1);
        for (Car car : Gui.cars) {
            carsLocMap.put(car.getLocation(), car);
        }

        carJudgement(carsLocMap);
        carMovement(carsLocMap);

        guiRoadText(carsLocMap);
        guiInfoText();
    }

    private void carJudgement(TreeMap<Integer, Car> carsLocMap) {
        for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
            int currDistance = entry.getKey();
            Car car = entry.getValue();

            int safeDistance;
            if (car.getSpeed() > Road.carDistance) {
                safeDistance = car.getSpeed();
            } else {
                safeDistance = Road.carDistance;
            }

            int carTillEnd = Gui.roadLength - currDistance;
            if (carTillEnd < safeDistance) {
                for (int i = 1; i <= carTillEnd; i++) {
                    if (carsLocMap.containsKey(currDistance + i) || (i == Roadblock.location || i == Trafficlight.location)) {
                        car.setSlow(true);
                        car.setSpeed(0);
                    }
                }
                for (int i = 1; i <= safeDistance - carTillEnd; i++) {
                    if (carsLocMap.containsKey(i) || (i == Roadblock.location || i == Trafficlight.location)) {
                        car.setSlow(true);
                        car.setSpeed(0);
                    }
                }
            } else {
                for (int i = 1; i <= safeDistance; i++) {
                    if (carsLocMap.containsKey(currDistance + i) || (i == Roadblock.location || i == Trafficlight.location)) {
                        car.setSpeed(0);
                    }
                }
            }

            if (car.getSpeed() < Road.maxSpeed && !car.getSlow()) {
                car.setSpeed(Road.maxSpeed);
            }
        }
    }

    private void guiRoadText(TreeMap<Integer, Car> carsLocMap) {
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
            strRoad.append("*");
            for (int j = 1; j < Gui.roadLength + 1; j++) {
                if (j == Roadblock.location) {
                    strRoad.append("|");
                } else if (j == Trafficlight.location) {
                    if (Trafficlight.redlight > 0) {
                        strRoad.append("¤");
                    } else if (Trafficlight.greenlight > 0) {
                        strRoad.append("»");
                    }
                } else {
                    strRoad.append(" ");
                }
            }
            strRoad.append("*").append("\n");
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
        laneLabel.setText(String.valueOf(strCar));
    }

    private void guiInfoText() {
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

        int time = Gui.cars.get(0).getTime();
        strInfo.append(" | Time: ").append(time);

        infoLabel.setText(String.valueOf(strInfo));
    }

    private void carMovement(TreeMap<Integer, Car> carsLocMap) {
        for (Map.Entry<Integer, Car> entry : carsLocMap.entrySet()) {
            Car car = entry.getValue();
            /* Car's Advancement */
            int distance = car.getDistance();
            int speed = car.getSpeed();
            int time = Gui.periodSecond;
            int distance_new = distance + speed * time;
            car.setDistance(distance_new);
            car.setLocation(distance_new % Gui.roadLength);
            car.setRound(distance_new / Gui.roadLength);
            car.setTime(car.getTime() + 1);
        }
    }
}