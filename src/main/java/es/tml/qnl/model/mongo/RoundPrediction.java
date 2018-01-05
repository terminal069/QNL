package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Document(collection = "roundPrediction")
public class RoundPrediction extends GenericRound {
	
	public RoundPrediction(int roundNumber, int seasonCode, String leagueCode, String local, String visitor,
			int localRes, int visitorRes, int localPoints, int visitorPoints) {
		
		super(roundNumber, seasonCode, leagueCode, local, visitor, localRes, visitorRes,
				localPoints, visitorPoints);
	}

	public RoundPrediction(int roundNumber, int seasonCode, String leagueCode, String local, String visitor,
			int localRes, int visitorRes, int localPoints, int visitorPoints, int localPosition, int visitorPosition) {
		
		super(roundNumber, seasonCode, leagueCode, local, visitor, localRes, visitorRes,
				localPoints, visitorPoints, localPosition, visitorPosition);
	}
}
