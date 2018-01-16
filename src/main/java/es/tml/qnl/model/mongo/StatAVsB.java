package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Document(collection = "statAVsB")
public class StatAVsB extends StatsBase {

	@NonNull
	private String local;
	
	@NonNull
	private String visitor;
}
