package com.house.houseautomation;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.house.houseautomation.library.Httppostaux;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Calefaccion extends Activity{
	
	public String cod_hab="1";
	Httppostaux post;
	String valor_temperatura="";
	String valor_temperatura_des="";
	String valor_histeresis="1.0";
	String estado_calefaccion="0";
	String estado_automatico="0";
	String estado_consulta;
	String[] lDirecciones;
	InputStream is=null;
	
	ToggleButton tButton,tButtonAut;
	Button btnProgramaCale;
	TextView valueTemperatureDes,valorhist;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calefaccion);
		
		// Obtenemos la dirección del servidor php
     	Resources res = this.getResources();
     	lDirecciones = res.getStringArray(R.array.DireccionesServidor);
 
		post=new Httppostaux();
		
		ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
 		
		postparameters2send.add(new BasicNameValuePair("cod_hab",cod_hab));
		postparameters2send.add(new BasicNameValuePair("tip_sen","1"));
        
        // Obtenemos la dirección del servidor php
     	String URL_connect=lDirecciones[7];
     	try
     	 { 
     		//realizamos una peticion y como respuesta obtienes un array JSON
     		JSONArray jdata= post.getserverdata(postparameters2send, URL_connect);

     			//si lo que obtuvimos no es null
     			if (jdata!=null && jdata.length() > 0)
     			{
     				for(int i=0; i<jdata.length(); i++) 
     				{ 
     					 JSONObject obj = jdata.getJSONObject(i); 
     					 valor_temperatura = obj.getString("VAL_SEN"); 
     				}
     				
     			} 
     	 }
     	 catch(Exception ex) 
     	 { 
     	   Log.e("ServicioRest","Error!", ex); 
     	 }
     	
     	TextView valueTemperature = (TextView)this.findViewById(R.id.valortemperatura);
        valueTemperature.setText(valor_temperatura+"ºC");
        
        ArrayList<NameValuePair> postparameters2send2= new ArrayList<NameValuePair>();
 		
		postparameters2send2.add(new BasicNameValuePair("tipo_disp","5"));
        
        // Obtenemos la dirección del servidor php
     	URL_connect=lDirecciones[18];
     	try
     	 { 
     		//realizamos una peticion y como respuesta obtienes un array JSON
     		JSONArray jdata= post.getserverdata(postparameters2send2, URL_connect);

     			//si lo que obtuvimos no es null
     			if (jdata!=null && jdata.length() > 0)
     			{
     				for(int i=0; i<jdata.length(); i++) 
     				{ 
     					 JSONObject obj = jdata.getJSONObject(i); 
     					 estado_calefaccion = obj.getString("EST_DISP");
     					 valor_histeresis = obj.getString("VAL_AUX1");
     					 valor_temperatura_des = obj.getString("VAL_AUX2"); 
     					 
     				}
     				
     			} 
     	 }
     	 catch(Exception ex) 
     	 { 
     	   Log.e("ServicioRest","Error!", ex); 
     	 }
     	
     	valueTemperatureDes = (TextView)this.findViewById(R.id.valortempdes);
        valueTemperatureDes.setText(valor_temperatura_des+"ºC");
        
        valorhist = (TextView)this.findViewById(R.id.valorhist);
        valorhist.setText(valor_histeresis);
        
        tButton = (ToggleButton) findViewById(R.id.tgl_onoff);
        
        if (estado_calefaccion.equals("1")){
			tButton.setChecked(true);
		}
		else
		{
			tButton.setChecked(false);
		}
        
        
        
        ArrayList<NameValuePair> postparameters2send3= new ArrayList<NameValuePair>();
 		
		postparameters2send3.add(new BasicNameValuePair("cod_aut","1"));
        
        // Obtenemos la dirección del servidor php
     	URL_connect=lDirecciones[19];
     	try
     	 { 
     		//realizamos una peticion y como respuesta obtienes un array JSON
     		JSONArray jdata= post.getserverdata(postparameters2send3, URL_connect);

     			//si lo que obtuvimos no es null
     			if (jdata!=null && jdata.length() > 0)
     			{
     				for(int i=0; i<jdata.length(); i++) 
     				{ 
     					 JSONObject obj = jdata.getJSONObject(i); 
     					 estado_automatico = obj.getString("ESTADO");
     					 
     				}
     				
     			} 
     	 }
     	 catch(Exception ex) 
     	 { 
     	   Log.e("ServicioRest","Error!", ex); 
     	 }
     	
     	tButtonAut = (ToggleButton) findViewById(R.id.tgl_manual);
        
        if (estado_automatico.equals("1")){
			tButtonAut.setChecked(true);
		}
		else
		{
			tButtonAut.setChecked(false);
		}
        
        tButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
				ArrayList<NameValuePair> post2send2 = new ArrayList<NameValuePair>();
						     	
				HttpClient httpClient = new DefaultHttpClient();
				String URL_connection = lDirecciones[20];				
				
				if(isChecked){
					try 
					 { 
						post2send2.add(new BasicNameValuePair("est_disp","1"));
						post2send2.add(new BasicNameValuePair("histeresis",valor_histeresis));
						post2send2.add(new BasicNameValuePair("temp_des",valor_temperatura_des));
					    
				        HttpPost httppost = new HttpPost(URL_connection);
				        httppost.setEntity(new UrlEncodedFormEntity(post2send2));
				        HttpResponse response = httpClient.execute(httppost); 
				        HttpEntity entity = response.getEntity();
				        is = entity.getContent();
				        Log.e("Paso 1", "Conexion OK ");
					 }
					catch(Exception ex) 
				     { 
				    	 Log.e("ServicioRest","Error!", ex); 
				     }  
				}else{
					try 
					 { 
						post2send2.add(new BasicNameValuePair("est_disp","0"));
						post2send2.add(new BasicNameValuePair("histeresis",valor_histeresis));
						post2send2.add(new BasicNameValuePair("temp_des",valor_temperatura_des));					
						
						HttpPost httppost = new HttpPost(URL_connection);
				        httppost.setEntity(new UrlEncodedFormEntity(post2send2));
				        HttpResponse response = httpClient.execute(httppost); 
				        HttpEntity entity = response.getEntity();
				        is = entity.getContent();
				        Log.e("Paso 1", "Conexion OK ");
		    			
					 }
					catch(Exception ex) 
				     { 
				    	 Log.e("ServicioRest","Error!", ex); 
				     }  
				}

			}
		});
        
        btnProgramaCale = (Button) findViewById(R.id.cale_programa);
        // Programar encendido o apagado valvula gas
        btnProgramaCale.setOnClickListener(new View.OnClickListener() {
        	
        @Override
        public void onClick(View view) {
        // Abre pantalla de programado de valvula de gas
           Intent i = new Intent(getApplicationContext(), ProgramaCale.class);
           i.putExtra("tipo_disp","5" );
           i.putExtra("cod_habitacion", "1");
           i.putExtra("cod_disp","E1");
           i.putExtra("est_disp",estado_calefaccion);
           startActivity(i);
           finish();
         }
        });
        
        tButtonAut.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {	 		
		 		
				HttpClient httpClient = new DefaultHttpClient();
				String URL_connection0 = lDirecciones[21];
				String URL_connection1 = lDirecciones[22];
				HttpGet upDate0 = new HttpGet(URL_connection0); 
				HttpGet upDate1 = new HttpGet(URL_connection1); 
				
				
				if(isChecked){
					try 
					 { 
						
						HttpResponse resp = httpClient.execute(upDate1); 
						String respStr = EntityUtils.toString(resp.getEntity()); 
						JSONArray respJSON = new JSONArray(respStr);
					 }
					catch(Exception ex) 
				     { 
				    	 Log.e("ServicioRest","Error!", ex); 
				     }  
				}else{
					try 
					 { 
						HttpResponse resp = httpClient.execute(upDate0); 
						String respStr = EntityUtils.toString(resp.getEntity()); 
						JSONArray respJSON = new JSONArray(respStr);
					 }
					catch(Exception ex) 
				     { 
				    	 Log.e("ServicioRest","Error!", ex); 
				     }  
				}

			}
		});
	}
	
	public void imageClickMenosDes(View view) {  
		  //Implement image click function  
		  Double valor_temp_des = Double.valueOf(valor_temperatura_des).doubleValue();
		  valor_temp_des = valor_temp_des - 0.5;
		  valor_temperatura_des = String.valueOf(valor_temp_des);
		  valueTemperatureDes.setText(valor_temperatura_des+"ºC");
	}  
	
	public void imageClickMasDes(View view) {  
		  //Implement image click function  
		  Double valor_temp_des = Double.valueOf(valor_temperatura_des).doubleValue();
		  valor_temp_des = valor_temp_des + 0.5;
		  valor_temperatura_des = String.valueOf(valor_temp_des);
		  valueTemperatureDes.setText(valor_temperatura_des+"ºC");
	}  
	public void imageClickMenosHis(View view) {  
		  //Implement image click function  
		  Double valor_temp_des = Double.valueOf(valor_histeresis).doubleValue();
		  if (valor_temp_des > 0)
		  {
			  valor_temp_des = valor_temp_des - 0.5;
			  valor_histeresis = String.valueOf(valor_temp_des);
			  valorhist.setText(valor_histeresis);
		  }
	}  
	public void imageClickMasHis(View view) {  
		  //Implement image click function  
		  Double valor_temp_des = Double.valueOf(valor_histeresis).doubleValue();
		  if (valor_temp_des < 4)
		  {
			  valor_temp_des = valor_temp_des + 0.5;
			  valor_histeresis = String.valueOf(valor_temp_des);
			  valorhist.setText(valor_histeresis);
		  }
	}  
	
	public void actualizarTodo(View view){
		HttpClient httpClient = new DefaultHttpClient();
		String URL_connection0 = lDirecciones[21];
		String URL_connection1 = lDirecciones[22];
		HttpGet upDate0 = new HttpGet(URL_connection0); 
		HttpGet upDate1 = new HttpGet(URL_connection1); 
		
		ArrayList<NameValuePair> post2send2 = new ArrayList<NameValuePair>();
	     	
		String URL_connection = lDirecciones[20];				
			
		if(tButton.isChecked()){
			try 
			 { 
				post2send2.add(new BasicNameValuePair("est_disp","1"));
				post2send2.add(new BasicNameValuePair("histeresis",valor_histeresis));
				post2send2.add(new BasicNameValuePair("temp_des",valor_temperatura_des));
				    
			    HttpPost httppost = new HttpPost(URL_connection);
			    httppost.setEntity(new UrlEncodedFormEntity(post2send2));
			    HttpResponse response = httpClient.execute(httppost); 
			    HttpEntity entity = response.getEntity();
			    is = entity.getContent();
			    Log.e("Paso 1", "Conexion OK ");
			 }
			 catch(Exception ex) 
			 { 
			   	 Log.e("ServicioRest","Error!", ex); 
			 }  
		}else
		{
			try 
		    { 
				post2send2.add(new BasicNameValuePair("est_disp","0"));
				post2send2.add(new BasicNameValuePair("histeresis",valor_histeresis));
				post2send2.add(new BasicNameValuePair("temp_des",valor_temperatura_des));					
					
				HttpPost httppost = new HttpPost(URL_connection);
			    httppost.setEntity(new UrlEncodedFormEntity(post2send2));
			    HttpResponse response = httpClient.execute(httppost); 
			    HttpEntity entity = response.getEntity();
			    is = entity.getContent();
			    Log.e("Paso 1", "Conexion OK ");
	    			
			 }
			 catch(Exception ex) 
			 { 
			   	 Log.e("ServicioRest","Error!", ex); 
			 }  
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent inten = new Intent();
		inten.setClass(this,PaginaPrincipal.class);	
		startActivity(inten);	
		finish();
	}
}
