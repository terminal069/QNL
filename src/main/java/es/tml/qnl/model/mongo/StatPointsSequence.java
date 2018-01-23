package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Document(collection = "statPointsSequence")
public class StatPointsSequence extends StatsBase {

	@NonNull
	private Integer points;
	
	@NonNull
	private String sequence;
}
