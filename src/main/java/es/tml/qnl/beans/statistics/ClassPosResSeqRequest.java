package es.tml.qnl.beans.statistics;

import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class ClassPosResSeqRequest {

	private int minRound;
	
	@Min(value = 1)
	private int maxIterations;
}
