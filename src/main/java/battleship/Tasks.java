package battleship;

import org.apache.commons.lang3.time.StopWatch;
import java.util.Scanner;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class Tasks {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String GOODBYE_MESSAGE = "Bons ventos!";

    private static final String GUI = "gui";
    private static final String AJUDA = "ajuda";
    private static final String GERAFROTA = "gerafrota";
    private static final String LEFROTA = "lefrota";
    private static final String DESISTIR = "desisto";
    private static final String RAJADA = "rajada";
    private static final String TIROS = "tiros";
    private static final String MAPA = "mapa";
    private static final String STATUS = "estado";
    private static final String SIMULA = "simula";

    public static void menu() {

        IFleet myFleet = null;
        IGame game = null;

        List<String> logJogadas = new ArrayList<>();
        List<String> scoreboard = new ArrayList<>();

        menuHelp();

        Scanner in = new Scanner(System.in);

        StopWatch turnWatch = new StopWatch();
        turnWatch.start();

        while (true) {

            System.out.print("> ");
            String command = in.next();

            if (command.equals(DESISTIR)) {
                break;
            }

            switch (command) {

                case GUI:
                    if (game != null) {
                        BoardGUI.setGame((Game) game);
                        BoardGUI.launchBoard();
                    } else {
                        System.out.println("Primeiro gera uma frota!");
                    }
                    break;

                case GERAFROTA:
                    myFleet = Fleet.createRandom();
                    game = new Game(myFleet);
                    logJogadas.clear();
                    game.printMyBoard(false, true);
                    break;

                case LEFROTA:
                    myFleet = buildFleet(in);
                    game = new Game(myFleet);
                    logJogadas.clear();
                    game.printMyBoard(false, true);
                    break;

                case STATUS:
                    if (myFleet != null)
                        myFleet.printStatus();
                    else
                        System.out.println("Nenhuma frota carregada.");
                    break;

                case MAPA:
                    if (game != null)
                        game.printMyBoard(false, true);
                    else
                        System.out.println("Nenhum jogo iniciado.");
                    break;

                case RAJADA:

                    if (game != null) {

                        turnWatch.stop();
                        double segundos = turnWatch.getTime() / 1000.0;
                        System.out.printf("Tempo até esta jogada: %.2f s%n", segundos);

                        turnWatch.reset();
                        turnWatch.start();

                        String jogada = game.readEnemyFire(in);

                        if (jogada != null)
                            logJogadas.add(jogada);

                        myFleet.printStatus();
                        game.printMyBoard(true, false);

                        if (game.getRemainingShips() == 0) {

                            game.over();

                            scoreboard.add("Jogo terminado — total jogadas: " + logJogadas.size());

                            gerarPdf(logJogadas);

                            game = null;
                            myFleet = null;
                        }

                    } else {
                        System.out.println("Primeiro precisa gerar ou carregar uma frota.");
                    }

                    break;

                case SIMULA:

                    if (game != null) {

                        while (game.getRemainingShips() > 0) {

                            String jogada = game.randomEnemyFire();

                            if (jogada != null)
                                logJogadas.add(jogada);

                            myFleet.printStatus();
                            game.printMyBoard(true, false);

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }

                        game.over();

                        scoreboard.add("Simulação terminada — total jogadas: " + logJogadas.size());

                        gerarPdf(logJogadas);

                        game = null;
                        myFleet = null;

                    } else {
                        System.out.println("Primeiro precisa gerar ou carregar uma frota.");
                    }

                    break;

                case TIROS:

                    if (game != null)
                        game.printMyBoard(true, true);
                    else
                        System.out.println("Nenhum jogo iniciado.");

                    break;

                case AJUDA:
                    menuHelp();
                    break;

                default:
                    System.out.println("Que comando é esse??? Repete ...");
            }
        }

        System.out.println("\n===== SCOREBOARD =====");

        for (String s : scoreboard)
            System.out.println(s);

        System.out.println(GOODBYE_MESSAGE);
    }

    private static void gerarPdf(List<String> logJogadas) {

        try {
            Path pdf = PdfReportGenerator.generateMovesReport(
                    logJogadas,
                    Path.of("target", "jogadas-" + System.currentTimeMillis() + ".pdf")
            );

            System.out.println("PDF gerado em: " + pdf.toAbsolutePath());

        } catch (Exception e) {
            System.out.println("Erro ao gerar PDF: " + e.getMessage());
        }
    }

    public static void menuHelp() {

        System.out.println("======================= AJUDA DO MENU =========================");
        System.out.println("- gui");
        System.out.println("- gerafrota");
        System.out.println("- lefrota");
        System.out.println("- estado");
        System.out.println("- mapa");
        System.out.println("- rajada");
        System.out.println("- simula");
        System.out.println("- tiros");
        System.out.println("- desisto");
        System.out.println("===============================================================");
    }

    public static Fleet buildFleet(Scanner in) {

        Fleet fleet = new Fleet();
        int i = 0;

        while (i < Fleet.FLEET_SIZE) {

            IShip s = readShip(in);

            if (s != null && fleet.addShip(s))
                i++;
        }

        return fleet;
    }

    public static Ship readShip(Scanner in) {

        String shipKind = in.next();
        Position pos = readPosition(in);
        char c = in.next().charAt(0);
        Compass bearing = Compass.charToCompass(c);

        return Ship.buildShip(shipKind, bearing, pos);
    }

    public static Position readPosition(Scanner in) {

        int row = in.nextInt();
        int column = in.nextInt();

        return new Position(row, column);
    }

    public static IPosition readClassicPosition(@NotNull Scanner in) {

        String part1 = in.next();
        String part2 = in.hasNextInt() ? in.next() : null;

        String input = (part2 != null) ? part1 + part2 : part1;

        input = input.toUpperCase();

        if (input.matches("[A-Z]\\d+")) {

            char column = input.charAt(0);
            int row = Integer.parseInt(input.substring(1));

            return new Position(column, row);
        }

        throw new IllegalArgumentException("Formato inválido");
    }
}