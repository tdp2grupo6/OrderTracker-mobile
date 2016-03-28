package ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions;

public class BusinessException extends Exception {
	private static final long serialVersionUID = 1L;

	public BusinessException(String errorMsg) {
        super(errorMsg);
    }
}