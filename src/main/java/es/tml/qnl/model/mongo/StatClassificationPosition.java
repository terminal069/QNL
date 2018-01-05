package es.tml.qnl.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Document(collection = "statClassificationPosition")
public class StatClassificationPosition {

	@Id
	private String id;
	
	@NonNull
	private Integer positionDifference;
	
	private int localWinner;
	
	private int tied;
	
	private int visitorWinner;
}
