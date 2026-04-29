package battleship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.*;

public class Game implements IGame {

	public static final int BOARD_SIZE = 10;
	public static final int NUMBER_SHOTS = 3;

	private static final char EMPTY_MARKER = '.';
	private static final char SHIP_MARKER = '#';
	private static final char SHOT_SHIP_MARKER = '*';
	private static final char SHOT_WATER_MARKER = 'o';
	private static final char SHIP_ADJACENT_MARKER = '-';

	private final IFleet myFleet;
	private final List<IMove> alienMoves;

	private final IFleet alienFleet;
	private final List<IMove> myMoves;

	private Integer countInvalidShots;
	private Integer countRepeatedShots;
	private Integer countHits;
	private Integer countSinks;
	private int moveNumber;

	public Game(IFleet myFleet) {
		this.moveNumber = 1;

		this.alienMoves = new ArrayList<>();
		this.myMoves = new ArrayList<>();

		this.alienFleet = new Fleet();
		this.myFleet = myFleet;

		this.countInvalidShots = 0;
		this.countRepeatedShots = 0;
		this.countHits = 0;
		this.countSinks = 0;
	}

	public static void printBoard(IFleet fleet, List<IMove> moves, boolean show_shots, boolean showLegend) {
		assert fleet != null;
		assert moves != null;

		char[][] map = new char[BOARD_SIZE][BOARD_SIZE];

		initializeBoardContents(map);
		placeShipsAndAdjacents(fleet, map);
		applyShotMarkers(moves, show_shots, map);

		System.out.println();
		System.out.print("    ");
		for (int col = 0; col < BOARD_SIZE; col++) {
			System.out.print(" " + (col + 1));
		}
		System.out.println();

		System.out.print("   +-");
		for (int col = 0; col < BOARD_SIZE; col++) {
			System.out.print("--");
		}
		System.out.println("+");

		for (int row = 0; row < BOARD_SIZE; row++) {
			Position pos = new Position(row, 0);
			char rowLabel = pos.getClassicRow();
			System.out.print(" " + rowLabel + " |");
			for (int col = 0; col < BOARD_SIZE; col++) {
				System.out.print(" " + map[row][col]);
			}
			System.out.println(" |");
		}

		System.out.print("   +");
		for (int col = 0; col < BOARD_SIZE; col++) {
			System.out.print("--");
		}
		System.out.println("-+");

		if (showLegend) {
			System.out.println("          LEGENDA");
			System.out.println("'" + SHIP_MARKER + "'->navio, '" + SHIP_ADJACENT_MARKER + "'->adjacente a navio, '" + EMPTY_MARKER + "'->água");
			System.out.println("'" + SHOT_SHIP_MARKER + "'->Tiro certeiro, '" + SHOT_WATER_MARKER + "'->Tiro na água");
		}
		System.out.println();
	}

	private static void applyShotMarkers(List<IMove> moves, boolean showShots, char[][] map) {
		if (showShots) {
			for (IMove move : moves) {
				for (IPosition shot : move.getShots()) {
					if (shot.isInside()) {
						int row = shot.getRow();
						int col = shot.getColumn();
						markShotOnBoard(map, row, col);
					}
				}
			}
		}
	}

	private static void markShotOnBoard(char[][] map, int row, int col) {
		if (map[row][col] == SHIP_MARKER) {
			map[row][col] = SHOT_SHIP_MARKER;
		}
		if (map[row][col] == EMPTY_MARKER || map[row][col] == SHIP_ADJACENT_MARKER) {
			map[row][col] = SHOT_WATER_MARKER;
		}
	}

	private static void placeShipsAndAdjacents(IFleet fleet, char[][] map) {
		for (IShip ship : fleet.getShips()) {
			for (IPosition shipPosition : ship.getPositions()) {
				map[shipPosition.getRow()][shipPosition.getColumn()] = SHIP_MARKER;
			}
			if (!ship.stillFloating()) {
				for (IPosition adjacentPosition : ship.getAdjacentPositions()) {
					map[adjacentPosition.getRow()][adjacentPosition.getColumn()] = SHIP_ADJACENT_MARKER;
				}
			}
		}
	}

