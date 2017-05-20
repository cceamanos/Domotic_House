package com.house.houseautomation;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import org.apache.http.HttpResponse; 
import org.apache.http.client.HttpClient; 
import org.apache.http.client.methods.HttpGet; 
import org.apache.http.impl.client.DefaultHttpClient; 
import org.apache.http.util.EntityUtils; 

import android.app.Activity; 
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle; 
import android.util.Log; 
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class HabLuz extends Activity {
	public static final int HDR_POS1 = 0;
	public String cabecera = "Habitaciones domotizadas de la casa ";

    private static final Integer LIST_HEADER = 0;
    private static final Integer LIST_ITEM = 0; 
    
    String[] habitaciones = new String[100];
    String[] descripciones = new String[100];
    String[] cod_habitaciones = new String[100];
    int numero_habitaciones = 0;
    String botonPrincipal ="1";
    String username="";
	
	@Override 
	public void onCreate(Bundle savedInstanceState) { 
		 super.onCreate(savedInstanceState); 
		 setContentView(R.layout.hab_luz);	 
		 
		 // Averiguamos quien lo llama
		 Intent intent = getIntent();
	     Bundle bundle = intent.getExtras();
	     if ( bundle != null ) {
	      	botonPrincipal = bundle.getString("boton");
	      	
	     }
		 
	     Globals g = Globals.getInstance();
	     username = g.getData();
	     
		 // Obtenemos la dirección del servidor php
		 Resources res = this.getResources();
		 String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
		 String url_datosluz=lDirecciones[5];
		 
		 HttpClient httpClient = new DefaultHttpClient(); 
		 HttpGet del = new HttpGet(url_datosluz); 
		 del.setHeader("content-type", "application/json"); 	 		 
		 
		 try 
		 { 
			 HttpResponse resp = httpClient.execute(del); 
			 String respStr = EntityUtils.toString(resp.getEntity()); 
			 JSONArray respJSON = new JSONArray(respStr); 
			    
			 for(int i=0; i<respJSON.length(); i++) 
			 { 
				JSONObject obj = respJSON.getJSONObject(i); 
				habitaciones[i] = obj.getString("NOM_HAB");
				if (habitaciones[i].contains("ny"))
				{
					habitaciones[i]=habitaciones[i].replace("ny","ñ");
				}
				descripciones[i] = obj.getString("DES_HAB");
				if (descripciones[i].contains("ny"))
				{
					descripciones[i]=descripciones[i].replace("ny","ñ");
				}
				cod_habitaciones[i] = obj.getString("COD_HAB");
				numero_habitaciones++;
		 
			 } 
		 } 
		 catch(Exception ex) 
		 { 
			    	 Log.e("ServicioHabitaciones","Error!", ex); 
		 }
		 
		 ListView lv = (ListView)findViewById(R.id.listView1);
		 lv.setAdapter(new MyListAdapter(this));		 
		 
	   } 
	 
	 private class MyListAdapter extends BaseAdapter {
	        public MyListAdapter(Context context) {
	            mContext = context;
	        }

	        @Override
	        public int getCount() {
	            return (numero_habitaciones+1);
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
	        public View getView(final int position, View convertView, ViewGroup parent) {

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
	                        R.layout.lv_layout, parent, false);
	                item.setTag(LIST_ITEM);
	            }

	            TextView header = (TextView)item.findViewById(R.id.lv_item_header);
	            header.setText(habitaciones[position-1]);

	            TextView subtext = (TextView)item.findViewById(R.id.lv_item_subtext);
	            subtext.setText(descripciones[position-1]);
	            
	            ImageButton btnGoLights = (ImageButton) item.findViewById(R.id.button);
	            btnGoLights.setOnClickListener(new View.OnClickListener() {
	            	 
	                @Override
	                public void onClick(View view) {
	                    // Launching create new product activity
	                	if (botonPrincipal.equals("1"))
                	  	{
	                		Intent i = new Intent(getApplicationContext(), GetTemperatura.class);
		    				i.putExtra("habitacion" , habitaciones[position-1]);
		    				i.putExtra("codigo_habitacion" , cod_habitaciones[position-1]);
		    				startActivity(i);
		    				finish();
		    				
                	  	}
	                	else 
	                		if (botonPrincipal.equals("2")||botonPrincipal.equals("3"))
	                		{
			                	Intent i = new Intent(getApplicationContext(), HabitacionLuces.class);				                    
					    		i.putExtra("habitacion" , habitaciones[position-1]);
					    		i.putExtra("codigo_habitacion" , cod_habitaciones[position-1]);
					    		i.putExtra("botonPrincipal", botonPrincipal);
					    		startActivity(i);
					    		finish();
					    		
	                		} 
	                		else
	                			if (botonPrincipal.equals("4"))
		                		{	         
				                	Intent i = new Intent(getApplicationContext(), SensoresActuales.class);				                    
						    		i.putExtra("habitacion" , habitaciones[position-1]);
						    		i.putExtra("codigo_habitacion" , cod_habitaciones[position-1]);
						    		i.putExtra("botonPrincipal", botonPrincipal);
						    		startActivity(i);
						    		finish();
						    		
		                		} else 
		                			if (botonPrincipal.equals("5"))
		    		                		{	         
		    				                	Intent i = new Intent(getApplicationContext(), ConfDispositivo.class);				                    
		    						    		i.putExtra("habitacion" , habitaciones[position-1]);
		    						    		i.putExtra("codigo_habitacion" , cod_habitaciones[position-1]);
		    						    		i.putExtra("botonPrincipal", botonPrincipal);
		    						    		startActivity(i);
		    						    		finish();
		    						    		
		    		                		}else 
		    		                			if (botonPrincipal.equals("6"))
			    		                		{	         
			    				                	Intent i = new Intent(getApplicationContext(), ConfSensor.class);				                    
			    						    		i.putExtra("habitacion" , habitaciones[position-1]);
			    						    		i.putExtra("codigo_habitacion" , cod_habitaciones[position-1]);
			    						    		i.putExtra("botonPrincipal", botonPrincipal);
			    						    		startActivity(i);
			    						    		finish();
			    						    		
			    		                		}
	                }
	            });
	            
	            return item;
	        }

	        private String getHeader(int position) {

	            if(position == HDR_POS1 ) {
	            	return cabecera;
	            }

	            return null;
	        }

	        private final Context mContext;
	    }
	 
	 	@Override
		public void onBackPressed() {
			Intent inten = new Intent();
			if ((botonPrincipal.equals("4"))||(botonPrincipal.equals("5"))||(botonPrincipal.equals("6")))
			{
				inten.setClass(this,ConfSistema.class);	
				startActivity(inten);	
				finish();
			}
			else
			{
				inten.setClass(this,PaginaPrincipal.class);	
				startActivity(inten);	
				finish();
			}
		}
	 	
}
