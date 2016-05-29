<?php

// contributeToStory
include("dbconnect.php");

$idstory = $_POST['idstory'];
$email = $_POST['email'];
$next = $_POST['next'];

$current_story = "uploads/" . $idstory . '.3gp';
$temp_name = "uploads/temp_" . $idstory . '.3gp';
rename($current_story, $temp_name);
unlink($current_story);
$target_path = "uploads/temp.mp3";

if (move_uploaded_file($_FILES['uploadedFile']['tmp_name'], $target_path)) {
    echo "The file has been uploaded";
} else {
    echo "There was an error uploading the file, please try again!";
}

$cmd = "(echo file $temp_name & echo file $target_path) > mylist.txt";
shell_exec($cmd);
$final_out = "uploads/" . $idstory . '.3gp';
$cmd = "D:\\ffmpeg\\bin\\ffmpeg -f concat -i mylist.txt -c copy $final_out";
$ret = shell_exec($cmd);
echo $ret;

$sql3 = "INSERT INTO "
        . "`contribution`(`story_idstory`, `user_email`,`next`) "
        . "VALUES ('$idstory', '$email', '$next')";
echo $sql3;
mysql_query($sql3);

$sql4 = "UPDATE `story` SET "
        . "`contribution_count` = contribution_count + 1 "
        . "WHERE `idstory` = '$idstory'";
echo $sql4;
mysql_query($sql4);

mysql_close($con);

unlink($temp_name);
unlink($target_path);
?>
