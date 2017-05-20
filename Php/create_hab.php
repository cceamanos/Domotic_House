<?php
	$host='localhost';
	$uname='user';
	$pwd='password';
	$db="db_name";

	$con = mysql_connect($host,$uname,$pwd) or die("connection failed");
	mysql_select_db($db,$con) or die("db selection failed");
	 
	$codigo=$_REQUEST['codigo'];
	$nombre=$_REQUEST['nombre'];
	$descripcion=$_REQUEST['descripcion'];
	$xbee=$_REQUEST['xbee'];



	$flag['code']=0;
	
	$result = mysql_query("SELECT COD_HAB FROM HABITACION WHERE COD_HAB = '$codigo'");

       $num_rows = mysql_num_rows($result); //numero de filas retornadas

        if ($num_rows > 0) {

            // el usuario existe 
	     $flag['code']=2; //ya existe y no se inserta


        } else {
            // no existe
            if($r=mysql_query("INSERT INTO HABITACION (COD_HAB, NOM_HAB, DES_HAB, ADDR_ZBEE) VALUES ('$codigo','$nombre','$descripcion','$xbee') ",$con)) 
	     {
		$flag['code']=1;
		
	     }
        }


	print(json_encode($flag));
	mysql_close($con);
?>