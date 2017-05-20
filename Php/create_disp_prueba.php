<?php
	$host='localhost';
	$uname='user';
	$pwd='password';
	$db="db_name";

	$con = mysql_connect($host,$uname,$pwd) or die("connection failed");
	mysql_select_db($db,$con) or die("db selection failed");
	 
	//$codigo=$_REQUEST['codigo'];
	//$cod_hab=$_REQUEST['cod_hab'];
	//$descripcion=$_REQUEST['descripcion'];
	//$tipo_disp=$_REQUEST['tipo_disp'];
	//$estado_ini=$_REQUEST['estado_ini'];
	$codigo="C3";
	$cod_hab="1";
	$descripcion="Luz pie";
	$tipo_disp="20";
	$estado_ini="1";




	$flag['code']=0;
	
	$result = mysql_query("SELECT COD_DISP FROM DISPOSITIVO WHERE COD_DISP = '$codigo'");

       $num_rows = mysql_num_rows($result); //numero de filas retornadas

        if ($num_rows > 0) {

            // el usuario existe 
	     $flag['code']=2; //ya existe y no se inserta


        } else {
		// Realizar una consulta MySQL
		if (!strcmp($tipo_disp, "10"))
		{
			if (!strcmp($estado_ini, "0"))
			{
				 // no existe
           			 if($r=mysql_query("INSERT INTO DISPOSITIVO (COD_DISP, DESC_DISP, TIPO_DISP, VAL_AUX1, COD_HAB ) VALUES ('$codigo','$descripcion','$tipo_disp',0,'$cod_hab') ",$con)) 
	     			{,
					$flag['code']=1;
		
	     			}
			}
			else
			{	
				// no existe
           			 if($r=mysql_query("INSERT INTO DISPOSITIVO (COD_DISP, DESC_DISP, TIPO_DISP, VAL_AUX1, COD_HAB ) VALUES ('$codigo','$descripcion','$tipo_disp',100,'$cod_hab') ",$con)) 
	     			{
					$flag['code']=1;
		
	     			}
			}
		}
		else
		{
			 // no existe
            		if($r=mysql_query("INSERT INTO DISPOSITIVO (COD_DISP, DESC_DISP, TIPO_DISP, EST_DISP, COD_HAB ) VALUES ('$codigo','$descripcion','$tipo_disp,'$estado_ini','$cod_hab') ",$con)) 
	     		{
				$flag['code']=1;
		
	     		}
		}
           
        }


	print(json_encode($flag));
	mysql_close($con);
?>
