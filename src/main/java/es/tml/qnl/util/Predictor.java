package es.tml.qnl.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.tml.qnl.beans.prediction.Match;
import es.tml.qnl.beans.prediction.Prediction;
import es.tml.qnl.repositories.mongo.RoundPredictionRepository;
import es.tml.qnl.repositories.mongo.RoundRepository;

@Component
public class Predictor {
	
	@Autowired
	RoundPredictionRepository roundPredictionRepository;
	
	@Autowired
	RoundRepository roundRepository;

	public List<Prediction> predict(List<Match> matches) {

		List<Prediction> predictions = new ArrayList<>();
		
		matches.forEach(match -> predictions.add(predict(match)));
		
		return predictions;
	}

	private Prediction predict(Match match) {

		// 
		
		return null;
	}

}
