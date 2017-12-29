package es.tml.qnl.beans.prediction;

import java.util.List;

import lombok.Data;

@Data
public class GetPredictionRequest {

	private String id;
	
	private List<Match> matches;
}
