package es.tml.qnl.services.statistics.util;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.tml.qnl.services.statistics.BaseStatType;
import es.tml.qnl.services.statistics.PointsPositionSequenceStatType;
import es.tml.qnl.services.statistics.PointsPositionStatType;
import es.tml.qnl.services.statistics.PointsSequenceStatType;
import es.tml.qnl.services.statistics.PointsStatType;
import es.tml.qnl.services.statistics.PositionSequenceStatType;
import es.tml.qnl.services.statistics.PositionStatType;
import es.tml.qnl.services.statistics.SequenceStatType;
import es.tml.qnl.util.enums.Result;

@Component
public class StatisticsType {
	
	public enum StatisticType {
	
		ALL,
		POINTS,
		POSITION,
		SEQUENCE,
		POINTS_POSITION,
		POINTS_SEQUENCE,
		POSITION_SEQUENCE,
		POINTS_POSITION_SEQUENCE;
	}
	
	@Autowired
	private PointsStatType pointsStatType;
	
	@Autowired
	private PositionStatType positionStatType;
	
	@Autowired
	private SequenceStatType sequenceStatType;
	
	@Autowired
	private PointsPositionStatType pointsPositionStatType;
	
	@Autowired
	private PointsSequenceStatType pointsSequenceStatType;
	
	@Autowired
	private PositionSequenceStatType positionSequenceStatType;
	
	@Autowired
	private PointsPositionSequenceStatType pointsPositionSequenceStatType;
	
	public void deleteOldData(StatisticType statisticType) {
		
		if (StatisticType.ALL.equals(statisticType)) {
			Arrays.stream(StatisticType.values()).forEach(stat -> {
				if (!StatisticType.ALL.equals(stat)) {
					deleteOldData(stat);
				}
			});
		}
		else {
			getStatType(statisticType).deleteOldData();
		}
	}
	
	public void saveStatistic(StatisticType statisticType, Integer points, Integer position, String sequence, Result result) {
		
		if (StatisticType.ALL.equals(statisticType)) {
			Arrays.stream(StatisticType.values()).forEach(stat -> {
				if (!StatisticType.ALL.equals(stat)) {
					saveStatistic(stat, points, position, sequence, result);
				}
			});
		}
		else {
			getStatType(statisticType).saveStatistic(points, position, sequence, result);
		}
	}
	
	private BaseStatType getStatType(StatisticType statisticType) {
		
		BaseStatType baseStatType = null;
		
		switch(statisticType) {
			case POINTS: {
				baseStatType = pointsStatType;
				break;
			}
			case POSITION: {
				baseStatType = positionStatType;
				break;
			}
			case SEQUENCE: {
				baseStatType = sequenceStatType;
				break;
			}
			case POINTS_POSITION: {
				baseStatType = pointsPositionStatType;
				break;
			}
			case POINTS_SEQUENCE: {
				baseStatType = pointsSequenceStatType;
				break;
			}
			case POSITION_SEQUENCE: {
				baseStatType = positionSequenceStatType;
				break;
			}
			case POINTS_POSITION_SEQUENCE: {
				baseStatType = pointsPositionSequenceStatType;
				break;
			}
			default: {
				throw new IllegalArgumentException("Enum value '" + statisticType.name() + "' not accepted");
			}
		}
		
		return baseStatType;
	}
}
