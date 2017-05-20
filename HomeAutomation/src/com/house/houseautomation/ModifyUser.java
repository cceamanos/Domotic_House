package com.house.houseautomation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import org.json.JSONException;
import org.json.JSONObject;

import com.house.houseautomation.library.Httppostaux;
 
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyUser extends Activity {
	String nom_user;
	String b_login;
	String password;
	String user_act;
	InputStream is=null;
	String result=null;
	String line=null;
	int code;
	Httppostaux post;
 
    // Php para crear nuevo usuario
    //private static String url_create_user = "http://domoticahouse.no-ip.biz/create_user.php";
    //String url_create_user = Constantes.PHP_CREATE_USER;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_user);
        
        final EditText nombre=(EditText) findViewById(R.id.inputName);
        final EditText login=(EditText) findViewById(R.id.inputLogin);
        final EditText passw=(EditText) findViewById(R.id.inputPassword);
        Button update=(Button) findViewById(R.id.btnCreateUser);
        
        post=new Httppostaux();
        
        Globals g = Globals.getInstance();
	    user_act = g.getData();
     	
     	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
 		
		postparameters2send.add(new BasicNameValuePair("usuario",user_act));
        
        // Obtenemos la dirección del servidor php
     	Resources res = this.getResources();
     	String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
     	String URL_connect=lDirecciones[41];
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
     					 nom_user = obj.getString("NOM_USER"); 
     					 password = obj.getString("PASSW");
     				}
     				
     			} 
     	 }
     	 catch(Exception ex) 
     	 { 
     	   Log.e("ServicioRest","Error!", ex); 
     	 }
        
     	nombre.setText(nom_user);
		login.setText(user_act);
		passw.setText(password);
     	
        update.setOnClickListener(new View.OnClickListener() {
			
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
				
			nom_user= nombre.getText().toString();
			b_login = login.getText().toString();
			password = passw.getText().toString();
			
			update();
		}
	});
    }
 
    public void update()
    {
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
 
    	nameValuePairs.add(new BasicNameValuePair("username",nom_user));
    	nameValuePairs.add(new BasicNameValuePair("login",b_login));
    	nameValuePairs.add(new BasicNameValuePair("passw",password));
    	nameValuePairs.add(new BasicNameValuePair("user_act",user_act));
    	
    	try
    	{
    		HttpClient httpclient = new DefaultHttpClient();
    		
    		// Obtenemos la dirección del servidor php
		    Resources res = this.getResources();
		    String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
		    String url_create_user=lDirecciones[42];
		    
	        HttpPost httppost = new HttpPost(url_create_user);
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost); 
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	        Log.e("Paso 1", "Conexion OK ");
	        Intent i = new Intent(getApplicationContext(), ConfUsers.class);
	        user_act = b_login;
	        Globals g = Globals.getInstance();
            g.setData(b_login);
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
            	Toast.makeText(getBaseContext(), "Usuario modificado",
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
		inten.setClass(this,ConfUsers.class);	
		inten.putExtra("username",user_act);
		startActivity(inten);	
		finish();
	}
}
