<?php

// include db connect class
require_once __DIR__  . '/../db_connect.php';

define("ADD", 1);
define("REMOVE", 0);

$res = array();

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

	if($operation == REMOVE && count($friends) == 1 ) {
		$lst_of_couples = "( " . $lst_of_couples . " ) ";
  }

	return $lst_of_couples;
}

function array_to_id_id($friends, $userid, $operation)
{
	$lst_of_couples = "";
	foreach ($friends as $id => $name) {
		$lst_of_couples .= "($userid, $id), ";
	}

	$lst_of_couples = rtrim($lst_of_couples, ", ");

	if($operation == REMOVE && count($friends) == 1 ) {
		$lst_of_couples = "( " . $lst_of_couples . " ) ";
  }

	return $lst_of_couples;
}

function removeFriends($friends_to_remove, $userid)
{
	$id_and_name_rem = array_to_query_id_name($friends_to_remove,  REMOVE);
	$id_and_id_rem   = array_to_id_id($friends_to_remove, $userid, REMOVE);

  $remove_friends_q1 = "DELETE FROM NAMES WHERE (ID, NAME) IN " .  $id_and_name_rem ;
	$remove_friends_q2 = "DELETE FROM FRIENDSHIP(ID1, ID2) WHERE (ID1, ID2) IN " . $id_and_id_rem;
  $remove_friends_q3 = "DELETE FROM FRIENDSHIP(ID1, ID2) WHERE (ID2, ID1) IN " . $id_and_id_rem;

  mysql_query($remove_friends_q1);
  mysql_query($remove_friends_q2);
  mysql_query($remove_friends_q3);
}

function addFriends($friends_to_add, $userid)
{
  $id_and_name_add = array_to_query_id_name($friends_to_add , ADD);
	$id_and_id_add   = array_to_id_id($friends_to_add, $userid, ADD);

	$add_friends_q1 = "INSERT INTO NAMES(ID, NAME) VALUES " . $id_and_name_add . " ON DUPLICATE KEY UPDATE NAME = VALUES(NAME)";
  $add_friends_q2 = "INSERT INTO FRIENDSHIP(ID1, ID2) VALUES " . $id_and_id_add . " ON DUPLICATE KEY UPDATE ID1 = VALUES(ID1)";
	$add_friends_q3 =  "INSERT INTO FRIENDSHIP(ID2, ID1) VALUES " . $id_and_id_add . " ON DUPLICATE KEY UPDATE ID1 = VALUES(ID1)";

  $GLOBALS['addedID']=$GLOBALS['addedID'].",".$add_friends_q1;

  mysql_query($add_friends_q1);
  mysql_query($add_friends_q2);
  mysql_query($add_friends_q3);
}

// check for required fields
if (isset($_POST['jfriendslist'])  && isset($_POST['userid'] )  )
{
  $jflist = $_POST['jfriendslist']; //Get the friends list
  $userid = $_POST['userid']; //Get the user id

	$data = json_decode($jflist, true); //Decode the friends list

	$updatedFriends = array();
	for($i = 0; $i < count($data['data']); $i++)
	{
		$id  = $data['data'][$i]['id']; //Get all friends' ids
		$updatedFriends[$id] = $data['data'][$i]['name']; //Get all friends' names
	}

	// connecting to db
  $db = new DB_CONNECT();

	$user_friends_q = "SELECT ID1 AS ID, NAME FROM friendship f, names n WHERE f.ID1=n.ID AND f.ID2=" . $userid;
	$user_friends = mysql_query($user_friends_q);

  $saved_friends = array();
  while ($row = mysql_fetch_array($user_friends) ) {
  	$saved_friends[$row['ID']] = $row['NAME'];
	}

	$friends_to_remove = array_diff($saved_friends, $updatedFriends);
	$to_remove_chunks = array_chunk($friends_to_remove, 1, true);
	foreach ($to_remove_chunks as  $friends_to_remove) {
		removeFriends($friends_to_remove, $userid);
	}

  $friends_to_add =  array_diff($updatedFriends, $saved_friends);
	$to_add_chunks = array_chunk($friends_to_add, 1, true);
	foreach ($to_add_chunks as  $f) {
		addFriends($f, $userid);
	}

  $res['success'] = 1;
  echo json_encode($res);
}
else
{
  $res['result'] = 0;
  echo json_encode($res);
}

?>