import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Gui extends Application {
    static int periodSecond = 1;
    static int periodThread = 200;
    static int roadLength = 80;
    static ArrayList<Car> cars = new ArrayList<>();

    public static void main(String[] args) {
        int carSpeed = 1;
        int carNum = 4;
        for (int i = 1; i <= carNum; i++) {
            Car car = new Car(carSpeed + 1, "");
            if (i % 2 == 0) {
                car.setIcon("●");
            } else if (i % 2 == 1) {
                car.setIcon("○");
            }
            car.setLocation(i);
            cars.add(car);
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Gui.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
