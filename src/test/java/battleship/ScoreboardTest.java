package battleship;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for Scoreboard")
class ScoreboardTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() throws Exception {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        clearResultados();
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        clearResultados();
    }

    @SuppressWarnings("unchecked")
    private void clearResultados() throws Exception {
        Field field = Scoreboard.class.getDeclaredField("resultados");
        field.setAccessible(true);
        List<String> resultados = (List<String>) field.get(null);
        resultados.clear();
    }

    @Test
    @DisplayName("addResultado should store a result")
    void addResultadoShouldStoreAResult() throws Exception {
        Scoreboard.addResultado("Player 1 venceu");

        Field field = Scoreboard.class.getDeclaredField("resultados");
        field.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> resultados = (List<String>) field.get(null);

        assertAll(
                () -> assertEquals(1, resultados.size()),
                () -> assertEquals("Player 1 venceu", resultados.get(0))
        );
    }

    @Test
    @DisplayName("mostrarResultados should show empty message when there are no results")
    void mostrarResultadosShouldShowEmptyMessage() {
        Scoreboard.mostrarResultados();

        String output = outputStream.toString();

        assertAll(
                () -> assertTrue(output.contains("=========================")),
                () -> assertTrue(output.contains("SCOREBOARD")),
                () -> assertTrue(output.contains("Nenhum jogo registado.")),
                () -> assertTrue(output.contains("======================"))
        );
    }

    @Test
    @DisplayName("mostrarResultados should show one stored result")
    void mostrarResultadosShouldShowOneStoredResult() {
        Scoreboard.addResultado("Jogador A venceu");

        Scoreboard.mostrarResultados();

        String output = outputStream.toString();

        assertAll(
                () -> assertTrue(output.contains("SCOREBOARD")),
                () -> assertTrue(output.contains("Jogo 1: Jogador A venceu")),
                () -> assertTrue(output.contains("======================"))
        );
    }

    @Test
    @DisplayName("mostrarResultados should show multiple stored results in order")
    void mostrarResultadosShouldShowMultipleStoredResultsInOrder() {
        Scoreboard.addResultado("Jogador A venceu");
        Scoreboard.addResultado("Jogador B venceu");

        Scoreboard.mostrarResultados();

        String output = outputStream.toString();

        assertAll(
                () -> assertTrue(output.contains("Jogo 1: Jogador A venceu")),
                () -> assertTrue(output.contains("Jogo 2: Jogador B venceu"))
        );
    }
}