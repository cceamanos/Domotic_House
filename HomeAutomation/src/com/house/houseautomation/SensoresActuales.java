package com.house.houseautomation;

import java.util.ArrayList;
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

public class SensoresActuales extends Activity {
	public static final int HDR_POS1 = 0;
	public String cabecera = "Sensores disponibles en ";
	public String hab_dom="";
	public String botonPrincipal ="";
	String cod_hab="1";
	
	private static final Integer LIST_HEADER = 0;
    private static final Integer LIST_ITEM = 0;
    
    int numero_dispositivos = 0;
    
    Httppostaux post;
	
    HashMap hashMap = new HashMap();  
    
    String[] cod_sens = new String[100];
    String[] desc_sens = new String[100];
    String[] tipo_sens = new String[100];
    String[] val_sens = new String[100];
    String[] hor_sens = new String[100];
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensores_actuales);
 
        hashMap.put("1","ºC");
        hashMap.put("2","%");
        hashMap.put("3"," %");
        hashMap.put("4"," Lx");
        hashMap.put("5"," Pir");
        hashMap.put("6"," ");
        
        
        String[] habitaciones = new String[100];
        
        post=new Httppostaux();
        
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if ( bundle != null ) {
        	hab_dom = bundle.getString("habitacion");
        	cod_hab = bundle.getString("codigo_habitacion");
        	botonPrincipal = bundle.getString("botonPrincipal");
        }
        
        ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
 		
		postparameters2send.add(new BasicNameValuePair("cod_hab",cod_hab));
		postparameters2send.add(new BasicNameValuePair("cod_oper",botonPrincipal));
		
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
					 val_sens[i] = obj.getString("VAL_SEN");
					 cod_sens[i] = obj.getString("COD_SEN");
					 tipo_sens[i] = obj.getString("TIP_SEN");
					 desc_sens[i] = obj.getString("DESC_SEN");
					 hor_sens[i] = obj.getString("TIME_SEN");
					  
					  
					 numero_dispositivos++;
				}
				ListView lv = (ListView)findViewById(R.id.sensores_hab);
				lv.setAdapter(new MyListAdapter(this));	
			} 
			else{
				ListView lv = (ListView)findViewById(R.id.sensores_hab);
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
	            return (numero_dispositivos+1);
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
	                        R.layout.sensores_actuales_aux, parent, false);
	                item.setTag(LIST_ITEM);
	            }

	            ImageView image = (ImageView) item.findViewById(R.id.imageView1);	
	            
	            // Busco en el hashmap con el tipo del dispositivo para sacar el icono correspondiente
	            String value=(String)hashMap.get(tipo_sens[position-1]);
	            //String icono_hab = "drawable/";
	    	    //int imageResource = mContext.getResources().getIdentifier(icono_hab+value, null, mContext.getPackageName());
	    	    //image.setImageDrawable(mContext.getResources().getDrawable(imageResource));
	    	    
	            TextView header = (TextView)item.findViewById(R.id.sens_item_header);
	            header.setText(desc_sens[position-1]);
	     
	            TextView valor = (TextView)item.findViewById(R.id.val_sen);
	            if (tipo_sens[position-1].equals("6")){
	            	if (val_sens[position-1].equals("0.0"))
	            	{           	
	            		valor.setText("NO");
	            	}
	            	else
	            	{	
	            		valor.setText("SI");          		
	            	}
	            }
	            else {
	            	valor.setText(val_sens[position-1]+ " " + value);	                     	
	            }
	            
	            TextView hora_dia = (TextView)item.findViewById(R.id.horaDia);
	            hora_dia.setText(hor_sens[position-1]);
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
			inten.putExtra("boton", botonPrincipal);
	   		startActivity(inten);	
	   		finish();
	   	}
}
