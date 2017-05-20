package com.house.houseautomation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Message;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.DatePicker;
import com.house.houseautomation.DatePickerFragment;

public class ProgramaPersiana extends FragmentActivity{
	String b_descripcion;
	String b_hora;
	String b_onoff = "1";
	
	String habitacion="",botonPrincipal="";
	
	String tipo_disp="";
    String cod_hab="";
    String est_disp="";
    String cod_disp="";
    
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
	
	private SeekBar persControl = null;
	int progressChanged=0;
	
	int mHour = 15;
    int mMinute = 15;
 
    private RadioGroup radioGrupo;
    String desc_disp="";
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.programa_persiana);
        
        
        final EditText descripcion=(EditText) findViewById(R.id.inputDescProg);
        fecha=(EditText) findViewById(R.id.inputFechProg);
        fecha.setEnabled(false);
        hora=(EditText) findViewById(R.id.inputHoraProg);
        hora.setEnabled(false);    
        Button cambiaHora=(Button) findViewById(R.id.buttonHora);
        Button cambiaFecha=(Button) findViewById(R.id.buttonFecha);
        Button insert=(Button) findViewById(R.id.btnCreateEvento);
        
        final Calendar c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		day = c.get(Calendar.DAY_OF_MONTH);
		month = c.get(Calendar.MONTH);
		month = month +1;
		year = c.get(Calendar.YEAR);
		String fechaHoy ="";
		String fechamany="";
    
		Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if ( bundle != null ) {
        	tipo_disp = bundle.getString("tipo_disp");
        	cod_hab = bundle.getString("cod_habitacion");
        	cod_disp = bundle.getString("cod_disp");
        	est_disp = bundle.getString("est_disp"); 
        	habitacion = bundle.getString("habitacion");
        	botonPrincipal = bundle.getString("botonPrincipal");
        	desc_disp = bundle.getString("descripcion_dispositivo");
        }
        
        persControl = (SeekBar) findViewById(R.id.seekBarPersianas);
		 
	    persControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	 
	            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
	                progressChanged = progress;
	            }
	 
	            public void onStartTrackingTouch(SeekBar seekBar) {
	                // TODO Auto-generated method stub
	            }
	 
	            public void onStopTrackingTouch(SeekBar seekBar) {
	                Toast.makeText(ProgramaPersiana.this,"Persiana "+progressChanged+"%", 
	                        Toast.LENGTH_SHORT).show();
	            }
	        });
        
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
    	nameValuePairs.add(new BasicNameValuePair("cod_disp",cod_disp));
    	nameValuePairs.add(new BasicNameValuePair("cod_hab",cod_hab)); 	
    	nameValuePairs.add(new BasicNameValuePair("val_aux",Integer.toString(progressChanged)));
    	
    	try
    	{
    		HttpClient httpclient = new DefaultHttpClient();
    		
    		// Obtenemos la dirección del servidor php
		    Resources res = this.getResources();
		    String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
		    String url_evento_programado=lDirecciones[37];
		    
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
	
    Intent i = new Intent(getApplicationContext(), SetPersianas.class);
    i.putExtra("codigo_habitacion", cod_hab);
    i.putExtra("tipo_dispositivo",tipo_disp);
    i.putExtra("codigo_dispositivo",cod_disp);
    i.putExtra("estado_dispositivo",est_disp);
    i.putExtra("habitacion", habitacion);
    i.putExtra("botonPrincipal", botonPrincipal);
    i.putExtra("descripcion_dispositivo", desc_disp);
    startActivity(i);
    
    finish();
		  
	  
	
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
	           Intent i = new Intent(getApplicationContext(), SetPersianas.class);
	           i.putExtra("codigo_habitacion", cod_hab);
	           i.putExtra("tipo_dispositivo",tipo_disp);
		       i.putExtra("codigo_dispositivo",cod_disp);
		       i.putExtra("estado_dispositivo",est_disp);
		       i.putExtra("habitacion", habitacion);
		       i.putExtra("botonPrincipal", botonPrincipal);
		       i.putExtra("descripcion_dispositivo", desc_disp);
	           startActivity(i);
	           finish();
    	    }
}
