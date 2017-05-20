"""
xbee_asincrono.py
 
Copyright 2017
 
Carlos Ceamanos

Fichero que lee trama que le llega de Arduino
 
"""
from xbee import ZigBee
import os, sys
import base64
import xbee
import serial
import struct
import base64
import time
import Queue

# Libreria para el manejo de los hilos
import threading
import mysql.connector

# Importo libreria de logs
import logging

#from Examples.EchoClient import WhatsappEchoClient
# Importamos librerias para uso de notificaciones
from pushetta import Pushetta
from time import clock
from datetime import datetime, timedelta

# on the Raspberry Pi the serial port is ttyAMA0
PORT = '/dev/ttyUSB0'
BAUD_RATE = 9600

# The XBee addresses I'm dealing with
BROADCAST = '\x00\x00\x00\x00\x00\x00\xff\xff'
UNKNOWN = '\xff\xfe' # This is the 'I don't know' 16 bit address
# Constantes
USER='carlos'
HOST_OR_IP='dir_IP'
PORT_DB=3306
DB_NAME='db_name'
PASSWD='password'
addr = "addr1"
addr2= "addr2"
addrzbee="zbee_addr1"

freq = 9810 # 98.1 Mhz
b1 = freq & 0xff
b2 = freq >> 8
start_time_pres = 0
elapsed_time_pres = 0
start_time_gas = 0
elapsed_time_gas = 0
start_time_inun = 0
elapsed_time_inun = 0
enviada_alarm_gas = 0
enviada_alarm_pres = 0
enviada_alarm_inundacion = 0

paquetes = Queue.Queue()
temperatura = 0

# Abrimos puerto serie
ser = serial.Serial(PORT, BAUD_RATE)

# Conectamos al servidor de base de datos
try:
	parameters = {
		'user': USER,
		'host': HOST_OR_IP,
		'port': PORT_DB,
		'passwd': PASSWD,
	}
	db_conn = mysql.connector.connect(**parameters)
except mysql.connector.Error, e:
	raise Exception("ERROR: No puedo conectar al servidor MySQL!")

cursor = db_conn.cursor()

# Funcion que devuelve direccion en hexa 
def hex(bindata):
    	return ''.join('%02x' % ord(byte) for byte in bindata)


# Funcion que configura el cursor y ejecuta una orden sql en BBDD
def run_query(conn, query_str):
	results = None
	cur = conn.cursor(cursor_class=mysql.connector.cursor.MySQLCursorBufferedRaw)
	try:
		res = cur.execute(query_str)
	except mysql.connector.Error, e:
		cur.close()
		raise Exception("Query failed. " + e.__str__())
	try:
		results = cur.fetchall()
	except mysql.connector.errors.InterfaceError, e:
		if e.msg.lower() == "ningun resultado que encontrar.":
			pass # Este error quiere decir que no hay resultados.
		else: # de otro modo, levanta otra vez error
			raise e
	conn.commit()
	cur.close()
	return results

# Funcion que configura el mensaje de la alarma correspondiente	
def enviar_alarma(code,habitacion):
	if code==1:
		mensaje=("Alerta de gas en %s" % habitacion)
		print mensaje 
		#envia_alarma(mensaje)
		#controlar el tiempo para esta alarma y enviar cada minuto
	if code==2:
		mensaje=("Alerta de inundacion en %s" % habitacion)
		print mensaje
		#envia_alarma(mensaje)
		#controlar el tiempo para esta alarma y enviar cada 5 minutos
	if code==3:
		mensaje=("Alerta de presencia en %s" % habitacion)
		print mensaje
		#envia_alarma(mensaje)
		#controlar el tiempo para esta alarma y enviar cada 3 minutos

# Funcion que escribe los datos recogidos del sensor en la base de datos		
def escribe_datos_sensor(source_addr):
	insertar_datos_sensor(tipo_sensor)

# Funcion que envia alarma mediante app notificaciones pushetta
def envia_alarma(mensaje):
	# API Key You get after signup on www.pushetta.com
	API_KEY="pushetta_key(introducir la correspondiente)"
	p=Pushetta(API_KEY)
	p.pushMessage("Domotic_House",mensaje)


# Recibir trama
def mensajeRecibido(data):
	paquetes.put(data, block=False)
	print 'Tengo un paquete'

# Funcion con la que envio valores a Arduino correspondiente
def enviaPaquete(where, what):
	zb.send('tx',dest_addr_long = where, dest_addr = UNKNOWN, data = what)           
                 

# Rutina que envia paquetes a Arduino
def enviaPaqueteEncolado():
	print 'sending query packet'
	enviaPaquete(BROADCAST, '?\r')

