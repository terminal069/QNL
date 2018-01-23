package es.tml.qnl.beans.statistics;

import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class StatisticsRequest {

	private Integer minRound;
	
	@Min(value = 1)
	private Integer maxIterations;
}
