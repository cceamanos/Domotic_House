package com.house.houseautomation;

import java.util.ArrayList;
import java.util.HashMap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddSensor extends Activity{
	String codigo1="";
	String codigo2="";
	String nombre="";
	String descripcion="",botonPrincipal="";
	String tipoDisp="";
	String estadoIni="";
	String habitacion,cod_hab;
	InputStream is=null;
	String result=null;
	String line=null;
	int code;
	Spinner spCode,spTipo,estIni;
	EditText eCode, desc;
	HashMap hashMap = new HashMap();  
 
    // Php para crear nuevo usuario
    //private static String url_create_user = "http://domoticahouse.no-ip.biz/create_user.php";
    //String url_create_user = Constantes.PHP_CREATE_USER;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_sensor);
        
        hashMap.put("Temperatura","1");
        hashMap.put("Humedad","2");
        hashMap.put("Gas","3");
        hashMap.put("Luminosidad","4");
        hashMap.put("Inundación","5");
        hashMap.put("Movimiento(PIR)","6");
        hashMap.put("Lluvia","7");
        hashMap.put("Fuego","8");
        hashMap.put("Sonido","9");
        hashMap.put("Ultrasonido","10"); 
       
        
        
        Intent intent = getIntent();
	     Bundle bundle = intent.getExtras();
	     if ( bundle != null ) {
	       	habitacion = bundle.getString("habitacion");
	       	cod_hab = bundle.getString("codigo_habitacion");
	       	botonPrincipal = bundle.getString("botonPrincipal");
	     }
        
        spCode=(Spinner) findViewById(R.id.spinner1);
        eCode=(EditText) findViewById(R.id.editText1);
        desc=(EditText) findViewById(R.id.editText2);
        spTipo=(Spinner) findViewById(R.id.spinner2);
 
        Button insert=(Button) findViewById(R.id.btnCreateDisp);
        
        insert.setText("Añadir nuevo sensor");
        
        insert.setOnClickListener(new View.OnClickListener() {
			
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
				
			codigo1 = spCode.getSelectedItem().toString();
			codigo2 = eCode.getText().toString();
			descripcion = desc.getText().toString();
			tipoDisp = spTipo.getSelectedItem().toString();
			String value=(String)hashMap.get(tipoDisp);
			tipoDisp = value;
			
			insert();
		}
	});
        
    }
    
    
 
    public void insert()
    {
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
 
    
    	nameValuePairs.add(new BasicNameValuePair("codigo",codigo1+codigo2));
    	nameValuePairs.add(new BasicNameValuePair("cod_hab",cod_hab));
    	nameValuePairs.add(new BasicNameValuePair("descripcion",descripcion));
    	nameValuePairs.add(new BasicNameValuePair("tipodisp",tipoDisp));
    	
    	;
    	
    	try
    	{
    		HttpClient httpclient = new DefaultHttpClient();
    		
    		// Obtenemos la dirección del servidor php
		    Resources res = this.getResources();
		    String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
		    String url_create_user=lDirecciones[40];
		    
	        HttpPost httppost = new HttpPost(url_create_user);
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost); 
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	        Log.e("Paso 1", "Conexion OK ");
	        
	}
        catch(Exception e)
	{
        	Log.e("Fallo 1", e.toString());
	    	Toast.makeText(getApplicationContext(), "IP no valida",
			Toast.LENGTH_LONG).show();
	}     
        
        try
        {
            BufferedReader reader = new BufferedReader
			(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
	    {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
	    Log.e("Paso 2", "Conexion OK ");
	}
        catch(Exception e)
	{
            Log.e("Fallo 2", e.toString());
	}     
       
	try
	{
            JSONObject json_data = new JSONObject(result);
            code=(json_data.getInt("code"));
			
            if(code==1)
            {
            	Toast.makeText(getBaseContext(), "Sensor añadido",
            	Toast.LENGTH_SHORT).show();
            }
            else{ 
            	if(code==2){
            		Toast.makeText(getBaseContext(), "El sensor ya existe",
                    Toast.LENGTH_LONG).show();
            	}
            }
	}
	catch(Exception e)
	{
            Log.e("Fallo 3", e.toString());
	}
	Intent i = new Intent(getApplicationContext(), ConfSensor.class);
	i.putExtra("habitacion" , habitacion);
	i.putExtra("codigo_habitacion" , cod_hab);
	i.putExtra("botonPrincipal", botonPrincipal);
    startActivity(i);

    // closing this screen
    finish();
   }
    
    @Override
	public void onBackPressed() {
		Intent inten = new Intent();
		inten.setClass(this,ConfSensor.class);	
		inten.putExtra("habitacion" , habitacion);
		inten.putExtra("codigo_habitacion" , cod_hab);
		inten.putExtra("botonPrincipal", botonPrincipal);
		startActivity(inten);	
		finish();
		
	}
}