# Manejo el paquete recibido
def manejoPaquete(data):
	print 'En manejoPaquete: ',
	print data['id'],
	if data['id'] == 'tx_status':
		print data['deliver_status'].encode('hex')
	elif data['id'] == 'rx':
		print data['rf_data'].encode('hex')
		print data['source_addr_long'].encode('hex')
		if 'source_addr' in data:
			rxList = data['rf_data'].split(',')
			temperatura = rxList[0]
			print rxList[0]
			print rxList[1]
			print rxList[2]
			print rxList[3]
			print rxList[4]
			print rxList[5]
	else:
		print 'Tipo de trama desconocido'
	return data

# Creo el elemento Xbee que se ejecuta en otro hilo
zb = ZigBee(ser, callback=mensajeRecibido)

# Create API object
#xbee = xbee.zigbee.ZigBee(ser)

# Fichero de logging
logging.basicConfig(filename="sample.log", level=logging.INFO)

# Prepara un objeto cursor con el metodo cursor()
last_time = 0

# Yowsup ha dejado de funcionar
#................Clave de Acceso a WhatsApp............................ 
#password = "passwd_whatsapp=" #Password dada al registrar el numero. 
#password = base64.b64decode(bytes(password.encode('utf-8'))) #Codificacion de Password para envio 
#username = '34telefono' #Numero de telefono para el inicio de secion. 
#keepAlive= False #Conexion persistente con el servidor.
#...................................................................... 
#whats = WhatsappEchoClient("34609694636", "ALERTA: ESCAPE DE GAS", keepAlive)
#whats.login(username, password) #Autentifica el dispositivo con el cliente de WhatsApp.

query_onoff = ("SELECT EST_DISP, COD_DISP, ADDR_ZBEE, COD1, COD0 FROM domotic_house.DISPOSITIVO WHERE CAMBIADO=1");
print(query_onoff)


