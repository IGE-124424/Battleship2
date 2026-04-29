package battleship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.*;

public class Move implements IMove {

	private static final String SHOT_TYPE_TIRO = "tiro";

	private final int number;
	private final List<IPosition> shots;
	private final List<IGame.ShotResult> shotResults;

	public Move(int moveNumber, List<IPosition> moveShots, List<IGame.ShotResult> moveResults) {
		this.number = moveNumber;
		this.shots = moveShots;
		this.shotResults = moveResults;
	}

	@Override
	public String toString() {
		int shotCount = shots.size();
		int resultCount = shotResults.size();

		return "Move{" +
				"number=" + number +
				", shots=" + shotCount +
				", results=" + resultCount +
				'}';
	}

	@Override
	public int getNumber() {
		return this.number;
	}

	@Override
	public List<IPosition> getShots() {
		return this.shots;
	}

	@Override
	public List<IGame.ShotResult> getShotResults() {
		return this.shotResults;
	}

	@Override
	public String processEnemyFire(boolean verbose) {
		int validShots = 0;
		int repeatedShots = 0;
		int missedShots = 0;

		Map<String, Integer> sunkBoatsCount = new HashMap<>();
		Map<String, Integer> hitsPerBoat = new HashMap<>();

		for (IGame.ShotResult result : this.shotResults) {
			if (!result.valid()) {
				continue;
			}

			if (result.repeated()) {
				repeatedShots++;
			} else {
				validShots++;

				if (result.ship() == null) {
					missedShots++;
				} else {
					String boatName = result.ship().getCategory();
					hitsPerBoat.put(boatName, hitsPerBoat.getOrDefault(boatName, 0) + 1);

					if (result.sunk()) {
						sunkBoatsCount.put(boatName, sunkBoatsCount.getOrDefault(boatName, 0) + 1);
					}
				}
			}
		}

		int outsideShots = Game.NUMBER_SHOTS - validShots - repeatedShots;

		if (verbose) {
			printVerboseResult(validShots, repeatedShots, missedShots, outsideShots, sunkBoatsCount, hitsPerBoat);
		}

		Map<String, Object> response = buildResponseMap(
				validShots,
				outsideShots,
				repeatedShots,
				missedShots,
				sunkBoatsCount,
				hitsPerBoat
		);

		return serializeResponse(response);
	}

	private void printVerboseResult(
			int validShots,
			int repeatedShots,
			int missedShots,
			int outsideShots,
			Map<String, Integer> sunkBoatsCount,
			Map<String, Integer> hitsPerBoat
	) {
		StringBuilder output = new StringBuilder();

		if (validShots == 0 && repeatedShots > 0) {
			appendRepeatedShots(output, repeatedShots);
		} else {
			appendValidShots(output, validShots);
			appendSunkBoats(output, sunkBoatsCount);
			appendBoatHits(output, sunkBoatsCount, hitsPerBoat);
			appendMissedShots(output, missedShots, sunkBoatsCount, hitsPerBoat);
			appendRepeatedShotsAfterValidShots(output, validShots, repeatedShots);
		}

		appendOutsideShots(output, outsideShots);

		System.out.println("Jogada nº" + this.number + " -> " + output);
	}

	private void appendValidShots(StringBuilder output, int validShots) {
		if (validShots > 0) {
			output.append(validShots)
					.append(" ")
					.append(SHOT_TYPE_TIRO)
					.append(validShots > 1 ? "s" : "")
					.append(" válido")
					.append(validShots > 1 ? "s" : "")
					.append(": ");
		}
	}

	private void appendSunkBoats(StringBuilder output, Map<String, Integer> sunkBoatsCount) {
		if (!sunkBoatsCount.isEmpty()) {
			for (Map.Entry<String, Integer> entry : sunkBoatsCount.entrySet()) {
				String boatName = entry.getKey();
				int count = entry.getValue();

				output.append(count)
						.append(" ")
						.append(boatName)
						.append(count > 1 ? "s" : "")
						.append(" ao fundo")
						.append(" + ");
			}
		}
	}

