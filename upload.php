<?php
// createStory
include("dbconnect.php");

$idstory = 0;
$title = $_POST['title'];
$email = $_POST['email'];
$next = $_POST['next'];

$sql = "INSERT INTO "
        . "`story`(`title`, `contribution_count`, `state`, `view_count`, `user_email`) "
        . "VALUES ('$title', 0, 'Ongoing', 0, '$email')";
echo $sql;
mysql_query($sql);

$sql2 = "SELECT MAX(idstory) AS idstory FROM story";
$result = mysql_query($sql2);
while ($row = mysql_fetch_array($result)) {
    $idstory = $row['idstory'];
}
echo $idstory;
$target_path = "uploads/";
$target_path = $target_path . $idstory . '.mp3';

if (move_uploaded_file($_FILES['uploadedFile']['tmp_name'], $target_path)) {
    echo "The file " . basename($_FILES['uploadedFile']['name']) .
    " has been uploaded";
    //^^mp3
    $cmd = "(echo file $target_path) > mylist.txt";
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
} else {
    echo "There was an error uploading the file, please try again!";
    echo "filename: " . basename($_FILES['uploadedFile']['name']);
    echo "target_path: " . $target_path;
    echo "\n\n" . $_FILES['uploadedFile']['error'];
}
mysql_close($con);
unlink($target_path);//mp3 remove
?>
