package battleship;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BoardGUI extends Application {

    private static final int SIZE = 10;
    private static final int CELL_SIZE = 40;

    @Override
    public void start(Stage stage) {

        GridPane grid = new GridPane();

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {

                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.LIGHTBLUE);
                cell.setStroke(Color.BLACK);

                grid.add(cell, col, row);
            }
        }

        Scene scene = new Scene(grid);

        stage.setTitle("Battleship Board");
        stage.setScene(scene);
        stage.show();
    }

    public static void launchBoard() {
        launch();
    }
}