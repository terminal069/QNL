package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Document(collection = "statPointsPositionSequence")
public class StatPointsPositionSequence extends StatsBase {

	@NonNull
	private Integer points;
	
	@NonNull
	private Integer position;
	
	@NonNull
	private String sequence;
}
