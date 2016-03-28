package ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions;

public class LocalException extends Exception {
	private static final long serialVersionUID = 1L;

	public LocalException(String errorMsg) {
        super(errorMsg);
    }
}
