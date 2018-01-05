package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Document(collection = "round")
public class Round extends GenericRound {

	public Round(int roundNumber, int seasonCode, String leagueCode, String local, String visitor, int localRes,
			int visitorRes, int localPoints, int visitorPoints) {
		
		super(roundNumber, seasonCode, leagueCode, local, visitor, localRes, visitorRes,
				localPoints, visitorPoints);
	}

	public Round(int roundNumber, int seasonCode, String leagueCode, String local, String visitor, int localRes,
			int visitorRes, int localPoints, int visitorPoints, int localPosition, int visitorPosition) {

		super(roundNumber, seasonCode, leagueCode, local, visitor, localRes, visitorRes,
				localPoints, visitorPoints, localPosition, visitorPosition);
	}
}
