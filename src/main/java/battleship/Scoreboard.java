package battleship;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class Scoreboard {

    private static List<String> resultados = new ArrayList<>();

    public static void addResultado(String resultado) {
        resultados.add(resultado);
    }

    public static void mostrarResultados() {
        System.out.println(StringUtils.repeat("=", 25));
        System.out.println("SCOREBOARD");
        System.out.println(StringUtils.repeat("=", 25));

        if (resultados.isEmpty()) {
            System.out.println("Nenhum jogo registado.");
        }

        for (String r : resultados) {
            System.out.println(r);
        }

        System.out.println("======================");
    }
}