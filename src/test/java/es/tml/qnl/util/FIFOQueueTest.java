package es.tml.qnl.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

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
	public void givenThreeElementsPushedWhenClearQueueThenQueueHasZeroSize() {
		
		fifo.clear();
		
		fifo.push(elementOne);
		fifo.push(elementTwo);
		fifo.push(elementThree);
		
		assertEquals(3, fifo.getQueueSize());
		
		fifo.clear();
		
		assertEquals(0, fifo.getQueueSize());
	}
	
	@Test
	public void givenOneElementWhenPushedThenFirstAndLastElementsAreTheSame() {
		
		fifo.clear();
		
		fifo.push(elementOne);
		
		assertEquals(elementOne, fifo.getTail());
		assertEquals(elementOne, fifo.getHead());
	}
	
	@Test
	public void givenTwoElementsWhenPushedThenFirstAndLastFollowFIFOPattern() {
		
		fifo.clear();
		
		fifo.push(elementOne);
		fifo.push(elementTwo);
		
		assertEquals(elementTwo, fifo.getTail());
		assertEquals(elementOne, fifo.getHead());
	}
	
	@Test
	public void givenQueueFullOfElementsWhenPushOneMoreElementThenLastElementIsPushedOut() {
		
		fifo.clear();
		
		fifo.push(elementOne);
		fifo.push(elementTwo);
		fifo.push(elementThree);
		fifo.push(elementFour);
		fifo.push(elementFive);
		
		assertEquals(elementOne, fifo.getHead());
		assertEquals(5, fifo.getQueueSize());
		
		fifo.push(elementSix);
		
		assertEquals(elementTwo, fifo.getHead());
		assertEquals(5, fifo.getQueueSize());
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
		
		assertEquals(threeElementsLiked, fifo.toStringFromHeadToTail());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void settingSizeOfQueueToZeroThenThrowsException() {
		
		fifo.clear();
		
		fifo.setSize(0);
	}
	
	@Test
	public void givenThreeElementsWhenGetQueueThenQueueHasThreeElements() {
		
		fifo.clear();
		
		fifo.push(elementOne);
		fifo.push(elementTwo);
		fifo.push(elementThree);
		
		List<String> queue = fifo.getQueue();
		
		assertEquals(3, queue.size());
	}
}
