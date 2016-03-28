package ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions;

import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;

public class ServiceException extends Exception {
	private static final long serialVersionUID = 1L;

	private ServiceExceptionType tipo;
	private Boolean timeOutError;
	private Boolean internalError;
	private Boolean conexionError;
	private Boolean applicationError;

	private ResponseObject response;

	/*
	public ServiceException(String errorMsg, ResponseObject response, Boolean conexion) {
	    super(errorMsg);
	    this.response = response;
	    this.timeOut = conexion;
	}
	*/

	public enum ServiceExceptionType {
		CONEXION(1), INTERNAL(2), APPLICATION(3), TIMEOUT(4);

		private final int value;
		private ServiceExceptionType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public ServiceException(String errorMsg, ResponseObject response, ServiceExceptionType tipo) {
		super(errorMsg);
		this.response = response;

		this.tipo = tipo;
		if (tipo == ServiceExceptionType.CONEXION) {
			this.conexionError = true;
			this.timeOutError = false;
			this.internalError = false;
			this.applicationError = false;
		} else if (tipo == ServiceExceptionType.INTERNAL) {
			this.conexionError = false;
			this.timeOutError = false;
			this.internalError = true;
			this.applicationError = false;
		} else if (tipo == ServiceExceptionType.APPLICATION) {
			this.conexionError = false;
			this.timeOutError = false;
			this.internalError = false;
			this.applicationError = true;
		} else if (tipo == ServiceExceptionType.TIMEOUT) {
			this.conexionError = true;
			this.timeOutError = true;
			this.internalError = false;
			this.applicationError = false;
		}
	}

	public Boolean isConexionError() {
		return this.conexionError;
	}

	public Boolean isInternalError() {
		return this.internalError;
	}

	public Boolean isApplicationError() {
		return this.applicationError;
	}

	public Boolean isTimeOutError() {
		return this.timeOutError;
	}

	public ServiceExceptionType getType() {
		return this.tipo;
	}


}
