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

public class AddDispositivo extends Activity {
	
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
        setContentView(R.layout.add_dispositivo);
        
        hashMap.put("Lavadora","81");
        hashMap.put("Lavavajillas","82");
        hashMap.put("CalientaAgua","83");
        hashMap.put("Cafetera","84");
        hashMap.put("Microondas","85");
        hashMap.put("Otro conectado","88");
        hashMap.put("Ventilador","87");
        hashMap.put("Equipo musica","86");
        hashMap.put("Luz","20");
        hashMap.put("Persiana","10"); 
        hashMap.put("Valvula de gas","1"); 
        hashMap.put("Valvula de agua","3"); 
        hashMap.put("Valvula de riego","4");
        hashMap.put("Calefaccion","5");
   
        
        
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
        estIni=(Spinner) findViewById(R.id.spinner3);
        Button insert=(Button) findViewById(R.id.btnCreateDisp);
        
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
			estadoIni= estIni.getSelectedItem().toString();
			
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
    	nameValuePairs.add(new BasicNameValuePair("estadoini",estadoIni));
    	
    	;
    	
    	try
    	{
    		HttpClient httpclient = new DefaultHttpClient();
    		
    		// Obtenemos la dirección del servidor php
		    Resources res = this.getResources();
		    String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
		    String url_create_user=lDirecciones[39];
		    
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
            	Toast.makeText(getBaseContext(), "Dispositivo añadido",
            	Toast.LENGTH_SHORT).show();
            }
            else{ 
            	if(code==2){
            		Toast.makeText(getBaseContext(), "El dispositivo ya existe",
                    Toast.LENGTH_LONG).show();
            	}
            }
	}
	catch(Exception e)
	{
            Log.e("Fallo 3", e.toString());
	}
	Intent i = new Intent(getApplicationContext(), ConfDispositivo.class);
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
		inten.setClass(this,ConfDispositivo.class);	
		inten.putExtra("habitacion" , habitacion);
		inten.putExtra("codigo_habitacion" , cod_hab);
		inten.putExtra("botonPrincipal", botonPrincipal);
		startActivity(inten);	
		finish();
		
	}
}
