package com.house.houseautomation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Dispositivo extends Activity{
	ToggleButton tButton;
    TextView tvTituloDisp;
    TextView tvStateofToggleButton;
    Button btnProgramaDisp, btnPrecioDeLaLuz, btnPrecioLuzManyana;
    String[] lDirecciones;
    String URL_connection0,URL_connection1;
    
    HashMap hashMap = new HashMap();  
    Httppostaux post;
    
    // Variables para leer el bundle
    String codigo_dispositivo = "",habitacion="",botonPrincipal="";
    String estado_dispositivo = "";
    String descripcion_dispositivo = "";
    String tipo_dispositivo = "";
    String codigo_habitacion = "";
    InputStream is=null;
    
    private int hour;
	private int minute;
	private int day;
	private int month;
	private int year;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
				
		String estadoDisp="";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.habitacion_luces_programa);

		hashMap.put("81","lavadora");
        hashMap.put("82","lavavajillas");
        hashMap.put("83","calientaagua");
        hashMap.put("84","cafetera");
        hashMap.put("85","microondas");
        hashMap.put("88","otroenchufado");
        hashMap.put("87","ventilador");
        hashMap.put("86","equipomusica");
        hashMap.put("20","bombilla");
        hashMap.put("10","persiana"); 
		
		// Recupero los datos que me remite la actividad HabitacionLuces
		Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if ( bundle != null ) {
        	codigo_dispositivo = bundle.getString("codigo_dispositivo");
        	estado_dispositivo = bundle.getString("estado_dispositivo");
        	descripcion_dispositivo = bundle.getString("descripcion_dispositivo");
        	tipo_dispositivo = bundle.getString("tipo_dispositivo");
        	codigo_habitacion = bundle.getString("codigo_habitacion");
        	habitacion = bundle.getString("habitacion");
        	botonPrincipal = bundle.getString("botonPrincipal");
        }
		
        ImageView image = (ImageView) findViewById(R.id.imageView1);
        
        // Busco en el hashmap con el tipo del dispositivo para sacar el icono correspondiente
        String value=(String)hashMap.get(tipo_dispositivo);
        String icono_hab = "drawable/";
        int imageResource = this.getResources().getIdentifier(icono_hab+value, null, this.getPackageName());
	    image.setImageDrawable(this.getResources().getDrawable(imageResource));
        
        tvTituloDisp=(TextView)findViewById(R.id.textView1);
        tvTituloDisp.setText("Estado de "+descripcion_dispositivo);
        
        post=new Httppostaux();
        
        // Obtenemos la dirección del servidor php
	    Resources res = this.getResources();
	    lDirecciones = res.getStringArray(R.array.DireccionesServidor);
	    String URL_connect=lDirecciones[35];
	     	
	     ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
	 		
		 postparameters2send.add(new BasicNameValuePair("cod_disp",codigo_dispositivo));
		 
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
	     			 estado_dispositivo = obj.getString("EST_DISP"); 
	     			 
	     		}
	     				
	     	} 
	     }
	     catch(Exception ex) 
	     { 
	        Log.e("ServicioRest","Error!", ex); 
	     }
		
		btnProgramaDisp = (Button) findViewById(R.id.buttonPrograma);
		btnPrecioDeLaLuz = (Button) findViewById(R.id.Button01);
		btnPrecioLuzManyana = (Button) findViewById(R.id.Button02);
		
		final Calendar c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		day = c.get(Calendar.DAY_OF_MONTH);
		month = c.get(Calendar.MONTH);
		month = month +1;
		year = c.get(Calendar.YEAR);
		
		//btnPrecioLuzManyana.setVisibility(View.INVISIBLE);
		
		if (hour < 21)
		{
			btnPrecioLuzManyana.setVisibility(View.INVISIBLE);
		}
		
		// Programar encendido o apagado valvula gas
        btnPrecioDeLaLuz.setOnClickListener(new View.OnClickListener() {
        	
        @Override
        public void onClick(View view) {
        // Abre pantalla con los precios de la luz
           Intent i = new Intent(getApplicationContext(), GetElectricityData.class);
           i.putExtra("codigo_dispositivo",codigo_dispositivo);
           i.putExtra("estado_dispositivo",estado_dispositivo);
           i.putExtra("tipo_dispositivo",tipo_dispositivo);
           i.putExtra("codigo_habitacion", codigo_habitacion);
           i.putExtra("descripcion_dispositivo",descripcion_dispositivo);
           i.putExtra("habitacion",habitacion);
           i.putExtra("botonPrincipal",botonPrincipal);
           i.putExtra("tomorrow" , "0");
           startActivity(i);
           finish();
         }
        });
        
        // Programar encendido o apagado valvula gas
        btnPrecioLuzManyana.setOnClickListener(new View.OnClickListener() {
        	
        @Override
        public void onClick(View view) {
        	// Abre pantalla con los precios de la luz
            Intent i = new Intent(getApplicationContext(), GetElectricityData.class);
            i.putExtra("codigo_dispositivo",codigo_dispositivo);
            i.putExtra("estado_dispositivo",estado_dispositivo);
            i.putExtra("tipo_dispositivo",tipo_dispositivo);
            i.putExtra("codigo_habitacion", codigo_habitacion);
            i.putExtra("descripcion_dispositivo",descripcion_dispositivo);
            i.putExtra("habitacion",habitacion);
            i.putExtra("botonPrincipal",botonPrincipal);
            i.putExtra("tomorrow" , "1");
            startActivity(i);
            finish();
         }
        });
		
		// Programar encendido o apagado de dispositivo
        btnProgramaDisp.setOnClickListener(new View.OnClickListener() {
        	
        @Override
        public void onClick(View view) {
           // Abre pantalla de programacion de eventos programados
           Intent i = new Intent(getApplicationContext(), ProgramaGas.class);
           i.putExtra("codigo_dispositivo",codigo_dispositivo);
           i.putExtra("estado_dispositivo",estado_dispositivo);
           i.putExtra("tipo_dispositivo",tipo_dispositivo);
           i.putExtra("codigo_habitacion", codigo_habitacion);
           i.putExtra("descripcion_dispositivo",descripcion_dispositivo);
           i.putExtra("habitacion",habitacion);
           i.putExtra("botonPrincipal",botonPrincipal);
          
           startActivity(i);
           
           // finalizamos la aplicacion
           finish();
         }
        });
		
		tButton = (ToggleButton) findViewById(R.id.toggleButton1);
		tvStateofToggleButton=(TextView)findViewById(R.id.tvstate);
		//Ver el estado actual del dispositivo
		if (estado_dispositivo.equals("1")){
			tvStateofToggleButton.setText(descripcion_dispositivo+" ON");
			tvStateofToggleButton.setTextColor(Color.parseColor("#33CC33"));
			tButton.setChecked(true);
		}else
		{
			tvStateofToggleButton.setText(descripcion_dispositivo+" OFF");
			tvStateofToggleButton.setTextColor(Color.RED);
		}
		URL_connection0=lDirecciones[34];
     	
     	tButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

		
				HttpClient httpClient = new DefaultHttpClient();
			
				ArrayList<NameValuePair> post2send2 = new ArrayList<NameValuePair>();
				
				if(isChecked)
				{
					post2send2.add(new BasicNameValuePair("estado","1"));
					tvStateofToggleButton.setText(descripcion_dispositivo+" ON");
					tvStateofToggleButton.setTextColor(Color.parseColor("#33CC33"));
				}
				else
				{
					post2send2.add(new BasicNameValuePair("estado","0"));
					tvStateofToggleButton.setText(descripcion_dispositivo+" OFF");
					tvStateofToggleButton.setTextColor(Color.RED);
				}
				
				post2send2.add(new BasicNameValuePair("cod_disp",codigo_dispositivo));
					
				try 
				{ 
					HttpPost httppost = new HttpPost(URL_connection0);
					httppost.setEntity(new UrlEncodedFormEntity(post2send2));
					HttpResponse response = httpClient.execute(httppost); 
					HttpEntity entity = response.getEntity();
					is = entity.getContent();
					Log.e("Paso 1", "Conexion OK ");
					
					Toast.makeText(Dispositivo.this,"Dispositivo cambiado", 
								Toast.LENGTH_SHORT).show();
								
				}
				catch(Exception ex) 
				{ 
					Log.e("ServicioRest","Error!", ex); 
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
   	public void onBackPressed() {
   		Intent inten = new Intent();
   		inten.setClass(this,HabitacionLuces.class);	
   		inten.putExtra("codigo_habitacion", codigo_habitacion);
		inten.putExtra("habitacion", habitacion);
		inten.putExtra("botonPrincipal", botonPrincipal);
   		startActivity(inten);	
   		finish();
   	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menu) {
		// Obtenemos la dirección del servidor php
	    Resources res = this.getResources();
	    String[] lTextos = res.getStringArray(R.array.TextosCompletos);
	    String textoMenu=lTextos[1];
	    Toast.makeText(getApplicationContext(), textoMenu, 
	                 Toast.LENGTH_LONG).show();
	    return true;
	        
	}
}

