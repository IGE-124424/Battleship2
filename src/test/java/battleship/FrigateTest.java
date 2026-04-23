package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FrigateTest {

    private static final Position ORIGIN = new Position(5, 5);

    private Frigate createFrigate(Compass bearing) {
        return new Frigate(bearing, ORIGIN);
    }

    @Test
    @DisplayName("Constructor should create a frigate correctly with NORTH bearing")
    void testConstructorNorth() {
        Frigate frigate = createFrigate(Compass.NORTH);
        List<IPosition> positions = frigate.getPositions();

        assertAll(
                () -> assertNotNull(frigate),
                () -> assertEquals("Fragata", frigate.getCategory()),
                () -> assertEquals(Compass.NORTH, frigate.getBearing()),
                () -> assertEquals(4, frigate.getSize()),
                () -> assertEquals(4, positions.size()),
                () -> assertEquals(new Position(5, 5), positions.get(0)),
                () -> assertEquals(new Position(6, 5), positions.get(1)),
                () -> assertEquals(new Position(7, 5), positions.get(2)),
                () -> assertEquals(new Position(8, 5), positions.get(3))
        );
    }

    @Test
    @DisplayName("Constructor should create a frigate correctly with SOUTH bearing")
    void testConstructorSouth() {
        Frigate frigate = createFrigate(Compass.SOUTH);
        List<IPosition> positions = frigate.getPositions();

        assertAll(
                () -> assertNotNull(frigate),
                () -> assertEquals("Fragata", frigate.getCategory()),
                () -> assertEquals(Compass.SOUTH, frigate.getBearing()),
                () -> assertEquals(4, frigate.getSize()),
                () -> assertEquals(4, positions.size()),
                () -> assertEquals(new Position(5, 5), positions.get(0)),
                () -> assertEquals(new Position(6, 5), positions.get(1)),
                () -> assertEquals(new Position(7, 5), positions.get(2)),
                () -> assertEquals(new Position(8, 5), positions.get(3))
        );
    }

    @Test
    @DisplayName("Constructor should create a frigate correctly with EAST bearing")
    void testConstructorEast() {
        Frigate frigate = createFrigate(Compass.EAST);
        List<IPosition> positions = frigate.getPositions();

        assertAll(
                () -> assertNotNull(frigate),
                () -> assertEquals("Fragata", frigate.getCategory()),
                () -> assertEquals(Compass.EAST, frigate.getBearing()),
                () -> assertEquals(4, frigate.getSize()),
                () -> assertEquals(4, positions.size()),
                () -> assertEquals(new Position(5, 5), positions.get(0)),
                () -> assertEquals(new Position(5, 6), positions.get(1)),
                () -> assertEquals(new Position(5, 7), positions.get(2)),
                () -> assertEquals(new Position(5, 8), positions.get(3))
        );
    }

    @Test
    @DisplayName("Constructor should create a frigate correctly with WEST bearing")
    void testConstructorWest() {
        Frigate frigate = createFrigate(Compass.WEST);
        List<IPosition> positions = frigate.getPositions();

        assertAll(
                () -> assertNotNull(frigate),
                () -> assertEquals("Fragata", frigate.getCategory()),
                () -> assertEquals(Compass.WEST, frigate.getBearing()),
                () -> assertEquals(4, frigate.getSize()),
                () -> assertEquals(4, positions.size()),
                () -> assertEquals(new Position(5, 5), positions.get(0)),
                () -> assertEquals(new Position(5, 6), positions.get(1)),
                () -> assertEquals(new Position(5, 7), positions.get(2)),
                () -> assertEquals(new Position(5, 8), positions.get(3))
        );
    }

    @Test
    @DisplayName("Frigate should still be floating when no position was hit")
    void testStillFloatingWithoutShots() {
        Frigate frigate = createFrigate(Compass.NORTH);

        assertTrue(frigate.stillFloating());
    }

    @Test
    @DisplayName("Frigate should still be floating when one position was hit")
    void testStillFloatingWithOneHit() {
        Frigate frigate = createFrigate(Compass.NORTH);

        frigate.getPositions().get(0).shoot();

        assertTrue(frigate.stillFloating());
    }

    @Test
    @DisplayName("Frigate should not be floating when all positions were hit")
    void testStillFloatingWithAllHits() {
        Frigate frigate = createFrigate(Compass.NORTH);

        frigate.getPositions().forEach(IPosition::shoot);

        assertFalse(frigate.stillFloating());
    }

    @Test
    @DisplayName("Vertical frigate should return correct boundary positions")
    void testBoundaryPositionsVertical() {
        Frigate frigate = createFrigate(Compass.NORTH);

        assertAll(
                () -> assertEquals(5, frigate.getTopMostPos()),
                () -> assertEquals(8, frigate.getBottomMostPos()),
                () -> assertEquals(5, frigate.getLeftMostPos()),
                () -> assertEquals(5, frigate.getRightMostPos())
        );
    }

    @Test
    @DisplayName("Horizontal frigate should return correct boundary positions")
    void testBoundaryPositionsHorizontal() {
        Frigate frigate = createFrigate(Compass.EAST);

        assertAll(
                () -> assertEquals(5, frigate.getTopMostPos()),
                () -> assertEquals(5, frigate.getBottomMostPos()),
                () -> assertEquals(5, frigate.getLeftMostPos()),
                () -> assertEquals(8, frigate.getRightMostPos())
        );
    }

    @Test
    @DisplayName("Constructor should throw NullPointerException when bearing is null")
    void testConstructorNullBearing() {
        assertThrows(NullPointerException.class, () -> new Frigate(null, ORIGIN));
    }

    @Test
    @DisplayName("Constructor should throw NullPointerException when position is null")
    void testConstructorNullPosition() {
        assertThrows(NullPointerException.class, () -> new Frigate(Compass.NORTH, null));
    }
}