package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Document(collection = "statPositionSequence")
public class StatPositionSequence extends StatsBase {

	@NonNull
	private Integer position;
	
	@NonNull
	private String sequence;
}
