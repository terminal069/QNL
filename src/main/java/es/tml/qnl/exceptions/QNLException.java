package es.tml.qnl.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class QNLException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private HttpStatus status;
	private String message;
	
	public QNLException(HttpStatus status, String message) {
		
		super();
		this.status = status;
		this.message = message;
	}
}
