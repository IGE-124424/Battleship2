package battleship;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class TasksTest {

    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    void setup() {
        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void cleanup() {
        System.setOut(originalOut);
    }

    // ----------------------------------------------------
    // menuHelp
    // ----------------------------------------------------

    @Test
    void testMenuHelp() {
        Tasks.menuHelp();

        String text = output.toString();

        assertTrue(text.contains("AJUDA DO MENU"));
        assertTrue(text.contains("gerafrota"));
        assertTrue(text.contains("desisto"));
    }

    // ----------------------------------------------------
    // readPosition
    // ----------------------------------------------------

    @Test
    void testReadPosition() {
        Scanner sc = new Scanner("4 7");

        Position p = Tasks.readPosition(sc);

        assertNotNull(p);
    }

    // ----------------------------------------------------
    // readClassicPosition
    // ----------------------------------------------------

    @Test
    void testReadClassicPositionCombined() {
        Scanner sc = new Scanner("A5");

        IPosition p = Tasks.readClassicPosition(sc);

        assertNotNull(p);
    }

    @Test
    void testReadClassicPositionSeparated() {
        Scanner sc = new Scanner("B 9");

        IPosition p = Tasks.readClassicPosition(sc);

        assertNotNull(p);
    }

    @Test
    void testReadClassicPositionLowercase() {
        Scanner sc = new Scanner("c12");

        IPosition p = Tasks.readClassicPosition(sc);

        assertNotNull(p);
    }

    @Test
    void testReadClassicPositionInvalid() {
        Scanner sc = new Scanner("99Z");

        assertThrows(IllegalArgumentException.class,
                () -> Tasks.readClassicPosition(sc));
    }

    // ----------------------------------------------------
    // menu()
    // ----------------------------------------------------

    @Test
    void testMenuImmediateExit() {
        InputStream originalIn = System.in;

        try {
            System.setIn(new ByteArrayInputStream("desisto\n".getBytes()));

            Tasks.menu();

            assertTrue(output.toString().contains("Bons ventos!"));

        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void testMenuUnknownCommandThenExit() {
        InputStream originalIn = System.in;

        try {
            System.setIn(new ByteArrayInputStream(
                    "comandoErrado\ndesisto\n".getBytes()
            ));

            Tasks.menu();

            String text = output.toString();

            assertTrue(text.contains("Que comando é esse"));
            assertTrue(text.contains("Bons ventos!"));

        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void testMenuEstadoWithoutFleet() {
        InputStream originalIn = System.in;

        try {
            System.setIn(new ByteArrayInputStream(
                    "estado\ndesisto\n".getBytes()
            ));

            Tasks.menu();

            assertTrue(output.toString()
                    .contains("Nenhuma frota carregada."));

        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void testMenuMapaWithoutGame() {
        InputStream originalIn = System.in;

        try {
            System.setIn(new ByteArrayInputStream(
                    "mapa\ndesisto\n".getBytes()
            ));

            Tasks.menu();

            assertTrue(output.toString()
                    .contains("Nenhum jogo iniciado."));

        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void testMenuTirosWithoutGame() {
        InputStream originalIn = System.in;

        try {
            System.setIn(new ByteArrayInputStream(
                    "tiros\ndesisto\n".getBytes()
            ));

            Tasks.menu();

            assertTrue(output.toString()
                    .contains("Nenhum jogo iniciado."));

        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void testMenuGuiWithoutGame() {
        InputStream originalIn = System.in;

        try {
            System.setIn(new ByteArrayInputStream(
                    "gui\ndesisto\n".getBytes()
            ));

            Tasks.menu();

            assertTrue(output.toString()
                    .contains("Primeiro gera uma frota!"));

        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void testMenuRajadaWithoutGame() {
        InputStream originalIn = System.in;

        try {
            System.setIn(new ByteArrayInputStream(
                    "rajada\ndesisto\n".getBytes()
            ));

            Tasks.menu();

            assertTrue(output.toString()
                    .contains("Primeiro precisa gerar ou carregar uma frota."));

        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void testMenuSimulaWithoutGame() {
        InputStream originalIn = System.in;

        try {
            System.setIn(new ByteArrayInputStream(
                    "simula\ndesisto\n".getBytes()
            ));

            Tasks.menu();

            assertTrue(output.toString()
                    .contains("Primeiro precisa gerar ou carregar uma frota."));

        } finally {
            System.setIn(originalIn);
        }
    }
}
