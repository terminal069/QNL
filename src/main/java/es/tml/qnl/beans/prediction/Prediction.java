package es.tml.qnl.beans.prediction;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Prediction {

	private static final String NO_DATA_AVAILABLE = "NO_DATA_AVAILABLE";
	
	private String local;
	
	private String visitor;
	
	private String prediction;
	
	private BigDecimal localWinProbability;
	
	private BigDecimal drawProbability;
	
	private BigDecimal visitorWinProbability;
	
}
