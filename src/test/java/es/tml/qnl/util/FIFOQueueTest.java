package es.tml.qnl.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FIFOQueueTest {

	private FIFOQueue<String> fifo = new FIFOQueue<>();
	
	private String elementOne = "one";
	private String elementTwo = "two";
	private String elementThree = "three";
	private String elementFour = "four";
	private String elementFive = "five";
	private String elementSix = "six";
	
	@Before
	public void before() {
		
		fifo.setSize(5);
	}
	
	@Test
	public void givenEmptyQueueWhenGetFirstOrLastElementThenReturnNull() {
		
		fifo.clear();
		
		assertNull(fifo.getTail());
		assertNull(fifo.getHead());
	}
	
	@Test
	public void givenThreeElementsPushedWhenClearQueueThenQueueWithZeroSize() {
		
		fifo.clear();
		
		fifo.push(elementOne);
		fifo.push(elementTwo);
		fifo.push(elementThree);
		
		assertEquals(fifo.getQueueSize(), 3);
		
		fifo.clear();
		
		assertEquals(fifo.getQueueSize(), 0);
	}
	
	@Test
	public void givenOneElementWhenPushedThenFirstAndLastElementsAreTheSame() {
		
		fifo.clear();
		
		fifo.push(elementOne);
		
		assertEquals(fifo.getTail(), elementOne);
		assertEquals(fifo.getHead(), elementOne);
	}
	
	@Test
	public void givenTwoElementsWhenPushedThenFirstAndLastFollowFIFOPattern() {
		
		fifo.clear();
		
		fifo.push(elementOne);
		fifo.push(elementTwo);
		
		assertEquals(fifo.getTail(), elementTwo);
		assertEquals(fifo.getHead(), elementOne);
	}
	
	@Test
	public void givenQueueFullOfElementsWhenPushOneMoreElementThenLastElementIsPushedOut() {
		
		fifo.clear();
		
		fifo.push(elementOne);
		fifo.push(elementTwo);
		fifo.push(elementThree);
		fifo.push(elementFour);
		fifo.push(elementFive);
		
		assertEquals(fifo.getHead(), elementOne);
		assertEquals(fifo.getQueueSize(), 5);
		
		fifo.push(elementSix);
		
		assertEquals(fifo.getHead(), elementTwo);
		assertEquals(fifo.getQueueSize(), 5);
	}
	
	@Test
	public void givenThreeElementsWhenToStringThenAllElementsArePrinted() {
		
		fifo.clear();
		
		fifo.push(elementOne);
		fifo.push(elementTwo);
		fifo.push(elementThree);
		
		String threeElementsLiked = new StringBuffer()
				.append(elementOne)
				.append(elementTwo)
				.append(elementThree)
				.toString();
		
		assertEquals(fifo.toStringFromHeadToTail(), threeElementsLiked);
	}
}