# Hago cosas en el hilo principal
while True:
	try:	
		time.sleep(0.1)
		print ("Pasa principio")
		cursor.execute(query_onoff)
		# Fetch all the rows in a list of lists.
		dispositivos=[]
		print("Pasa por aqui")
		for row in cursor.fetchall():
			print("Pasa por aqui2")
    			dispositivos.append(row)
			if (row[0] == 1):
				print "EJECUTO 2"
				print row
				codigo=row[3].decode("string-escape")
				addr_envio=hex(row[2])
				print codigo
				actual_addr=row[2].decode('string-escape')
				#xbee.send("tx", dest_addr_long=addr, dest_addr="\xff\xfe", data=str(row[3]))
				zb.send("tx", dest_addr_long=addr, dest_addr="\xff\xfe", data=codigo)
				#ejecuta_orden(cod_disp,est_disp,addr_zbee, cod1)
			else:
				print "EJECUTO 1"
				print row
				codigo=row[4].decode("string-escape")
				addr_envio=hex(row[2])
				print codigo
				print addr_envio
				actual_addr=row[2].decode('string-escape')
				zb.send("tx", dest_addr_long=addr, dest_addr="\xff\xfe", data=codigo)
				#ejecuta_orden(cod_disp,est_disp,addr_zbee, cod0)
			print "Voy a actualizar estado de dispositivo "
			print row[1]
			query2 = ("UPDATE domotic_house.DISPOSITIVO SET CAMBIADO=0 WHERE COD_DISP='%s'" % row[1])
			cursor.execute(query2)
			db_conn.commit()
		db_conn.commit()

		# Falta meter codigo de la calefaccion

		# Programar la calefaccion, y dependiendo de si la temperatura actual es mayor o menor con la histeresis, disparar que se encienda o se apague
		query = ("SELECT EST_DISP, VAL_AUX1, VAL_AUX2 FROM domotic_house.DISPOSITIVO WHERE TIPO_DISP=5");
		print (query)
		results=run_query(db_conn, query)
		print (results)
		for registro in results:
			calefaccion = registro[0]
			histeresis = registro[1]
			temp_deseada = registro[2]
			# Imprimimos los resultados obtenidos
			print "CALEFACCION=%s" % (calefaccion)
			temp_disparo_high = float(temp_deseada) + float(histeresis)
			temp_disparo_low = float(temp_deseada) - float(histeresis)
			print temp_disparo_high
			print temp_disparo_low
			temperatura=23.0 
			estado_calefaccion=int(calefaccion)
			print(estado_calefaccion)

		# Miro si la calefaccion esta en manual o en automatico (para programacion de eventos en tabla de eventos
		#query = ("SELECT ESTADO FROM domotic_house.AUTOMATISMOS WHERE TIPO=1");
		#print (query)
		#results=run_query(db_conn, query)
		#print (results)
		#for registro in results:
		#	calefaccion_auto = registro[0]

		if (int(calefaccion) == 1):
			print "Pasa por calefaccion igual a 1"
			# Si esta encendida manual, enciendo por temperatura
			if (float(temperatura) < float(temp_disparo_low)):
				print "Enciendo calefaccion"
				zb.send("tx", dest_addr_long=addr, dest_addr="\xff\xfe", data="\x20")
				query_update = ("UPDATE domotic_house.DISPOSITIVO SET CAMBIADO=1 WHERE TIPO_DISP=5")
				cursor.execute(query_update)
				db_conn.commit()
				time.sleep(1)

			# En caso de que estando encendida pase del disparo bajo, la apago
			if (float(temperatura) > float(temp_disparo_high)):
				print "Apago calefaccion"
				zb.send("tx", dest_addr_long=addr, dest_addr="\xff\xfe", data="\x50")
				query_update = ("UPDATE domotic_house.DISPOSITIVO SET CAMBIADO=0 WHERE TIPO_DISP=5")
				cursor.execute(query_update)
				db_conn.commit()
				time.sleep(1)
		else:
			print "Nada"
		if (int(calefaccion) == 0):
			print "Pasa por calefaccion igual a 0"
			print "Apago calefaccion"
			zb.send("tx", dest_addr_long=addr, dest_addr="\xff\xfe", data="\x21")
			query_update = ("UPDATE domotic_house.DISPOSITIVO SET CAMBIADO=1 WHERE TIPO_DISP=5")
			cursor.execute(query_update)
			db_conn.commit()
			time.sleep(1)
		else:
			print "Nada"


		time.sleep(0.1)
		if paquetes.qsize() > 0:
			nuevoPaquete = paquetes.get_nowait()
                     # Trato el paquete
			datos = manejoPaquete(nuevoPaquete)
			# Recibo los valores de los sensores y los trato
			print "El valor de la temperatura es: "
			if 'source_addr' in datos:
				rxList = datos['rf_data'].split(',')
				temperatura = rxList[0]
				print rxList[0]
				print rxList[1]
				print rxList[2]
				print rxList[3]
				print rxList[4]
				print rxList[5]

				logging.debug("Voy a tratar los datos de los sensores.")
				# logging.info("Eston Mensaje de Informacion")
				# logging.error("Courriorror")
				# En datos tengo todos los valores actuales de los sensores
				temperatura = float(rxList[0])
				humedad = float(rxList[1])
				gas = float(rxList[2])
				luz = float(rxList[3])
				inundacion = float(rxList[4])
				movimiento = float(rxList[5])

				# Vemos la habitacion de procedencia de los datos de los sensores
				addr_value = "".join("{:02X}".format(ord(c)) for c in datos['source_addr_long'])
				addr_value_str = str(addr_value)
				
				query = ("SELECT NOM_HAB, COD_HAB FROM domotic_house.HABITACION WHERE ADDR_ZBEE='%s'" % addr_value_str)
				print (query)
				results=run_query(db_conn, query)
				print (results)
				for registro in results:
					habitacion = registro[0]
					cod_habitacion = registro[1]
				print habitacion
				print cod_habitacion
			
				# Obtenemos el estado de la alarma de presencia en la casa
				query = ("SELECT ESTADO FROM domotic_house.AUTOMATISMOS WHERE COD_AUT=4")
				print (query)
				results=run_query(db_conn, query)
				print (results)
				for registro in results:
					estado_alarma = registro[0]
				print estado_alarma

				# Evaluamos si hay que enviar alerta de gas a partir de valor y envio anterior
				if (gas > 45):
					if (enviada_alarm_gas == 0):
						enviar_alarma(1,habitacion)
						print 'Envio alarma de gas'
						enviada_alarm_gas = 1

					start_time_gas = time.clock()
					print start_time_gas
        	        
        				# Calculamos el tiempo que ha pasado
        				elapsed_time_gas = elapsed_time_gas + time.clock() - start_time_gas
        				print("Elapsed time: %0.10f seconds." % elapsed_time_gas)
					if (elapsed_time_gas > 60) and (enviada_alarm_gas == 1): 
						enviar_alarma(1,habitacion)
						print 'Envio alarma de gas'
						print elapsed_time_gas
				else:
					enviada_alarm_gas = 0


				# Evaluamos si hay que enviar alerta de presencia
				if (estado_alarma=="1") and (movimiento == 1):
					if (enviada_alarm_pres == 0):
						enviar_alarma(3,habitacion)
						print 'Envio alarma de presencia'
						enviada_alarm_pres = 1

					start_time_pres = time.clock()
					print start_time_pres
        	        
        				# Calculamos el tiempo que ha pasado
        				elapsed_time_pres = elapsed_time_pres + time.clock() - start_time_pres
        				print("Elapsed time: %0.10f seconds." % elapsed_time_pres)
					if (elapsed_time_pres > 60) and (enviada_alarm_pres == 1): 
						enviar_alarma(3,habitacion)
						print 'Envio alarma de presencia'
						print elapsed_time
				else:
					enviada_alarm_pres = 0

				
				# Evaluamos si hay que enviar alerta de inundacion a partir de valor y envio anterior
				if (inundacion > 1):
					if (enviada_alarm_inundacion == 0):
						enviar_alarma(2,habitacion)
						print 'Envio alarma de inundacion'
						enviada_alarm_inundacion = 1

					start_time_inun = time.clock()
					print start_time_inun
        	        
        				# Calculamos el tiempo que ha pasado
        				elapsed_time_inun = elapsed_time_inun + time.clock() - start_time_inun
        				print("Elapsed time: %0.10f seconds." % elapsed_time_inun)
					if (elapsed_time_inun > 60) and (enviada_alarm_inun == 1): 
						enviar_alarma(2,habitacion)
						print 'Envio alarma de inundacion'
						print elapsed_time_inun
				else:
					enviada_alarm_inundacion = 0

				print 'temperatura= ',temperatura
				print 'humedad= ',humedad
				print 'gas= ', gas
				print 'luz= ',luz
				print 'inundacion= ',inundacion
				print 'movimiento= ', movimiento
			
				# HAY QUE HACER SENTENCIA QUE ESCRIBE EN BBDD escribe_datos_sensor(sa) en SENSORES_ACTUALES
				query_update = ("UPDATE domotic_house.SENSORES_ACTUALES SET VAL_SEN='%f' WHERE TIP_SEN=1 AND COD_HAB='%d'" % (temperatura, int(cod_habitacion)))
				cursor.execute(query_update)
				db_conn.commit()
				query_update2 = ("UPDATE domotic_house.SENSORES_ACTUALES SET VAL_SEN='%f' WHERE TIP_SEN=2 AND COD_HAB='%d'" % (humedad, int(cod_habitacion)))
				cursor.execute(query_update2)
				db_conn.commit()
				query_update3 = ("UPDATE domotic_house.SENSORES_ACTUALES SET VAL_SEN='%f' WHERE TIP_SEN=4 AND COD_HAB='%d'" % (luz, int(cod_habitacion)))
				cursor.execute(query_update3)
				db_conn.commit()
				query_update4 = ("UPDATE domotic_house.SENSORES_ACTUALES SET VAL_SEN='%f' WHERE TIP_SEN=3 AND COD_HAB='%d'" % (gas, int(cod_habitacion)))
				cursor.execute(query_update4)
				db_conn.commit()
				query_update5 = ("UPDATE domotic_house.SENSORES_ACTUALES SET VAL_SEN='%f' WHERE TIP_SEN=5 AND COD_HAB='%d'" % (inundacion, int(cod_habitacion)))
				cursor.execute(query_update5)
				db_conn.commit()
				query_update6 = ("UPDATE domotic_house.SENSORES_ACTUALES SET VAL_SEN='%f' WHERE TIP_SEN=6 AND COD_HAB='%d'" % (movimiento, int(cod_habitacion)))
				cursor.execute(query_update6)
				db_conn.commit()

				ahora = datetime.now()
				print ahora.hour
				print ahora.minute
				print ahora.year
				if (ahora.hour > last_time):
					query_insert = ("INSERT INTO domotic_house.HIST_SENSORES (`COD_HAB`, `TIP_SEN`, `DESC_SEN`,`VAL_SEN`,`COD_SEN`,`TIME_HIST`) VALUES ('%s', 1, 'Sensor temperatura', '%f', 1, CURRENT_TIMESTAMP)" % (cod_habitacion, temperatura))
					cursor.execute(query_insert)
					db_conn.commit()
					query_insert2 = ("INSERT INTO domotic_house.HIST_SENSORES (`COD_HAB`, `TIP_SEN`, `DESC_SEN`,`VAL_SEN`,`COD_SEN`,`TIME_HIST`) VALUES ('%s', 2, 'Sensor humedad', '%f', 1, CURRENT_TIMESTAMP)" % (cod_habitacion, humedad))
					cursor.execute(query_insert2)
					db_conn.commit()

					last_time = ahora.hour

				# Execute the SQL command

	except KeyboardInterrupt:
		break

zb.halt()
ser.close()
