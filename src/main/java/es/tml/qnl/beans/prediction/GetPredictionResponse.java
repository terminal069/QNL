package es.tml.qnl.beans.prediction;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetPredictionResponse {

	private String id;
	
	private List<Prediction> predictions;
}
