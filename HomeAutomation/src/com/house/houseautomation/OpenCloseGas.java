package com.house.houseautomation;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

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

public class OpenCloseGas extends Activity {
		ToggleButton tButton;
	    TextView tvStateofToggleButton;
	    Button btnProgramaGas;
	    String URL_connection0, URL_connection1, estadoGas;
	    
	    
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.gas);

			// Obtenemos la dirección del servidor php
	     	Resources res = this.getResources();
	     	String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
	     	String URL_connect=lDirecciones[9];
	     	
			HttpClient httpClient = new DefaultHttpClient(); 
			HttpGet del = new HttpGet(URL_connect);
			del.setHeader("content-type", "application/json");
			
			btnProgramaGas = (Button) findViewById(R.id.buttonPrograma);
			
			
			
			try 
			 { 
				 HttpResponse resp = httpClient.execute(del); 
				 String respStr = EntityUtils.toString(resp.getEntity()); 
				 JSONArray respJSON = new JSONArray(respStr); 
				 
				 for(int i=0; i<respJSON.length(); i++) 
				 { 
					 JSONObject obj = respJSON.getJSONObject(i); 
					 estadoGas = obj.getString("EST_DISP"); 
					 
				 } 
		     } 
		     catch(Exception ex) 
		     { 
		    	 Log.e("ServicioRest","Error!", ex); 
		     }	   		
			
			// Programar encendido o apagado valvula gas
	        btnProgramaGas.setOnClickListener(new View.OnClickListener() {
	        	
	        @Override
	        public void onClick(View view) {
	        // Abre pantalla de programado de valvula de gas
	           Intent i = new Intent(getApplicationContext(), ProgramaGas.class);
	           i.putExtra("tipo_dispositivo","1" );
	           i.putExtra("codigo_habitacion", "1");
	           i.putExtra("codigo_dispositivo","B1");
	           i.putExtra("estado_dispositivo",estadoGas);
	           startActivity(i);
	           finish();
	         }
	        });
			
			tButton = (ToggleButton) findViewById(R.id.toggleButton1);
			tvStateofToggleButton=(TextView)findViewById(R.id.tvstate);
			//tvStateofToggleButton.setText("GAS ESTÁ APAGADO");
			if (estadoGas.equals("1")){
				tvStateofToggleButton.setText("GAS ESTÁ ABIERTO");
				tvStateofToggleButton.setTextColor(Color.parseColor("#33CC33"));
				tButton.setChecked(true);
			}
			else
			{
				tvStateofToggleButton.setText("GAS ESTÁ CERRADO");
				tvStateofToggleButton.setTextColor(Color.RED);
				tButton.setChecked(false);
			}
			
			// Obtenemos la dirección del servidor php
	     	
	     	URL_connection0=lDirecciones[10];
	     	URL_connection1=lDirecciones[11];
	     	
			tButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
							     	
					HttpClient httpClient = new DefaultHttpClient();
					
					HttpGet upDate0 = new HttpGet(URL_connection0); 
					HttpGet upDate1 = new HttpGet(URL_connection1); 
					
					
					if(isChecked){
						tvStateofToggleButton.setText("GAS ESTÁ ABIERTO");
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
						tvStateofToggleButton.setText("GAS ESTÁ CERRADO");
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
		    String textoMenu=lTextos[0];
		    Toast.makeText(getApplicationContext(), textoMenu, 
		                 Toast.LENGTH_SHORT).show();
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
