package es.tml.qnl.beans.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetRoundResponse {

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
