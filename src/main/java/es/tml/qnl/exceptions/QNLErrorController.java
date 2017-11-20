package es.tml.qnl.exceptions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class QNLErrorController {

	@ExceptionHandler(value = QNLException.class)
	@ResponseBody
	public QNLGenericError handleQNLException(QNLException e, HttpServletResponse response,
            HttpServletRequest request) {
		
		response.setStatus(e.getStatus().value());
		
		QNLGenericError genericError = new QNLGenericError();
		genericError.setCode(e.getStatus().getReasonPhrase());
		genericError.setErrorMessage(e.getMessage());
		
		return genericError;
	}
}
