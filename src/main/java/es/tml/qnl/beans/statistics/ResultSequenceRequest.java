package es.tml.qnl.beans.statistics;

import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class ResultSequenceRequest {

	@Min(value = 1)
	private int maxIterations;
}
