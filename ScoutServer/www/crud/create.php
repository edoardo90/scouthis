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
if (isset($_GET['friend1']) && isset($_GET['friend2']) && isset($_GET['lat'])  && isset($_GET['long']) )
{
  $friend1 = $_GET['friend1'];
  $friend2 = $_GET['friend2'];
  $lat = $_GET['lat'];
  $long = $_GET['long'];

  // connecting to db
  $db = new DB_CONNECT();
	
	$update_position_query =  "INSERT INTO POSITION
						(ID, ONLINE, LATITUDE, LONGITUDE)
						VALUES
						($friend1, 1, $lat, $long)
						ON DUPLICATE KEY UPDATE
						ONLINE = 1,
						LATITUDE = $lat,
						LONGITUDE = $long  ";

	//update position
	$res = mysql_query($update_position_query);
	
	// mysql inserting a new row
    $result = mysql_query("INSERT INTO FRIENDSHIP(ID1, ID2) VALUES('$friend1', '$friend2')
						  ON DUPLICATE KEY UPDATE
						  ID1 = $friend1,
						  ID2 = $friend2 ");
	
	$get_online_friends_q = "SELECT ID2, LATITUDE, LONGITUDE 
								FROM POSITION, FRIENDSHIP
								WHERE POSITION.ONLINE =1
								AND  FRIENDSHIP.ID2 = POSITION.ID";
								/// DEBUG ONLY !!! AND  FRIENDSHIP.ID1 = $friend1";

	
	
	$result = mysql_query($get_online_friends_q);
	
	
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
			
			$response["markers"] = array();
			
            while ($row = mysql_fetch_array($result)){
 
				$marker = array();
				$marker["id"] = $row["ID2"];
				$marker["latitude"] = $row["LATITUDE"];
				$marker["longitude"] = $row["LONGITUDE"];
				
				array_push($response["markers"], $marker);
				
			}
			// success
            $response["success"] = 1;
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
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
								
	
	
 
    }
?>