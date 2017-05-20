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
 
public class ConfSistema extends Activity{
 
	public static final int HDR_POS1 = 0;
	public static final int NUM_OPCIONES = 5; // Cambiamos de 5 a 4 para suprimir
	public String cabecera = "Opciones de configuracion ";
	String usuario;

    private static final Integer LIST_HEADER = 0;
    private static final Integer LIST_ITEM = 0; 
    
    String[] configuraciones = new String[100];
    String[] descripciones = new String[100];
    String[] cod_habitaciones = new String[100];
    int numero_habitaciones = 0;
    String botonPrincipal ="1";
	
	@Override 
	public void onCreate(Bundle savedInstanceState) { 
		 super.onCreate(savedInstanceState); 
		 setContentView(R.layout.hab_luz);	 
		 
		 configuraciones[0] = "Configurar usuarios";
		 configuraciones[1] = "Configurar habitaciones";
		 configuraciones[2] = "Configurar sensores";
		 configuraciones[3] = "Configurar dispositivos";
		 configuraciones[4] = "Valores de sensores";
		 
		 descripciones[0] = "Añadir/modificar usuarios";
		 descripciones[1] = "Añadir/modificar/borrar habitaciones";
		 descripciones[2] = "Añadir/modificar/borrar sensores";
		 descripciones[3] = "Añadir/modificar/borrar dispositivos";
		 descripciones[4] = "Valores de los sensores de cada habitación";
		 
		 ListView lv = (ListView)findViewById(R.id.listView1);
		 lv.setAdapter(new MyListAdapter(this));		 
		 
	   } 
	 
	 private class MyListAdapter extends BaseAdapter {
	        public MyListAdapter(Context context) {
	            mContext = context;
	        }

	        @Override
	        public int getCount() {
	            return (NUM_OPCIONES+1);
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
	            header.setText(configuraciones[position-1]);

	            TextView subtext = (TextView)item.findViewById(R.id.lv_item_subtext);
	            subtext.setText(descripciones[position-1]);
	            
	            ImageButton btnGoLights = (ImageButton) item.findViewById(R.id.button);
	            btnGoLights.setOnClickListener(new View.OnClickListener() {
	            	 
	                @Override
	                public void onClick(View view) {
	                    // Launching create new product activity
	                	if (position==1)
	                	{
		                    Intent i = new Intent(getApplicationContext(), ConfUsers.class);	
		                    i.putExtra("username",usuario);
		    				startActivity(i);
		    				finish();
	                	}
	                	else 
	                		if (position==2)
	                	  	{
		                		Intent i = new Intent(getApplicationContext(), ConfHabitacion.class);
			    				startActivity(i);
			    				finish();
			    				
	                	  	} else
	                	  		if (position==3)
	                	  		{
	                	  			Intent i = new Intent(getApplicationContext(), HabLuz.class);
	                	  			i.putExtra("boton" , "6");
			                    	startActivity(i);
			                    	finish();
			                    	
	                	  		} else
	                	  			if (position==4)
		                	  		{
		                	  			Intent i = new Intent(getApplicationContext(), HabLuz.class);
		                	  			i.putExtra("boton" , "5");
				                    	startActivity(i);
				                    	finish();
				                    	
		                	  		} else 
		                	  			if (position==5)
			                	  		{
		                	  				Intent i = new Intent(getApplicationContext(), HabLuz.class);
		                	  				i.putExtra("boton" , "4");
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
			inten.setClass(this,PaginaPrincipal.class);	
			inten.putExtra("username",usuario);
			startActivity(inten);	
			finish();
		}
    
	
}