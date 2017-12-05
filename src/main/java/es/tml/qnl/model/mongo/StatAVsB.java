package es.tml.qnl.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Document(collection = "statAVsB")
public class StatAVsB {

	@Id
	private String id;
	
	@NonNull
	private String local;
	
	@NonNull
	private String visitor;
	
	private int localWinner;
	
	private int tied;
	
	private int visitorWinner;
}
