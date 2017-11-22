package es.tml.qnl.beans.catalog;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class LoadDataRequest {

	@NotNull
	private String leagueCode;
	
	private Integer fromSeasonCode;
	
	private Integer toSeasonCode;
}
