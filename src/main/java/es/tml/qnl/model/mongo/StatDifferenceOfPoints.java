package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Document(collection = "statDifferenceOfPoints")
public class StatDifferenceOfPoints extends StatsBase {

	@NonNull
	private Integer difference;
}
