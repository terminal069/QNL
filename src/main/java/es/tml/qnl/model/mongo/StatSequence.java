package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Document(collection = "statSequence")
public class StatSequence extends StatsModelBase {

	@NonNull
	private String sequence;
}
