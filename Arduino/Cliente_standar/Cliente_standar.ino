//***********************************************************************************
//*                                                                                 *
//*                     Programa configuración Arduinos                             *
//*                     como dispositivos domóticos                                 *
//*                     proyecto HomeAutomation                                     *
//*                     Autor: Carlos Ceamanos                                      *
//*                                                                                 *
//***********************************************************************************

// Definición de librerías
#include "Adafruit_GFX.h" // Librería para utilizar Nokia 5110 Monocromo LCD
#include "Adafruit_PCD8544.h" // Librería para utilizar Nokia 5110 Monocromo LCD
#include "DHT.h"  //Añadimos la libreria con la cual trabaja nuestro sensor
#include "Wire.h" //Librería para I2C (Sensor de luminosidad)
#include "XBee.h" //Librería para actuar con los XBee ZigBee


// Definición de constantes
#define DHTPIN 52     // Indicamos el pin donde conectaremos la patilla data de nuestro sensor
 
// Descomenta el tipo de sensor que vas a emplear. En este caso usamos el DHT11
#define DHTTYPE DHT11   // DHT 11 

// pin 9 - Pin que controla Relé de válvula del gas
// pin 8 - Serial clock out (SCLK)
// pin 7 - Serial data out (DIN)
// pin 6 - Data/Command select (D/C)
// pin 5 - LCD chip select (CS)
// pin 4 - LCD reset (RST)
Adafruit_PCD8544 display = Adafruit_PCD8544(4, 5, 6, 8, 7);

#define NUMFLAKES 10
#define XPOS 0
#define YPOS 1
#define DELTAY 2

#define LOGO16_GLCD_HEIGHT 16
#define LOGO16_GLCD_WIDTH  16

// Definicion de variables para DHT
DHT dht(DHTPIN, DHTTYPE);  //Indica el pin con el que trabajamos y el tipo de sensor
int maxh=0, minh=100, maxt=0, mint=100;  //Variables para ir comprobando maximos y minimos

struct valores_DHT {
  float valor_temperatura;
  float valor_humedad;
};
struct valores_DHT val_DHT;
int result_DHT=0;

// Definicion configuracion inicial - sensores que posee el dispositivo
// Variando esta configuracion inicial, podemos adaptar el programa a diversos dispositivos con configuraciones diferentes
int tiene_sensor_hum_temp = 1;
int tiene_sensor_gas = 1;
int tiene_sensor_presencia = 1;
int tiene_sensor_inundacion = 1;
int tiene_sensor_luz = 1;

// Variables de estados
int estado_Gas = 0; // Inicialmente abierta la llave de paso; ordenes 1 y 2
int estado_Agua = 0; // Inicialmente abierta la llave de paso; ordenes 3 y 4
int estado_Riego = 0; // Inicialmente desconectada; ordenes 5 y 6
int estado_LucesGrupo1 = 0; // Inicialmente apagadas; ordenes 7 y 8
int estado_LucesGrupo2 = 0; // Incialmente apagada; ordenes 9 y 10
int estado_Lavadora = 0; // Inicialmente apagado; ordenes 11 y 12
int estado_Calefaccion = 0; // Inicialmente apagada; ordenes 13 y 14

int movimiento_detectado = 0;

const int buttonPin = 2;
int buttonState = 0;
int boton_valor = 0;
int outputPinGas = 9;               // Pin que gobierna el Rele del gas (electroválvula)
int outputPinAgua = 30; // Pin que gobierna el rele del agua (electroválvula)
int outputPinRiego = 31; // Pin que gobierna el rele del riego (electroválvula)
int outputPinLucesGrupo1 = 32; // Pin que gobierna rele de luces grupo 1 (interruptor)
int outputPinLucesGrupo2 = 33; // Pin que gobierna rele de luces grupo 2 (interruptor)
int outputPinPersiana = 34; // Pin que gobierna rele de persiana (interruptor a motor)
int outputPinCalefaccion = 35; // Pin que gobierna rele de calefaccion
int inputPinGas = 53; // Definimos el Pin de entrada para el sensor de gas
int releState = 0;              // Estado inicial del Rele
int BH1750_address = 0x23; // Dirección I2C
byte buff[2];
int ledPin = 13;                // LED de sensor PIR de presencia
int inputPin = 10;               // Pin de entrada para el sensor PIR
int pirState = LOW;             // Variable utilizada para PIR. Inicialmente no hay movimiento
int val = 0;
int valor_agua=0;     // Variable donde vamos a recoger el valor del sensor de inundacion
float valor_luz=0;    // Variable donde vamos a recoger el valor del sensor de luz
float valor_gas=0;    // Variable donde vamos a recoger el valor del sensor de gas

