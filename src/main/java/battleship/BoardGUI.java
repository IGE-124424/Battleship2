package battleship;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BoardGUI extends Application {

    private static final int SIZE = 10;
    private static final int CELL_SIZE = 40;

    private static Game game;

    public static void setGame(Game g) {
        game = g;
    }

    @Override
    public void start(Stage stage) {

        Image shipImage = new Image(
                getClass().getResourceAsStream("/images/navio.jpg")
        );
        GridPane grid = new GridPane();

        char[][] board = game.getBoard(true);

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {

                char value = board[row][col];

                if (value == '#') {

                    ImageView ship = new ImageView(shipImage);
                    ship.setFitWidth(CELL_SIZE);
                    ship.setFitHeight(CELL_SIZE);
                    grid.add(ship, col, row);

                } else {

                    Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                    cell.setStroke(Color.BLACK);

                    if (value == '.')
                        cell.setFill(Color.LIGHTBLUE);
                    else if (value == '*')
                        cell.setFill(Color.RED);
                    else if (value == 'o')
                        cell.setFill(Color.WHITE);

                    grid.add(cell, col, row);
                }
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