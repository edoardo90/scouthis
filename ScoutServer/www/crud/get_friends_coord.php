<?php
/*
 * Following code will create a new product row
 * All product details are read from HTTP GET Request
 */
 
// include db connect class
require_once __DIR__  . '/../db_connect.php';

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['userid']) && isset($_POST['lat'])  && isset($_POST['long']) ) {
  $user = $_POST['userid'];
  $lat = $_POST['lat'];
  $long = $_POST['long'];
}
else {
	$user = $_GET['userid'];
	$lat = $_GET['lat'];
  $long = $_GET['long'];
}

// connecting to db
$db = new DB_CONNECT();

// update your position on server
$update_position_query =  "INSERT INTO POSITION (ID, LATITUDE, LONGITUDE) VALUES ($user, $lat, $long)
					ON DUPLICATE KEY UPDATE	ID = $user, LATITUDE = $lat, LONGITUDE = $long, LASTACCESS = NOW() ";
$res = mysql_query($update_position_query);

$get_online_friends_q = "SELECT ID2, NAME, LATITUDE, LONGITUDE 
							FROM POSITION, FRIENDSHIP, NAMES
							WHERE FRIENDSHIP.ID2 = POSITION.ID
							AND  FRIENDSHIP.ID1 = $user
							AND  FRIENDSHIP.ID2 = NAMES.ID ";

$i_am_in_the_mood = false; //DEBUG : MOSTRO TUTTI E VIA
if($i_am_in_the_mood)
	$get_online_friends_q .= "	AND  LASTACCESS 
		 BETWEEN DATE_SUB( NOW() , INTERVAL 10 MINUTE ) 
		 AND NOW()";

$result = mysql_query($get_online_friends_q);

if (!empty($result)) {
  // check for empty result
  if (mysql_num_rows($result) > 0) {
  	$response["markers"] = array();
    while ($row = mysql_fetch_array($result))
    {
      $marker = array();
      $marker["id"] = $row["ID2"];
      $marker["name"] = $row["NAME"];
      $marker["latitude"] = $row["LATITUDE"];
      $marker["longitude"] = $row["LONGITUDE"];

      array_push($response["markers"], $marker);
  	}
    // success
    $response["success"] = 1;
    // echoing JSON response
    echo json_encode($response);
  }
  else
  {
    $response["success"] = 0;
    $response["message"] = "No one is online :(";

    // echo no users JSON
    echo json_encode($response);
  }
} else {
    // no product found
    $response["success"] = 0;
    $response["message"] = "No one is online ";

    // echo no users JSON
    echo json_encode($response);
}
?>