int movimientoPositivo; // Hay movimiento o no
int calibrationTime = 30;
long unsigned int lowIn;
long unsigned int pause = 5000;
boolean lockLow = true;
boolean takeLowTime;
int PIRValue = 0;

uint8_t* data = NULL;
uint8_t* rawData = NULL;


float ultTempEnv = 0; // Guardaremos ultimo valor enviado para compararlo
float ultHumEnv = 0; // Guardaremos ultimo valor enviado para compararlo
int lastInt=0;
long interval = 1000;   // intervalo del envio de temp. y hum. (en milisegundos) (10 segundos)

int umbralInundacion = 60; //Ponemos el umbral para cuando detectemos una fuga de agua.
int umbralLuz = 250; // Ponemos l umbral para luces automáticas en caso baje iluminación.
int umbralGas = 20; // Ponemos inicialmente el umbral de gas a 25.

int intervalDisplay = 2000; 

// Variables para realiar timer
long previousMillis = 0;        // will store last time LED was updated
long intervalOn = 500;           // medio segundo  ON
long intervalOff = 5000;         // cinco segundos OFF
long anteriorMillis = 0;   // almacenará la última vez que se envio la temperatura

static unsigned char PROGMEM gotas[] =
{ B0001000,
  B0001000,
  B0001000,
  B0010100,
  B0100010,
  B0100010,
  B0011100
};

int valor_rele_actual=0;
volatile int etat = LOW;

// Variables XBee
// Creamos un objeto XBee que nos servirá para enviar y recibir
XBee xbee = XBee();
// Vamos a enviar dos floats de 4 bytes cada uno y la cabecera que indica lo que llega que es un int (2 bytes)
char Buffer[128]; 
char Buffer2[128];
uint8_t payload[12] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

// Union para convertir el int a byte string
union u_tag {
    uint8_t b[4];
    float f_val;
} u;

// Dirección SH + SL del XBee receptor (el coordinador en este caso)
// Enviamos al coordinador (servidor)
XBeeAddress64 addr64 = XBeeAddress64(0x00000000, 0x00000000);
ZBTxRequest zbTx = ZBTxRequest(addr64, payload, sizeof(payload));
ZBTxStatusResponse txStatus = ZBTxStatusResponse();

// Objeto creado para manejar las respuestas que le llegan a Arduino
ZBRxResponse zbRx = ZBRxResponse();

// Parte del programa donde se inicializan los pines
void setup()   {
 
    pinMode(outputPinCalefaccion, OUTPUT);
  if (tiene_sensor_gas == 1)
  {
    pinMode(outputPinGas, OUTPUT);
    pinMode(inputPinGas,INPUT); // Valor digital gas
  }
  if (tiene_sensor_inundacion == 1)
  {
    pinMode(outputPinAgua, OUTPUT);
  }
  if (tiene_sensor_inundacion == 1)
  {
    pinMode(outputPinRiego, OUTPUT);
  }
  pinMode(outputPinLucesGrupo1, OUTPUT);
  pinMode(outputPinLucesGrupo2, OUTPUT);
  pinMode(outputPinPersiana, OUTPUT);
  pinMode(ledPin, OUTPUT);      // declare LED as output
  if (tiene_sensor_presencia == 1)
  {
    pinMode(inputPin, INPUT);     // declare sensor as input
  }
  if (tiene_sensor_luz == 1)
  {
    // Inicializamos I2C
    Wire.begin();
    BH1750_Init(BH1750_address);
    // Inicializamos el sensor de temperatura
    dht.begin();
  }
  //Inicialización de la pantalla
  display.begin();
  display.setContrast(50);
  display.display(); 
  // Inicializamos la líena serie de Arduino
  Serial.begin(9600);
  
  // Inicializamos la línea serie del XBee
  xbee.setSerial(Serial);
  // Inicialización realizada
  
}

