package com.house.houseautomation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
 
public class PaginaPrincipal extends Activity{
 
    Button btnTemperatura;
    Button btnLuces;
    Button btnPersianas;
    Button btnRiego;
    Button btnAgua;
    Button btnGas;
    Button btnCalefac;
    Button btnAlarma;
    Button btnSettings;
    
    String usuario="";
 
    //String user = getIntent().getStringExtra("user"); // Sabemos cual es el usuario
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
 
        // Buttons
        btnTemperatura = (Button) findViewById(R.id.btnViewTemperatura);
        btnLuces = (Button) findViewById(R.id.btnViewLuces);
        btnPersianas = (Button) findViewById(R.id.btnViewPersianas);
        btnRiego = (Button) findViewById(R.id.btnViewRiego);
        btnAgua = (Button) findViewById(R.id.btnViewAgua);
        btnGas = (Button) findViewById(R.id.btnViewGas);
        btnCalefac = (Button) findViewById(R.id.btnViewCalefac);
        btnAlarma = (Button) findViewById (R.id.btnViewAlarma);
        btnSettings = (Button) findViewById (R.id.btnViewSettings);
        
        Globals g = Globals.getInstance();
        usuario = g.getData();
        
        Intent intent = getIntent();
	     Bundle bundle = intent.getExtras();
	     if ( bundle != null ) {
	      	usuario = bundle.getString("username");
	     }
        
        // view products click event
        btnTemperatura.setOnClickListener(new View.OnClickListener() {
        
            @Override
            public void onClick(View view) {
                // Launching All Temperatures Activity
                Intent i = new Intent(getApplicationContext(), HabLuz.class);
                i.putExtra("username",usuario);
                i.putExtra("boton" , "1");
                startActivity(i);
                finish();
                
 
            }
        });
 
        // view products click event
        btnLuces.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // Launching create new product activity
                Intent i = new Intent(getApplicationContext(), HabLuz.class);
                i.putExtra("boton" , "2");
                startActivity(i);
                finish();
 
            }
        });
        
     // view products click event
        btnPersianas.setOnClickListener(new View.OnClickListener() {
        	
        	@Override
            public void onClick(View view) {
                // Launching create new product activity
                Intent i = new Intent(getApplicationContext(), HabLuz.class);
                i.putExtra("boton" , "3");
                startActivity(i);
                finish();
            }
        });
        
     // ver precios electricidad click event
        btnRiego.setOnClickListener(new View.OnClickListener() {
        	
            @Override
            public void onClick(View view) {
                // Sistema de riego
                Intent i = new Intent(getApplicationContext(), OpenCloseRiego.class);
                startActivity(i);
                finish();
            }
        });
        
        // Apertura o cierre de la llave del gas
        btnGas.setOnClickListener(new View.OnClickListener() {
        	
            @Override
            public void onClick(View view) {
                // Cerrar o abrir la llave del gas
                Intent i = new Intent(getApplicationContext(), OpenCloseGas.class);
                i.putExtra("user" , "2");
                startActivity(i);
                finish();
            }
        });
        
        // Apertura o cierre de la llave del gas
        btnAgua.setOnClickListener(new View.OnClickListener() {
        	
            @Override
            public void onClick(View view) {
                // Cerrar o abrir la llave del gas
                Intent i = new Intent(getApplicationContext(), OpenCloseAgua.class);
                startActivity(i);
                finish();
            }
        });
        
     // Configuración del sistema
        btnSettings.setOnClickListener(new View.OnClickListener() {
        	
            @Override
            public void onClick(View view) {
                // Menu de configuracion del sistema
                Intent i = new Intent(getApplicationContext(), ConfSistema.class);
                startActivity(i);
                finish();
            }
        });
        
        // view products click event
        btnCalefac.setOnClickListener(new View.OnClickListener() {
        
            @Override
            public void onClick(View view) {
                // Launching All Temperatures Activity
                Intent i = new Intent(getApplicationContext(), Calefaccion.class);
                startActivity(i);
                finish();
                
 
            }
        });
        
        // enciende y apaga sistema aviso alarma
        btnAlarma.setOnClickListener(new View.OnClickListener() {
        
            @Override
            public void onClick(View view) {
                // Lanza pagina de encender apagar alarma
                Intent i = new Intent(getApplicationContext(), OnOffAlarma.class);
                startActivity(i);
                finish();
                
 
            }
        });
    }
 
}
