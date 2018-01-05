package es.tml.qnl.beans.prediction;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PredictionRequest {

	@NotNull
	private String id;
	
	@NotNull
	private List<Match> matches;
}
