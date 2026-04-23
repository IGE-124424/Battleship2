package battleship;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for Caravel")
class CaravelTest {

    private static Caravel northCaravel;
    private static Caravel southCaravel;
    private static Caravel eastCaravel;
    private static Caravel westCaravel;

    @BeforeAll
    static void setUp() {
        Position start = new Position(5, 5);
        northCaravel = new Caravel(Compass.NORTH, start);
        southCaravel = new Caravel(Compass.SOUTH, start);
        eastCaravel = new Caravel(Compass.EAST, start);
        westCaravel = new Caravel(Compass.WEST, start);
    }

    @Test
    @DisplayName("getSize should return 2")
    void getSizeShouldReturnTwo() {
        assertEquals(2, northCaravel.getSize());
    }

    @Test
    @DisplayName("constructor should create a caravel facing NORTH with correct positions")
    void constructorNorth() {
        assertAll(
                () -> assertNotNull(northCaravel),
                () -> assertEquals(Compass.NORTH, northCaravel.getBearing()),
                () -> assertEquals(2, northCaravel.getPositions().size()),
                () -> assertEquals(new Position(5, 5), northCaravel.getPositions().get(0)),
                () -> assertEquals(new Position(6, 5), northCaravel.getPositions().get(1)),
                () -> assertEquals(5, northCaravel.getTopMostPos())
        );
    }

    @Test
    @DisplayName("constructor should create a caravel facing SOUTH with correct positions")
    void constructorSouth() {
        assertAll(
                () -> assertNotNull(southCaravel),
                () -> assertEquals(Compass.SOUTH, southCaravel.getBearing()),
                () -> assertEquals(2, southCaravel.getPositions().size()),
                () -> assertEquals(new Position(5, 5), southCaravel.getPositions().get(0)),
                () -> assertEquals(new Position(6, 5), southCaravel.getPositions().get(1)),
                () -> assertEquals(6, southCaravel.getBottomMostPos())
        );
    }

    @Test
    @DisplayName("constructor should create a caravel facing EAST with correct positions")
    void constructorEast() {
        assertAll(
                () -> assertNotNull(eastCaravel),
                () -> assertEquals(Compass.EAST, eastCaravel.getBearing()),
                () -> assertEquals(2, eastCaravel.getPositions().size()),
                () -> assertEquals(new Position(5, 5), eastCaravel.getPositions().get(0)),
                () -> assertEquals(new Position(5, 6), eastCaravel.getPositions().get(1)),
                () -> assertEquals(6, eastCaravel.getRightMostPos())
        );
    }

    @Test
    @DisplayName("constructor should create a caravel facing WEST with correct positions")
    void constructorWest() {
        assertAll(
                () -> assertNotNull(westCaravel),
                () -> assertEquals(Compass.WEST, westCaravel.getBearing()),
                () -> assertEquals(2, westCaravel.getPositions().size()),
                () -> assertEquals(new Position(5, 5), westCaravel.getPositions().get(0)),
                () -> assertEquals(new Position(5, 6), westCaravel.getPositions().get(1)),
                () -> assertEquals(5, westCaravel.getLeftMostPos())
        );
    }

    @Test
    @DisplayName("constructor should throw NullPointerException when bearing is null")
    void constructorShouldThrowWhenBearingIsNull() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Caravel(null, new Position(0, 0))
        );

        assertEquals("Ship's bearing must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("constructor should throw NullPointerException when bearing and position are null")
    void constructorShouldThrowWhenBearingAndPositionAreNull() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Caravel(null, null)
        );

        assertEquals("Ship's bearing must not be null", exception.getMessage());
    }
}