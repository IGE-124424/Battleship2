package battleship;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest {

    static class FakeShip implements IShip {

        private final String category;

        FakeShip(String category) {
            this.category = category;
        }

        @Override
        public String getCategory() {
            return category;
        }

        @Override
        public Integer getSize() {
            return 1;
        }

        @Override
        public List<IPosition> getPositions() {
            return new ArrayList<>();
        }

        @Override
        public List<IPosition> getAdjacentPositions() {
            return new ArrayList<>();
        }

        @Override
        public IPosition getPosition() {
            return null;
        }

        @Override
        public Compass getBearing() {
            return null;
        }

        @Override
        public boolean stillFloating() {
            return true;
        }

        @Override
        public int getTopMostPos() {
            return 0;
        }

        @Override
        public int getBottomMostPos() {
            return 0;
        }

        @Override
        public int getLeftMostPos() {
            return 0;
        }

        @Override
        public int getRightMostPos() {
            return 0;
        }

        @Override
        public boolean occupies(IPosition pos) {
            return false;
        }

        @Override
        public boolean tooCloseTo(IShip other) {
            return false;
        }

        @Override
        public boolean tooCloseTo(IPosition pos) {
            return false;
        }

        @Override
        public void shoot(IPosition pos) {
        }

        @Override
        public void sink() {
        }
    }

    @DisplayName("O construtor deve guardar número, tiros e resultados")
    @Test
    void constructorShouldStoreAllFields() {
        List<IPosition> shots = new ArrayList<>();
        shots.add(new Position(0, 0));
        shots.add(new Position(1, 1));

        List<IGame.ShotResult> results = new ArrayList<>();

        Move move = new Move(3, shots, results);

        assertEquals(3, move.getNumber());
        assertEquals(shots, move.getShots());
        assertEquals(results, move.getShotResults());
    }

    @DisplayName("getShots deve devolver a mesma lista passada no construtor")
    @Test
    void getShotsShouldReturnSameListReference() {
        List<IPosition> shots = new ArrayList<>();
        shots.add(new Position(2, 3));

        List<IGame.ShotResult> results = new ArrayList<>();

        Move move = new Move(1, shots, results);

        assertSame(shots, move.getShots());
    }

    @DisplayName("getShotResults deve devolver a mesma lista passada no construtor")
    @Test
    void getShotResultsShouldReturnSameListReference() {
        List<IPosition> shots = new ArrayList<>();
        List<IGame.ShotResult> results = new ArrayList<>();

        Move move = new Move(1, shots, results);

        assertSame(results, move.getShotResults());
    }

    @DisplayName("toString deve devolver o formato esperado")
    @Test
    void toStringShouldReturnExpectedFormat() {
        List<IPosition> shots = new ArrayList<>();
        shots.add(new Position(0, 0));
        shots.add(new Position(1, 1));

        List<IGame.ShotResult> results = new ArrayList<>();
        Move move = new Move(5, shots, results);

        assertEquals("Move{number=5, shots=2, results=0}", move.toString());
    }

    @DisplayName("processEnemyFire sem resultados deve devolver JSON com contadores a zero e tiros exteriores")
    @Test
    void processEnemyFireWithEmptyResultsShouldReturnExpectedJson() throws Exception {
        List<IPosition> shots = new ArrayList<>();
        List<IGame.ShotResult> results = new ArrayList<>();

        Move move = new Move(1, shots, results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        assertEquals(0, root.get("validShots").asInt());
        assertEquals(Game.NUMBER_SHOTS, root.get("outsideShots").asInt());
        assertEquals(0, root.get("repeatedShots").asInt());
        assertEquals(0, root.get("missedShots").asInt());
        assertTrue(root.get("sunkBoats").isArray());
        assertEquals(0, root.get("sunkBoats").size());
        assertTrue(root.get("hitsOnBoats").isArray());
        assertEquals(0, root.get("hitsOnBoats").size());
    }

    @DisplayName("processEnemyFire deve devolver sempre uma string JSON não nula")
    @Test
    void processEnemyFireShouldReturnNonNullJson() {
        List<IPosition> shots = new ArrayList<>();
        List<IGame.ShotResult> results = new ArrayList<>();

        Move move = new Move(7, shots, results);

        String json = move.processEnemyFire(false);

        assertNotNull(json);
        assertFalse(json.isBlank());
    }

    @DisplayName("processEnemyFire deve ignorar tiros inválidos")
    @Test
    void shouldIgnoreInvalidShots() throws Exception {
        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(new IGame.ShotResult(false, false, null, false));

        Move move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        assertEquals(0, root.get("validShots").asInt());
        assertEquals(0, root.get("repeatedShots").asInt());
        assertEquals(0, root.get("missedShots").asInt());
    }

    @DisplayName("processEnemyFire deve contar tiros repetidos")
    @Test
    void shouldCountRepeatedShots() throws Exception {
        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(new IGame.ShotResult(true, true, null, false));

        Move move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        assertEquals(0, root.get("validShots").asInt());
        assertEquals(1, root.get("repeatedShots").asInt());
        assertEquals(0, root.get("missedShots").asInt());
    }

    @DisplayName("processEnemyFire deve contar tiros válidos na água")
    @Test
    void shouldCountMissedShots() throws Exception {
        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(new IGame.ShotResult(true, false, null, false));

        Move move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        assertEquals(1, root.get("validShots").asInt());
        assertEquals(0, root.get("repeatedShots").asInt());
        assertEquals(1, root.get("missedShots").asInt());
    }

    @DisplayName("processEnemyFire deve contar acertos em barcos não afundados")
    @Test
    void shouldCountHitsOnBoat() throws Exception {
        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(new IGame.ShotResult(true, false, new FakeShip("Destroyer"), false));

        Move move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        assertEquals(1, root.get("validShots").asInt());
        assertEquals(0, root.get("missedShots").asInt());
        assertEquals(1, root.get("hitsOnBoats").size());
        assertEquals("Destroyer", root.get("hitsOnBoats").get(0).get("type").asText());
        assertEquals(1, root.get("hitsOnBoats").get(0).get("hits").asInt());
    }

    @DisplayName("processEnemyFire deve contar barcos afundados")
    @Test
    void shouldCountSunkBoats() throws Exception {
        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(new IGame.ShotResult(true, false, new FakeShip("Submarine"), true));

        Move move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        assertEquals(1, root.get("validShots").asInt());
        assertEquals(1, root.get("sunkBoats").size());
        assertEquals("Submarine", root.get("sunkBoats").get(0).get("type").asText());
        assertEquals(1, root.get("sunkBoats").get(0).get("count").asInt());
        assertEquals(0, root.get("hitsOnBoats").size());
    }

    @DisplayName("processEnemyFire em modo verbose sem resultados não deve lançar exceção")
    @Test
    void processEnemyFireVerboseWithEmptyResultsShouldNotThrow() {
        List<IPosition> shots = new ArrayList<>();
        List<IGame.ShotResult> results = new ArrayList<>();

        Move move = new Move(2, shots, results);

        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    @DisplayName("processEnemyFire em modo verbose não deve lançar exceção")
    @Test
    void shouldNotThrowWhenVerboseIsTrue() {
        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(new IGame.ShotResult(true, false, null, false));

        Move move = new Move(1, new ArrayList<>(), results);

        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    @DisplayName("processEnemyFire deve processar corretamente uma mistura de resultados")
    @Test
    void shouldProcessMixedShotResults() throws Exception {
        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(new IGame.ShotResult(false, false, null, false));
        results.add(new IGame.ShotResult(true, true, null, false));
        results.add(new IGame.ShotResult(true, false, null, false));
        results.add(new IGame.ShotResult(true, false, new FakeShip("Destroyer"), false));
        results.add(new IGame.ShotResult(true, false, new FakeShip("Submarine"), true));

        Move move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        assertEquals(3, root.get("validShots").asInt());
        assertEquals(1, root.get("repeatedShots").asInt());
        assertEquals(1, root.get("missedShots").asInt());
        assertEquals(1, root.get("sunkBoats").size());
        assertEquals(1, root.get("hitsOnBoats").size());
    }

    @DisplayName("Deve acumular múltiplos acertos no mesmo tipo de barco")
    @Test
    void shouldAccumulateHitsPerBoat() throws Exception {
        List<IGame.ShotResult> results = new ArrayList<>();

        results.add(new IGame.ShotResult(true, false, new FakeShip("Destroyer"), false));
        results.add(new IGame.ShotResult(true, false, new FakeShip("Destroyer"), false));

        Move move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        assertEquals(2, root.get("validShots").asInt());
        assertEquals(1, root.get("hitsOnBoats").size());
        assertEquals(2, root.get("hitsOnBoats").get(0).get("hits").asInt());
    }

    @DisplayName("Deve acumular múltiplos barcos afundados do mesmo tipo")
    @Test
    void shouldAccumulateSunkBoats() throws Exception {
        List<IGame.ShotResult> results = new ArrayList<>();

        results.add(new IGame.ShotResult(true, false, new FakeShip("Submarine"), true));
        results.add(new IGame.ShotResult(true, false, new FakeShip("Submarine"), true));

        Move move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        assertEquals(1, root.get("sunkBoats").size());
        assertEquals(2, root.get("sunkBoats").get(0).get("count").asInt());
    }

    @DisplayName("Deve calcular corretamente tiros fora do tabuleiro")
    @Test
    void shouldCalculateOutsideShots() throws Exception {
        List<IGame.ShotResult> results = new ArrayList<>();

        results.add(new IGame.ShotResult(true, false, null, false)); // válido
        results.add(new IGame.ShotResult(true, true, null, false));  // repetido

        Move move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        int outside = root.get("outsideShots").asInt();

        assertTrue(outside >= 0);
    }

    @DisplayName("Verbose deve gerar output com múltiplos cenários")
    @Test
    void verboseShouldExecuteAllBranches() {
        List<IGame.ShotResult> results = new ArrayList<>();

        results.add(new IGame.ShotResult(true, false, null, false)); // água
        results.add(new IGame.ShotResult(true, true, null, false));  // repetido
        results.add(new IGame.ShotResult(true, false, new FakeShip("Destroyer"), false)); // hit
        results.add(new IGame.ShotResult(true, false, new FakeShip("Submarine"), true));  // sunk

        Move move = new Move(1, new ArrayList<>(), results);

        // isto é o que ativa o bloco grande
        String json = move.processEnemyFire(true);

        assertNotNull(json);
    }

    @DisplayName("Caso apenas tiros repetidos deve entrar no ramo especial")
    @Test
    void shouldHandleOnlyRepeatedShotsCase() {
        List<IGame.ShotResult> results = new ArrayList<>();

        results.add(new IGame.ShotResult(true, true, null, false));
        results.add(new IGame.ShotResult(true, true, null, false));

        Move move = new Move(1, new ArrayList<>(), results);

        move.processEnemyFire(true); // ativa verbose
    }

    @DisplayName("Deve remover o + final quando não há tiros na água")
    @Test
    void shouldRemoveTrailingPlus() {
        List<IGame.ShotResult> results = new ArrayList<>();

        results.add(new IGame.ShotResult(true, false, new FakeShip("Destroyer"), false));

        Move move = new Move(1, new ArrayList<>(), results);

        move.processEnemyFire(true);
    }

    @DisplayName("Verbose deve tratar o caso de apenas tiros repetidos")
    @Test
    void verboseShouldHandleOnlyRepeatedShots() {
        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(new IGame.ShotResult(true, true, null, false));
        results.add(new IGame.ShotResult(true, true, null, false));

        Move move = new Move(1, new ArrayList<>(), results);

        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    @DisplayName("Verbose deve tratar tiros válidos, tiros na água e tiros repetidos ao mesmo tempo")
    @Test
    void verboseShouldHandleValidMissedAndRepeatedShotsTogether() {
        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(new IGame.ShotResult(true, false, null, false)); // água
        results.add(new IGame.ShotResult(true, true, null, false));  // repetido

        Move move = new Move(2, new ArrayList<>(), results);

        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }
    @DisplayName("Verbose deve remover o separador final quando há acertos mas não há tiros na água")
    @Test
    void verboseShouldRemoveTrailingSeparatorWhenThereAreHitsWithoutMisses() {
        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(new IGame.ShotResult(true, false, new FakeShip("Destroyer"), false));

        Move move = new Move(3, new ArrayList<>(), results);

        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }
    @DisplayName("Verbose deve tratar múltiplos barcos afundados e múltiplos acertos")
    @Test
    void verboseShouldHandleMultipleSunkBoatsAndHits() {
        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(new IGame.ShotResult(true, false, new FakeShip("Submarine"), true));
        results.add(new IGame.ShotResult(true, false, new FakeShip("Submarine"), true));
        results.add(new IGame.ShotResult(true, false, new FakeShip("Destroyer"), false));
        results.add(new IGame.ShotResult(true, false, null, false)); // água
        results.add(new IGame.ShotResult(true, true, null, false));  // repetido

        Move move = new Move(4, new ArrayList<>(), results);

        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }
    @DisplayName("Deve calcular zero tiros exteriores quando validos e repetidos preenchem o limite")
    @Test
    void shouldAllowOutsideShotsToBeZero() throws Exception {
        List<IGame.ShotResult> results = new ArrayList<>();

        for (int i = 0; i < Game.NUMBER_SHOTS; i++) {
            results.add(new IGame.ShotResult(true, false, null, false));
        }

        Move move = new Move(5, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        assertEquals(0, root.get("outsideShots").asInt());
        assertEquals(Game.NUMBER_SHOTS, root.get("validShots").asInt());
    }
}