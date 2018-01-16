package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Document(collection = "statDiffPointsResSeq")
public class StatDiffPointsResSeq extends StatsBase {

	@NonNull
	private Integer difference;
	
	@NonNull
	private String sequence;
}
