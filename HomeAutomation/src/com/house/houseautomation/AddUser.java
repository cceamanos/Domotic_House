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


public class AddUser extends Activity{
	String username;
	String b_login;
	String b_passw;
	InputStream is=null;
	String result=null;
	String line=null;
	int code;
	
	String usuario;
 
    // Php para crear nuevo usuario
    //private static String url_create_user = "http://domoticahouse.no-ip.biz/create_user.php";
    //String url_create_user = Constantes.PHP_CREATE_USER;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user);
        
        final EditText nombre=(EditText) findViewById(R.id.inputName);
        final EditText login=(EditText) findViewById(R.id.inputLogin);
        final EditText passw=(EditText) findViewById(R.id.inputPassword);
        Button insert=(Button) findViewById(R.id.btnCreateUser);
        
        Globals g = Globals.getInstance();
	    usuario = g.getData();
        
        insert.setOnClickListener(new View.OnClickListener() {
			
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
				
			username= nombre.getText().toString();
			b_login = login.getText().toString();
			b_passw = passw.getText().toString();
			
			insert();
		}
	});
    }
 
    public void insert()
    {
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
 
    	nameValuePairs.add(new BasicNameValuePair("username",username));
    	nameValuePairs.add(new BasicNameValuePair("login",b_login));
    	nameValuePairs.add(new BasicNameValuePair("passw",b_passw));
    	
    	try
    	{
    		HttpClient httpclient = new DefaultHttpClient();
    		
    		// Obtenemos la dirección del servidor php
		    Resources res = this.getResources();
		    String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
		    String url_create_user=lDirecciones[1];
		    
	        HttpPost httppost = new HttpPost(url_create_user);
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost); 
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	        Log.e("Paso 1", "Conexion OK ");
	        Intent i = new Intent(getApplicationContext(), ConfUsers.class);
	        i.putExtra("username",usuario);
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
            	Toast.makeText(getBaseContext(), "Usuario añadido",
            	Toast.LENGTH_SHORT).show();
            }
            else{ 
            	if(code==2){
            		Toast.makeText(getBaseContext(), "El usuario ya existe",
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
		inten.setClass(this,ConfUsers.class);	
		startActivity(inten);	
		finish();
	}

 
    
}