// Comenzamos el bucle de la placa Arduino
void loop() { 
  Serial.print("Movimiento detectado ");
  Serial.println(movimiento_detectado);
  if (tiene_sensor_inundacion==1)
  {
    valor_agua=evalua_agua();
    Serial.print("El valor de agua es: ");
    Serial.print(valor_agua);
  }
  
  if (tiene_sensor_luz==1)
  {
    valor_luz=evalua_luz();
    Serial.print("El valor de la luz es: ");
    Serial.println((int)valor_luz);
  }
  
  if (tiene_sensor_gas==1)
  {
    valor_gas=evalua_gas(); 
    Serial.print("El valor del gas es: ");
    Serial.println(valor_gas);
  }
  
  if (digitalRead(inputPin)== HIGH)
  {
    
      Serial.println("MOVIMIENTO");
      movimiento_detectado=1;
   
  }
    
   
  /*if (tiene_sensor_presencia == 1)
  {
    int cont_movimientoPositivo;
    cont_movimientoPositivo=evalua_movimiento();
    Serial.print("Evalua Movimiento ");
    Serial.println(cont_movimientoPositivo);
    movimiento_detectado=cont_movimientoPositivo+movimiento_detectado;
    Serial.print("Movimiento detectado ");
    Serial.println(movimiento_detectado);
    
    if (ledPin == HIGH)
    {
      movimientoPositivo=1;
      movimiento_detectado=1;
      Serial.println("Hay movimiento");
    }
    else
    {
      movimientoPositivo=0;
      Serial.println("No hay movimiento");
    }
  }*/
  
  // Definimos el tiempo en que la pantalla va a estar encendida para cada valor 
  if (tiene_sensor_hum_temp == 1)
  {  
    int zs = millis();
    int us = 0;
    int display = 0;
    if ((zs-us) > intervalDisplay)
    {
      // Display de temperatura
      display = 1;
    }else{
      // Display de humedad
      display = 0;
    }
    val_DHT=evalua_temperatura(display);
  
    if (result_DHT==1)
    {
      Serial.println("Error de DHT");
    }
  }
  
  unsigned long currentMillis = millis();    // Se toma el tiempo actual
  
  if (currentMillis - anteriorMillis > interval){
    // Si se cumple la condición se guarda el nuevo tiempo
    // en el que se envio la temperatura
    anteriorMillis = currentMillis;
    
    // Mira si devuelve algo que sea válido, y si son NaN (not a number) entonces existe error
    // Deben enviarse los valores de temperatura, humedad, gas, luminosidad, inundación y si ha habido movimiento
    // float, float, float, int, int, int
    if (!isnan(val_DHT.valor_temperatura) && !isnan(val_DHT.valor_humedad) && !isnan(valor_gas) ) {
       
       // Enviamos los datos de los sensores (6 valores)
       String datosAEnviar;
       // Añadimos datos sensor temperatura
       String datosAEnviar2;
       if (tiene_sensor_hum_temp == 1)
       {
         datosAEnviar2=dtostrf(val_DHT.valor_temperatura,4,2,Buffer);
         datosAEnviar = datosAEnviar2;
       }
       else
       {
         datosAEnviar2=dtostrf(0,4,2,Buffer);
         datosAEnviar = datosAEnviar2;
       }
       // Añadimos datos sensor humedad
       if (tiene_sensor_hum_temp == 1)
       {
         datosAEnviar2 = dtostrf(val_DHT.valor_humedad,4,2,Buffer2);
         datosAEnviar = datosAEnviar + ',' + datosAEnviar2;
         Serial.print("Los datos a enviar son: ");
         Serial.println(datosAEnviar);
       }
       else
       {
         datosAEnviar2 = dtostrf(0,4,2,Buffer2);
         datosAEnviar = datosAEnviar + ',' + datosAEnviar2;
         Serial.print("Los datos a enviar son: ");
         Serial.println(datosAEnviar);
       }
       // Añadimos datos sensor gas
       if (tiene_sensor_gas == 1)
       {
         datosAEnviar2 = dtostrf(valor_gas,4,2,Buffer2);
         datosAEnviar = datosAEnviar + ',' + datosAEnviar2;
         Serial.print("Los datos a enviar son: ");
         Serial.println(datosAEnviar);
       }
       else
       {
         datosAEnviar2 = dtostrf(0,4,2,Buffer2);
         datosAEnviar = datosAEnviar + ',' + datosAEnviar2;
         Serial.print("Los datos a enviar son: ");
         Serial.println(datosAEnviar);
       }
       
       // Añadimos datos sensor luz
       if (tiene_sensor_luz == 1)
       {
         datosAEnviar2 = dtostrf(valor_luz,4,2,Buffer2);
         datosAEnviar = datosAEnviar + ',' + datosAEnviar2;
         Serial.print("Los datos a enviar son: ");
         Serial.println(datosAEnviar);
       }
       else
       {
         datosAEnviar2 = dtostrf(0,4,2,Buffer2);
         datosAEnviar = datosAEnviar + ',' + datosAEnviar2;
         Serial.print("Los datos a enviar son: ");
         Serial.println(datosAEnviar);
       }
       
       // Añadimos datos sensor inundacion
       if (tiene_sensor_inundacion == 1)
       {
         datosAEnviar2 = dtostrf(valor_agua,4,2,Buffer2);
         datosAEnviar = datosAEnviar + ',' + datosAEnviar2;
         Serial.print("Los datos a enviar son: ");
         Serial.println(datosAEnviar);
       }
       else
       {
         datosAEnviar2 = dtostrf(0,4,2,Buffer2);
         datosAEnviar = datosAEnviar + ',' + datosAEnviar2;
         Serial.print("Los datos a enviar son: ");
         Serial.println(datosAEnviar);
       }
       
       // Añadimos datos sensor movimiento
       if (tiene_sensor_presencia == 1)
       {
         datosAEnviar2 = dtostrf(movimiento_detectado,4,2,Buffer2);
         datosAEnviar = datosAEnviar + ',' + datosAEnviar2;
         Serial.print("Los datos a enviar son: ");
         Serial.println(datosAEnviar);
       }
       else
       {
         datosAEnviar2 = dtostrf(0,4,2,Buffer2);
         datosAEnviar = datosAEnviar + ',' + datosAEnviar2;
         Serial.print("Los datos a enviar son: ");
         Serial.println(datosAEnviar);
       }
              
       int TempNumOne=datosAEnviar.length();
       Serial.println(TempNumOne);
       for (int a=0;a<=TempNumOne;a++)
        {
            Buffer[a]=datosAEnviar[a];
            Serial.print(Buffer[a]);
        }
       zbTx = ZBTxRequest(addr64, (uint8_t *)Buffer, strlen(Buffer));
       Serial.println(strlen(Buffer));
      
      // Controlo aquí que se envíe o no los datos al central en caso de que no cambien los valores detectados - debo añadir los datos de gas, luz, presencia e inundacion
      // (valor_gas > 45)|(movimiento_detectado == 1)|(valor_agua > 5)  (val_DHT.valor_humedad != ultHumEnv)|(val_DHT.valor_temperatura!=ultTempEnv)
      if ((val_DHT.valor_humedad != ultHumEnv)|(val_DHT.valor_temperatura!=ultTempEnv)|(valor_gas > 45)|(movimiento_detectado == 1)|(valor_agua > 5)){
        xbee.send(zbTx);
        Serial.println("Envio datos sensores");
        Serial.println(datosAEnviar);
        movimiento_detectado = 0;
       
        Serial.println("Pongo a cero detectada presencia");
        Serial.println(movimiento_detectado);
        ultHumEnv=val_DHT.valor_humedad;
        ultTempEnv=val_DHT.valor_temperatura;
              
        if (xbee.readPacket(500)){
        // Tengo respuesta
     
        // Será znet tx status             
          if (xbee.getResponse().getApiId() == ZB_TX_STATUS_RESPONSE){
              xbee.getResponse().getZBTxStatusResponse(txStatus);
     
              // Cojo el status, el quinto byte
              if (txStatus.getDeliveryStatus() == SUCCESS){
                // Exito.  Todo OK
                Serial.println("SUCCESS");
              } else {
                // El XBee remoto no recibe nuestro paquete. ¿Estará conectado?
                Serial.println("No recibe el paquete");
              }
            }
          } else if (xbee.getResponse().isError()) {
            Serial.println("Error leyendo paquete.  Codigo error: ");  
            Serial.println(xbee.getResponse().getErrorCode());
          } else {
            // El XBee local no proporciona un TX status response a tiempo -- no deberia ocurrir
            Serial.println("Raro raro esto");
          }
      }
    }  
  }
  
  xbee.readPacket(500);
  char l;
  Serial.println("Pasa por leer orden remitida");
  
  // Chequeamos lo recibido: 
  if (xbee.getResponse().isAvailable()) {
    if (xbee.getResponse().getApiId() == ZB_RX_RESPONSE) {
      xbee.getResponse().getZBRxResponse(zbRx);
      Serial.print("He recibido un ");
      Serial.println(zbRx.getData(0));
      for (int i = 0; i < zbRx.getDataLength(); i++) {
          print8Bits(zbRx.getData()[i]);
          Serial.print(' ');
      }
      
  /*    for (int i= 0; i < zbRx.getDataLength(); i++){
          Serial.write(' ');
          if (iscntrl(zbRx.getData()[i]))
            Serial.write(' ');
          else
            l = toascii(zbRx.getData()[i]);
          Serial.print(l);
        }
      handleXbeeRxMessage(zbRx.getData(), zbRx.getDataLength());
    
      for (int i = 0; i < zbRx.getDataLength(); i++) {
          Serial.print("payload [");
          Serial.print(i, DEC);
          Serial.print("] is ");
          Serial.println(zbRx.getData()[i], HEX);
          Serial.write(zbRx.getData()[i]);
        }
        
       for (int i = 0; i < xbee.getResponse().getFrameDataLength(); i++) {
        Serial.print("frame data [");
        Serial.print(i, DEC);
        Serial.print("] is ");
        Serial.println(xbee.getResponse().getFrameData()[i], HEX);
      }
    
      rawData = zbRx.getFrameData();
      data = zbRx.getData();
      Serial.println("raw data:");
      Serial.print("Ox");
          
        for (int i=0; i<zbRx.getFrameDataLength(); i++) {

          Serial.print(rawData[i], HEX);
          Serial.print(" ");
        }
        
        Serial.println("");
        Serial.println("App data");
        Serial.print("Ox");
          
        for (int i=0; i<zbRx.getDataLength(); i++) {

          Serial.print(data[i], HEX);
          Serial.print(" ");
        }
        Serial.println("");
        
        for (int i=0; i<zbRx.getDataLength(); i++) {

          Serial.print((char)data[i]);
        }
        
        Serial.println("");
    */    
      switch(zbRx.getData(0)){
        /* Ordenes que puede recibir Arduino
          1.- Apertura de válvula de gas (llaveGas) 1
          2.- Cierre de válvula de gas (llaveGas) 1
          3.- Apertura de llave de agua (llaveAgua) 2
          4.- Cierre de llave de agua (llaveAgua) 2
          5.- Apertura de válvula de riego (llaveRiego) 3
          6.- Cierre de válvula de riego (llaveRiego) 3
          7.- Apertura de luces salón grupo 1 (llaveLucesGrupo1) 4
          8.- Cierre de luces salón grupo 1 (llaveLucesGrupo1) 4
          9.- Apertura de luces salón grupo 2 (llaveLucesGrupo2) 5
          10.- Cierre de luces salón grupo 2 (llaveLucesGrupo2) 5
          11.- Apertura de persiana salón (interruptorPersiana) 6
          12.- Cierre de persiana de salón (interruptorPersiana) 6
          13.- Encendido de calefaccion (interruptorCalefaccion) 7
          14.- Apagado de calefaccion (interruptorCalefaccion) 7
          19.- Encendido de lavadora (interruptorLavadora) 7
          20.- Apagado de lavadora (interruptorLavadora) 7
        */
        case 0x01: 
           Serial.println("He recibido un 1: valvula gas ON");
           estado_Gas = 1;
           // Cambia pin de rele de gas a 1
           cambia_rele(estado_Gas, 1);
           break;
           
        case 0x02:
           Serial.println("He recibido un 2: valvula gas OFF");
           estado_Gas = 0;
           // Cambia pin de rele de gas a 0
           cambia_rele(estado_Gas, 1);
           break;
           
        case 0x03:
           Serial.println("He recibido un 3: llave agua ON");
           estado_Agua=1;
           // Cambia pin de rele de agua a 1
           cambia_rele(estado_Agua, 2);
           break;
           
        case 0x04:
           Serial.println("He recibido un 4: llave agua OFF");
           estado_Agua=0;
           // Cambia pin de rele de agua a 0
           cambia_rele(estado_Agua, 2);
           break;
           
         case 0x05:
           Serial.println("He recibido un 5: llave riego ON");
           estado_Riego=1;
           // Cambia pin de rele de riego a 1
           cambia_rele(estado_Riego, 3);
           break;
           
        case 0x06:
           Serial.println("He recibido un 6: llave riego OFF");
           estado_Riego=0;
           // Cambia pin de rele de riego a 0
           cambia_rele(estado_Riego, 3);
           break;
           
         case 0x07:
           Serial.println("He recibido un 7: llave luces grupo1 ON");
           estado_LucesGrupo1=1;
           // Cambia pin de rele de luces grupo1 a 1
           cambia_rele(estado_LucesGrupo1, 4);
           break;
           
        case 0x08:
           Serial.println("He recibido un 8: llave luces grupo1 OFF");
           estado_LucesGrupo1=0;
           // Cambia pin de rele de luces grupo1 a 0
           cambia_rele(estado_LucesGrupo1, 4);
           break;
           
         case 0x09:
           Serial.println("He recibido un 9: llave luces grupo2 ON");
           estado_LucesGrupo2=1;
           // Cambia pin de rele de luces grupo2 a 1
           cambia_rele(estado_LucesGrupo2, 5);
           break;
           
        case 0x10:
           Serial.println("He recibido un 16: llave luces grupo2 OFF");
           estado_LucesGrupo2=0;
           // Cambia pin de rele de luces grupo2 a 0
           cambia_rele(estado_LucesGrupo2, 5);
           break;
           
         case 0x19:
           Serial.println("He recibido un 19: lavadora ON");
           estado_Lavadora = 1;
           // Cambia pin de interruptor de persiana a ON
           cambia_rele(estado_Lavadora, 6);
           break;
           
         case 0x20:
           Serial.println("He recibido un 20: lavadora OFF");
           estado_Lavadora = 0;
           // Cambia pin de interruptor de persiana a OFF
           cambia_rele(estado_Lavadora, 6);
           break;
         
         case 0x13:
           Serial.println("He recibido un 80: calefaccion OFF");
           estado_Calefaccion = 0;
           // Cambia pin de interruptor de calefaccion a OFF
           cambia_rele(estado_Calefaccion, 7);
           break;
           
         case 0x14:
           Serial.println("He recibido un 96: calefaccion ON");
           estado_Calefaccion = 1;
           // Cambia pin de interruptor de calefaccion a ON
           cambia_rele(estado_Calefaccion, 7);
           break;
      }
      Serial.println((int)(zbRx.getData(1) * 256) + zbRx.getData(2));
    }
  }
}

