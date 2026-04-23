package battleship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FleetTest {

    private Fleet fleet;

    @BeforeEach
    void setUp() {
        fleet = new Fleet();
    }

    /* ---------- Factory Methods ---------- */

    private Barge barge(int x, int y) {
        return new Barge(Compass.NORTH, new Position(x, y));
    }

    private Caravel caravel(int x, int y) {
        return new Caravel(Compass.NORTH, new Position(x, y));
    }

    /* ---------- Constructor ---------- */

    @Test
    @DisplayName("Fleet deve iniciar vazia")
    void shouldInitializeEmptyFleet() {
        assertNotNull(fleet);
        assertTrue(fleet.getShips().isEmpty());
    }

    /* ---------- getShips ---------- */

    @Test
    @DisplayName("getShips deve devolver navios adicionados")
    void shouldReturnAddedShips() {

        IShip ship = barge(1,1);

        fleet.addShip(ship);

        assertEquals(1, fleet.getShips().size());
        assertSame(ship, fleet.getShips().get(0));
    }

    /* ---------- addShip ---------- */

    @Nested
    class AddShipTests {

        @Test
        @DisplayName("Deve adicionar navio válido")
        void shouldAddValidShip() {
            assertTrue(fleet.addShip(barge(1,1)));
        }

        @Test
        @DisplayName("Não deve adicionar navio fora do tabuleiro")
        void shouldRejectShipOutsideBoard() {
            assertFalse(fleet.addShip(barge(99,99)));
        }

        @Test
        @DisplayName("Não deve adicionar navio em colisão total")
        void shouldRejectCollidingShip() {

            fleet.addShip(barge(1,1));

            assertFalse(
                    fleet.addShip(barge(1,1))
            );
        }

        @Test
        @DisplayName("Não deve adicionar navio com colisão parcial")
        void shouldRejectPartialOverlap() {

            fleet.addShip(
                    caravel(4,4)
            );

            assertFalse(
                    fleet.addShip(
                            caravel(4,5)
                    )
            );
        }

        @Test
        @DisplayName("Não deve ultrapassar limite da frota")
        void shouldRejectFleetOverflow() {

            for(int i=0; i<Fleet.FLEET_SIZE; i++) {
                fleet.addShip(
                        barge(i,0)
                );
            }

            assertFalse(
                    fleet.addShip(
                            barge(10,10)
                    )
            );
        }
    }

    /* ---------- getShipsLike ---------- */

    @Test
    @DisplayName("Deve filtrar navios por categoria")
    void shouldFilterShipsByCategory() {

        IShip b = barge(1,1);

        fleet.addShip(b);
        fleet.addShip(caravel(4,4));

        List<IShip> result =
                fleet.getShipsLike("Barca");

        assertEquals(1,result.size());
        assertSame(b,result.get(0));
    }

    @Test
    @DisplayName("Deve devolver vazio para categoria inexistente")
    void shouldReturnEmptyWhenCategoryDoesNotExist() {

        fleet.addShip(barge(1,1));

        assertTrue(
                fleet.getShipsLike("Submarino")
                        .isEmpty()
        );
    }

    /* ---------- getFloatingShips ---------- */

    @Test
    @DisplayName("Deve devolver apenas navios não afundados")
    void shouldReturnOnlyFloatingShips() {

        IShip b = barge(1,1);
        IShip c = caravel(4,4);

        fleet.addShip(b);
        fleet.addShip(c);

        assertEquals(
                2,
                fleet.getFloatingShips().size()
        );

        b.getPositions().get(0).shoot();

        List<IShip> floating =
                fleet.getFloatingShips();

        assertEquals(1,floating.size());
        assertSame(c,floating.get(0));
    }

    @Test
    @DisplayName("Fleet vazia não deve ter navios a flutuar")
    void shouldHandleEmptyFloatingShips() {

        assertTrue(
                fleet.getFloatingShips()
                        .isEmpty()
        );
    }

    @Test
    @DisplayName("Todos afundados deve devolver vazio")
    void shouldReturnNoFloatingShipsWhenAllSunk() {

        IShip b = barge(1,1);

        fleet.addShip(b);

        b.getPositions()
                .get(0)
                .shoot();

        assertTrue(
                fleet.getFloatingShips()
                        .isEmpty()
        );
    }

    /* ---------- shipAt ---------- */

    @Test
    @DisplayName("shipAt deve devolver navio quando existe")
    void shouldFindExistingShipAtPosition() {

        IShip ship = barge(1,1);

        fleet.addShip(ship);

        assertSame(
                ship,
                fleet.shipAt(
                        new Position(1,1)
                )
        );
    }

    @Test
    @DisplayName("shipAt deve devolver null quando não existe navio")
    void shouldReturnNullWhenNoShipAtPosition() {

        fleet.addShip(barge(1,1));

        assertNull(
                fleet.shipAt(
                        new Position(5,5)
                )
        );
    }

    @Test
    @DisplayName("shipAt deve encontrar navio em todas posições ocupadas")
    void shouldFindShipInAllOccupiedPositions() {

        IShip c = caravel(4,4);

        fleet.addShip(c);

        for (IPosition p : c.getPositions()) {
            assertSame(
                    c,
                    fleet.shipAt(p)
            );
        }
    }

    /* ---------- printStatus ---------- */

    @Test
    @DisplayName("printStatus não deve lançar exceção")
    void shouldPrintStatusWithoutErrors() {

        fleet.addShip(barge(1,1));

        assertDoesNotThrow(
                () -> fleet.printStatus()
        );
    }

    @Test
    @DisplayName("Deve devolver navios afundados")
    void shouldReturnSunkShips() {

        IShip b = barge(1,1);
        fleet.addShip(b);

        b.getPositions().get(0).shoot();

        assertEquals(
                1,
                fleet.getSunkShips().size()
        );
    }

    @Test
    @DisplayName("Sem navios afundados deve devolver vazio")
    void shouldReturnNoSunkShips() {

        assertTrue(
                fleet.getSunkShips().isEmpty()
        );
    }

    @Test
    @DisplayName("printShips não deve lançar exceção")
    void shouldPrintShipsWithoutErrors() {

        fleet.addShip(barge(1,1));

        assertDoesNotThrow(
                () -> fleet.printShips(
                        fleet.getShips()
                )
        );
    }

    @Test
    @DisplayName("printShipsByCategory não deve lançar exceção")
    void shouldPrintShipsByCategoryWithoutErrors() {

        fleet.addShip(barge(1,1));

        assertDoesNotThrow(
                () -> fleet.printShipsByCategory("Barca")
        );
    }

    @Test
    @DisplayName("printFloatingShips não deve lançar exceção")
    void shouldPrintFloatingShipsWithoutErrors() {

        fleet.addShip(barge(1,1));

        assertDoesNotThrow(
                () -> fleet.printFloatingShips()
        );
    }

    @Test
    @DisplayName("printAllShips não deve lançar exceção")
    void shouldPrintAllShipsWithoutErrors() {

        fleet.addShip(barge(1,1));

        assertDoesNotThrow(
                () -> fleet.printAllShips()
        );
    }

    @Test
    @DisplayName("createRandom deve criar frota válida")
    void shouldCreateRandomFleet() {

        IFleet randomFleet = Fleet.createRandom();

        assertNotNull(randomFleet);

        assertFalse(
                randomFleet.getShips().isEmpty()
        );

        assertTrue(
                randomFleet.getShips().size()
                        <= Fleet.FLEET_SIZE
        );
    }

    @Test
    void shouldReturnMultipleShipsSameCategory() {

        fleet.addShip(barge(1,1));
        fleet.addShip(barge(3,3));

        assertEquals(
                2,
                fleet.getShipsLike("Barca").size()
        );
    }

    @Test
    void shouldReturnNullInEmptyFleet() {

        assertNull(
                fleet.shipAt(
                        new Position(1,1)
                )
        );
    }

    @Test
    void shouldPrintEmptyShipList() {

        assertDoesNotThrow(
                () -> fleet.printShips(
                        List.of()
                )
        );
    }

    @Test
    void shouldThrowAssertionForNullShip() {

        assertThrows(
                AssertionError.class,
                () -> fleet.addShip(null)
        );
    }

    @Test
    void shouldRejectShipLeftOutsideBoard() {
        assertFalse(
                fleet.addShip(
                        new Barge(
                                Compass.NORTH,
                                new Position(-1,1)
                        )
                )
        );
    }

    @Test
    void shouldRejectShipTopOutsideBoard() {
        assertFalse(
                fleet.addShip(
                        new Barge(
                                Compass.NORTH,
                                new Position(1,-1)
                        )
                )
        );
    }

    @Test
    void shouldRejectShipRightOutsideBoard() {
        assertFalse(
                fleet.addShip(
                        new Barge(
                                Compass.EAST,
                                new Position(Game.BOARD_SIZE,1)
                        )
                )
        );
    }

    @Test
    void shouldRejectShipBottomOutsideBoard() {
        assertFalse(
                fleet.addShip(
                        new Barge(
                                Compass.SOUTH,
                                new Position(1,Game.BOARD_SIZE)
                        )
                )
        );
    }

    @Test
    void shouldDetectCollisionAfterScanningMultipleShips() {

        fleet.addShip(barge(1,1));
        fleet.addShip(barge(5,5));

        assertFalse(
                fleet.addShip(
                        barge(1,2)
                )
        );
    }

    @Test
    void shouldAssertNullShip() {
        assertThrows(
                AssertionError.class,
                () -> fleet.addShip(null)
        );
    }

    @Test
    void shouldAssertNullPosition() {
        assertThrows(
                AssertionError.class,
                () -> fleet.shipAt(null)
        );
    }

    @Test
    void shouldAssertNullCategory() {
        assertThrows(
                AssertionError.class,
                () -> fleet.getShipsLike(null)
        );
    }

    @Test
    void shouldAssertNullShipList() {
        assertThrows(
                AssertionError.class,
                () -> fleet.printShips(null)
        );
    }

    @Test
    void shouldCheckCollisionAgainstLaterShip() {

        fleet.addShip(barge(1,1));
        fleet.addShip(barge(8,8));

        IShip candidate =
                barge(8,8);

        assertFalse(
                fleet.addShip(candidate)
        );
    }

    @Test
    void shouldReturnOnlySunkShips() {

        IShip b = barge(1,1);
        IShip c = caravel(4,4);

        fleet.addShip(b);
        fleet.addShip(c);

        b.getPositions().get(0).shoot();

        assertEquals(
                1,
                fleet.getSunkShips().size()
        );
    }


    @Test
    void shouldExerciseRandomFleetFailures() {

        for(int i=0; i<50; i++) {

            IFleet f = Fleet.createRandom();

            assertNotNull(f);

            assertFalse(
                    f.getShips().isEmpty()
            );
        }
    }

    @Test
    void shouldFindCollisionOnlyOnSecondShip() throws Exception {

        fleet.addShip(
                barge(1,1)
        ); // primeira: não colide

        fleet.addShip(
                barge(7,7)
        ); // segunda: colide

        var m = Fleet.class.getDeclaredMethod(
                "colisionRisk",
                IShip.class
        );

        m.setAccessible(true);

        assertTrue(
                (Boolean)m.invoke(
                        fleet,
                        barge(7,7)
                )
        );
    }

    @Test
    void shouldReturnFalseCollisionWithEmptyFleet() throws Exception {

        var m = Fleet.class.getDeclaredMethod(
                "colisionRisk",
                IShip.class
        );

        m.setAccessible(true);

        assertFalse(
                (Boolean)m.invoke(
                        fleet,
                        barge(3,3)
                )
        );
    }


}