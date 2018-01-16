package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Document(collection = "statClassificationPosition")
public class StatClassificationPosition extends StatsBase {

	@NonNull
	private Integer positionDifference;
}