// Lectura del sensor de luz BH1750
void BH1750_Init(int address){
  
  Wire.beginTransmission(address);
  Wire.write(0x10); // 1 [lux]
  Wire.endTransmission();
}

byte BH1750_Read(int address){
  
  byte i=0;
  Wire.beginTransmission(address);
  Wire.requestFrom(address, 2);
  while(Wire.available()){
    buff[i] = Wire.read(); 
    i++; // leemos los dos bytes que nos envía el sensor
  }
  Wire.endTransmission();  
  return i;
}

// Se apunta la salida del sensor al pin A1
// El valor oscila entre 0 y 1024 al tratarse de un
// sensor analógico
int evalua_agua(){
  int result=analogRead(1);
  result = map(result,0,7152,0,100);
  return result;
}

// Función que cambia de estado el relé con la llave correspondiente
int cambia_rele(int estado_rele, int llave){
  int result=0;
  switch (llave)
  {
     case 1:
        if (estado_rele == 0) 
        {            // miramos si el estado es LOW
         digitalWrite(outputPinGas, LOW);  // encendemos electroválvula gas
         result=1;
        } 
        else
        {
          digitalWrite(outputPinGas, HIGH); // apagamos electroválvula gas
          result=0;
        }
        break;
      case 2:
        if (estado_rele == 0) {            // miramos si el estado es LOW
         digitalWrite(outputPinAgua, HIGH);  // encendemos electroválvula agua
         result=1;
        } 
        else{
          digitalWrite(outputPinAgua, LOW);
          result=0;
        } 
        break;     
      case 3:
        if (estado_rele == 0) {            // miramos si el estado es LOW
         digitalWrite(outputPinRiego, HIGH);  // encendemos electroválvula riego
         result=1;
        } 
        else{
          digitalWrite(outputPinRiego, LOW);
          result=0;
        }
        break;
      case 4:
        if (estado_rele == 0) {            // miramos si el estado es LOW
         digitalWrite(outputPinLucesGrupo1, HIGH);  // encendemos las luces correspondiente
         result=1;
        } 
        else{
          digitalWrite(outputPinLucesGrupo1, LOW);
          result=0;
        }
        break;     
      case 5:
        if (estado_rele == 0) {            // miramos si el estado es LOW
         digitalWrite(outputPinLucesGrupo2, HIGH);  // encendemos las luces correspondiente
         result=1;
        } 
        else{
          digitalWrite(outputPinLucesGrupo2, LOW);
          result=0;
        }
        break;
      case 6:
        if (estado_rele == 0) {            // miramos si el estado es LOW
         digitalWrite(outputPinPersiana, HIGH);  // abrimos persiana
         result=1;
        } 
        else{
          digitalWrite(outputPinPersiana, LOW);
          result=0;
        }
        break;
      case 7:
        if (estado_rele == 0) {            // miramos si el estado es LOW
         digitalWrite(outputPinCalefaccion, LOW);  // apagamos calefaccion
         result=1;
        } 
        else{
          digitalWrite(outputPinCalefaccion, HIGH); // encendemos calefaccion
          result=0;
        }
        break;
  } 
  return result;
}

