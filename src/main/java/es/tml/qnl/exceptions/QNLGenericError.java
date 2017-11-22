package es.tml.qnl.exceptions;

import lombok.Data;

@Data
public class QNLGenericError {

	private String code;
	private String errorType;
	private String errorMessage;
}
