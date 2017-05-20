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
int tiene_sensor_hum_temp = 0;
int tiene_sensor_gas = 0;
int tiene_sensor_presencia = 0;
int tiene_sensor_inundacion = 0;
int tiene_sensor_luz = 0;

// Variables de estados
int estado_Gas = 1; // Inicialmente abierta la llave de paso; ordenes 1 y 2
int estado_Agua = 1; // Inicialmente abierta la llave de paso; ordenes 3 y 4
int estado_Riego = 0; // Inicialmente desconectada; ordenes 5 y 6
int estado_LucesBanyo = 0; // Inicialmente apagadas; ordenes 7 y 8
int estado_LucesGrupo2 = 0; // Incialmente apagada; ordenes 9 y 10
int estado_Persiana = 0; // Inicialmente apagado; ordenes 11 y 12

int movimiento_detectado = 0;

const int buttonPin = 2;
int buttonState = 0;
int boton_valor = 0;
int outputPinGas = 9;               // Pin que gobierna el Rele del gas (electroválvula)
int outputPinAgua = 30; // Pin que gobierna el rele del agua (electroválvula)
int outputPinRiego = 31; // Pin que gobierna el rele del riego (electroválvula)
int outputPinLucesBanyo = 13; // Pin que gobierna rele de luces grupo 1 (interruptor)
int outputPinLucesGrupo2 = 33; // Pin que gobierna rele de luces grupo 2 (interruptor)
int outputPinPersiana = 34; // Pin que gobierna rele de persiana (interruptor a motor)
int inputPinGas = 53; // Definimos el Pin de entrada para el sensor de gas
int releState = 0;              // Estado inicial del Rele
int BH1750_address = 0x23; // Dirección I2C
byte buff[2];
int ledPin = 13;                // LED de sensor PIR de presencia
int inputPin = 12;               // Pin de entrada para el sensor PIR
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

float ultTempEnv = 0; // Guardaremos ultimo valor enviado para compararlo
float ultHumEnv = 0; // Guardaremos ultimo valor enviado para compararlo
int lastInt=0;
long interval = 10000;   // intervalo del envio de temp. y hum. (en milisegundos) (10 segundos)

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
  Serial.begin(9600);
  
  pinMode(outputPinLucesBanyo, OUTPUT);
 
  // Inicializamos la línea serie del XBee
  xbee.setSerial(Serial);
  // Inicialización realizada
  
}

// Comenzamos el bucle de la placa Arduino
void loop() { 
    
  xbee.readPacket(500);
  Serial.println("Pasa por leer orden remitida");
  
  // Chequeamos lo recibido: 
  if (xbee.getResponse().isAvailable()) {
    if (xbee.getResponse().getApiId() == ZB_RX_RESPONSE) {
      xbee.getResponse().getZBRxResponse(zbRx);
      Serial.print("He recibido un ");
      Serial.println(zbRx.getData(0));
      
      switch(zbRx.getData(0)){
        /* Ordenes que puede recibir Arduino
        
          7.- Apertura de luces banyo (llaveLucesGrupo1) 4
          8.- Cierre de luces banyo (llaveLucesGrupo1) 4
        
        */
        case 7:
           Serial.println("He recibido un 7: llave luces grupo1 OFF");
           estado_LucesBanyo=1;
           // Cambia pin de rele de luces grupo1 a 1
           cambia_rele(estado_LucesBanyo, 4);
           break;
           
        case 8:
           Serial.println("He recibido un 8: llave luces grupo1 ON");
           estado_LucesBanyo=0;
           // Cambia pin de rele de luces grupo1 a 0
           cambia_rele(estado_LucesBanyo, 4);
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
     
      case 4:
        if (estado_rele == 0) {            // miramos si el estado es LOW
         digitalWrite(outputPinLucesBanyo, LOW);  // encendemos las luces correspondiente
         result=1;
        } 
        else{
          digitalWrite(outputPinLucesBanyo, HIGH);
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

