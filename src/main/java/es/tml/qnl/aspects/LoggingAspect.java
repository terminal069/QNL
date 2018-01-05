package es.tml.qnl.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Aspect
public class LoggingAspect {

	private static final String TOTAL = "## {} ##";
	private static final String START = "------------------- START ({}) -------------------";
	private static final String END = "-------------------  END ({})  -------------------";
	
	@Around("execution(public * es.tml.qnl.services.*.*Service.*(..))")
	public Object loggingService(ProceedingJoinPoint pjp) throws Throwable {
		
		return executeMethod(pjp, pjp.getSignature().getName());
	}
	
	private Object executeMethod(ProceedingJoinPoint pjp, String methodName) throws Throwable {
		
		long startMillis = System.currentTimeMillis();
		
		try {
			log.info(START, methodName);
			return pjp.proceed();
		}
		finally {
			log.info(END, methodName);
			log.info(TOTAL, getTotalExecution(startMillis));
		}
	}
	
	private String getTotalExecution(long startMillis) {
		
		long millis = System.currentTimeMillis() - startMillis;
		long hours = millis / 3600000;
		millis = millis % 3600000;
		long minutes = millis / 60000;
		millis = millis % 60000;
		long seconds = millis / 1000;
		millis = millis % 1000;
		
		return new StringBuilder()
			.append(hours == 0 ? "" : hours + "h ")
			.append(minutes == 0 ? "" : minutes + "m ")
			.append(seconds == 0 ? "" : seconds + "s ")
			.append(millis + "ms")
			.toString();
	}
}
