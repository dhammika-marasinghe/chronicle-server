<?php

include("dbconnect.php");

$sql = "SELECT * FROM `user` ORDER BY email";

$result = mysql_query($sql);

$rows = array();
while ($r = mysql_fetch_assoc($result)) {
    $rows['users'][] = $r;
}

echo json_encode($rows);


mysql_close($con);
?>