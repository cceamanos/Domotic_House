package com.house.houseautomation;

import java.util.ArrayList;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AddHabitacion extends Activity{

	String codigo="";
	String nombre="";
	String descripcion="";
	String xbee="";
	InputStream is=null;
	String result=null;
	String line=null;
	int code;
 
    // Php para crear nuevo usuario
    //private static String url_create_user = "http://domoticahouse.no-ip.biz/create_user.php";
    //String url_create_user = Constantes.PHP_CREATE_USER;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_habitacion);
        
        final EditText code=(EditText) findViewById(R.id.inputCode);
        final EditText name=(EditText) findViewById(R.id.inputNombre);
        final EditText desc=(EditText) findViewById(R.id.inputDesc);
        final EditText bee=(EditText) findViewById(R.id.inputBee);
        Button insert=(Button) findViewById(R.id.btnCreateHab);
        
        insert.setOnClickListener(new View.OnClickListener() {
			
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
				
			codigo = code.getText().toString();
			nombre = name.getText().toString();
			descripcion = desc.getText().toString();
			xbee = bee.getText().toString();
			
			insert();
		}
	});
    }
 
    public void insert()
    {
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	if (nombre.contains("ñ"))
		{
			nombre=nombre.replace("ñ","ny");
		}
		
		if (descripcion.contains("ñ"))
		{
			descripcion=descripcion.replace("ñ","ny");
		}
    	nameValuePairs.add(new BasicNameValuePair("codigo",codigo));
    	nameValuePairs.add(new BasicNameValuePair("nombre",nombre));
    	nameValuePairs.add(new BasicNameValuePair("descripcion",descripcion));
    	nameValuePairs.add(new BasicNameValuePair("xbee",xbee));
    	
    	try
    	{
    		HttpClient httpclient = new DefaultHttpClient();
    		
    		// Obtenemos la dirección del servidor php
		    Resources res = this.getResources();
		    String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
		    String url_create_user=lDirecciones[38];
		    
	        HttpPost httppost = new HttpPost(url_create_user);
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost); 
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	        Log.e("Paso 1", "Conexion OK ");
	        Intent i = new Intent(getApplicationContext(), ConfHabitacion.class);
            startActivity(i);

            // closing this screen
            finish();
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
            	Toast.makeText(getBaseContext(), "Espacio añadido",
            	Toast.LENGTH_SHORT).show();
            }
            else{ 
            	if(code==2){
            		Toast.makeText(getBaseContext(), "El espacio ya existe",
                    Toast.LENGTH_LONG).show();
            	}
            }
	}
	catch(Exception e)
	{
            Log.e("Fallo 3", e.toString());
	}
   }
	
    @Override
	public void onBackPressed() {
		Intent inten = new Intent();
		inten.setClass(this,ConfHabitacion.class);	
		startActivity(inten);	
		finish();
	}
}
