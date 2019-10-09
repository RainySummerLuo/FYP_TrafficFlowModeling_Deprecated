import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * @author Laurence
 * @date 2019/10/8
 */
public class Gui extends Application {
    static int periodSecond = 1;
    static int periodThread = 200;
    static int roadLength = 60;
    static ArrayList<ArrayList<Car>> carLanes = new ArrayList<>();
    static ArrayList<RoadFacility> roadFacilities = new ArrayList<>();

    public static void main(String[] args) {
        ArrayList<Car> carsLane1 = new ArrayList<>();
        ArrayList<Car> carsLane2 = new ArrayList<>();

        int carNum = 3;
        /*
        for (ArrayList<Car> carsLane : carLanes) {
            for (int i = 1; i <= carNum; i++) {
                Car car = new Car(i);
                if (i % 2 == 0) {
                    car.setIcon("●");
                } else if (i % 2 == 1) {
                    car.setIcon("○");
                }
                carsLane.add(car);
            }
            carLanes.add(carsLane);
        }
        */
        for (int i = 1; i <= carNum; i++) {
            Car car = new Car(i);
            carsLane1.add(car);
        }
        for (int i = 1; i <= carNum; i++) {
            Car car = new Car(i);
            carsLane2.add(car);
        }
        carLanes.add(carsLane1);
        carLanes.add(carsLane2);

        Roadblock roadblock = new Roadblock(10, "|", false);
        roadFacilities.add(roadblock);
        Trafficlight trafficlight = new Trafficlight(50, 10, 10, "¤", "»");
        roadFacilities.add(trafficlight);
        Monitor monitoring = new Monitor(20, ":");
        roadFacilities.add(monitoring); //≣♿
        Crosswalk crosswalk1 = new Crosswalk(30, "♿", 4, 1);
        roadFacilities.add(crosswalk1);
        Crosswalk crosswalk2 = new Crosswalk(30, "☃", 4, 2);
        roadFacilities.add(crosswalk2);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Gui.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }
}
