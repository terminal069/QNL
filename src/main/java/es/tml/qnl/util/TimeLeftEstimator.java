package es.tml.qnl.util;

import org.springframework.stereotype.Component;

@Component
public class TimeLeftEstimator {

	private static final String UNKNOWN = "UNKNOWN";

	private long individualAverage;
	private int totalElements;
	private int actualElements;
	private int sumatory;
	private long partialInit;
	private long totalEstimated;
	
	public void init(int totalElements) {
		
		this.totalElements = totalElements;
		this.actualElements = 0;
	}
	
	public void startPartial() {
		
		this.partialInit = System.currentTimeMillis();
		actualElements++;
	}
	
	public void finishPartial() {
		
		long partial = System.currentTimeMillis() - partialInit;
		sumatory = sumatory + (int)partial;
		individualAverage = sumatory / actualElements;
		totalEstimated = (totalElements - actualElements) * individualAverage;
	}
	
	public String getTimeLeft() {
		
		long total = totalEstimated;
		long hours = total / 3600000;
		total = total % 3600000;
		long minutes = total / 60000;
		total = total % 60000;
		long seconds = total / 1000;
		total = total % 1000;
		
		return new StringBuilder()
			.append(hours == 0 ? "" : hours + "h ")
			.append(minutes == 0 ? "" : minutes + "m ")
			.append(seconds == 0 ? "" : seconds + "s ")
			.append(total == 0 ? UNKNOWN : total + "ms")
			.toString();
	}
	
}
