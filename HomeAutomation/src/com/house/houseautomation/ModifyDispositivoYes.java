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

public class ModifyDispositivoYes extends Activity {
	String codigo_dispositivo="";
	String codigo2="",cod1="2",cod2="",descripcion="",tipoDisp="",botonPrincipal="";
	String estado_dispositivo="";
	String nombre="";
	String habitacion="";
	String descripcion_dispositivo="";
	String tipo_dispositivo="";
	String estadoIni="";
	String codigo_habitacion;
	InputStream is=null;
	String result=null;
	String line=null;
	int code;
	Spinner spCode,spTipo,estIni;
	EditText eCode, desc;
	HashMap hashMap = new HashMap(); 
	HashMap hashMap2 = new HashMap(); 
 
    // Php para crear nuevo usuario
    //private static String url_create_user = "http://domoticahouse.no-ip.biz/create_user.php";
    //String url_create_user = Constantes.PHP_CREATE_USER;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_dispositivoyes);
        
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
        
        hashMap2.put("81","Lavadora");
        hashMap2.put("82","Lavavajillas");
        hashMap2.put("83","CalientaAgua");
        hashMap2.put("84","Cafetera");
        hashMap2.put("85","Microondas");
        hashMap2.put("88","Otro conectado");
        hashMap2.put("87","Ventilador");
        hashMap2.put("86","Equipo musica");
        hashMap2.put("20","Luz");
        hashMap2.put("10","Persiana"); 
        hashMap2.put("1","Valvula de gas"); 
        hashMap2.put("3","Valvula de agua"); 
        hashMap2.put("4","Valvula de riego");
        hashMap2.put("5","Calefaccion"); 
        
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
           	botonPrincipal = bundle.getString("boton");
        }
        
        spCode=(Spinner) findViewById(R.id.spinner1);
        eCode=(EditText) findViewById(R.id.editText1);
        desc=(EditText) findViewById(R.id.editText2);
        spTipo=(Spinner) findViewById(R.id.spinner2);
        estIni=(Spinner) findViewById(R.id.spinner3);
        Button insert=(Button) findViewById(R.id.btnCreateDisp);
        
        insert.setText("Modificar dispositivo");
        
        char val = codigo_dispositivo.charAt(0);
        String value = ""+val;
        for (int i=0;i<7;i++)
        {
        	if (value.equals(spCode.getItemAtPosition(i)))
        	{
        		spCode.setSelection(i);
        		
        	}
        }
        
        codigo2=codigo_dispositivo.substring(1);
  
        eCode.setText(codigo2);
        desc.setText(descripcion_dispositivo);
        int index=0;
        for (int i=0;i<spTipo.getCount();i++)
        {
        	   value=(String)hashMap2.get(tipo_dispositivo);
        	   if (spTipo.getItemAtPosition(i).equals(value))
        	   {
        	    index = i;
        	   }
        }
        spTipo.setSelection(index);
        estIni.setSelection(Integer.parseInt(estado_dispositivo));
        
        insert.setOnClickListener(new View.OnClickListener() {
			
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
				
			cod1 = spCode.getSelectedItem().toString();
			cod2 = eCode.getText().toString();
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
 
    
    	nameValuePairs.add(new BasicNameValuePair("codigo",cod1+cod2));
    	nameValuePairs.add(new BasicNameValuePair("descripcion",descripcion));
    	nameValuePairs.add(new BasicNameValuePair("tipodisp",tipoDisp));
    	nameValuePairs.add(new BasicNameValuePair("estadoini",estadoIni));
    	nameValuePairs.add(new BasicNameValuePair("codigo_ini",codigo_dispositivo));
    	
    	;
    	
    	try
    	{
    		HttpClient httpclient = new DefaultHttpClient();
    		
    		// Obtenemos la dirección del servidor php
		    Resources res = this.getResources();
		    String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
		    String url_create_user=lDirecciones[48];
		    
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
            	Toast.makeText(getBaseContext(), "Dispositivo modificado",
            	Toast.LENGTH_SHORT).show();
            }
	}
	catch(Exception e)
	{
            Log.e("Fallo 3", e.toString());
	}
	Intent i = new Intent(getApplicationContext(), ModifyDispositivo.class);
	i.putExtra("habitacion" , habitacion);
	i.putExtra("codigo_habitacion" , codigo_habitacion);
	i.putExtra("botonPrincipal", botonPrincipal);
    startActivity(i);

    // closing this screen
    finish();
   }
    
    @Override
	public void onBackPressed() {
		Intent inten = new Intent();
		inten.setClass(this,ModifyDispositivo.class);	
		inten.putExtra("codigo_habitacion" , codigo_habitacion);
		inten.putExtra("habitacion" , habitacion);
		inten.putExtra("botonPrincipal", botonPrincipal);
		startActivity(inten);
		finish();
		
	}
}
