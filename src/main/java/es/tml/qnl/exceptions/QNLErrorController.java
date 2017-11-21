package es.tml.qnl.exceptions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public QNLGenericError handleException(Exception e) {
		
		QNLGenericError genericError = new QNLGenericError();
		genericError.setCode(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		genericError.setErrorMessage(e.getMessage());
		
		log.error("Internal error: ", e);
		
		return genericError;
	}
}
