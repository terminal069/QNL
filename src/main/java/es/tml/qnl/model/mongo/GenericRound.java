package es.tml.qnl.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class GenericRound {

	public GenericRound(int roundNumber, int seasonCode, String leagueCode, String local, String visitor,
			int localRes, int visitorRes, int localPoints, int visitorPoints) {
		
		this.roundNumber = roundNumber;
		this.seasonCode = seasonCode;
		this.leagueCode = leagueCode;
		this.local = local;
		this.visitor = visitor;
		this.localRes = localRes;
		this.visitorRes = visitorRes;
		this.localPoints = localPoints;
		this.visitorPoints = visitorPoints;
	}

	protected int roundNumber;
	
	protected int seasonCode;
	
	protected String leagueCode;
	
	protected String local;
	
	protected String visitor;
	
	protected int localRes;
	
	protected int visitorRes;
	
	protected int localPoints;
	
	protected int visitorPoints;
	
	protected int localPosition;
	
	protected int visitorPosition;
}
