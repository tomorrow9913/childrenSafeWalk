<?php
  
   $file_path = "./image/";
   // 파일명 가져오기
   $filename = basename($_FILES['uploaded_file']['name'];
   // 확장자 제거
   $filename = substr($filename, 0, strrpos($filename, ".")); 

   $file_path = $file_path.$filename.'.png');

   $result = array();
   if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path)) {
      $result['result'] = 'success';
   } else{
      $result['result'] = 'fail';
   }
   echo(json_encode($result));
?>