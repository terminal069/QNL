package es.tml.qnl.beans.catalog;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoadDataRequest {

	public LoadDataRequest(String leagueCode) {
		
		this.leagueCode = leagueCode;
	}
	
	private String leagueCode;
	
	private Integer fromSeasonCode;
	
	private Integer toSeasonCode;
}
