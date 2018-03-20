package es.tml.qnl.services.prediction;

import java.math.BigDecimal;

import es.tml.qnl.beans.prediction.WeightResponse;

public interface WeightService {

	/**
	 * Calculate weights of each statistic
	 * 
	 * @param increment Increment used to calculate weights
	 * @param maxIterations Maximum number of iterations used to calculate the sequence of results
	 * @return Weights of each statistic
	 */
	WeightResponse calculateWeights(BigDecimal increment, Integer maxIterations);
}
