package battleship;

public class TestLongMethod {

    public void metodoMuitoGrande() {
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                if (i > 5) {
                    for (int j = 0; j < 10; j++) {
                        if (j % 3 == 0) {
                            if (j > 5) {
                                System.out.println(i + " " + j);
                            }
                        }
                    }
                }
            }
        }
    }
}