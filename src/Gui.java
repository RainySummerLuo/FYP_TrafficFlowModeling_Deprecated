import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * @author Laurence
 */
public class Gui extends Application {
    static int periodSecond = 1;
    static int periodThread = 200;
    static int roadLength = 82;
    static ArrayList<Car> cars = new ArrayList<>();
    static ArrayList<RoadFacility> roadFacilities = new ArrayList<>();

    public static void main(String[] args) {
        int carNum = 1;
        for (int i = 1; i <= carNum; i++) {
            Car car = new Car();
            if (i % 2 == 0) {
                car.setIcon("●");
            } else if (i % 2 == 1) {
                car.setIcon("○");
            }
            car.setLocation(i);
            cars.add(car);
        }

        Roadblock roadblock = new Roadblock(75, "|", false);
        roadFacilities.add(roadblock);
        Trafficlight trafficlight = new Trafficlight(50, 10, 10, "¤", "»");
        roadFacilities.add(trafficlight);
        Monitor monitoring = new Monitor(20, ":");
        roadFacilities.add(monitoring);
        Crosswalk crosswalk = new Crosswalk(30, "≣", 8);
        roadFacilities.add(crosswalk);
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