	public static String jsonShots(List<IPosition> shots) {
		assert shots != null;

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		List<Map<String, Object>> simplifiedShots = new ArrayList<>();
		for (IPosition shot : shots) {
			Map<String, Object> simplePos = new LinkedHashMap<>();
			simplePos.put("row", String.valueOf(shot.getClassicRow()));
			simplePos.put("column", shot.getClassicColumn());
			simplifiedShots.add(simplePos);
		}

		try {
			return objectMapper.writeValueAsString(simplifiedShots);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Erro ao serializar o JSON", e);
		}
	}

	@Override
	public IFleet getMyFleet() {
		return myFleet;
	}

	@Override
	public List<IMove> getAlienMoves() {
		return alienMoves;
	}

	@Override
	public IFleet getAlienFleet() {
		return alienFleet;
	}

	@Override
	public List<IMove> getMyMoves() {
		return myMoves;
	}

	public String randomEnemyFire() {
		Random random = new Random(System.currentTimeMillis());

		Set<IPosition> usablePositions = buildUsablePositions();
		List<IPosition> candidateShots = new ArrayList<>(usablePositions);
		List<IPosition> shots = new ArrayList<>();

		System.out.println();

		generateShots(candidateShots, shots, random);

		System.out.print("rajada ");
		printVolley(shots);
		System.out.println();

		this.fireShots(shots);

		return Game.jsonShots(shots);
	}

	private static void printVolley(List<IPosition> shots) {
		for (IPosition shot : shots) {
			System.out.print(shot + " ");
		}
	}

	private static void generateShots(List<IPosition> candidateShots, List<IPosition> shots, Random random) {
		IPosition newShot = null;

		if (candidateShots.size() >= Game.NUMBER_SHOTS) {
			while (shots.size() < Game.NUMBER_SHOTS) {
				newShot = candidateShots.get(random.nextInt(candidateShots.size()));
				if (!shots.contains(newShot)) {
					shots.add(newShot);
				}
			}
		} else {
			while (shots.size() < candidateShots.size()) {
				newShot = candidateShots.get(random.nextInt(candidateShots.size()));
				if (!shots.contains(newShot)) {
					shots.add(newShot);
				}
			}
			while (shots.size() < Game.NUMBER_SHOTS) {
				shots.add(newShot);
			}
		}
	}

	private Set<IPosition> buildUsablePositions() {
		Set<IPosition> usablePositions = new HashSet<>();

		for (int r = 0; r < BOARD_SIZE; r++) {
			for (int c = 0; c < BOARD_SIZE; c++) {
				usablePositions.add(new Position(r, c));
			}
		}

		this.myFleet.getSunkShips().forEach(ship -> usablePositions.removeAll(ship.getAdjacentPositions()));
		this.alienMoves.forEach(move -> usablePositions.removeAll(move.getShots()));

		return usablePositions;
	}

	public String readEnemyFire(Scanner in) {
		assert in != null;

		String input = in.nextLine().trim();
		List<IPosition> shots = new ArrayList<>();

		try (Scanner inputScanner = new Scanner(input)) {
			while (shots.size() < NUMBER_SHOTS && inputScanner.hasNext()) {
				String token = inputScanner.next();
				addShotFromToken(shots, inputScanner, token);
			}
		}

		if (shots.size() != NUMBER_SHOTS) {
			throw new IllegalArgumentException("Você deve inserir exatamente " + NUMBER_SHOTS + " posições!");
		}

		this.fireShots(shots);

		return Game.jsonShots(shots);
	}

	private void addShotFromToken(List<IPosition> shots, Scanner inputScanner, String token) {
		if (token.matches("[A-Za-z]")) {
			addSeparatedClassicPosition(shots, inputScanner, token);
		} else {
			Scanner singleScanner = new Scanner(token);
			shots.add(Tasks.readClassicPosition(singleScanner));
		}
	}

	private void addSeparatedClassicPosition(List<IPosition> shots, Scanner inputScanner, String token) {
		if (inputScanner.hasNextInt()) {
			int row = inputScanner.nextInt();
			shots.add(new Position(token.toUpperCase().charAt(0), row));
		} else {
			throw new IllegalArgumentException("Posição incompleta! A coluna '" + token + "' não é seguida por uma linha.");
		}
	}

