package com.house.houseautomation;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import org.apache.http.HttpResponse; 
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient; 
import org.apache.http.client.methods.HttpGet; 
import org.apache.http.impl.client.DefaultHttpClient; 
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils; 

import com.house.houseautomation.library.Httppostaux;

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

public class ConfUsers extends Activity{
	public static final int HDR_POS1 = 0;
	public static final int NUM_OPCIONES = 2;
	public String cabecera = "Configuración de usuarios ";
	String usuario;
	String tipo_user;

    private static final Integer LIST_HEADER = 0;
    private static final Integer LIST_ITEM = 0; 
    
    String[] configuraciones = new String[100];
    String[] descripciones = new String[100];
    String[] cod_habitaciones = new String[100];
    int numero_habitaciones = 0;
    String botonPrincipal ="1";
    
    Httppostaux post;
	
	@Override 
	public void onCreate(Bundle savedInstanceState) { 
		 super.onCreate(savedInstanceState); 
		 setContentView(R.layout.conf_user);	 
		 
		 Globals g = Globals.getInstance();
	     usuario = g.getData();
		 
	     post=new Httppostaux();
	     
	     ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
	 		
			postparameters2send.add(new BasicNameValuePair("usuario",usuario));
	        
	        // Obtenemos la dirección del servidor php
	     	Resources res = this.getResources();
	     	String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
	     	String URL_connect=lDirecciones[43];
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
	     					 tipo_user = obj.getString("TIP_USER"); 
	     				}
	     				
	     			} 
	     	 }
	     	 catch(Exception ex) 
	     	 { 
	     	   Log.e("ServicioRest","Error!", ex); 
	     	 }
	     
		 configuraciones[0] = "Añadir usuarios";
		 configuraciones[1] = "Modificar usuario";
		 
		 		 
		 descripciones[0] = "Nuevos usuarios";
		 descripciones[1] = "Modificar usuario actual";
		 
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
	                		
	                		if (tipo_user.equals("1"))
	                		{	                		
			                    Intent i = new Intent(getApplicationContext(), AddUser.class);		    				
			                    startActivity(i);
			    				finish();
	                		}
	                		else
	                		{
	                			Toast.makeText(getBaseContext(), "El usuario no tiene permisos para esta opción.",
	                	        Toast.LENGTH_LONG).show();
	                		}
	                	}
	                	else 
	                		if (position==2)
	                	  	{
		                		Intent i = new Intent(getApplicationContext(), ModifyUser.class);
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
			inten.setClass(this,ConfSistema.class);	
			startActivity(inten);	
			finish();
			
		}
    
    
}
