package com.example.bqshelf;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

/**
 * Representa un Epub
 * @author MarinaG
 *
 */
public class Epub {
	
	private static final String TAG = "Epub";
	
	private String nombre;
	private Date fecha;

	/**
	 * Constructor Epub
	 * @param nombre Título del Epub
	 * @param fecha Fecha en la que fue añadido a Dropbox (fecha de creación)
	 */
	public Epub (String nombre, String fecha) {
		this.nombre = nombre;	
		SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss ZZZZZ", Locale.US);
		Date date = new Date();
		 try {
			 date = df.parse(fecha);	
		 } catch (java.text.ParseException e) {
			 Log.i(TAG, "Error parsing");
		 }
		 
		 this.fecha = date;
	}
	
	/**
	 * Devuelve el título
	 * @return Título
	 */
	public String getNombre(){
		return nombre;
	}
	
	/**
	 * Devuelve la fecha de creación
	 * @return fecha de creación
	 */
	public Date getFecha() {
		return fecha;
		
	}
	
	/**
	 * Devuelve el texto que aparecerá en la lista de Epub
	 */
	public String toString(){
		return (nombre);
	}
}
