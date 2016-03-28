/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONObject;

import android.graphics.Bitmap;

//Clase para modelar la respuesta a la llamada a un metodo del servicio
public class ResponseObject {

	private String webMethod = null;
	private Bitmap bitmap = null;
	private String data = null;

	private String error = null;
	private Boolean has_error = false;

	/**
	 * @return the webMethod
	 */
	public String getWebMethod() {
		return webMethod;
	}

	/**
	 * @param webMethod the webMethod to set
	 */
	public void setWebMethod(String webMethod) {
		this.webMethod = webMethod;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
		this.has_error = true;
	}

	public Boolean getHasError() {
		return this.has_error;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * @return the bitmap
	 */
	public Bitmap getBitmap() {
		return bitmap;
	}

	/**
	 * @param bitmap the bitmap to set
	 */
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
}