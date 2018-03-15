package es.tml.qnl.beans.prediction;

import java.math.BigDecimal;

import es.tml.qnl.util.enums.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Prediction {

	private String local;
	
	private String visitor;
	
	private Result prediction;
	
	private BigDecimal localWinProbability;
	
	private BigDecimal drawProbability;
	
	private BigDecimal visitorWinProbability;
	
	/**
	 * Constructor
	 * 
	 * @param local Local
	 * @param visitor Visitor
	 */
	public Prediction(String local, String visitor) {
		
		this.local = local;
		this.visitor = visitor;
	}
	
}
