package es.tml.qnl.services.statistics.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import es.tml.qnl.model.mongo.StatsModelBase;
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
	
	/**
	 * Enum representing types of statistics
	 */
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
	
	/**
	 * Enum representing names of statistic classes
	 */
	private enum StatisticClassName {
		StatPoints,
		StatPosition,
		StatSequence,
		StatPointsPosition,
		StatPointsSequence,
		StatPositionSequence,
		StatPointsPositionSequence;
	}
	
	private static final String QNL_STATISTICS_WEIGHT_POINTS = "qnl.statistics.weight.points";
	private static final String QNL_STATISTICS_WEIGHT_POSITION = "qnl.statistics.weight.position";
	private static final String QNL_STATISTICS_WEIGHT_SEQUENCE = "qnl.statistics.weight.sequence";
	private static final String QNL_STATISTICS_WEIGHT_POINTS_POSITION = "qnl.statistics.weight.pointsPosition";
	private static final String QNL_STATISTICS_WEIGHT_POINTS_SEQUENCE = "qnl.statistics.weight.pointsSequence";
	private static final String QNL_STATISTICS_WEIGHT_POSITION_SEQUENCE = "qnl.statistics.weight.positionSequence";
	private static final String QNL_STATISTICS_WEIGHT_POINTS_POSITION_SEQUENCE = "qnl.statistics.weight.pointsPositionSequence";
	
	@Value("${" + QNL_STATISTICS_WEIGHT_POINTS + "}")
	private BigDecimal pointsWeight;
	
	@Value("${" + QNL_STATISTICS_WEIGHT_POSITION + "}")
	private BigDecimal positionWeight;
	
	@Value("${" + QNL_STATISTICS_WEIGHT_SEQUENCE + "}")
	private BigDecimal sequenceWeight;
	
	@Value("${" + QNL_STATISTICS_WEIGHT_POINTS_POSITION + "}")
	private BigDecimal pointsPositionWeight;
	
	@Value("${" + QNL_STATISTICS_WEIGHT_POINTS_SEQUENCE + "}")
	private BigDecimal pointsSequenceWeight;
	
	@Value("${" + QNL_STATISTICS_WEIGHT_POSITION_SEQUENCE + "}")
	private BigDecimal positionSequenceWeight;
	
	@Value("${" + QNL_STATISTICS_WEIGHT_POINTS_POSITION_SEQUENCE + "}")
	private BigDecimal pointsPositionSequenceWeight;
	
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
	
	/**
	 * Deletes old data from repository for a statistic type
	 *  
	 * @param statisticType Statistic type
	 */
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
	
	/**
	 * Save data into repository for a statistic type
	 * 
	 * @param statisticType Statistic type
	 * @param points Points value
	 * @param position Position value
	 * @param sequence Sequence value
	 * @param result Result of the match
	 */
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
	
	/**
	 * Get data for a statistic type
	 * 
	 * @param statisticType Statistic type 
	 * @param points Points value
	 * @param position Position value
	 * @param sequence Sequence value
	 * @return A list with data of the statistics type
	 */
	public List<StatsModelBase> getStatistic(StatisticType statisticType, Integer points, Integer position, String sequence) {
		
		List<StatsModelBase> stats = new ArrayList<>();
		
		if (StatisticType.ALL.equals(statisticType)) {
			Arrays.stream(StatisticType.values()).forEach(stat -> {
				if (!StatisticType.ALL.equals(stat)) {
					StatsModelBase statistic = getStatType(stat).getStatistic(points, position, sequence);
					if (statistic != null) {
						stats.add(statistic);
					}
				}
			});
		}
		else {
			StatsModelBase statistic = getStatType(statisticType).getStatistic(points, position, sequence);
			if (statistic != null) {
				stats.add(statistic);
			}
		}
		
		return stats;
	}
	
	/**
	 * Get the statistic type service from the statistic type
	 *  
	 * @param statisticType Statistic type
	 * @return Statistic type service
	 */
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

	/**
	 * Get weight of a statistic
	 * 
	 * @param stat Statistic
	 * @return Weight
	 */
	public BigDecimal getStatisticWeigth(StatsModelBase stat) {

		BigDecimal weight = null;
		
		StatisticClassName clazzName = StatisticClassName.valueOf(stat.getClass().getSimpleName());
		
		switch(clazzName) {
			case StatPoints: {
				weight = pointsWeight;
				break;
			}
			case StatPosition: {
				weight = positionWeight;
				break;
			}
			case StatSequence: {
				weight = sequenceWeight;
				break;
			}
			case StatPointsPosition: {
				weight = pointsPositionWeight;
				break;
			}
			case StatPointsSequence: {
				weight = pointsSequenceWeight;
				break;
			}
			case StatPositionSequence: {
				weight = positionSequenceWeight;
				break;
			}
			case StatPointsPositionSequence: {
				weight = pointsPositionSequenceWeight;
				break;
			}
		}
		
		return weight;
	}
}
