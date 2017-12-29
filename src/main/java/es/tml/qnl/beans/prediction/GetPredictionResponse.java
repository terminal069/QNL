package es.tml.qnl.beans.prediction;

import java.util.List;

import lombok.Data;

@Data
public class GetPredictionResponse {

	private String id;
	
	private List<Prediction> predictions;
}
