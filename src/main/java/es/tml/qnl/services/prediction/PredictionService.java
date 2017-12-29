package es.tml.qnl.services.prediction;

import es.tml.qnl.beans.prediction.GetPredictionRequest;
import es.tml.qnl.beans.prediction.GetPredictionResponse;

public interface PredictionService {

	GetPredictionResponse makePrediction(GetPredictionRequest request);

}
