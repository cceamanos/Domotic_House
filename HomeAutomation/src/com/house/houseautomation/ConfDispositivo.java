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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConfDispositivo extends Activity{

	public static final int HDR_POS1 = 0;
	public static final int NUM_OPCIONES = 3;
	public String cabecera = "Configuración de dispositivos";

    private static final Integer LIST_HEADER = 0;
    private static final Integer LIST_ITEM = 0; 
    
    String[] configuraciones = new String[100];
    String[] descripciones = new String[100];
    String[] cod_habitaciones = new String[100];
    int numero_habitaciones = 0;
    String habitacion,cod_hab,boton;
	
	@Override 
	public void onCreate(Bundle savedInstanceState) { 
		 super.onCreate(savedInstanceState); 
		 setContentView(R.layout.conf_user);	 
		 
		 Intent intent = getIntent();
	     Bundle bundle = intent.getExtras();
	     if ( bundle != null ) {
	       	habitacion = bundle.getString("habitacion");
	       	cod_hab = bundle.getString("codigo_habitacion");
	       	boton = bundle.getString("botonPrincipal");
	     }
		 
		 configuraciones[0] = "Añadir dispositivo";
		 configuraciones[1] = "Modificar dispositivo";
		 configuraciones[2] = "Borrar dispositivo";
		 		 
		 descripciones[0] = "Añadir actuadores habitación";
		 descripciones[1] = "Modificar actuadores habitación";
		 descripciones[2] = "Eliminar actuadores habitación";
		 
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
		                    Intent i = new Intent(getApplicationContext(), AddDispositivo.class);
		                    i.putExtra("habitacion" , habitacion);
				    		i.putExtra("codigo_habitacion" , cod_hab);
				    		i.putExtra("botonPrincipal", "1");
		    				startActivity(i);
		    				finish();
	                	}
	                	else 
	                		if (position==2)
	                	  	{
		                		Intent i = new Intent(getApplicationContext(), ModifyDispositivo.class);
		                		i.putExtra("habitacion" , habitacion);
					    		i.putExtra("codigo_habitacion" , cod_hab);
					    		i.putExtra("botonPrincipal", "2");
		                		startActivity(i);
		                		finish();
			    				
	                	  	} else
	                	  		if (position==3)
	                	  		{
	                	  			Intent i = new Intent(getApplicationContext(), ModifyDispositivo.class);
	                	  			i.putExtra("habitacion" , habitacion);
	    				    		i.putExtra("codigo_habitacion" , cod_hab);
	    				    		i.putExtra("botonPrincipal", "3");
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
			inten.setClass(this,HabLuz.class);	
			inten.putExtra("boton" , "5");
			startActivity(inten);	
			finish();
			
		}
}
