package es.tml.qnl.beans.prediction;

import lombok.Data;

@Data
public class Match {

	private String league;
	
	private int season;
	
	private int round;
	
	private String local;
	
	private String visitor;
}
