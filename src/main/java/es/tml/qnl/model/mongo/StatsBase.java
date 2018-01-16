package es.tml.qnl.model.mongo;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public abstract class StatsBase {

	@Id
	private String id;
	
	private int localWinner;
	
	private int tied;
	
	private int visitorWinner;
}
