package com.house.houseautomation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Message;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.DatePicker;
import com.house.houseautomation.DatePickerFragment;

public class ProgramaGas extends FragmentActivity{
	String b_descripcion="";
	String b_hora="";
	String b_onoff = "1";
	
	String tipo_disp="";
    String cod_hab="";
    String est_disp="";
    String cod_disp="";
    String desc_disp="";
    String habitacion="";
    String botonPrincipal="";
    
	InputStream is=null;
	String result=null;
	String line=null;
	int code;
	EditText fecha;
	EditText hora;
	
	private int hour;
	private int minute;
	private int day;
	private int month;
	private int year;
	private String dateSql;
	static final int TIME_DIALOG_ID = 200;
	
	int mHour = 15;
    int mMinute = 15;
    
    String [] datosLuzHoras = new String[100];
    public static final String[] horasDia = { "0 h", "1 h", "2 h", "3 h", "4 h", "5 h","6 h","7 h","8 h","9 h","10 h","11 h","12 h",
        "13 h", "14 h", "15 h","16 h","17 h","18 h","19 h","20 h","21 h","22 h","23 h","24 h" };
 
    double valoresLuz[] = new double[24];
    int posicionMasBarata = 0;
    int posicionMasCara = 0;
    String url_datosluzmany="";
    
    private RadioGroup radioGrupo;
    
    HttpClient Client = new DefaultHttpClient();
	String Content;
	String Error = null;
	
	//Variable que marca que han solucionado el tema de la web
	String arreglado = "1";
    
    // Php para crear nuevo usuario
    //private static String url_evento_programado = "http://domoticahouse.no-ip.biz/add_evento.php";
    //String url_create_user = Constantes.PHP_ADD_EVENTO;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.programa_gas);
        
        
        final EditText descripcion=(EditText) findViewById(R.id.inputDescProg);
        fecha=(EditText) findViewById(R.id.inputFechProg);
        fecha.setEnabled(false);
        hora=(EditText) findViewById(R.id.inputHoraProg);
        hora.setEnabled(false);
        RadioButton r1, r2;
        r1 = (RadioButton) findViewById(R.id.radioButtonOn);
        r1 = (RadioButton) findViewById(R.id.radioButtonOff);
        Button cambiaHora=(Button) findViewById(R.id.buttonHora);
        Button cambiaFecha=(Button) findViewById(R.id.buttonFecha);
        Button insert=(Button) findViewById(R.id.btnCreateEvento);
        radioGrupo = (RadioGroup)findViewById(R.id.rdgGrupo);
        TextView horaRecomendada = (TextView) findViewById(R.id.textHoraRecomendada);
        TextView textHoraRecomendadaManya = (TextView) findViewById(R.id.textHoraRecomendadaManya);
        
        final Calendar c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		day = c.get(Calendar.DAY_OF_MONTH);
		month = c.get(Calendar.MONTH);
		month = month +1;
		year = c.get(Calendar.YEAR);
		String fechaHoy ="";
		String fechamany="";
    
		// Recupero los datos que me remite la actividad HabitacionLuces
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if ( bundle != null ) {
		   	cod_disp = bundle.getString("codigo_dispositivo");
		   	est_disp = bundle.getString("estado_dispositivo");
		   	desc_disp = bundle.getString("descripcion_dispositivo");
		   	tipo_disp = bundle.getString("tipo_dispositivo");
		   	cod_hab = bundle.getString("codigo_habitacion");
		   	habitacion = bundle.getString("habitacion");
		   	botonPrincipal = bundle.getString("botonPrincipal");
		}
        
        
		// set current time into textview
		hora.setText(
                    new StringBuilder().append(pad(hour))
                                       .append(":").append(pad(minute)));
		
		fecha.setText(
				new StringBuilder().append(pad(day))
					.append("/").append(pad(month))
					.append("/").append(pad(year)));
		
		dateSql = year + "-" + month + "-" + day;
		
		// Obtenemos la dirección del servidor php
		Resources res = this.getResources();
		String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
		String url_datosluz=lDirecciones[4];
				
		if (hour>21)
		{
			StringBuilder Fecha = new StringBuilder().append(pad(year))
							.append(pad(month)).append(pad(day+1));
			url_datosluzmany=url_datosluz+ "?date=" + Fecha;
			Fecha = new StringBuilder().append(pad(day+1))
							.append("-").append(pad(month))
							.append("-").append(pad(year));
			fechamany = Fecha+ " ";
		}
		
		// Metemos los datos de la nueva web
		for (int i=0;i<24;i++)
		 {
			 datosLuzHoras[i]="0.0";
		 }
	       
