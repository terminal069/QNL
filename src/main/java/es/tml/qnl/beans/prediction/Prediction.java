package es.tml.qnl.beans.prediction;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Prediction {

	private String local;
	
	private String visitor;
	
	private String prediction;
	
	private BigDecimal localWinProbability;
	
	private BigDecimal drawProbability;
	
	private BigDecimal visitorWinProbability;
}
