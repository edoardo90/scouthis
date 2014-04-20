<?php
 
    // include db connect class
    require_once __DIR__  . '/../db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
	
	
    mysql_query("DELETE FROM POSITION WHERE 1");
    mysql_query("DELETE FROM NAMES WHERE 1");
    mysql_query("DELETE FROM FRIENDSHIP WHERE 1");

?>