package es.tml.qnl.beans.prediction;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Prediction {

	private static final String NO_DATA_AVAILABLE = "NO_DATA_AVAILABLE";
	
	private String local;
	
	private String visitor;
	
	private String prediction;
	
	private BigDecimal localWinProbability;
	
	private BigDecimal drawProbability;
	
	private BigDecimal visitorWinProbability;
	
	/**
	 * Constructor with local and visitor teams and no more data. The rest of parameters
	 * are initialized with default values
	 * 
	 * @param local Local team
	 * @param visitor Visitor team
	 */
	public Prediction (String local, String visitor) {
		this.local = local;
		this.visitor = visitor;
		this.prediction = NO_DATA_AVAILABLE;
		this.localWinProbability = BigDecimal.ZERO;
		this.drawProbability = BigDecimal.ZERO;
		this.visitorWinProbability = BigDecimal.ZERO;
	}
}
