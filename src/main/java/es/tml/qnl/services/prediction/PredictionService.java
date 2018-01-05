package es.tml.qnl.services.prediction;

import es.tml.qnl.beans.prediction.PredictionRequest;
import es.tml.qnl.beans.prediction.PredictionResponse;

public interface PredictionService {

	PredictionResponse makePrediction(PredictionRequest request);

}
