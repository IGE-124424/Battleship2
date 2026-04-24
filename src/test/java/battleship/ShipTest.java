package battleship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    private Ship barge;
    private Ship frigate;
    private Ship galleon;

    @BeforeEach
    void setup() {
        barge = new Barge(Compass.NORTH, new Position(5,5));
        frigate = new Frigate(Compass.EAST, new Position(3,3));
        galleon = new Galleon(Compass.SOUTH, new Position(8,8));
    }

    // ----------------------------------------------------
    // buildShip
    // ----------------------------------------------------

    @Test
    void testBuildShipAllBranches() {
        assertNotNull(Ship.buildShip("barca", Compass.NORTH, new Position(1,1)));
        assertNotNull(Ship.buildShip("caravela", Compass.NORTH, new Position(1,1)));
        assertNotNull(Ship.buildShip("nau", Compass.NORTH, new Position(1,1)));
        assertNotNull(Ship.buildShip("fragata", Compass.NORTH, new Position(1,1)));
        assertNotNull(Ship.buildShip("galeao", Compass.NORTH, new Position(1,1)));
        assertNull(Ship.buildShip("erro", Compass.NORTH, new Position(1,1)));
    }

    // ----------------------------------------------------
    // getters
    // ----------------------------------------------------

    @Test
    void testGetters() {
        assertNotNull(barge.getCategory());
        assertEquals(Compass.NORTH, barge.getBearing());
        assertEquals(1, barge.getSize());
        assertNotNull(barge.getPosition());
        assertNotNull(barge.getPositions());
    }

    @Test
    void testToString() {
        assertTrue(barge.toString().contains("["));
    }

    // ----------------------------------------------------
    // occupies
    // ----------------------------------------------------

    @Test
    void testOccupiesBranches() {
        assertTrue(barge.occupies(new Position(5,5)));
        assertFalse(barge.occupies(new Position(1,1)));

        assertTrue(frigate.occupies(frigate.getPositions().get(1)));
    }

    // ----------------------------------------------------
    // shoot / sink / floating
    // ----------------------------------------------------

    @Test
    void testShootAndFloating() {
        assertTrue(frigate.stillFloating());

        frigate.shoot(frigate.getPositions().get(0));
        assertTrue(frigate.stillFloating());

        frigate.sink();
        assertFalse(frigate.stillFloating());
    }

    @Test
    void testShootMiss() {
        barge.shoot(new Position(1,1));
        assertTrue(barge.stillFloating());
    }

    // ----------------------------------------------------
    // tooCloseTo position
    // ----------------------------------------------------

    @Test
    void testTooCloseToPositionBranches() {
        assertTrue(barge.tooCloseTo(new Position(5,6)));
        assertTrue(barge.tooCloseTo(new Position(4,4)));
        assertFalse(barge.tooCloseTo(new Position(20,20)));
    }

    // ----------------------------------------------------
    // tooCloseTo ship
    // ----------------------------------------------------

    @Test
    void testTooCloseToShipBranches() {
        Ship near = new Barge(Compass.NORTH, new Position(5,6));
        Ship far = new Barge(Compass.NORTH, new Position(20,20));

        assertTrue(barge.tooCloseTo(near));
        assertFalse(barge.tooCloseTo(far));
    }

    // ----------------------------------------------------
    // adjacent positions
    // ----------------------------------------------------

    @Test
    void testAdjacentPositions() {
        List<IPosition> adj = frigate.getAdjacentPositions();

        assertNotNull(adj);
        assertFalse(adj.isEmpty());
    }

    // ----------------------------------------------------
    // extremes
    // ----------------------------------------------------

    @Test
    void testTopBottomLeftRight() {
        assertTrue(galleon.getTopMostPos() <= galleon.getBottomMostPos());
        assertTrue(galleon.getLeftMostPos() <= galleon.getRightMostPos());

        assertTrue(frigate.getTopMostPos() <= frigate.getBottomMostPos());
        assertTrue(frigate.getLeftMostPos() <= frigate.getRightMostPos());
    }

    // ----------------------------------------------------
    // positions populated
    // ----------------------------------------------------

    @Test
    void testSizes() {
        assertEquals(1, barge.getPositions().size());
        assertTrue(frigate.getPositions().size() > 1);
        assertTrue(galleon.getPositions().size() > 1);
    }

    @Test
    void testTooCloseToOwnPosition() {

        assertTrue(
                barge.tooCloseTo(
                        new Position(5,5)
                )
        );
    }

    @Test
    void testAdjacentDuplicatesHandled() {
        Ship s = new Frigate(Compass.NORTH, new Position(5,5));

        assertFalse(s.getAdjacentPositions().isEmpty());
    }

    @Test
    void testShootEveryPositionIndividually() {
        Ship s = new Frigate(Compass.EAST, new Position(3,3));

        for (IPosition p : s.getPositions()) {
            s.shoot(p);
        }

        assertFalse(s.stillFloating());
    }

    @Test
    void testTopBottomExact() {
        Ship s = new Frigate(Compass.SOUTH, new Position(3,3));

        assertTrue(s.getBottomMostPos() >= s.getTopMostPos());
    }

    @Test
    void testLeftRightExact() {
        Ship s = new Frigate(Compass.WEST, new Position(3,3));

        assertTrue(s.getRightMostPos() >= s.getLeftMostPos());
    }

    @Test
    void testOccupiesAllBearings() {

        Ship north =
                new Frigate(
                        Compass.NORTH,
                        new Position(5,5)
                );

        Ship south =
                new Frigate(
                        Compass.SOUTH,
                        new Position(5,5)
                );

        Ship east =
                new Frigate(
                        Compass.EAST,
                        new Position(5,5)
                );

        Ship west =
                new Frigate(
                        Compass.WEST,
                        new Position(5,5)
                );

        assertTrue(
                north.occupies(
                        north.getPositions().get(1)
                )
        );

        assertTrue(
                south.occupies(
                        south.getPositions().get(1)
                )
        );

        assertTrue(
                east.occupies(
                        east.getPositions().get(1)
                )
        );

        assertTrue(
                west.occupies(
                        west.getPositions().get(1)
                )
        );
    }

    @Test
    void testTooCloseShipAllBearings() {

        assertTrue(
                new Frigate(
                        Compass.NORTH,
                        new Position(5,5)
                ).tooCloseTo(
                        new Barge(
                                Compass.NORTH,
                                new Position(5,4)
                        )
                )
        );

        assertTrue(
                new Frigate(
                        Compass.EAST,
                        new Position(5,5)
                ).tooCloseTo(
                        new Barge(
                                Compass.NORTH,
                                new Position(6,5)
                        )
                )
        );
    }

    @Test
    void testShootAlreadyHitPosition() {

        Ship s =
                new Barge(
                        Compass.NORTH,
                        new Position(5,5)
                );

        s.shoot(
                new Position(5,5)
        );

        s.shoot(
                new Position(5,5)
        );

        assertFalse(
                s.stillFloating()
        );
    }

    @Test
    void testBuildShipNullCategory() {

        assertThrows(
                AssertionError.class,
                () -> Ship.buildShip(
                        null,
                        Compass.NORTH,
                        new Position(1,1)
                )
        );
    }

    @Test
    void testAdjacentPositionsSingleCellShip() {

        assertFalse(
                barge.getAdjacentPositions()
                        .isEmpty()
        );
    }

    @Test
    void testOccupiesEdgeExtremes() {

        Ship s =
                new Galleon(
                        Compass.SOUTH,
                        new Position(8,8)
                );

        assertTrue(
                s.occupies(
                        s.getPositions().get(0)
                )
        );

        assertTrue(
                s.occupies(
                        s.getPositions().get(
                                s.getPositions().size()-1
                        )
                )
        );
    }

    @Test
    void testTooCloseBoundaryExact() {

        assertTrue(
                barge.tooCloseTo(
                        new Position(6,6)
                )
        );

        assertFalse(
                barge.tooCloseTo(
                        new Position(7,7)
                )
        );
    }

    @Test
    void testTooCloseShipExactBoundary() {

        Ship adjacent =
                new Barge(
                        Compass.NORTH,
                        new Position(6,5)
                );

        Ship overlap =
                new Barge(
                        Compass.NORTH,
                        new Position(5,5)
                );

        assertTrue(
                barge.tooCloseTo(adjacent)
        );

        assertTrue(
                barge.tooCloseTo(overlap)
        );
    }

    @Test
    void testRepeatedShotSamePosition() {

        Ship s =
                new Barge(
                        Compass.NORTH,
                        new Position(5,5)
                );

        s.shoot(
                new Position(5,5)
        );

        s.shoot(
                new Position(5,5)
        );

        assertFalse(
                s.stillFloating()
        );
    }

    @Test
    void testSinkAlreadySunk() {

        Ship s = barge;

        s.sink();
        s.sink();

        assertFalse(
                s.stillFloating()
        );
    }

    @Test
    void testBuildShipCaseBranches() {

        assertNull(
                Ship.buildShip(
                        "BARCA",
                        Compass.NORTH,
                        new Position(1,1)
                )
        );

        assertNull(
                Ship.buildShip(
                        "",
                        Compass.NORTH,
                        new Position(1,1)
                )
        );
    }

    @Test
    void testBuildShipNullPosition() {

        assertThrows(
                AssertionError.class,
                () -> Ship.buildShip(
                        "barca",
                        Compass.NORTH,
                        null
                )
        );
    }

    @Test
    void testBuildShipNullBearing() {

        assertThrows(
                AssertionError.class,
                () -> Ship.buildShip(
                        "barca",
                        null,
                        new Position(1,1)
                )
        );
    }

    @Test
    void testBottomUpdatesDuringScan() {

        Ship s =
                new Galleon(
                        Compass.SOUTH,
                        new Position(3,3)
                );

        assertEquals(
                s.getPositions()
                        .stream()
                        .mapToInt(IPosition::getRow)
                        .max()
                        .getAsInt(),

                s.getBottomMostPos()
        );
    }

    @Test
    void testLeftUpdatesDuringScan() {

        Ship s =
                new Frigate(
                        Compass.WEST,
                        new Position(5,5)
                );

        assertEquals(
                s.getPositions()
                        .stream()
                        .mapToInt(IPosition::getColumn)
                        .min()
                        .getAsInt(),

                s.getLeftMostPos()
        );
    }

    @Test
    void testTopUpdatesDuringScan() {

        Ship s =
                new Galleon(
                        Compass.NORTH,
                        new Position(8,8)
                );

        assertEquals(
                s.getPositions()
                        .stream()
                        .mapToInt(IPosition::getRow)
                        .min()
                        .getAsInt(),

                s.getTopMostPos()
        );
    }

    @Test
    void testRightUpdatesDuringScan() {

        Ship s =
                new Frigate(
                        Compass.EAST,
                        new Position(5,5)
                );

        assertEquals(
                s.getPositions()
                        .stream()
                        .mapToInt(IPosition::getColumn)
                        .max()
                        .getAsInt(),

                s.getRightMostPos()
        );
    }

    @Test
    void testRightMostNoUpdateBranch() {

        Ship s =
                new Barge(
                        Compass.NORTH,
                        new Position(5,5)
                );

        assertEquals(
                5,
                s.getRightMostPos()
        );
    }

    @Test
    void testBottomMostNoUpdateBranch() {

        Ship s =
                new Barge(
                        Compass.NORTH,
                        new Position(5,5)
                );

        assertEquals(
                5,
                s.getBottomMostPos()
        );
    }

    @Test
    void testBuildShipCaseSensitivity() {

        assertNull(
                Ship.buildShip(
                        "Barca",
                        Compass.NORTH,
                        new Position(1,1)
                )
        );
    }

    @Test
    void testBuildShipWhitespace() {

        assertNull(
                Ship.buildShip(
                        " barca ",
                        Compass.NORTH,
                        new Position(1,1)
                )
        );
    }

}

