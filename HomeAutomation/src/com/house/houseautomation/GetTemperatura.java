package com.house.houseautomation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class GetTemperatura extends Activity{
	
	public String habitacion="";
	public String cod_hab="";
	String botonPrincipal="";
	Httppostaux post;
	String valor_temperatura="";
	String valor_humedad="";
	String[] valor_hist = new String[100];
	
	private int hour;
	private int minute;
	private int second;
	private int day;
	private int month;
	private int year;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_temperature);
        
        post=new Httppostaux();
        
     // Recupero los datos que me remite la actividad HabitacionLuces
     		Intent intent = getIntent();
     		Bundle bundle = intent.getExtras();
     		if ( bundle != null ) {
     		   	cod_hab = bundle.getString("codigo_habitacion");
     		   	habitacion = bundle.getString("habitacion");
     		   	botonPrincipal = bundle.getString("botonPrincipal");
     		}
        
        TextView headerTemperature = (TextView)this.findViewById(R.id.cabecera);
        headerTemperature.setText("La temperatura en " + habitacion +" es:");
        
        TextView headerHumidity = (TextView)this.findViewById(R.id.cabecerahumedad);
        headerHumidity.setText("La humedad en " + habitacion +" es:");
        
        ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
 		
		postparameters2send.add(new BasicNameValuePair("cod_hab",cod_hab));
		postparameters2send.add(new BasicNameValuePair("tip_sen","1"));
        
        // Obtenemos la dirección del servidor php
     	Resources res = this.getResources();
     	String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
     	String URL_connect=lDirecciones[7];
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
     					 valor_temperatura = obj.getString("VAL_SEN"); 
     				}
     				
     			} 
     	 }
     	 catch(Exception ex) 
     	 { 
     	   Log.e("ServicioRest","Error!", ex); 
     	 }
     	
     	 TextView valueTemperature = (TextView)this.findViewById(R.id.valortemperatura);
         valueTemperature.setText(valor_temperatura+"ºC");
     	
         ArrayList<NameValuePair> postparameters2send2= new ArrayList<NameValuePair>();
  		
 		 postparameters2send2.add(new BasicNameValuePair("cod_hab",cod_hab));
 		 postparameters2send2.add(new BasicNameValuePair("tip_sen","2"));
     	
 		 try
    	 { 
    		//realizamos una peticion y como respuesta obtienes un array JSON
    		JSONArray jdata= post.getserverdata(postparameters2send2, URL_connect);

    			//si lo que obtuvimos no es null
    			if (jdata!=null && jdata.length() > 0)
    			{
    				for(int i=0; i<jdata.length(); i++) 
    				{ 
    					 JSONObject obj = jdata.getJSONObject(i); 
    					 valor_humedad = obj.getString("VAL_SEN"); 
    				}
    				
    			} 
    	 }
    	 catch(Exception ex) 
    	 { 
    	   Log.e("ServicioRest","Error!", ex); 
    	 }
 		 
 		TextView valueHumedad = (TextView)this.findViewById(R.id.valorhumedad);
        valueHumedad.setText(valor_humedad+"%");
        
        ArrayList<NameValuePair> postparameters2send3= new ArrayList<NameValuePair>();
 		
		postparameters2send3.add(new BasicNameValuePair("cod_hab",cod_hab));
		postparameters2send3.add(new BasicNameValuePair("tip_sen","1"));
        
		final Calendar c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		second = c.get(Calendar.SECOND);
		day = c.get(Calendar.DAY_OF_MONTH);
		month = c.get(Calendar.MONTH);
		month = month +1;
		year = c.get(Calendar.YEAR);
		
		StringBuilder Fecha = new StringBuilder().append(pad(year))
				.append("-").append(pad(month))
				.append("-").append(pad(day));
		StringBuilder Hora = new StringBuilder().append(pad(hour-5))
				.append(":").append(pad(minute))
				.append(":").append(pad(second));
		String fechaQuery = Fecha+ " "+Hora;
		postparameters2send3.add(new BasicNameValuePair("fecha",fechaQuery));
		
     	URL_connect=lDirecciones[8];
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
     					 valor_hist[i] = obj.getString("VAL_SEN"); 
     				}
     				
     			} 
     	 }
     	 catch(Exception ex) 
     	 { 
     	   Log.e("ServicioRest","Error!", ex); 
     	 }
     	
     	TextView value1 = (TextView)this.findViewById(R.id.tabletextView1);
        value1.setText(pad(hour-4)+"h");
        value1 = (TextView)this.findViewById(R.id.tabletextView2);
        value1.setText(pad(hour-3)+"h");
        value1 = (TextView)this.findViewById(R.id.tabletextView3);
        value1.setText(pad(hour-2)+"h");
        value1 = (TextView)this.findViewById(R.id.tabletextView4);
        value1.setText(pad(hour-1)+"h");
        value1 = (TextView)this.findViewById(R.id.tabletextView5);
        value1.setText(pad(hour)+"h");
     	
     	value1 = (TextView)this.findViewById(R.id.tabletextView6);
        value1.setText(valor_hist[0]+"ºC");
        value1 = (TextView)this.findViewById(R.id.tabletextView7);
        value1.setText(valor_hist[1]+"ºC");
        value1 = (TextView)this.findViewById(R.id.tabletextView8);
        value1.setText(valor_hist[2]+"ºC");
        value1 = (TextView)this.findViewById(R.id.tabletextView9);
        value1.setText(valor_hist[3]+"ºC");
        value1 = (TextView)this.findViewById(R.id.tabletextView10);
        value1.setText(valor_hist[4]+"ºC");
     	 
        ArrayList<NameValuePair> postparameters2send4= new ArrayList<NameValuePair>();
 		
		postparameters2send4.add(new BasicNameValuePair("cod_hab",cod_hab));
		postparameters2send4.add(new BasicNameValuePair("tip_sen","2"));
		postparameters2send4.add(new BasicNameValuePair("fecha",fechaQuery));
		
        try
    	 { 
    		//realizamos una peticion y como respuesta obtienes un array JSON
    		JSONArray jdata= post.getserverdata(postparameters2send4, URL_connect);

    			//si lo que obtuvimos no es null
    			if (jdata!=null && jdata.length() > 0)
    			{
    				for(int i=0; i<jdata.length(); i++) 
    				{ 
    					 JSONObject obj = jdata.getJSONObject(i); 
    					 valor_hist[i] = obj.getString("VAL_SEN"); 
    				}
    				
    			} 
    	 }
    	 catch(Exception ex) 
    	 { 
    	   Log.e("ServicioRest","Error!", ex); 
    	 }
        
        value1 = (TextView)this.findViewById(R.id.tabletextView11);
        value1.setText(valor_hist[0]+"%");
        value1 = (TextView)this.findViewById(R.id.tabletextView12);
        value1.setText(valor_hist[1]+"%");
        value1 = (TextView)this.findViewById(R.id.tabletextView13);
        value1.setText(valor_hist[2]+"%");
        value1 = (TextView)this.findViewById(R.id.tabletextView14);
        value1.setText(valor_hist[3]+"%");
        value1 = (TextView)this.findViewById(R.id.tabletextView15);
        value1.setText(valor_hist[4]+"%");
	}
	
	private static String pad(int c) {
		if (c >= 10)
		   return String.valueOf(c);
		else
		   return "0" + String.valueOf(c);
	}
	
	@Override
	public void onBackPressed() {
		Intent inten = new Intent();
		inten.setClass(this,HabLuz.class);	
		startActivity(inten);	
		finish();
	}
}

