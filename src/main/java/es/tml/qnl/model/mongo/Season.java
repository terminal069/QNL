package es.tml.qnl.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Season {

	private int code;
	
	private String name;
	
	private String leagueCode;
	
	private String url;
}
