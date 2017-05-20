package com.house.houseautomation;

public class ItemTemperatura {
	  protected long id;
	  protected String rutaImagen;
	  protected String nombre;
	  protected String valor;
	  protected String humedad;
	         
	  public void ItemTemperatura() {
	    this.nombre = "";
	    this.valor = "";
	    this.humedad = "";
	    this.rutaImagen = "";
	  }
	     
	  public ItemTemperatura(long id, String nombre, String valor, String humedad) {
	    this.id = id;
	    this.nombre = nombre;
	    this.valor = valor;
	    this.humedad = humedad;
	    this.rutaImagen = "";
	  }
	     
	  public ItemTemperatura(long id, String nombre, String valor, String humedad, String rutaImagen) {
	    this.id = id;
	    this.nombre = nombre;
	    this.valor = valor;
	    this.rutaImagen = rutaImagen;
	    this.humedad = humedad;
	  }
	     
	  public long getId() {
	    return id;
	  }
	     
	  public void setId(long id) {
	    this.id = id;
	  }
	     
	  public String getRutaImagen() {
	    return rutaImagen;
	  }
	     
	  public void setRutaImagen(String rutaImagen) {
	    this.rutaImagen = rutaImagen;
	  }
	     
	  public String getNombre() {
	    return nombre;
	  }
	     
	  public void setNombre(String nombre) {
	    this.nombre = nombre;
	  }
	     
	  public String getValor() {
	    return valor;
	  }
	     
	  public void setValor(String valor) {
	    this.valor = valor;
	  }
	  
	  public String getHumedad() {
		    return humedad;
		  }
		     
	  public void setHumedad(String humedad) {
		    this.humedad = humedad;
	  }
}

