package es.tml.qnl.services.statistics;

import es.tml.qnl.model.mongo.StatsModelBase;
import es.tml.qnl.util.enums.Result;

public abstract class BaseStatType {

	public abstract void deleteOldData();
	
	public abstract void saveStatistic(Integer points, Integer position, String sequence, Result result);
	
	public abstract StatsModelBase getStatistic(Integer points, Integer position, String sequence);
	
	protected void setResult(StatsModelBase statsBase, Result result) {
		
		switch(result) {
			case A: {
				statsBase.setLocalWinner(statsBase.getLocalWinner() + 1);
				break;
			}
			case B: {
				statsBase.setTied(statsBase.getTied() + 1);
				break;
			}
			case C: {
				statsBase.setVisitorWinner(statsBase.getVisitorWinner() + 1);
				break;
			}
		}
	}
}
