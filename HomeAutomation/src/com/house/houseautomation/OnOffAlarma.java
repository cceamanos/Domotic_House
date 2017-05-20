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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class OnOffAlarma extends Activity{
	Httppostaux post;
	ImageView imagen;
	ToggleButton tButton;
    TextView tvStateofToggleButton;
    Button btnProgramaAlarma;
    String URL_connection0, URL_connection1;
    String estadoAlarma="0";
    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarma);

		// Obtenemos la dirección del servidor php
     	Resources res = this.getResources();
     	String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
     	String URL_connect=lDirecciones[19];
		
		btnProgramaAlarma = (Button) findViewById(R.id.buttonProgramaAlarma);
		
		post=new Httppostaux();
		
		ArrayList<NameValuePair> postparameters2send3= new ArrayList<NameValuePair>();
 		
		postparameters2send3.add(new BasicNameValuePair("cod_aut","4"));
        
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
     					 estadoAlarma = obj.getString("ESTADO");
     					 
     				}
     				
     			} 
     	 }
     	 catch(Exception ex) 
     	 { 
     	   Log.e("ServicioRest","Error!", ex); 
     	 }
		
		// Programar encendido o apagado de la alarma
        btnProgramaAlarma.setOnClickListener(new View.OnClickListener() {
        	
        @Override
        public void onClick(View view) {
        // Abre pantalla de programado de la alarma
           Intent i = new Intent(getApplicationContext(), ProgramaAlarma.class);
           i.putExtra("tip_disp","99" );
           i.putExtra("cod_habitacion", "1");
           i.putExtra("cod_disp","A1");
           i.putExtra("est_disp",estadoAlarma);
           startActivity(i);
           finish();
         }
        });
		
		tButton = (ToggleButton) findViewById(R.id.toggleButtonAlarm);
		tvStateofToggleButton=(TextView)findViewById(R.id.tvstateAlarma);
		imagen= (ImageView)findViewById(R.id.imageViewCasa);
		
		
		if (estadoAlarma.equals("1")){
			tvStateofToggleButton.setText("ALARMA ACTIVADA");
			tvStateofToggleButton.setTextColor(Color.parseColor("#33CC33"));
			imagen.setImageResource(R.drawable.green_home);
			tButton.setChecked(true);
		}
		else
		{
			tvStateofToggleButton.setText("ALARMA DESACTIVADA");
			tvStateofToggleButton.setTextColor(Color.RED);
			imagen.setImageResource(R.drawable.red_home);
			tButton.setChecked(false);
		}
		
		// Obtenemos la dirección del servidor php
     	
     	URL_connection0=lDirecciones[30];
     	URL_connection1=lDirecciones[31];
     	
		tButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

		
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
						     	
				HttpClient httpClient = new DefaultHttpClient();
				
				HttpGet upDate0 = new HttpGet(URL_connection0); 
				HttpGet upDate1 = new HttpGet(URL_connection1); 
				
				
				if(isChecked){
					tvStateofToggleButton.setText("ALARMA ACTIVADA");
					tvStateofToggleButton.setTextColor(Color.parseColor("#33CC33"));
					imagen.setImageResource(R.drawable.green_home);
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
					tvStateofToggleButton.setText("ALARMA DESACTIVADA");
					tvStateofToggleButton.setTextColor(Color.RED);
					imagen.setImageResource(R.drawable.red_home);
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
	    String textoMenu=lTextos[4];
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
