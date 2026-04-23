package battleship;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Test class for Game.
 * Author: britoeabreu
 * Date: 2024-03-19
 * Time: 15:30
 * Cyclomatic Complexity for each method:
 * - Game (constructor): 1
 * - fire: 7
 * - getShots: 1
 * - getRepeatedShots: 1
 * - getInvalidShots: 1
 * - getHits: 1
 * - getSunkShips: 1
 * - getRemainingShips: 1
 * - validShot: 3
 * - repeatedShot: 2
 * - printBoard: 1
 * - printValidShots: 1
 * - printFleet: 1
 */
public class GameTest {

	private Game game;

	@BeforeEach
	void setUp() {
		game = new Game(new Fleet()); // Assuming Fleet is a concrete implementation of IFleet
	}

	@AfterEach
	void tearDown() {
		game = null;
	}

	@Test
	void constructor() {
		assertNotNull(game, "Game instance should not be null after construction.");
		assertNotNull(game.getAlienMoves(), "Shots list should not be null after initialization.");
		assertTrue(game.getAlienMoves().isEmpty(), "Shots list should be empty upon initialization.");
		assertEquals(0, game.getInvalidShots(), "Invalid shots count should be zero upon initialization.");
		assertEquals(0, game.getRepeatedShots(), "Repeated shots count should be zero upon initialization.");
		assertEquals(0, game.getHits(), "Hits count should be zero upon initialization.");
		assertEquals(0, game.getSunkShips(), "Sunk ships count should be zero upon initialization.");
	}

	@Test
	void fire2() {
		Position invalidPosition = new Position(-1, 5);
		game.fireSingleShot(invalidPosition, false);
		assertEquals(1, game.getInvalidShots(), "Invalid shots counter should increase for an invalid shot.");
	}

	@Test
	void fire3() {
		Position position = new Position(2, 3);
		game.fireSingleShot(position, false);
		game.fireSingleShot(position, true);
		assertEquals(1, game.getRepeatedShots(), "Repeated shots counter should increase for a repeated shot.");
	}

