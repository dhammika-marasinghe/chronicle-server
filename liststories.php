<?php

include("dbconnect.php");

$sql = "SELECT * FROM `story` ORDER BY idstory DESC";

$result = mysql_query($sql);

$rows = array();
while ($r = mysql_fetch_assoc($result)) {
    $rows['stories'][] = $r;
}

echo json_encode($rows);


mysql_close($con);
?>