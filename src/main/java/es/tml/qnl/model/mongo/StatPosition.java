package es.tml.qnl.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Document(collection = "statPosition")
public class StatPosition extends StatsModelBase {

	@NonNull
	private Integer position;
}
