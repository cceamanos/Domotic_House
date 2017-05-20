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
import org.json.JSONArray;
import org.json.JSONObject;

import com.house.houseautomation.library.Httppostaux;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ModifyHabitacion extends Activity {
	String codigo="";
	String nombre="";
	String descripcion="";
	String xbee="";
	InputStream is=null;
	String result=null;
	String line=null;
	int code;
	Httppostaux post;
	String hab_dom,cod_hab,botonPrincipal;
 
    // Php para crear nuevo usuario
    //private static String url_create_user = "http://domoticahouse.no-ip.biz/create_user.php";
    //String url_create_user = Constantes.PHP_CREATE_USER;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_habitacion);
        
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if ( bundle != null ) {
        	hab_dom = bundle.getString("habitacion");
        	cod_hab = bundle.getString("codigo_habitacion");
        	botonPrincipal = bundle.getString("botonPrincipal");
        }
        
        final EditText code=(EditText) findViewById(R.id.inputCode);
        final EditText name=(EditText) findViewById(R.id.inputNombre);
        final EditText desc=(EditText) findViewById(R.id.inputDesc);
        final EditText bee=(EditText) findViewById(R.id.inputBee);
        Button update=(Button) findViewById(R.id.btnCreateHab);
        update.setText("Modificar valores habitación");
        
        post=new Httppostaux();
        
        ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
 		
		postparameters2send.add(new BasicNameValuePair("cod_hab",cod_hab));
        
        // Obtenemos la dirección del servidor php
     	Resources res = this.getResources();
     	String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
     	String URL_connect=lDirecciones[44];
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
     					 nombre = obj.getString("NOM_HAB");
     					 descripcion = obj.getString("DES_HAB");
     					 xbee = obj.getString("ADDR_ZBEE");
     					if (nombre.contains("ny"))
     					{
     						nombre=nombre.replace("ny","ñ");
     					}
     					
     					if (descripcion.contains("ny"))
     					{
     						descripcion=descripcion.replace("ny","ñ");
     					}
     				}
     				
     			} 
     	 }
     	 catch(Exception ex) 
     	 { 
     	   Log.e("ServicioRest","Error!", ex); 
     	 }
        
     	code.setText(cod_hab);
        name.setText(nombre);
        desc.setText(descripcion);
        bee.setText(xbee);
     	
        update.setOnClickListener(new View.OnClickListener() {
			
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
				
			codigo = code.getText().toString();
			nombre = name.getText().toString();
			descripcion = desc.getText().toString();
			xbee = bee.getText().toString();
			
			update();
		}
	});
    }
 
    public void update()
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
    	nameValuePairs.add(new BasicNameValuePair("cod_hab",codigo));
    	nameValuePairs.add(new BasicNameValuePair("nom_hab",nombre));
    	nameValuePairs.add(new BasicNameValuePair("des_hab",descripcion));
    	nameValuePairs.add(new BasicNameValuePair("addr_zbee",xbee));
    	nameValuePairs.add(new BasicNameValuePair("cod_hab_ant",cod_hab));
    	
    	try
    	{
    		HttpClient httpclient = new DefaultHttpClient();
    		
    		// Obtenemos la dirección del servidor php
		    Resources res = this.getResources();
		    String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
		    String url_create_user=lDirecciones[45];
		    
	        HttpPost httppost = new HttpPost(url_create_user);
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost); 
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	        Log.e("Paso 1", "Conexion OK ");
	        Intent inten = new Intent();
	        inten.setClass(this,HabLuz2.class);	
			inten.putExtra("boton", botonPrincipal);
            startActivity(inten);

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
            	Toast.makeText(getBaseContext(), "Espacio actualizado",
            	Toast.LENGTH_SHORT).show();
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
   		inten.setClass(this,HabLuz2.class);	
		inten.putExtra("boton", botonPrincipal);
   		startActivity(inten);	
   		finish();
   	}
}
