package com.house.houseautomation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse; 
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient; 
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete; 
import org.apache.http.client.methods.HttpGet; 
import org.apache.http.client.methods.HttpPost; 
import org.apache.http.client.methods.HttpPut; 
import org.apache.http.entity.StringEntity; 
import org.apache.http.impl.client.DefaultHttpClient; 
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils; 

import com.house.houseautomation.library.Httppostaux;

import android.app.Activity; 
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle; 
import android.util.Log; 
import android.view.View; 
import android.view.View.OnClickListener; 
import android.widget.ArrayAdapter; 
import android.widget.Button; 
import android.widget.CompoundButton;
import android.widget.EditText; 
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;
 
import android.widget.TextView; 
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SetPersianas extends Activity { 
    private SeekBar persControl = null;
    String est_disp="", cod_hab="", desc_disp="", tipo_disp="",cod_disp="",habitacion="";
    String header = "Estado de persiana";
    String botonPrincipal="";
    String aperturaPers="0";
    Button btnProgramaPers, btnActualizaPers;
    int progressChanged=0;
    String[] lDirecciones;
    String URL_connection0;
    String estado_inicial;
    InputStream is=null;
    String estado_aut="";
    
    Httppostaux post;
	 
    TextView textCabecera;
    ToggleButton tButton;
    
    
	 @Override 
	 public void onCreate(Bundle savedInstanceState) { 
		 super.onCreate(savedInstanceState); 
		 setContentView(R.layout.set_persianas);
		 
		 Intent intent = getIntent();
		 Bundle bundle = intent.getExtras();
		 if ( bundle != null ) {
	    	cod_hab = bundle.getString("codigo_habitacion");
	    	desc_disp = bundle.getString("descripcion_dispositivo");
	    	tipo_disp = bundle.getString("tipo_dispositivo");
	    	cod_disp = bundle.getString("codigo_dispositivo");
	    	est_disp = bundle.getString("estado_dispositivo");
        	habitacion = bundle.getString("habitacion");
        	botonPrincipal = bundle.getString("botonPrincipal");
        	
        	
		 }
		 
		 post=new Httppostaux();
		 
		 // Obtenemos la dirección del servidor php
	     Resources res = this.getResources();
	     lDirecciones = res.getStringArray(R.array.DireccionesServidor);
	     String URL_connect=lDirecciones[32];
	     	
	     ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
	 		
		 postparameters2send.add(new BasicNameValuePair("cod_disp",cod_disp));
		 
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
	     			 aperturaPers = obj.getString("VAL_AUX1"); 
	     			 estado_inicial = aperturaPers;
	     		}
	     				
	     	} 
	     }
	     catch(Exception ex) 
	     { 
	        Log.e("ServicioRest","Error!", ex); 
	     }
		 
		 // Obtenemos la dirección del servidor php
	     	
     	
     	ArrayList<NameValuePair> postparameters2send3= new ArrayList<NameValuePair>();
 		
		postparameters2send3.add(new BasicNameValuePair("cod_aut","5"));
        
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
     					 estado_aut = obj.getString("ESTADO");
     					 
     				}
     				
     			} 
     	 }
     	 catch(Exception ex) 
     	 { 
     	   Log.e("ServicioRest","Error!", ex); 
     	 }
     	
     	tButton = (ToggleButton) findViewById(R.id.tgl_onoff);
        
        if (estado_aut.equals("1")){
			tButton.setChecked(true);
		}
		else
		{
			tButton.setChecked(false);
		}
        
     	btnActualizaPers = (Button) findViewById(R.id.buttonActualiza);
		btnActualizaPers.setOnClickListener(new View.OnClickListener() {
			@Override
	        public void onClick(View view) {
				HttpClient httpClient = new DefaultHttpClient();
				String URL_connection0 = lDirecciones[33]; 
				
				ArrayList<NameValuePair> post2send2 = new ArrayList<NameValuePair>();
					
				try 
				{ 
					post2send2.add(new BasicNameValuePair("cod_disp",cod_disp));
					post2send2.add(new BasicNameValuePair("val_aux",Integer.toString(progressChanged)));
					
				    HttpPost httppost = new HttpPost(URL_connection0);
				    httppost.setEntity(new UrlEncodedFormEntity(post2send2));
				    HttpResponse response = httpClient.execute(httppost); 
					HttpEntity entity = response.getEntity();
					is = entity.getContent();
					Log.e("Paso 1", "Conexion OK ");
					if (Integer.valueOf(progressChanged)<Integer.valueOf(estado_inicial))
					{
						Toast.makeText(SetPersianas.this,"Persiana subida", 
	                        Toast.LENGTH_SHORT).show();
						
					}
					else
					{
						Toast.makeText(SetPersianas.this,"Persiana bajada", 
		                        Toast.LENGTH_SHORT).show();
					}
					estado_inicial = Integer.toString(progressChanged);
				}
				catch(Exception ex) 
				{ 
				    Log.e("ServicioRest","Error!", ex); 
				}  
			}
		});
		
		tButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {	 		
		 		
				HttpClient httpClient = new DefaultHttpClient();
				String URL_connection0 = lDirecciones[52];
				String URL_connection1 = lDirecciones[53];
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
		 
		 textCabecera = (TextView) findViewById(R.id.textView1); 
		 textCabecera.setText(header + "\n" + desc_disp);
		 
		 persControl = (SeekBar) findViewById(R.id.seekBarPersianas);
		 int apertura = Integer.valueOf(aperturaPers);
		 persControl.setProgress(apertura);
		 
	     persControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	 
	            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
	                progressChanged = progress;
	            }
	 
	            public void onStartTrackingTouch(SeekBar seekBar) {
	                // TODO Auto-generated method stub
	            }
	 
	            public void onStopTrackingTouch(SeekBar seekBar) {
	                Toast.makeText(SetPersianas.this,"Persiana cerrada: "+progressChanged+"%", 
	                        Toast.LENGTH_SHORT).show();
	            }
	        });
	     
	     	btnProgramaPers = (Button) findViewById(R.id.buttonPrograma);
	     	// Programar encendido o apagado de dispositivo
	        btnProgramaPers.setOnClickListener(new View.OnClickListener() {
	        	
	        @Override
	        public void onClick(View view) {
	           // Abre pantalla de programacion de eventos programados
	           Intent i = new Intent(getApplicationContext(), ProgramaPersiana.class);
	           i.putExtra("tipo_disp",tipo_disp);
	           i.putExtra("cod_habitacion", cod_hab);
	           i.putExtra("cod_disp",cod_disp);
	           i.putExtra("est_disp",est_disp);
	           i.putExtra("habitacion", habitacion);
	           i.putExtra("botonPrincipal", botonPrincipal);
	           i.putExtra("descripcion_dispositivo", desc_disp);
	           startActivity(i);
	           finish();
	         }
	        });
		 
	 }
	 @Override
	   	public void onBackPressed() {
	   		Intent inten = new Intent();
	   		inten.setClass(this,HabitacionLuces.class);	
	   		inten.putExtra("codigo_habitacion", cod_hab);
			inten.putExtra("habitacion", habitacion);
			inten.putExtra("botonPrincipal", botonPrincipal);
	   		startActivity(inten);	
	   		finish();
	   	}
}