// Este sensor el GY-30 va por IC2 luego por la linea de datos
// Apuntaremos tierra y VCC y luego SDA a A4 y SCL a A5 e iniciaremos la linea 
// de datos con las funciones de BH1750 definidas
  float evalua_luz(){
  float valf=0;

  if(BH1750_Read(BH1750_address)==2){
    
    valf=((buff[0]<<8)|buff[1])/1.2;
    
    if(valf<0)Serial.print("> 65535");
    else Serial.print((int)valf,DEC); 
    
    Serial.println(" lux"); 
  }
  return valf;
}

// Funcion que evalua el valor del sensor del gas
float evalua_gas(){
  float vol, outputValue;
  int sensorValue = analogRead(A0);
  vol=(float)sensorValue/1024*5.0;
  Serial.print("El valor del gas es: ");
  Serial.println(vol,1);
  // Calibramos el valor de salida de 0 a 100
  outputValue = map(sensorValue, 0, 1023, 0, 100); 
  Serial.print("El otro valor del gas es: ");
  Serial.println(outputValue,1);
  return outputValue; 
}

// Función que nos devuelve si ha habido movimiento o no
int evalua_movimiento() {
   int contador;
   if(digitalRead(inputPin) == HIGH) {
      if(lockLow) {
         PIRValue = 1;
         lockLow = false;
         digitalWrite(ledPin, HIGH);
         Serial.println("Motion detected.");
         contador = 1;
         Serial.println(contador);
         delay(50);
      }
      takeLowTime = true;
   }
   if(digitalRead(inputPin) == LOW) {
      if(takeLowTime){
         lowIn = millis();takeLowTime = false;
      }
      if(!lockLow && millis() - lowIn > pause) {
         PIRValue = 0;
         lockLow = true;
         Serial.println("Motion ended.");
         contador = 0;
         digitalWrite(ledPin, LOW);
         delay(50);
      }
   }
   return PIRValue;
}

