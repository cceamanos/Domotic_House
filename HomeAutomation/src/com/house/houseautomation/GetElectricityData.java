package com.house.houseautomation;

import java.io.IOException;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;


import org.apache.http.HttpResponse; 
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient; 
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet; 
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient; 
import org.apache.http.util.EntityUtils; 

import android.app.Activity; 
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle; 
import android.util.Log; 
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GetElectricityData extends Activity { 
	//private ListView lstDatosElectricidad; 
	
	String noFunciona = "0";
	public static final int HDR_POS1 = 0;
	public String cabecera = "Precio de la luz de ";
	String otro="0",cod_disp="",est_disp="",tipo_disp="",desc_disp="";
	String botonPrincipal="",cod_hab="",habitacion="";
	public String fecha ="";
    String [] datosLuzHoras = new String[100];
    String [] datosLuzHorasOtro = new String[100];
    public static final String[] horasDia = { "0 h", "1 h", "2 h", "3 h", "4 h", "5 h","6 h","7 h","8 h","9 h","10 h","11 h","12 h",
        "13 h", "14 h", "15 h","16 h","17 h","18 h","19 h","20 h","21 h","22 h","23 h","24 h" };

    private static final Integer LIST_HEADER = 0;
    private static final Integer LIST_ITEM = 0; 
    
    int posicionMasBarata = 0;
    int posicionMasCara = 0;
    double valoresLuz[] = new double[24];
    String Aux;
    String tomorrow ="";
    
    private int hour;
	private int minute;
	private int day;
	private int month;
	private int year;
	HttpClient Client = new DefaultHttpClient();
	String Content;
	String Error = null;
	
	 @Override 
	 public void onCreate(Bundle savedInstanceState) { 
		 super.onCreate(savedInstanceState); 
		 setContentView(R.layout.recibir_electricidad);	 
		 
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
  		   	tomorrow = bundle.getString("tomorrow");
  		}
		 
		 for (int i=0;i<24;i++)
		 {
			 datosLuzHoras[i]="0.0";
		 }
	       
	     try {
         	
         	// Call long running operations here (perform background computation)
         	// NOTE: Don't call UI Element here.
         	
         	// Server url call by GET method
	    	 final Calendar c = Calendar.getInstance();
	    	 day = c.get(Calendar.DAY_OF_MONTH);
			 month = c.get(Calendar.MONTH);
			 month = month +1;
			 year = c.get(Calendar.YEAR);
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
            	 datosLuzHorasOtro[j]=tokens[i];
            	 i=i+6;
             }
	     }
             
         catch (ClientProtocolException e) {
             Error = e.getMessage();
             
         } catch (IOException e) {
             Error = e.getMessage();
             
         }
	     
	if (noFunciona.equals("0"))
	{
		// Obtenemos la dirección del servidor php
		Resources res = this.getResources();
		String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
		String url_datosluz=lDirecciones[4];
		
		if (tomorrow.equals("1"))
		{
			final Calendar c2 = Calendar.getInstance();
			hour = c2.get(Calendar.HOUR_OF_DAY);
			minute = c2.get(Calendar.MINUTE);
			day = c2.get(Calendar.DAY_OF_MONTH);
			month = c2.get(Calendar.MONTH);
			month = month +1;
			year = c2.get(Calendar.YEAR);
			StringBuilder Fecha = new StringBuilder().append(pad(year))
					.append(pad(month)).append(pad(day+1));
			url_datosluz=url_datosluz+ "?date=" + Fecha;
			Fecha = new StringBuilder().append(pad(day+1))
					.append("-").append(pad(month))
					.append("-").append(pad(year));
			fecha = Fecha+ " ";
		}
		
		HttpClient httpClient = new DefaultHttpClient(); 
		HttpGet del = new HttpGet(url_datosluz); 
		del.setHeader("content-type", "application/json"); 	 
		 
		 
		 try 
		 { 
			 HttpResponse resp = httpClient.execute(del); 
			 String respStr = EntityUtils.toString(resp.getEntity()); 
			 JSONObject jsonObject = new JSONObject(respStr);
			 
			 //String formateddate = jsonObject.getString("formated-date"); 
			 //fecha = formateddate;
			 
			 JSONArray respJSON = new JSONArray(jsonObject.getString("normal"));
			 
			 for(int i=0; i<respJSON.length(); i++) 
			 { 			 
				 datosLuzHoras[i] = respJSON.getString(i);
				 Aux = datosLuzHoras[i];
				 Aux=Aux.replace(',','.');
				 Double fromString = new Double(Aux);
				 valoresLuz[i]= fromString;
				 
			 }
				 
	     }
	
	     catch(Exception ex) 
	     { 
	    	 Log.e("ServicioRest","Error!", ex); 
	     }
	}	 
		 if (datosLuzHoras[0].equals("0.0"))
		 {
			 datosLuzHoras = datosLuzHorasOtro;
			 otro = "1";
			 for (int i=0;i<24;i++)
			 {
				 Aux = datosLuzHoras[i];
				 Aux=Aux.replace(',','.');
				 Double fromString = new Double(Aux);
				 valoresLuz[i]= fromString;
			 }
		 }
		 double min = valoresLuz[0];
		 int num = 24;
		 for (int i = 1; i < num; i++)
		 {
			    if (valoresLuz[i] < min) 
			    {
				  min = valoresLuz[i];
				  posicionMasBarata=i;
				}
		 }
		 Log.d("La posicion mas barata es:",String.valueOf(posicionMasBarata));
		 double max = valoresLuz[0]; 
		 for (int i = 1; i < num; i++)
		 {
		    if (valoresLuz[i] > max)
		    {
			  max = valoresLuz[i]; 
			  posicionMasCara = i;
		    }
		 }
		 Log.d("La posicion mas cara es:",String.valueOf(posicionMasCara));
		 ListView lv = (ListView)findViewById(R.id.listView1);
		 lv.setAdapter(new MyListAdapter(this));
	   } 
	 
	 private class MyListAdapter extends BaseAdapter {
	        public MyListAdapter(Context context) {
	            mContext = context;
	        }

	        @Override
	        public int getCount() {
	            return 25;
	        }

	        @Override
	        public boolean areAllItemsEnabled() {
	            return true;
	        }

	        @Override
	        public boolean isEnabled(int position) {
	            return true;
	        }

	        @Override
	        public Object getItem(int position) {
	            return position;
	        }

	        @Override
	        public long getItemId(int position) {
	            return position;
	        }

	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {

	            String headerText = getHeader(position);
	            if(headerText != null) {

	                View item = convertView;
	                if(convertView == null || convertView.getTag() == LIST_ITEM) {

	                    item = LayoutInflater.from(mContext).inflate(
	                            R.layout.lv_header_layout, parent, false);
	                    item.setTag(LIST_HEADER);

	                }

	                TextView headerTextView = (TextView)item.findViewById(R.id.lv_list_hdr);
	                headerTextView.setText(headerText);
	                return item;
	            }

	            View item = convertView;
	            if(convertView == null || convertView.getTag() == LIST_HEADER) {
	                item = LayoutInflater.from(mContext).inflate(
	                        R.layout.lv_layout_electricity, parent, false);
	                item.setTag(LIST_ITEM);
	            }
	            TextView header = (TextView)item.findViewById(R.id.lv_item_header);
	            if (otro.equals("1"))
	            {
	                   	header.setText(horasDia[position]);
	            }
	            else 
	            {
	            		header.setText(horasDia[position-1]);
	            }

	            TextView subtext = (TextView)item.findViewById(R.id.lv_item_subtext);
	            if (otro.equals("1"))
	            {
	            	
	            	subtext.setText(datosLuzHoras[position-1]+"€ Mw/h");
	            }
	            else{
	            	subtext.setText(datosLuzHoras[position-1]+"€ Kw/h");
	            }
	            if ((position-1)==posicionMasCara)
	            {
	            	header.setTextColor(Color.RED);
	            	subtext.setTextColor(Color.RED);
	            }
	            if ((position-1)==posicionMasBarata)
	            {
	            	header.setTextColor(Color.GREEN);
	            	subtext.setTextColor(Color.GREEN);
	            }       

	            return item;
	        }

	        private String getHeader(int position) {

	            if(position == HDR_POS1 ) {
	            	String result = cabecera + fecha;
	                return result;
	            }

	            return null;
	        }

	        private final Context mContext;
	    }
	 
	 private static String pad(int c) {
			if (c >= 10)
			   return String.valueOf(c);
			else
			   return "0" + String.valueOf(c);
		}
	 
	 @Override
		public void onBackPressed() {
			Intent i = new Intent();
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
