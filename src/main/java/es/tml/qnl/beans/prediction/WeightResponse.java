package es.tml.qnl.beans.prediction;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeightResponse {
	
	private BigDecimal testHitPercentage;
	
	private BigDecimal controlHitPercentage;
	
	private List<StatisticWeight> statisticWeights;
	
	public WeightResponse(List<StatisticWeight> statisticWeights) {
		
		this.statisticWeights = statisticWeights;
	}
}
