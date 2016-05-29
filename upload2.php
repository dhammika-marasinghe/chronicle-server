<?php

// Where the file is going to be placed 
$target_path = "uploads/";

/* Add the original filename to our target path.  
  Result is "uploads/filename.extension" */
//echo implode(" ",$_FILES);

echo "filename: " . $_FILES['uploadedFile']['tmp_name'];

$target_path = $target_path . basename($_FILES['uploadedFile']['name']);
//echo pathinfo($target_path, PATHINFO_EXTENSION);
//echo  $_FILES['uploadedFile']['size'];
//sleep(10);

echo $_POST['title'] . '\n<br>';
echo $_POST['email'];


//if(copy($_FILES['uploadedFile']['tmp_name'], $target_path)){
if (move_uploaded_file($_FILES['uploadedFile']['tmp_name'], $target_path)) {
    echo "The file " . basename($_FILES['uploadedFile']['name']) .
    " has been uploaded";
    // chmod ("uploads/".basename( $_FILES['uploadedFile']['name']), 0644);
} else {
    echo "There was an error uploading the file, please try again!";
    echo "filename: " . basename($_FILES['uploadedFile']['name']);
    echo "target_path: " . $target_path;
    echo "\n\n" . $_FILES['uploadedFile']['error'];
}
?>