	     try {
        	
        	// Call long running operations here (perform background computation)
        	// NOTE: Don't call UI Element here.
        	
        	// Server url call by GET method
	    	 
	    	String URL = "http://www.omie.es/datosPub/marginalpdbc/marginalpdbc_";
	    	String URL_total = URL+year+pad(month)+pad(day)+".1";
            HttpGet httpget = new HttpGet(URL_total);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            Content = Client.execute(httpget, responseHandler);
            String delims = "[;]";
            String[] tokens = Content.split(delims);
            for (int i=0;i<tokens.length;i++)
            {
            	Log.d("El dato es: ",tokens[i]);
            }
            int j=0;
            for (int i=5;j<24;j++)
            {
           	 datosLuzHoras[j]=tokens[i];
           	 i=i+6;
            }
	     }
            
        catch (ClientProtocolException e) {
            Error = e.getMessage();
            
        } catch (IOException e) {
            Error = e.getMessage();
            
        }
	     
	    // Obtenemos los valores de la luz en double
	    for (int i=0;i<24;i++)
		{
			 String Aux = datosLuzHoras[i];
			 Aux=Aux.replace(',','.');
			 Double fromString = new Double(Aux);
			 valoresLuz[i]= fromString;
		}
	    
	     Double min = valoresLuz[hour-1];
		 int num = 24;
		 posicionMasBarata = hour-1;
		 for (int i = 1; i < num; i++)
		 {
			 if (i>=hour)
			 {
			    if (valoresLuz[i-1] < min) 
			    {
				  min = valoresLuz[i];
				  posicionMasBarata=i;
				}
			 }
		 }
		 Log.d("La posicion mas barata es:",String.valueOf(posicionMasBarata));
		
		fechaHoy=""+
				new StringBuilder().append(pad(day))
					.append("/").append(pad(month))
					.append("/").append(pad(year));
		
		horaRecomendada.setText(horasDia[posicionMasBarata+1]+" " +fechaHoy);
		horaRecomendada.setTextColor(Color.parseColor("#33CC33"));
		
		textHoraRecomendadaManya.setVisibility(View.INVISIBLE);
	
		if (tipo_disp.equals("1")||tipo_disp.equals("3")||tipo_disp.equals("4"))
		{
			textHoraRecomendadaManya.setVisibility(View.INVISIBLE);
			horaRecomendada.setVisibility(View.INVISIBLE);
			TextView tituloHora = (TextView) findViewById(R.id.textHoraRec);
			tituloHora.setVisibility(View.INVISIBLE);
		}
		
