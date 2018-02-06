package es.tml.qnl.util.enums;

public enum Result {

	A("A"),
	B("B"),
	C("C");
	
	private String value;
	
	private Result(String value) {
		
		this.value = value;
	}
	
	public String toString() {
		
		return this.value;
	}
}
