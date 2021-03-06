package es.tml.qnl.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class FIFOQueue<T> {

	private int size;
	
	private List<T> queue = new ArrayList<>();
	
	public void setSize(int size) {
		
		if (size < 1) {
			throw new IllegalArgumentException("Size of the FIFO queue must be greater than zero");
		}
		
		this.size = size;
	}
	
	public List<T> getQueue() {
		
		return queue;
	}
	
	public T getTail() {
		
		return queue.size() == 0 ? null : queue.get(0);
	}
	
	public T getHead() {
		
		return queue.size() == 0 ? null : queue.get(queue.size() - 1);
	}
	
	public void push(T element) {
		
		queue.add(0, element);
		
		if (queue.size() > this.size) {
			queue.remove(this.size);
		}
	}
	
	public void clear() {
		
		queue.clear();
	}
	
	public int getQueueSize() {
		
		return queue.size();
	}
	
	public String toStringFromHeadToTail() {
		
		StringBuilder sb = new StringBuilder();
		
		if (queue != null) {
			queue.forEach(element -> {
				sb.insert(0, element.toString());
			});
		}
		
		return sb.toString();
	}
	
}
