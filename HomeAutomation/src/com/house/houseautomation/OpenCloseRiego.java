package com.house.houseautomation;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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


public class OpenCloseRiego extends Activity {
	ToggleButton tButton,tButtonMens,tButtonAut;
    TextView tvStateofToggleButton;
    Button btnProgramaAgua;
    String URL_connection0, URL_connection1, estadoAgua;
    String[] lDirecciones;
    Httppostaux post;
    String estado_mensaje, estado_apaga;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.riego);

		// Obtenemos la dirección del servidor php
     	Resources res = this.getResources();
     	lDirecciones = res.getStringArray(R.array.DireccionesServidor);
     	String URL_connect=lDirecciones[36];
     	
		HttpClient httpClient = new DefaultHttpClient(); 
		HttpGet del = new HttpGet(URL_connect);
		del.setHeader("content-type", "application/json");
		
		btnProgramaAgua = (Button) findViewById(R.id.buttonProgramaAgua);
		
		try 
		 { 
			 HttpResponse resp = httpClient.execute(del); 
			 String respStr = EntityUtils.toString(resp.getEntity()); 
			 JSONArray respJSON = new JSONArray(respStr); 
			 
			 for(int i=0; i<respJSON.length(); i++) 
			 { 
				 JSONObject obj = respJSON.getJSONObject(i); 
				 estadoAgua = obj.getString("EST_DISP"); 
				 
			 } 
	     } 
	     catch(Exception ex) 
	     { 
	    	 Log.e("ServicioRest","Error!", ex); 
	     }	   		
		
		// Programar encendido o apagado valvula agua
        btnProgramaAgua.setOnClickListener(new View.OnClickListener() {
       
        @Override
        public void onClick(View view) {
        // Abre pantalla de programado de valvula de gas
           Intent i = new Intent(getApplicationContext(), ProgramaGas.class);
           i.putExtra("tipo_dispositivo","4" );
           i.putExtra("codigo_habitacion", "1");
           i.putExtra("codigo_dispositivo","B3");
           i.putExtra("estado_dispositivo",estadoAgua);
           startActivity(i);
           finish();
         }
        });
		
		tButton = (ToggleButton) findViewById(R.id.toggleButton1);
		tvStateofToggleButton=(TextView)findViewById(R.id.tvstate);
	
		if (estadoAgua.equals("1")){
			tvStateofToggleButton.setText("RIEGO FUNCIONANDO");
			tvStateofToggleButton.setTextColor(Color.parseColor("#33CC33"));
			tButton.setChecked(true);
		}
		else
		{
			tvStateofToggleButton.setText("RIEGO APAGADO");
			tvStateofToggleButton.setTextColor(Color.RED);
			tButton.setChecked(false);
		}
		
		post=new Httppostaux();
		
		ArrayList<NameValuePair> postparameters2send3= new ArrayList<NameValuePair>();
 		
		postparameters2send3.add(new BasicNameValuePair("cod_aut","2"));
        
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
     					 estado_apaga = obj.getString("ESTADO");
     					 
     				}
     				
     			} 
     	 }
     	 catch(Exception ex) 
     	 { 
     	   Log.e("ServicioRest","Error!", ex); 
     	 }
     	
     	tButtonAut = (ToggleButton) findViewById(R.id.apag_onoff);
        
        if (estado_apaga.equals("1")){
			tButtonAut.setChecked(true);
		}
		else
		{
			tButtonAut.setChecked(false);
		}
        
        ArrayList<NameValuePair> post2send3= new ArrayList<NameValuePair>();
 		
		post2send3.add(new BasicNameValuePair("cod_aut","3"));
        
        // Obtenemos la dirección del servidor php
     	URL_connect=lDirecciones[19];
     	try
     	 { 
     		//realizamos una peticion y como respuesta obtienes un array JSON
     		JSONArray jdata= post.getserverdata(post2send3, URL_connect);

     			//si lo que obtuvimos no es null
     			if (jdata!=null && jdata.length() > 0)
     			{
     				for(int i=0; i<jdata.length(); i++) 
     				{ 
     					 JSONObject obj = jdata.getJSONObject(i); 
     					 estado_mensaje = obj.getString("ESTADO");
     					 
     				}
     				
     			} 
     	 }
     	 catch(Exception ex) 
     	 { 
     	   Log.e("ServicioRest","Error!", ex); 
     	 }
     	
     	tButtonMens = (ToggleButton) findViewById(R.id.mens_onoff);
        
        if (estado_mensaje.equals("1")){
			tButtonMens.setChecked(true);
		}
		else
		{
			tButtonMens.setChecked(false);
		}
        
		// Obtenemos la dirección del servidor php
     	
     	URL_connection0=lDirecciones[24];
     	URL_connection1=lDirecciones[25];
     	
		tButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

		
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
						     	
				HttpClient httpClient = new DefaultHttpClient();
				
				HttpGet upDate0 = new HttpGet(URL_connection0); 
				HttpGet upDate1 = new HttpGet(URL_connection1); 
				
				
				if(isChecked){
					tvStateofToggleButton.setText("RIEGO FUNCIONANDO");
					tvStateofToggleButton.setTextColor(Color.parseColor("#33CC33"));
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
					tvStateofToggleButton.setText("RIEGO APAGADO");
					tvStateofToggleButton.setTextColor(Color.RED);
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
		
		tButtonAut = (ToggleButton) findViewById(R.id.apag_onoff);
		
		tButtonAut.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {	 		
		 		
				HttpClient httpClient = new DefaultHttpClient();
				String URL_connection0 = lDirecciones[26];
				String URL_connection1 = lDirecciones[27];
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
		
		tButtonMens = (ToggleButton) findViewById(R.id.mens_onoff);
		
		tButtonMens.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {	 		
		 		
				HttpClient httpClient = new DefaultHttpClient();
				String URL_connection0 = lDirecciones[28];
				String URL_connection1 = lDirecciones[29];
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
    {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.activity_menu, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menu) {
		// Obtenemos la dirección del servidor php
	    Resources res = this.getResources();
	    String[] lTextos = res.getStringArray(R.array.TextosCompletos);
	    String textoMenu=lTextos[3];
	    Toast.makeText(getApplicationContext(), textoMenu, 
	                 Toast.LENGTH_LONG).show();
	    return true;
	        
	}
	
	@Override
	public void onBackPressed() {
		Intent inten = new Intent();
		inten.setClass(this,PaginaPrincipal.class);	
		startActivity(inten);	
		finish();
	}
}

