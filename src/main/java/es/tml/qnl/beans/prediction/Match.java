package es.tml.qnl.beans.prediction;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Match {

	private String league;
	
	private int season;
	
	private int round;
	
	private String local;
	
	private String visitor;
}
