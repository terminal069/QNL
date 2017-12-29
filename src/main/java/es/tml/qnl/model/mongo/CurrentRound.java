package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "currentRound")
public class CurrentRound {

	private int roundNumber;
	
	private int seasonCode;
	
	private String leagueCode;
	
	private String local;
	
	private String visitor;
	
	private int localRes;
	
	private int visitorRes;
	
	private int localPoints;
	
	private int visitorPoints;
}
