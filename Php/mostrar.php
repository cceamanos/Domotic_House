<?php 
mysql_connect("localhost", "user","passwd"); 
mysql_select_db("domotic_house"); 
 
$pregunta = "SELECT HABITACION, TEMPERATURA, HUMEDAD FROM TEMPERATURAS"; 
 
$sql=mysql_query($pregunta); 
while($row=mysql_fetch_assoc($sql)) 
$output[]=$row; print(json_encode($output)); mysql_close(); 
?> 