void handleXbeeRxMessage(uint8_t *data, uint8_t length){
  // this is just a stub to show how to get the data,
  // and is where you put your code to do something with
  // it.
  for (int i = 0; i < length; i++){
    Serial.print(data[i]);
  }
    Serial.println();
}

void print32Bits(uint32_t dw){
  print16Bits(dw >> 16);
  print16Bits(dw & 0xFFFF);
}


void print16Bits(uint16_t w){
  print8Bits(w >> 8);
  print8Bits(w & 0x00FF);
}

void print8Bits(byte c){
  uint8_t nibble = (c >> 4);
  if (nibble <= 9)
    Serial.write(nibble + 0x30);
  else
    Serial.write(nibble + 0x37);
  nibble = (uint8_t) (c & 0x0F);
  if (nibble <= 9)
    Serial.write(nibble + 0x30);
  else
    Serial.write(nibble + 0x37);
}

// Función para evaluar temperatura y controlar pantalla 5110 Nokia
struct valores_DHT evalua_temperatura(int disp){
  int result;
  struct valores_DHT ab;
  //Calculamos la temperatura
  ab.valor_humedad = dht.readHumidity();  //Guarda la lectura de la humedad en la variable float h
  ab.valor_temperatura = dht.readTemperature();  //Guarda la lectura de la temperatura en la variable float t
  float h = ab.valor_humedad;
  float t = ab.valor_temperatura; 
  // Comprobamos si lo que devuelve el sensor es valido, si no son numeros algo esta fallando
  if (isnan(t) || isnan(h)) // funcion que comprueba si son numeros las variables indicadas 
  {
    Serial.println("Fallo al leer del sensor DHT"); //Mostramos mensaje de fallo si no son numeros
    result = 0;
  } else {
    //Mostramos mensaje con valores actuales de humedad y temperatura, asi como maximos y minimos de cada uno de ellos
    display.display();
    // Limpiamos display y buffer
    display.clearDisplay();
    
    //Comprobacion de maximos y minimos de humedad y temperatura
    if (maxh<h)
      maxh=h;
    if (h<minh)
      minh=h;
    if (maxt<t)
      maxt=t;
    if (t<mint)
      mint=t;
    if (disp == 1)
    {
      display.setTextSize(1);
      display.setTextColor(BLACK);
      display.setCursor(0,0);
      display.print("Max: ");
      display.print(maxt);
      display.print(" ");
      display.write(247);
      display.println("C");
      display.print("Min: ");
      display.print(mint);
      display.print(" ");
      display.write(247);
      display.println("C");
      display.setTextSize(4);
      display.setTextColor(BLACK);
      display.setCursor(0,20);
      display.print((int)t);
      display.setCursor(65,20);
      display.print("C");
      display.setTextSize(2);
      display.setTextColor(BLACK);
      display.setCursor(48,20);
      display.print("o");
      display.display(); 
     } else {
      display.setTextSize(1);
      display.setTextColor(BLACK);
      display.setCursor(0,0);
      display.print("Max: ");
      display.print(maxh);
      display.println(" % ");
      display.print("Min: ");
      display.print(minh);
      display.println(" %");
      display.setTextSize(4);
      display.setCursor(0,20);
      display.print((int)h);
      display.setCursor(60,20);
      display.print("H");
      display.drawBitmap(50, 36,  gotas, 8, 8, 1);
      display.drawBitmap(44, 40,  gotas, 8, 8, 1);
  
      display.setTextSize(2);
      display.setTextColor(BLACK);
      display.setCursor(48,20);
      display.print("%");
      display.display();
     }
  }
  result=1;
  return ab;
}

