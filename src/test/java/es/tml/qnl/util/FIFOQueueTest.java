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
	
	@Before
	public void before() {
		
		fifo.setSize(5);
	}
	
	@Test
	public void givenEmptyQueueWhenGetFirstOrLastElementThenReturnNull() {
		
		fifo.clear();
		
		assertNull(fifo.getFirst());
		assertNull(fifo.getLast());
	}
	
	@Test
	public void givenThreeElementsPushedWhenClearQueueThenQueueWithZeroSize() {
		
		fifo.clear();
		
		fifo.push("one");
		fifo.push("two");
		fifo.push("three");
		
		assertEquals(fifo.getQueueSize(), 3);
		
		fifo.clear();
		
		assertEquals(fifo.getQueueSize(), 0);
	}
	
	@Test
	public void givenOneElementWhenPushedThenFirstAndLastElementsAreTheSame() {
		
		fifo.clear();
		
		String element = "element";
		fifo.push(element);
		
		assertEquals(fifo.getFirst(), element);
		assertEquals(fifo.getLast(), element);
	}
	
	@Test
	public void givenTwoElementsWhenPushedThenFirstAndLastFollowFIFOPattern() {
		
		fifo.clear();
		
		String elementOne = "one";
		String elementTwo = "two";
		
		fifo.push(elementOne);
		fifo.push(elementTwo);
		
		assertEquals(fifo.getFirst(), elementTwo);
		assertEquals(fifo.getLast(), elementOne);
	}
	
	@Test
	public void givenQueueFullOfElementsWhenPushOneMoreElementThenLastElementIsPushedOut() {
		
		fifo.clear();
		
		String elementOne = "one";
		String elementTwo = "two";
		String elementThree = "three";
		String elementFour = "four";
		String elementFive = "five";
		String elementSix = "six";
		
		fifo.push(elementOne);
		fifo.push(elementTwo);
		fifo.push(elementThree);
		fifo.push(elementFour);
		fifo.push(elementFive);
		
		assertEquals(fifo.getLast(), elementOne);
		assertEquals(fifo.getQueueSize(), 5);
		
		fifo.push(elementSix);
		
		assertEquals(fifo.getLast(), elementTwo);
		assertEquals(fifo.getQueueSize(), 5);
	}
}
