package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Document(collection = "statResultSequence")
public class StatResultSequence extends StatsBase {

	@NonNull
	private String sequence;
}