    if (arreglado.equals("1"))
    {
		HttpClient httpClient = new DefaultHttpClient(); 
		HttpGet del = new HttpGet(url_datosluz); 
		del.setHeader("content-type", "application/json"); 	 
				 
				 
		try 
		 { 
			 HttpResponse resp = httpClient.execute(del); 
			 String respStr = EntityUtils.toString(resp.getEntity()); 
			 JSONObject jsonObject = new JSONObject(respStr);
			 
			 String formateddate = jsonObject.getString("formated-date"); 
			 fechaHoy = formateddate;
			 
			 JSONArray respJSON = new JSONArray(jsonObject.getString("normal"));
			 
			 for(int i=0; i<respJSON.length(); i++) 
			 { 			 
				 datosLuzHoras[i] = respJSON.getString(i);
				 String Aux = datosLuzHoras[i];
				 Aux=Aux.replace(',','.');
				 Double fromString = new Double(Aux);
				 valoresLuz[i]= fromString;
				 
			 }
				 
	     } 
	     catch(Exception ex) 
	     { 
	    	 Log.e("ServicioRest","Error!", ex); 
	     } 
		 min = valoresLuz[hour];
		 posicionMasBarata=hour;
		 num = 24;
		 for (int i = 1; i < num; i++)
		 {
			 if (i>=hour)
			 {
			    if (valoresLuz[i] < min) 
			    {
				  min = valoresLuz[i];
				  posicionMasBarata=i;
				}
			 }
		 }
		 Log.d("La posicion mas barata es:",String.valueOf(posicionMasBarata));
		
		horaRecomendada.setText(horasDia[posicionMasBarata]+" " +fechaHoy);
		horaRecomendada.setTextColor(Color.parseColor("#33CC33"));
		
		if (hour>21)
		{
			httpClient = new DefaultHttpClient(); 
			del = new HttpGet(url_datosluzmany); 
			del.setHeader("content-type", "application/json"); 
			
			try 
			 { 
				 HttpResponse resp = httpClient.execute(del); 
				 String respStr = EntityUtils.toString(resp.getEntity()); 
				 JSONObject jsonObject = new JSONObject(respStr);
				 
				 String formateddate = jsonObject.getString("formated-date"); 
				 fechaHoy = formateddate;
				 
				 JSONArray respJSON = new JSONArray(jsonObject.getString("normal"));
				 
				 for(int i=0; i<respJSON.length(); i++) 
				 { 			 
					 datosLuzHoras[i] = respJSON.getString(i);
					 String Aux = datosLuzHoras[i];
					 Aux=Aux.replace(',','.');
					 Double fromString = new Double(Aux);
					 valoresLuz[i]= fromString;
					 
				 }
					 
		     } 
		     catch(Exception ex) 
		     { 
		    	 Log.e("ServicioRest","Error!", ex); 
		     } 
			 min = valoresLuz[0];
			 num = 24;
			 posicionMasBarata=hour+1;
			 for (int i = 0; i < num; i++)
			 {
				if (valoresLuz[i] < min) 
				{
				  min = valoresLuz[i];
				  posicionMasBarata=i;
				}
			 }
			 Log.d("La posicion mas barata es:",String.valueOf(posicionMasBarata));
			 
			 
			 textHoraRecomendadaManya.setText(horasDia[posicionMasBarata]+" " +fechamany);
			 textHoraRecomendadaManya.setTextColor(Color.parseColor("#33CC33"));
		}
		else {
			textHoraRecomendadaManya.setVisibility(View.INVISIBLE);
		}
		if (tipo_disp.equals("1")||tipo_disp.equals("3")||tipo_disp.equals("4"))
        {
			textHoraRecomendadaManya.setVisibility(View.INVISIBLE);
			horaRecomendada.setVisibility(View.INVISIBLE);
			TextView tituloHora = (TextView) findViewById(R.id.textHoraRec);
			tituloHora.setVisibility(View.INVISIBLE);
			
        }
    }
		radioGrupo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
			 
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
			  if (checkedId == R.id.radioButtonOn){
			            b_onoff = "1";
			  }else if (checkedId == R.id.radioButtonOff){
			            b_onoff = "0";
			        		            
			  }
			         
			}
		});
		
        cambiaFecha.setOnClickListener(new View.OnClickListener() {
     	        	
			@Override
    		public void onClick(View v) {
        		showDatePicker();
    				
       		}
    	});
		
        cambiaHora.setOnClickListener(new View.OnClickListener() {
	        	
			@Override
    		public void onClick(View v) {
        		showTimePicker();
    				
       		}
    	});
        
        insert.setOnClickListener(new View.OnClickListener() {
			
		@Override
		public void onClick(View v) {
			
			b_descripcion = descripcion.getText().toString();
			b_hora = hora.getText().toString() + ":00";
			insert();
		}
	});
    }
 
    public void insert()
    {
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
 
    	
    	nameValuePairs.add(new BasicNameValuePair("fecha",dateSql));
    	nameValuePairs.add(new BasicNameValuePair("descripcion",b_descripcion));
    	Log.d("La hora que remito es: ",b_hora);
    	nameValuePairs.add(new BasicNameValuePair("hora",b_hora));
    	nameValuePairs.add(new BasicNameValuePair("estado",b_onoff));
    	nameValuePairs.add(new BasicNameValuePair("cod_disp",cod_disp));
    	nameValuePairs.add(new BasicNameValuePair("cod_hab",cod_hab));
    	
    	
    	
    	try
    	{
    		HttpClient httpclient = new DefaultHttpClient();
    		
    		// Obtenemos la dirección del servidor php
		    Resources res = this.getResources();
		    String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
		    String url_evento_programado=lDirecciones[2];
		    
	        HttpPost httppost = new HttpPost(url_evento_programado);
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost); 
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	        Log.e("Paso 1", "Conexion OK ");

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
            	Toast.makeText(getBaseContext(), "Evento añadido",
            	Toast.LENGTH_SHORT).show();
            }
            else{ 
            	if(code==2){
            		Toast.makeText(getBaseContext(), "El evento ya existe",
                    Toast.LENGTH_LONG).show();
            	}
            }
	}
	catch(Exception e)
	{
            Log.e("Fallo 3", e.toString());
	}
	Intent i = new Intent();
	if (tipo_disp.equals("1")){
		i.setClass(this,OpenCloseGas.class);
		startActivity(i);	
		finish();
	}
	else if (tipo_disp.equals("3")){
		i.setClass(this,OpenCloseAgua.class);
		startActivity(i);	
		finish();
	  } else if (tipo_disp.equals("4")){
		  i.setClass(this,OpenCloseRiego.class);
		  startActivity(i);	
		  finish(); 
	  } else {
		  i.setClass(this,Dispositivo.class);	
		  i.putExtra("codigo_dispositivo" , cod_disp);
		  i.putExtra("estado_dispositivo" , est_disp);
		  i.putExtra("descripcion_dispositivo", desc_disp);
		  i.putExtra("tipo_dispositivo", tipo_disp);
		  i.putExtra("codigo_habitacion", cod_hab);
		  i.putExtra("habitacion", habitacion);
		  i.putExtra("botonPrincipal",botonPrincipal);
	      startActivity(i);	
		  finish();
		  
	  }
	
   }
    private static String pad(int c) {
		if (c >= 10)
		   return String.valueOf(c);
		else
		   return "0" + String.valueOf(c);
	}
    
    private void showDatePicker() {
    	  DatePickerFragment date = new DatePickerFragment();
    	  
    	  //  Cambia la fecha en el Dialog
    	  
    	  Calendar calender = Calendar.getInstance();
    	  Bundle args = new Bundle();
    	  args.putInt("year", calender.get(Calendar.YEAR));
    	  args.putInt("month", calender.get(Calendar.MONTH));
    	  args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
    	  date.setArguments(args);
    	  
    	   // Set Call back to capture selected date
    	  
    	  date.setCallBack(ondate);
    	  date.show(getSupportFragmentManager(), "Date Picker");
    	 }

    	OnDateSetListener ondate = new OnDateSetListener() {
    	  @Override
    	  public void onDateSet(DatePicker view, int year, int monthOfYear,
    	    int dayOfMonth) {
    		  String diaMes;
    		  String mes;
    		  if (dayOfMonth < 10){
    			  diaMes = "0" + String.valueOf(dayOfMonth);
    		  }
    		  else{
    			  diaMes = String.valueOf(dayOfMonth);
    		  }
    		  if (monthOfYear+1 < 10){
    			  mes = "0" + String.valueOf(monthOfYear+1);
    		  }
    		  else{
    			  mes= String.valueOf(monthOfYear+1);
    		  }
        	  fecha.setText(diaMes + "/" + mes + "/" + String.valueOf(year));
        	  dateSql = String.valueOf(year) + "-" + mes 
        			  + "-" + diaMes;
        	  Log.d("El valor de dateSql es: ", dateSql);
    	  }
    	 };

    	 private void showTimePicker() {
    		 // Creando el objeto bundle para pasar a cambiar la hora al fragment
             Bundle b = new Bundle();
             // Añadiendo la hora actual al objeto bundle
             b.putInt("set_hour", hour);
             // Añadiendo los minutos actuales al objeto bundle
             b.putInt("set_minute", minute);

             TimePickerFragment timePicker = new TimePickerFragment(mHandler);

             // Cambiamos argumentos del objeto
             timePicker.setArguments(b);

             FragmentManager fm = getSupportFragmentManager();
             // Comenzamos la transacción
             FragmentTransaction ft = fm.beginTransaction();
             // Añadiendo el fragment a la transaccion
             ft.add(timePicker, "time_picker");
             // Abriendo el objeto
             ft.commit(); 
    	 }
    	 
    	 // Esto maneja el mensaje enviado desde TimePickerFragment al cambiar la hora
    	 Handler mHandler = new Handler(){
    	        @Override
    	        public void handleMessage(Message m){
    	        	String hand_min;
    	    		String hand_hour;
    	            // Creando el objeto bundel para pasarle los datos al fragment
    	            Bundle b = m.getData();
    	            
    	            hour = b.getInt("set_hour");
    	            minute = b.getInt("set_minute");
    	 
    	            // Añadimos un 0 cuando es menor la cifra que 10
    	            if (minute < 10){
    	    			  hand_min = "0" + minute;
    	    		}
    	    		else{
    	    			  hand_min = "" + minute;
    	    		}
    	    		if (hour < 10){
    	    			  hand_hour = "0" + hour;
    	    		  }
    	    		  else{
    	    			  hand_hour = "" + hour;
    	    		  }
    	            hora.setText(hand_hour + ":" + hand_min);
    	            //Toast.makeText(getBaseContext(), b.getString("set_time"), Toast.LENGTH_SHORT).show();
    	        }
    	    };
    	    
    	    @Override
    		public void onBackPressed() {
    			Intent i = new Intent();
    			if (tipo_disp.equals("1")){
    				i.setClass(this,OpenCloseGas.class);
    				startActivity(i);	
    				finish();
    			}
    			else if (tipo_disp.equals("3")){
    				i.setClass(this,OpenCloseAgua.class);
    				startActivity(i);	
    				finish();
    			  } else if (tipo_disp.equals("4")){
    				  i.setClass(this,OpenCloseRiego.class);
    				  startActivity(i);	
    				  finish(); 
    			  } else {
    				  i.setClass(this,Dispositivo.class);	
    				  i.putExtra("codigo_dispositivo" , cod_disp);
      				  i.putExtra("estado_dispositivo" , est_disp);
      				  i.putExtra("descripcion_dispositivo", desc_disp);
      				  i.putExtra("tipo_dispositivo", tipo_disp);
      				  i.putExtra("codigo_habitacion", cod_hab);
      				  i.putExtra("habitacion", habitacion);
      				  i.putExtra("botonPrincipal",botonPrincipal);
    			      startActivity(i);	
    				  finish();
    				  
    			  }
    		}
}
