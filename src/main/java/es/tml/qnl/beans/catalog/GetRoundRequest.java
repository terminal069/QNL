package es.tml.qnl.beans.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetRoundRequest {

	private Integer roundNumber;
	
	private Integer seasonCode;
	
	private String leagueCode;
	
	private String local;
	
	private String visitor;
}