	@Test
	void repeatedShot1() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		Position position = new Position(2, 3);
		assertTrue(game.repeatedShot(position), "Position (2,3) should be marked as repeated after firing.");
	}

	@Test
	void repeatedShot2() {
		Position position = new Position(2, 3);
		assertFalse(game.repeatedShot(position), "Position (2,3) should not be marked as repeated before firing.");
	}

	@Test
	void getAlienMoves() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		assertEquals(1, game.getAlienMoves().size(), "Shots list should contain one shot after firing once.");
	}

	@Test
	void getRemainingShips() {
		IFleet fleet = game.getMyFleet();
		Ship ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		Ship ship2 = new Frigate(Compass.EAST, new Position(5, 5));

		fleet.addShip(ship1);
		assertEquals(1, game.getRemainingShips(), "Just one ship was created!");
		fleet.addShip(ship2);
		assertEquals(2, game.getRemainingShips(), "Two ships were created!");
		ship2.sink();
		assertEquals(1, game.getRemainingShips(), "Remaining ships count should be 1 after sinking one of two ships.");
	}
	@DisplayName("getMyFleet deve devolver a fleet passada no construtor")
	@Test
	void getMyFleetShouldReturnConstructorFleet() {
		IFleet fleet = new Fleet();
		Game localGame = new Game(fleet);

		assertSame(fleet, localGame.getMyFleet());
	}

	@DisplayName("getMyMoves deve começar vazio")
	@Test
	void getMyMovesShouldStartEmpty() {
		assertNotNull(game.getMyMoves());
		assertTrue(game.getMyMoves().isEmpty());
	}

	@DisplayName("jsonShots deve devolver JSON com as coordenadas clássicas corretas")
	@Test
	void jsonShotsShouldSerializeClassicCoordinates() throws Exception {
		List<IPosition> shots = List.of(
				new Position(0, 0), // A1
				new Position(2, 4), // C5
				new Position(9, 9)  // J10
		);

		String json = Game.jsonShots(shots);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		assertEquals(3, root.size());
		assertEquals("A", root.get(0).get("row").asText());
		assertEquals(1, root.get(0).get("column").asInt());

		assertEquals("C", root.get(1).get("row").asText());
		assertEquals(5, root.get(1).get("column").asInt());

		assertEquals("J", root.get(2).get("row").asText());
		assertEquals(10, root.get(2).get("column").asInt());
	}

	@DisplayName("readEnemyFire deve aceitar formato compacto")
	@Test
	void readEnemyFireShouldAcceptCompactFormat() throws Exception {
		Scanner scanner = new Scanner("A1 B2 C3");

		String json = game.readEnemyFire(scanner);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		assertEquals(3, root.size());
		assertEquals(1, game.getAlienMoves().size());
	}

	@DisplayName("readEnemyFire deve aceitar formato separado")
	@Test
	void readEnemyFireShouldAcceptSeparatedFormat() throws Exception {
		Scanner scanner = new Scanner("A 1 B 2 C 3");

		String json = game.readEnemyFire(scanner);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		assertEquals(3, root.size());
		assertEquals(1, game.getAlienMoves().size());
	}

	@DisplayName("readEnemyFire deve lançar exceção quando faltam posições")
	@Test
	void readEnemyFireShouldThrowWhenPositionsAreMissing() {
		Scanner scanner = new Scanner("A1 B2");

		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(scanner));
	}

	@DisplayName("readEnemyFire deve lançar exceção quando a posição está incompleta")
	@Test
	void readEnemyFireShouldThrowWhenTokenIsIncomplete() {
		Scanner scanner = new Scanner("A B2 C3");

		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(scanner));
	}

	@DisplayName("fireShots deve lançar exceção quando não há exatamente três tiros")
	@Test
	void fireShotsShouldThrowWhenShotCountIsInvalid() {
		List<IPosition> shots = List.of(new Position(0, 0), new Position(1, 1));

		assertThrows(IllegalArgumentException.class, () -> game.fireShots(shots));
	}

	@DisplayName("fireSingleShot deve devolver tiro válido na água")
	@Test
	void fireSingleShotShouldReturnMissWhenNoShipExists() {
		Position pos = new Position(0, 0);

		IGame.ShotResult result = game.fireSingleShot(pos, false);

		assertTrue(result.valid());
		assertFalse(result.repeated());
		assertNull(result.ship());
		assertFalse(result.sunk());
		assertEquals(0, game.getHits());
	}

	@DisplayName("fireSingleShot deve acertar num barco")
	@Test
	void fireSingleShotShouldHitShip() {
		IFleet fleet = new Fleet();
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);

		Game localGame = new Game(fleet);

		IGame.ShotResult result = localGame.fireSingleShot(new Position(1, 1), false);

		assertTrue(result.valid());
		assertFalse(result.repeated());
		assertNotNull(result.ship());
		assertEquals(1, localGame.getHits());
	}

	@DisplayName("getBoard sem tiros deve mostrar navios")
	@Test
	void getBoardWithoutShotsShouldShowShips() {
		IFleet fleet = new Fleet();
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);

		Game localGame = new Game(fleet);
		char[][] board = localGame.getBoard(false);

		assertEquals('#', board[1][1]);
	}

	@DisplayName("getBoard com tiros deve marcar água")
	@Test
	void getBoardWithShotsShouldMarkWaterShots() {
		List<IPosition> shots = List.of(
				new Position(0, 0),
				new Position(0, 1),
				new Position(0, 2)
		);

		game.fireShots(shots);

		char[][] board = game.getBoard(true);

		assertEquals('o', board[0][0]);
		assertEquals('o', board[0][1]);
		assertEquals('o', board[0][2]);
	}

	@DisplayName("randomEnemyFire deve devolver três tiros em JSON")
	@Test
	void randomEnemyFireShouldReturnThreeShotsJson() throws Exception {
		String json = game.randomEnemyFire();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		assertEquals(Game.NUMBER_SHOTS, root.size());
		assertEquals(1, game.getAlienMoves().size());
	}

	@DisplayName("printMyBoard não deve lançar exceção")
	@Test
	void printMyBoardShouldNotThrow() {
		assertDoesNotThrow(() -> game.printMyBoard(true, true));
	}

	@DisplayName("printAlienBoard não deve lançar exceção")
	@Test
	void printAlienBoardShouldNotThrow() {
		assertDoesNotThrow(() -> game.printAlienBoard(true, true));
	}
	@DisplayName("fireSingleShot deve devolver repetido quando a posição já foi disparada anteriormente")
	@Test
	void fireSingleShotShouldDetectPreviouslyRepeatedShot() {
		List<IPosition> firstMove = List.of(
				new Position(2, 2),
				new Position(2, 3),
				new Position(2, 4)
		);
		game.fireShots(firstMove);

		IGame.ShotResult result = game.fireSingleShot(new Position(2, 2), false);

		assertTrue(result.valid());
		assertTrue(result.repeated());
		assertNull(result.ship());
		assertEquals(1, game.getRepeatedShots());
	}
	@DisplayName("fireSingleShot deve afundar um barco quando atinge a última posição em falta")
	@Test
	void fireSingleShotShouldSinkShip() {
		IFleet fleet = new Fleet();
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);

		Game localGame = new Game(fleet);

		for (IPosition pos : ship.getPositions()) {
			localGame.fireSingleShot(pos, false);
		}

		assertEquals(1, localGame.getSunkShips());
		assertEquals(ship.getSize(), localGame.getHits());
	}
	@DisplayName("getBoard com tiros deve marcar acerto em navio")
	@Test
	void getBoardWithShotsShouldMarkHitOnShip() {
		IFleet fleet = new Fleet();
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);

		Game localGame = new Game(fleet);

		List<IPosition> shots = List.of(
				new Position(1, 1),
				new Position(0, 0),
				new Position(0, 1)
		);
		localGame.fireShots(shots);

		char[][] board = localGame.getBoard(true);

		assertEquals('*', board[1][1]);
	}
	@DisplayName("getBoard sem mostrar tiros não deve marcar tiros na grelha")
	@Test
	void getBoardWithoutShowingShotsShouldNotMarkShots() {
		List<IPosition> shots = List.of(
				new Position(0, 0),
				new Position(0, 1),
				new Position(0, 2)
		);
		game.fireShots(shots);

		char[][] board = game.getBoard(false);

		assertEquals('.', board[0][0]);
		assertEquals('.', board[0][1]);
		assertEquals('.', board[0][2]);
	}
	@DisplayName("readEnemyFire deve aceitar letras minúsculas")
	@Test
	void readEnemyFireShouldAcceptLowercaseLetters() throws Exception {
		Scanner scanner = new Scanner("a1 b2 c3");

		String json = game.readEnemyFire(scanner);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		assertEquals("A", root.get(0).get("row").asText());
		assertEquals("B", root.get(1).get("row").asText());
		assertEquals("C", root.get(2).get("row").asText());
	}
	@DisplayName("randomEnemyFire deve devolver sempre três tiros")
	@Test
	void randomEnemyFireShouldAlwaysReturnExactlyThreeShots() throws Exception {
		String json = game.randomEnemyFire();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		assertEquals(3, root.size());
	}
	@DisplayName("printBoard não deve lançar exceção sem mostrar tiros nem legenda")
	@Test
	void printBoardShouldNotThrowWithoutShotsOrLegend() {
		assertDoesNotThrow(() -> Game.printBoard(game.getMyFleet(), game.getAlienMoves(), false, false));
	}
	@DisplayName("printBoard não deve lançar exceção com tiros e legenda")
	@Test
	void printBoardShouldNotThrowWithShotsAndLegend() {
		List<IPosition> shots = List.of(
				new Position(0, 0),
				new Position(0, 1),
				new Position(0, 2)
		);
		game.fireShots(shots);

		assertDoesNotThrow(() -> Game.printBoard(game.getMyFleet(), game.getAlienMoves(), true, true));
	}

	@DisplayName("getAlienFleet devolve a mesma referência usada pela implementação atual")
	@Test
	void getAlienFleetShouldReturnCurrentImplementationReference() {
		assertSame(game.getMyFleet(), game.getAlienFleet());
	}

	@DisplayName("fireSingleShot deve contar repetido quando o parâmetro isRepeated é true")
	@Test
	void fireSingleShotShouldCountRepeatedWhenFlagIsTrue() {
		IGame.ShotResult result = game.fireSingleShot(new Position(0, 0), true);

		assertTrue(result.valid());
		assertTrue(result.repeated());
		assertNull(result.ship());
		assertFalse(result.sunk());
		assertEquals(1, game.getRepeatedShots());
	}

	@DisplayName("fireSingleShot deve acertar e afundar um Barge num único tiro")
	@Test
	void fireSingleShotShouldSinkBargeInOneShot() {
		IFleet fleet = new Fleet();
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);

		Game localGame = new Game(fleet);

		IGame.ShotResult result = localGame.fireSingleShot(new Position(1, 1), false);

		assertTrue(result.valid());
		assertFalse(result.repeated());
		assertNotNull(result.ship());
		assertTrue(result.sunk());
		assertEquals(1, localGame.getHits());
		assertEquals(1, localGame.getSunkShips());
		assertEquals(0, localGame.getRemainingShips());
	}

	@DisplayName("fireShots deve registar uma jogada e incrementar o número de jogadas")
	@Test
	void fireShotsShouldRegisterMove() {
		List<IPosition> shots = List.of(
				new Position(0, 0),
				new Position(0, 1),
				new Position(0, 2)
		);

		game.fireShots(shots);

		assertEquals(1, game.getAlienMoves().size());
		assertEquals(shots, game.getAlienMoves().get(0).getShots());
	}

	@DisplayName("fireShots deve marcar tiros repetidos dentro da mesma jogada")
	@Test
	void fireShotsShouldHandleRepeatedShotsWithinSameMove() {
		List<IPosition> shots = List.of(
				new Position(0, 0),
				new Position(0, 0),
				new Position(0, 1)
		);

		game.fireShots(shots);

		assertEquals(1, game.getRepeatedShots());
		assertEquals(1, game.getAlienMoves().size());
	}

	@DisplayName("repeatedShot deve detetar posições disparadas em jogadas anteriores")
	@Test
	void repeatedShotShouldDetectShotFromPreviousMove() {
		List<IPosition> shots = List.of(
				new Position(1, 1),
				new Position(1, 2),
				new Position(1, 3)
		);

		game.fireShots(shots);

		assertTrue(game.repeatedShot(new Position(1, 2)));
		assertFalse(game.repeatedShot(new Position(9, 9)));
	}

	@DisplayName("jsonShots deve serializar lista vazia")
	@Test
	void jsonShotsShouldSerializeEmptyList() throws Exception {
		String json = Game.jsonShots(List.of());

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		assertTrue(root.isArray());
		assertEquals(0, root.size());
	}

	@DisplayName("jsonShots deve serializar um único tiro")
	@Test
	void jsonShotsShouldSerializeSingleShot() throws Exception {
		String json = Game.jsonShots(List.of(new Position(0, 0)));

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		assertEquals(1, root.size());
		assertEquals("A", root.get(0).get("row").asText());
		assertEquals(1, root.get(0).get("column").asInt());
	}

	@DisplayName("readEnemyFire deve ignorar espaços extra")
	@Test
	void readEnemyFireShouldHandleExtraSpaces() throws Exception {
		Scanner scanner = new Scanner("   A1    B2    C3   ");

		String json = game.readEnemyFire(scanner);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		assertEquals(3, root.size());
		assertEquals(1, game.getAlienMoves().size());
	}

	@DisplayName("readEnemyFire deve ignorar tokens a mais depois de três posições")
	@Test
	void readEnemyFireShouldIgnoreExtraTokensAfterThreePositions() throws Exception {
		Scanner scanner = new Scanner("A1 B2 C3 D4");

		String json = game.readEnemyFire(scanner);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		assertEquals(3, root.size());
		assertEquals("A", root.get(0).get("row").asText());
		assertEquals("B", root.get(1).get("row").asText());
		assertEquals("C", root.get(2).get("row").asText());
	}

	@DisplayName("readEnemyFire deve aceitar combinação de formato compacto e separado")
	@Test
	void readEnemyFireShouldAcceptMixedFormats() throws Exception {
		Scanner scanner = new Scanner("A1 B 2 C3");

		String json = game.readEnemyFire(scanner);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		assertEquals(3, root.size());
		assertEquals("A", root.get(0).get("row").asText());
		assertEquals(1, root.get(0).get("column").asInt());
		assertEquals("B", root.get(1).get("row").asText());
		assertEquals(2, root.get(1).get("column").asInt());
		assertEquals("C", root.get(2).get("row").asText());
		assertEquals(3, root.get(2).get("column").asInt());
	}

	@DisplayName("readEnemyFire deve lançar exceção para coluna sem linha")
	@Test
	void readEnemyFireShouldThrowForLetterWithoutNumber() {
		Scanner scanner = new Scanner("A B 2 C3");

		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(scanner));
	}

	@DisplayName("getBoard sem tiros deve deixar água intacta")
	@Test
	void getBoardWithoutShotsShouldKeepWaterCellsAsDots() {
		char[][] board = game.getBoard(false);

		assertEquals('.', board[0][0]);
		assertEquals('.', board[5][5]);
	}

	@DisplayName("getBoard com tiros deve marcar água com o")
	@Test
	void getBoardWithShotsShouldMarkWater() {
		List<IPosition> shots = List.of(
				new Position(0, 0),
				new Position(0, 1),
				new Position(0, 2)
		);

		game.fireShots(shots);

		char[][] board = game.getBoard(true);

		assertEquals('o', board[0][0]);
		assertEquals('o', board[0][1]);
		assertEquals('o', board[0][2]);
	}

	@DisplayName("getBoard com tiros deve marcar navio atingido com asterisco")
	@Test
	void getBoardWithShotsShouldMarkShipHit() {
		IFleet fleet = new Fleet();
		IShip ship = new Barge(Compass.NORTH, new Position(2, 2));
		fleet.addShip(ship);

		Game localGame = new Game(fleet);

		List<IPosition> shots = List.of(
				new Position(2, 2),
				new Position(0, 0),
				new Position(0, 1)
		);

		localGame.fireShots(shots);

		char[][] board = localGame.getBoard(true);

		assertEquals('*', board[2][2]);
		assertEquals('o', board[0][0]);
		assertEquals('o', board[0][1]);
	}

	@DisplayName("getBoard sem mostrar tiros não deve alterar o tabuleiro com tiros feitos")
	@Test
	void getBoardWithoutShowShotsShouldNotPaintShotMarkers() {
		List<IPosition> shots = List.of(
				new Position(0, 0),
				new Position(0, 1),
				new Position(0, 2)
		);

		game.fireShots(shots);

		char[][] board = game.getBoard(false);

		assertEquals('.', board[0][0]);
		assertEquals('.', board[0][1]);
		assertEquals('.', board[0][2]);
	}

	@DisplayName("printMyBoard não deve lançar exceção com diferentes flags")
	@Test
	void printMyBoardShouldNotThrowForDifferentFlags() {
		assertDoesNotThrow(() -> game.printMyBoard(false, false));
		assertDoesNotThrow(() -> game.printMyBoard(true, false));
		assertDoesNotThrow(() -> game.printMyBoard(false, true));
		assertDoesNotThrow(() -> game.printMyBoard(true, true));
	}

	@DisplayName("printAlienBoard não deve lançar exceção com diferentes flags")
	@Test
	void printAlienBoardShouldNotThrowForDifferentFlags() {
		assertDoesNotThrow(() -> game.printAlienBoard(false, false));
		assertDoesNotThrow(() -> game.printAlienBoard(true, false));
		assertDoesNotThrow(() -> game.printAlienBoard(false, true));
		assertDoesNotThrow(() -> game.printAlienBoard(true, true));
	}

	@DisplayName("printBoard não deve lançar exceção com fleet vazia")
	@Test
	void staticPrintBoardShouldNotThrowWithEmptyFleet() {
		assertDoesNotThrow(() -> Game.printBoard(new Fleet(), List.of(), false, false));
	}

	@DisplayName("printBoard deve cobrir navio afundado e adjacentes")
	@Test
	void staticPrintBoardShouldHandleSunkShipAndAdjacents() {
		IFleet fleet = new Fleet();
		IShip ship = new Barge(Compass.NORTH, new Position(3, 3));
		fleet.addShip(ship);
		ship.sink();

		assertDoesNotThrow(() -> Game.printBoard(fleet, List.of(), false, true));
	}

	@DisplayName("randomEnemyFire deve devolver sempre três tiros")
	@Test
	void randomEnemyFireShouldReturnExactlyThreeShots() throws Exception {
		String json = game.randomEnemyFire();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		assertEquals(Game.NUMBER_SHOTS, root.size());
		assertEquals(1, game.getAlienMoves().size());
	}

	@DisplayName("randomEnemyFire deve entrar no ramo com menos de três posições candidatas")
	@Test
	void randomEnemyFireShouldHandleLessThanThreeCandidateShots() throws Exception {
		Game localGame = new Game(new Fleet());

		List<IPosition> almostAllPositions = new ArrayList<>();
		for (int r = 0; r < Game.BOARD_SIZE; r++) {
			for (int c = 0; c < Game.BOARD_SIZE; c++) {
				if (!((r == 9 && c == 8) || (r == 9 && c == 9))) {
					almostAllPositions.add(new Position(r, c));
				}
			}
		}

		List<IGame.ShotResult> dummyResults = new ArrayList<>();
		Move previousMove = new Move(1, almostAllPositions, dummyResults);
		localGame.getAlienMoves().add(previousMove);

		String json = localGame.randomEnemyFire();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		assertEquals(Game.NUMBER_SHOTS, root.size());
	}

	@DisplayName("over não deve lançar exceção")
	@Test
	void overShouldNotThrow() {
		assertDoesNotThrow(() -> game.over());
	}

	@DisplayName("printBoard deve produzir algum output na consola")
	@Test
	void staticPrintBoardShouldWriteToConsole() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream original = System.out;
		System.setOut(new PrintStream(out));

		try {
			Game.printBoard(new Fleet(), List.of(), false, false);
		} finally {
			System.setOut(original);
		}

		assertFalse(out.toString().isBlank());
	}
	@DisplayName("fireSingleShot deve acertar num navio sem o afundar")
	@Test
	void fireSingleShotShouldHitWithoutSinkingShip() {
		IFleet fleet = new Fleet();
		IShip ship = new Frigate(Compass.EAST, new Position(4, 4));
		fleet.addShip(ship);

		Game localGame = new Game(fleet);

		IGame.ShotResult result = localGame.fireSingleShot(new Position(4, 4), false);

		assertTrue(result.valid());
		assertFalse(result.repeated());
		assertNotNull(result.ship());
		assertFalse(result.sunk());
		assertEquals(1, localGame.getHits());
		assertEquals(0, localGame.getSunkShips());
	}
	@DisplayName("fireSingleShot deve eventualmente afundar uma Frigate após vários tiros")
	@Test
	void fireSingleShotShouldEventuallySinkFrigate() {
		IFleet fleet = new Fleet();
		IShip ship = new Frigate(Compass.EAST, new Position(5, 5));
		fleet.addShip(ship);

		Game localGame = new Game(fleet);

		for (IPosition pos : ship.getPositions()) {
			localGame.fireSingleShot(pos, false);
		}

		assertEquals(ship.getSize(), localGame.getHits());
		assertEquals(1, localGame.getSunkShips());
		assertEquals(0, localGame.getRemainingShips());
	}
	@DisplayName("fireShots deve processar hit, repetido e inválido na mesma jogada")
	@Test
	void fireShotsShouldProcessMixedResults() {
		IFleet fleet = new Fleet();
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);

		Game localGame = new Game(fleet);

		List<IPosition> shots = List.of(
				new Position(1, 1),   // hit
				new Position(1, 1),   // repetido na mesma jogada
				new Position(-1, 0)   // inválido
		);

		localGame.fireShots(shots);

		assertEquals(1, localGame.getAlienMoves().size());
		assertEquals(1, localGame.getHits());
		assertEquals(1, localGame.getRepeatedShots());
		assertEquals(1, localGame.getInvalidShots());
	}
	@DisplayName("printBoard deve tratar tiro em posição adjacente a navio afundado")
	@Test
	void printBoardShouldHandleShotOnAdjacentToSunkShip() {
		IFleet fleet = new Fleet();
		IShip ship = new Barge(Compass.NORTH, new Position(3, 3));
		fleet.addShip(ship);
		ship.sink();

		List<IPosition> shots = List.of(
				new Position(2, 2),
				new Position(0, 0),
				new Position(0, 1)
		);

		Move move = new Move(1, shots, new ArrayList<>());
		List<IMove> moves = List.of(move);

		assertDoesNotThrow(() -> Game.printBoard(fleet, moves, true, true));
	}
	@DisplayName("printBoard deve marcar tiro certeiro num navio")
	@Test
	void printBoardShouldHandleShotOnShip() {
		IFleet fleet = new Fleet();
		IShip ship = new Barge(Compass.NORTH, new Position(4, 4));
		fleet.addShip(ship);

		List<IPosition> shots = List.of(
				new Position(4, 4),
				new Position(0, 0),
				new Position(0, 1)
		);

		Move move = new Move(1, shots, new ArrayList<>());
		List<IMove> moves = List.of(move);

		assertDoesNotThrow(() -> Game.printBoard(fleet, moves, true, false));
	}
	@DisplayName("readEnemyFire deve aceitar letras minúsculas em formato separado")
	@Test
	void readEnemyFireShouldAcceptLowercaseSeparatedFormat() throws Exception {
		Scanner scanner = new Scanner("a 1 b 2 c 3");

		String json = game.readEnemyFire(scanner);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		assertEquals(3, root.size());
		assertEquals("A", root.get(0).get("row").asText());
		assertEquals("B", root.get(1).get("row").asText());
		assertEquals("C", root.get(2).get("row").asText());
	}
	@DisplayName("randomEnemyFire deve criar uma nova jogada e devolver JSON não vazio")
	@Test
	void randomEnemyFireShouldCreateMoveAndReturnJson() {
		String json = game.randomEnemyFire();

		assertNotNull(json);
		assertFalse(json.isBlank());
		assertEquals(1, game.getAlienMoves().size());
	}



}