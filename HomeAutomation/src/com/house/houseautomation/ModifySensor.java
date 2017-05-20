package com.house.houseautomation;

import java.util.ArrayList;
import java.util.HashMap;

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


public class ModifySensor extends Activity{
	public static final int HDR_POS1 = 0;
	public String cabecera = "Sensores disponibles en ";
	public String hab_dom="";
	public String botonPrincipal ="";
	String cod_hab="1";
	
	private static final Integer LIST_HEADER = 0;
    private static final Integer LIST_ITEM = 0;
    
    int numero_sensores = 0;
    
    Httppostaux post;
	
    HashMap hashMap = new HashMap();  
    
    String[] cod_disp = new String[100];
    String[] desc_sens = new String[100];
    String[] tipo_sens = new String[100];
    String[] val_sens = new String[100];
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conf_user);
        
        hashMap.put("1","Temperatura");
        hashMap.put("2","Humedad");
        hashMap.put("3","Gas");
        hashMap.put("4","Luminosidad");
        hashMap.put("5","Inundación");
        hashMap.put("6","Movimiento(PIR)");
        hashMap.put("7","Lluvia");
        hashMap.put("8","Fuego");
        hashMap.put("9","Sonido");
        hashMap.put("10","Ultrasonido"); 
        
        
        String[] habitaciones = new String[100];
        
        post=new Httppostaux();
        
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if ( bundle != null ) {
        	hab_dom = bundle.getString("habitacion");
        	cod_hab = bundle.getString("codigo_habitacion");
        	botonPrincipal = bundle.getString("boton");
        }
        
        ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
 		
		postparameters2send.add(new BasicNameValuePair("cod_hab",cod_hab));
		postparameters2send.add(new BasicNameValuePair("cod_oper","4"));
		
		// Obtenemos la dirección del servidor php
		Resources res = this.getResources();
		String[] lDirecciones = res.getStringArray(R.array.DireccionesServidor);
			
		String URL_connect=lDirecciones[6];
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
					 desc_sens[i] = obj.getString("DESC_SEN"); 
					 tipo_sens[i] = obj.getString("TIP_SEN"); 
					 val_sens[i] = obj.getString("VAL_SEN");
					 cod_disp[i] = obj.getString("COD_SEN");
					 numero_sensores++;
				}
				ListView lv = (ListView)findViewById(R.id.listView1);
				lv.setAdapter(new MyListAdapter(this));	
			} 
		 }
	     catch(Exception ex) 
	     { 
	    	 Log.e("ServicioRest","Error!", ex); 
	     }
		
		
	   } 
	 
    private class MyListAdapter extends BaseAdapter {
        public MyListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return (numero_sensores+1);
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
                headerTextView.setText(headerText+hab_dom);
                return item;
            }

            View item = convertView;
            if(convertView == null || convertView.getTag() == LIST_HEADER) {
                item = LayoutInflater.from(mContext).inflate(
                        R.layout.lv_layout, parent, false);
                item.setTag(LIST_ITEM);
            }

            String value=(String)hashMap.get(tipo_sens[position-1]);
            
            TextView header = (TextView)item.findViewById(R.id.lv_item_header);
            header.setText(desc_sens[position-1]);

            TextView subtext = (TextView)item.findViewById(R.id.lv_item_subtext);
            subtext.setText(value);
            
            ImageButton btnGoLights = (ImageButton) item.findViewById(R.id.button);
            btnGoLights.setOnClickListener(new View.OnClickListener() {
            	 
                @Override
                public void onClick(View view) {
                    // Launching create new product activity
                	if (botonPrincipal.equals("2"))
                	{
		               Intent i = new Intent(getApplicationContext(), ModifySensorYes.class);
		               i.putExtra("desc_sens", desc_sens[position-1]);
		               i.putExtra("tipo_sens", tipo_sens[position-1]);
		               i.putExtra("cod_sens", cod_disp[position-1]);
		               i.putExtra("codigo_habitacion", cod_hab);
	    			   i.putExtra("habitacion", hab_dom);
	    			   i.putExtra("boton", botonPrincipal);
		               startActivity(i);
		    		   finish();
                	}else
                	{
                	   Intent i = new Intent(getApplicationContext(), DeleteSensor.class);
 		               i.putExtra("desc_sens", desc_sens[position-1]);
 		               i.putExtra("tipo_sens", tipo_sens[position-1]);
 		               i.putExtra("cod_sens", cod_disp[position-1]);
 		               i.putExtra("codigo_habitacion", cod_hab);
	    			   i.putExtra("habitacion", hab_dom);
	    			   i.putExtra("boton", botonPrincipal);
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
		inten.setClass(this,ConfSensor.class);	
		inten.putExtra("codigo_habitacion" , cod_hab);
		inten.putExtra("habitacion" , hab_dom);
		inten.putExtra("boton", botonPrincipal);
		startActivity(inten);	
		finish();
		
	}
}
