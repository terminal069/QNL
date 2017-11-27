package es.tml.qnl.model.mongo;

import lombok.Data;

@Data
public class Round {

	private int roundNumber;
	
	private String local;
	
	private String visitor;
	
	private int localRes;
	
	private int visitorRes;
	
	private int localPoints;
	
	private int visitorPoints;
}