	private void appendBoatHits(
			StringBuilder output,
			Map<String, Integer> sunkBoatsCount,
			Map<String, Integer> hitsPerBoat
	) {
		if (!hitsPerBoat.isEmpty()) {
			for (Map.Entry<String, Integer> entry : hitsPerBoat.entrySet()) {
				String boatName = entry.getKey();
				int hits = entry.getValue();

				if (!sunkBoatsCount.containsKey(boatName)) {
					output.append(hits)
							.append(" ")
							.append(SHOT_TYPE_TIRO)
							.append(hits > 1 ? "s" : "")
							.append(" num(a) ")
							.append(boatName)
							.append(" + ");
				}
			}
		}
	}

	private void appendMissedShots(
			StringBuilder output,
			int missedShots,
			Map<String, Integer> sunkBoatsCount,
			Map<String, Integer> hitsPerBoat
	) {
		if (missedShots > 0) {
			output.append(missedShots)
					.append(" ")
					.append(SHOT_TYPE_TIRO)
					.append(missedShots > 1 ? "s" : "")
					.append(" na água");
		} else if (!sunkBoatsCount.isEmpty() || !hitsPerBoat.isEmpty()) {
			output.setLength(output.length() - 2);
		}
	}

	private void appendRepeatedShotsAfterValidShots(StringBuilder output, int validShots, int repeatedShots) {
		if (repeatedShots > 0) {
			if (validShots > 0) {
				output.append(", ");
			}
			appendRepeatedShots(output, repeatedShots);
		}
	}

	private void appendRepeatedShots(StringBuilder output, int repeatedShots) {
		output.append(repeatedShots)
				.append(" ")
				.append(SHOT_TYPE_TIRO)
				.append(repeatedShots > 1 ? "s" : "")
				.append(" repetido")
				.append(repeatedShots > 1 ? "s" : "");
	}

	private void appendOutsideShots(StringBuilder output, int outsideShots) {
		if (outsideShots > 0) {
			if (!output.isEmpty()) {
				output.append(", ");
			}

			output.append(outsideShots)
					.append(" ")
					.append(SHOT_TYPE_TIRO)
					.append(outsideShots > 1 ? "s" : "")
					.append(" exterior")
					.append(outsideShots > 1 ? "es" : "");
		}
	}

	private Map<String, Object> buildResponseMap(
			int validShots,
			int outsideShots,
			int repeatedShots,
			int missedShots,
			Map<String, Integer> sunkBoatsCount,
			Map<String, Integer> hitsPerBoat
	) {
		Map<String, Object> response = new HashMap<>();

		response.put("validShots", validShots);
		response.put("outsideShots", outsideShots);
		response.put("repeatedShots", repeatedShots);
		response.put("missedShots", missedShots);
		response.put("sunkBoats", buildSunkBoatsList(sunkBoatsCount));
		response.put("hitsOnBoats", buildBoatHitsList(sunkBoatsCount, hitsPerBoat));

		return response;
	}

	private List<Map<String, Object>> buildSunkBoatsList(Map<String, Integer> sunkBoatsCount) {
		List<Map<String, Object>> sunkBoats = new ArrayList<>();

		for (Map.Entry<String, Integer> entry : sunkBoatsCount.entrySet()) {
			Map<String, Object> boat = new HashMap<>();
			boat.put("type", entry.getKey());
			boat.put("count", entry.getValue());
			sunkBoats.add(boat);
		}

		return sunkBoats;
	}

	private List<Map<String, Object>> buildBoatHitsList(
			Map<String, Integer> sunkBoatsCount,
			Map<String, Integer> hitsPerBoat
	) {
		List<Map<String, Object>> boatHits = new ArrayList<>();

		for (Map.Entry<String, Integer> entry : hitsPerBoat.entrySet()) {
			if (!sunkBoatsCount.containsKey(entry.getKey())) {
				Map<String, Object> boat = new HashMap<>();
				boat.put("type", entry.getKey());
				boat.put("hits", entry.getValue());
				boatHits.add(boat);
			}
		}

		return boatHits;
	}

	private String serializeResponse(Map<String, Object> response) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		try {
			return objectMapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Erro ao serializar o JSON dos resultados da jogada", e);
		}
	}
}