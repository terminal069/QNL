package es.tml.qnl.beans.catalog;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class LoadDataRequest {

	@NotNull
	private boolean fullLoad;
	
	@NotNull
	private String league;
	
	private String fromSeason;
	
	private String toSeason;
}
