package battleship;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.Objects;

public class BoardGUI extends Application {

    private static final int SIZE = 10;
    private static final int CELL_SIZE = 40;
    private static final String SHIP_IMAGE_PATH = "/images/navio.jpg";
    private static final String WINDOW_TITLE = "Battleship Board";

    private static Game game;

    public static void setGame(Game g) {
        game = g;
    }

    @Override
    public void start(Stage stage) {

        Image shipImage = loadShipImage();
        GridPane grid = createBoardGrid(shipImage);

        Scene scene = new Scene(grid);

        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    private GridPane createBoardGrid(Image shipImage) {
        GridPane grid = new GridPane();
        char[][] board = game.getBoard(true);

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                char value = board[row][col];

                if (value == '#') {
                    ImageView ship = createShipView(shipImage);
                    grid.add(ship, col, row);
                } else {
                    Rectangle cell = createWaterCell(value);
                    grid.add(cell, col, row);
                }
            }
        }

        return grid;
    }

    private Image loadShipImage() {
        InputStream imageStream = Objects.requireNonNull(
                getClass().getResourceAsStream(SHIP_IMAGE_PATH),
                "Ship image resource not found: " + SHIP_IMAGE_PATH
        );
        return new Image(imageStream);
    }

    private ImageView createShipView(Image shipImage) {
        ImageView ship = new ImageView(shipImage);
        ship.setFitWidth(CELL_SIZE);
        ship.setFitHeight(CELL_SIZE);
        return ship;
    }

    private Rectangle createWaterCell(char value) {
        Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
        cell.setStroke(Color.BLACK);

        if (value == '.') {
            cell.setFill(Color.LIGHTBLUE);
        } else if (value == '*') {
            cell.setFill(Color.RED);
        } else if (value == 'o') {
            cell.setFill(Color.WHITE);
        }

        return cell;
    }

    public static void launchBoard() {
        launch();
    }
}