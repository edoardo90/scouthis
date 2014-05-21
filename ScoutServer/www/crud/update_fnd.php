<?php

$addedNames = 0;
$addedID  = 0;
$addedFnd2 = 0;


function cmp($fndA, $fndB)
{
    return strcmp($fndA['id'] , $fndB['id']);
}

function sanitize($var)
{
	$var = str_replace("'", "\'", $var);
	$var = str_replace("\\'", "\'", $var);
	$var = str_replace("\\'", "\'", $var);
	return $var;
}

function array_to_query_id_name($friends, $operation)
{
	$num = 0;
	$lst_of_couples = "";
	foreach ($friends as $id => $name){
		$num ++;
		if ($num == 50)
			break;
		$name_sane = sanitize($name);
	    $lst_of_couples .= "($id, '$name_sane'), ";
	}

	
	$lst_of_couples = rtrim($lst_of_couples, ", ");

	if($operation == REMOVE && count($friends) == 1 )
		$lst_of_couples = "( " . $lst_of_couples . " ) ";
	

	return$lst_of_couples;
} 

function array_to_id_id($friends, $userid, $operation)
{
	$lst_of_couples = "";
	foreach ($friends as $id => $name) {
		$lst_of_couples .= "($userid, $id), ";
	}

	$lst_of_couples = rtrim($lst_of_couples, ", ");

	if($operation == REMOVE && count($friends) == 1 )
		$lst_of_couples = "( " . $lst_of_couples . " ) ";
	

	return$lst_of_couples;
}

function removeFriends($friends_to_remove, $userid)
{
	$id_and_name_rem = array_to_query_id_name($friends_to_remove,  REMOVE);
	$id_and_id_rem   = array_to_id_id($friends_to_remove, $userid, REMOVE);
    $remove_friends_q1 = "DELETE FROM NAMES 
    					 WHERE (ID, NAME) IN " .  $id_and_name_rem ;			    

	$remove_friends_q2 = "DELETE FROM FRIENDSHIP(ID1, ID2)
						  WHERE (ID1, ID2) IN " . $id_and_id_rem;
    
    $remove_friends_q3 = "DELETE FROM FRIENDSHIP(ID1, ID2)
						  WHERE (ID2, ID1) IN " . $id_and_id_rem;	

      if(count($friends_to_remove) > 0) {
    	
	    mysql_query($remove_friends_q1);
	    mysql_query($remove_friends_q2);
	    mysql_query($remove_friends_q3);
	 }
}



function addFriends($friends_to_add, $userid)
{

    $id_and_name_add = array_to_query_id_name($friends_to_add , ADD);
	$id_and_id_add   = array_to_id_id($friends_to_add, $userid, ADD);

	$add_friends_q1 =  "INSERT INTO NAMES(ID, NAME) " .
					   "VALUES " . $id_and_name_add .
					   " ON DUPLICATE KEY UPDATE " . 
					   "	 NAME = VALUES(NAME) ";
					   
    $add_friends_q2 =   "INSERT INTO FRIENDSHIP(ID1, ID2) 
					     VALUES " . $id_and_id_add .
  						 " ON DUPLICATE KEY  UPDATE ID1=VALUES(ID1) ";
					   	 
	
	$add_friends_q3 =  "INSERT INTO FRIENDSHIP(ID2, ID1) 
					   VALUES " . $id_and_id_add .
					   " ON DUPLICATE KEY  UPDATE ID1=VALUES(ID1) ";

   

	if(count($friends_to_add) > 0 ) {

	    $res = mysql_query($add_friends_q1);

		if($res )
			$GLOBALS['addedNames']=$GLOBALS['addedNames']+1;

	    $res2 = mysql_query($add_friends_q2);
	    if($res2) 
	    	$GLOBALS['addedID'] = $GLOBALS['addedID']  + 1 ;
	    mysql_query($add_friends_q3);
	    //$addedFnd2++;
	}

}


define("ADD", 1);
define("REMOVE", 0);

$response = array();
// array for JSON response
 
// check for required fields
if (isset($_POST['jfriendslist'])  && isset($_POST['userid'] )  )  {
    
    $jflist = $_POST['jfriendslist'];
    $userid = $_POST['userid'];

	$json_source = '{"data":[{"name":"Ed Garay 1","id":"32815152"}'  .
							 ',{"name":"Carolina 2     Lim","id":"57568557"}' .
							 ',{"name":"Dario     3   Maf","id":"234238557"}' .
							 ',{"name":"ZIO      4  Maf","id":"6635323"}' .
							 ',{"name":"bobO     5   Maf","id":"2345245"}' .
							 ',{"name":"BUNNY LLOLa    6    Maf","id":"9990900"}' .
							 ',{"name":"BUNNY LLOLa   7     Maf","id":"31231"}' .
							 ',{"name":"BUNNY LLOLa  8      Maf","id":"342342"}' .
							 ',{"name":"BUNNY LLOLa  9      Maf","id":"342142"}' .
							 ',{"name":"BUNNY LLOLa  10      Maf","id":"342242"}' .
							 ',{"name":"BUNNY LLOLa  11      Maf","id":"342343"}' .
							 ',{"name":"BUNNY LLOLa  12      Maf","id":"344442"}' .
							 ',{"name":"BUNNY LLOLa  13      Maf","id":"3477742"}' .
							 ',{"name":"BUNNY LLOLa  14      Maf","id":"3433442"}' .
							 ',{"name":"BUNNY LLOLa  15      Maf","id":"346666442"}' .
							 ',{"name":"BUNNY LLOLa  16      Maf","id":"3666442"}' .
							 ',{"name":"Danilo Culetto 17 ","id":"523594984"}]}'; 
	$json_source = $jflist;							 
	$data = json_decode($json_source, true);
	
	$updatedFriends = array();
	
	for($i = 0; $i < count($data['data']); $i++)
	{
		$id  = $data['data'][$i]['id'];
		$updatedFriends[$id] = $data['data'][$i]['name'];
		
	}
	
	// include db connect class
    require_once __DIR__  . '/../db_connect.php';
	// connecting to db
    $db = new DB_CONNECT();

	
	$user_friends_q = "SELECT ID, NAME 
					   FROM   NAMES " ;
					//"WHERE ID=" . $userid;
    

	$user_friends = mysql_query($user_friends_q);

    $saved_friends = array();
    $i =0;
    while ($row = mysql_fetch_array($user_friends) ) {
 
		$id =  $row['ID'];
		$saved_friends[$id] = $row['NAME'];
		$i++;		
	}					
	

	$friends_to_remove = array_diff($saved_friends, $updatedFriends);

	$friends_to_add =  array_diff($updatedFriends, $saved_friends );
	$foo = $friends_to_add;

	$to_add_chunks = array_chunk($friends_to_add, 4, true);
	$to_remove_chunks = array_chunk($friends_to_add, 4, true);
	
	foreach ($to_add_chunks as  $friends_to_add) {
		addFriends($friends_to_add, $userid);
	}
	
	foreach ($to_remove_chunks as  $friends_to_remove) {
		removeFriends($friends_to_remove, $userid);
	}		


	 $res = array('success' =>  1 );
	 $res['n_add'] = count ($foo);
	 $res['updated'] = count($updatedFriends);
	 $res['addednames'] = $GLOBALS['addedNames'];
	 $res['addedID'] = $GLOBALS['addedID'];
 	 echo json_encode($res);
	
}
else
{
	 $res = array('result' =>  0 );
	 echo json_encode($res);
	
}
 
?>