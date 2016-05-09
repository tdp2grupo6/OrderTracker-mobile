package ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions;

import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;

public class ServiceException extends Exception {
	private static final long serialVersionUID = 1L;

	private ServiceExceptionType tipo;
	private Boolean timeOutError;
	private Boolean internalError;
	private Boolean conexionError;
	private Boolean applicationError;
	private Boolean autorizationError;

	private ResponseObject response;

	/*
	public ServiceException(String errorMsg, ResponseObject response, Boolean conexion) {
	    super(errorMsg);
	    this.response = response;
	    this.timeOut = conexion;
	}
	*/

	public enum ServiceExceptionType {
		CONEXION(1), INTERNAL(2), APPLICATION(3), TIMEOUT(4), AUTORIZATION(5);

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
			this.autorizationError = false;
		} else if (tipo == ServiceExceptionType.INTERNAL) {
			this.conexionError = false;
			this.timeOutError = false;
			this.internalError = true;
			this.applicationError = false;
			this.autorizationError = false;
		} else if (tipo == ServiceExceptionType.APPLICATION) {
			this.conexionError = false;
			this.timeOutError = false;
			this.internalError = false;
			this.applicationError = true;
			this.autorizationError = false;
		} else if (tipo == ServiceExceptionType.TIMEOUT) {
			this.conexionError = true;
			this.timeOutError = true;
			this.internalError = false;
			this.applicationError = false;
			this.autorizationError = false;
		} else if (tipo == ServiceExceptionType.AUTORIZATION) {
			this.conexionError = false;
			this.timeOutError = false;
			this.internalError = false;
			this.applicationError = false;
			this.autorizationError = true;
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

	public Boolean isAutorizationError() {
		return this.autorizationError;
	}

	public ServiceExceptionType getType() {
		return this.tipo;
	}


}