	public void fireShots(List<IPosition> shots) {
		assert shots != null;

		validateShotCount(shots);

		List<ShotResult> shotResults = new ArrayList<>();
		List<IPosition> alreadyShot = new ArrayList<>();

		for (IPosition pos : shots) {
			shotResults.add(fireSingleShot(pos, alreadyShot.contains(pos)));
			alreadyShot.add(pos);
		}

		Move move = new Move(moveNumber, shots, shotResults);
		move.processEnemyFire(true);

		alienMoves.add(move);
		moveNumber++;
	}

	private void validateShotCount(List<IPosition> shots) {
		if (shots.size() != NUMBER_SHOTS) {
			throw new IllegalArgumentException("Must fire exactly " + NUMBER_SHOTS + " shots per move.");
		}
	}

	public ShotResult fireSingleShot(IPosition pos, boolean isRepeated) {
		assert pos != null;

		if (!pos.isInside()) {
			countInvalidShots++;
			return new ShotResult(false, false, null, false);
		}

		if (isRepeated || repeatedShot(pos)) {
			countRepeatedShots++;
			return new ShotResult(true, true, null, false);
		}

		IShip ship = myFleet.shipAt(pos);
		if (ship == null) {
			return new ShotResult(true, false, null, false);
		}

		ship.shoot(pos);
		countHits++;

		if (!ship.stillFloating()) {
			countSinks++;
		}

		return new ShotResult(true, false, ship, !ship.stillFloating());
	}

	@Override
	public int getRepeatedShots() {
		return this.countRepeatedShots;
	}

	@Override
	public int getInvalidShots() {
		return this.countInvalidShots;
	}

	@Override
	public int getHits() {
		return this.countHits;
	}

	@Override
	public int getSunkShips() {
		return this.countSinks;
	}

	@Override
	public int getRemainingShips() {
		List<IShip> floatingShips = myFleet.getFloatingShips();
		return floatingShips.size();
	}

	public boolean repeatedShot(IPosition pos) {
		assert pos != null;

		for (IMove move : alienMoves) {
			if (move.getShots().contains(pos)) {
				return true;
			}
		}
		return false;
	}

	public void printMyBoard(boolean show_shots, boolean show_legend) {
		Game.printBoard(this.myFleet, this.alienMoves, show_shots, show_legend);
	}

	public void printAlienBoard(boolean show_shots, boolean show_legend) {
		Game.printBoard(this.alienFleet, this.myMoves, show_shots, show_legend);
	}

	public char[][] getBoard(boolean showShots) {
		char[][] map = new char[BOARD_SIZE][BOARD_SIZE];

		initializeBoardContents(map);
		placeShips(map);

		if (showShots) {
			applyShots(map);
		}

		return map;
	}

	private void applyShots(char[][] map) {
		for (IMove move : alienMoves) {
			for (IPosition shot : move.getShots()) {
				int row = shot.getRow();
				int col = shot.getColumn();

				if (map[row][col] == SHIP_MARKER) {
					map[row][col] = SHOT_SHIP_MARKER;
				} else if (map[row][col] == EMPTY_MARKER || map[row][col] == SHIP_ADJACENT_MARKER) {
					map[row][col] = SHOT_WATER_MARKER;
				}
			}
		}
	}

	private void placeShips(char[][] map) {
		for (IShip ship : myFleet.getShips()) {
			for (IPosition pos : ship.getPositions()) {
				map[pos.getRow()][pos.getColumn()] = SHIP_MARKER;
			}
		}
	}

	private static void initializeBoardContents(char[][] map) {
		for (int r = 0; r < BOARD_SIZE; r++) {
			for (int c = 0; c < BOARD_SIZE; c++) {
				map[r][c] = EMPTY_MARKER;
			}
		}
	}

	public void over() {
		String resultado = "Hits: " + this.countHits +
				" | Navios afundados: " + this.countSinks +
				" | Jogadas: " + (this.moveNumber - 1);

		Scoreboard.addResultado(resultado);

		System.out.println();
		System.out.println("+--------------------------------------------------------------+");
		System.out.println("| Maldito sejas, Java Sparrow, eu voltarei, glub glub glub ... |");
		System.out.println("+--------------------------------------------------------------+");

		Scoreboard.mostrarResultados();
	}
}