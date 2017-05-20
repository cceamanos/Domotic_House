package com.house.houseautomation;

public class Globals{
	   private static Globals instance;
	 
	   // Global variable
	   private String username;
	 
	   // Restrict the constructor from being instantiated
	   private Globals(){}
	 
	   public void setData(String d){
	     this.username=d;
	   }
	   public String getData(){
	     return this.username;
	   }
	 
	   public static synchronized Globals getInstance(){
	     if(instance==null){
	       instance=new Globals();
	     }
	     return instance;
	   }
	}
