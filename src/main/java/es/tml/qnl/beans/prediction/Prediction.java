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

	public static final String NO_DATA_AVAILABLE = "NO_DATA_AVAILABLE";
	
	private String local;
	
	private String visitor;
	
	private Result prediction;
	
	private BigDecimal localWinProbability;
	
	private BigDecimal drawProbability;
	
	private BigDecimal visitorWinProbability;
	
	private String message;
	
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
