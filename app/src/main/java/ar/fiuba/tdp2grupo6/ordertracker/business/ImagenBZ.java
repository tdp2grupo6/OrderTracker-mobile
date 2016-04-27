package ar.fiuba.tdp2grupo6.ordertracker.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;

public class ImagenBZ {

	public File grabar(String fileName, Bitmap bitmap) throws BusinessException {
		File newFile = null;
		if (isExternalStorageWritable()) {
			newFile = createDownloadFile(fileName);

			FileOutputStream outputStream;
			try {

				outputStream = new FileOutputStream(newFile);
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
				outputStream.flush();
				outputStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return newFile;
	}

	public Bitmap leer(String fileName) {
		Bitmap bitmap = null;
		if (isExternalStorageReadable()) {
			try {
				File file = new File(getFolder(), fileName);
				if (file.exists())
					bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	public boolean existe(String fileName) throws BusinessException {
		boolean existe = false;
		if (isExternalStorageReadable()) {
			try {
				File file = new File(getFolder(), fileName);
				existe = file.exists();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return existe;
	}


	/* Checks if external storage is available for read and write */
	private boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	private boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	/** Create a File for saving an image or video 
	 * @throws BusinessException */
	private File createDownloadFile(String fileName) throws BusinessException {

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.
		File mediaFile = new File(getFolder(), fileName);

		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		// Create the storage directory if it does not exist
		if (!mediaFile.exists()) {
			try {
				if (!mediaFile.createNewFile()) {
					throw new BusinessException("No se pudo crear la carpeta");
				}
			} catch (IOException e) {
				throw new BusinessException("No se pudo crear la carpeta");
			}
		}

		return mediaFile;
	}

	private File getFolder() {
		File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ordertracker/images/");
		return folder;
	}

}
