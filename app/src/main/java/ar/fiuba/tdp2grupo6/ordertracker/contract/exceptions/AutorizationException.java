package ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions;

public class AutorizationException extends Exception {
	private static final long serialVersionUID = 1L;

	public AutorizationException(String errorMsg) {
        super(errorMsg);
    }
}