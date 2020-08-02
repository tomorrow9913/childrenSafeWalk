<?php
function resize_image($file, $newfile, $w, $h) {
    list($width, $height) = getimagesize($file);
    if(strpos(strtolower($file), ".jpg"))
       $src = imagecreatefromjpeg($file);
    else if(strpos(strtolower($file), ".png"))
       $src = imagecreatefrompng($file);
    else if(strpos(strtolower($file), ".gif"))
       $src = imagecreatefromgif($file);
    $dst = imagecreatetruecolor($w, $h);
    imagecopyresampled($dst, $src, 0, 0, 0, 0, $w, $h, $width, $height);
    if(strpos(strtolower($newfile), ".jpg"))
       imagejpeg($dst, $newfile);
    else if(strpos(strtolower($newfile), ".png"))
       imagepng($dst, $newfile);
    else if(strpos(strtolower($newfile), ".gif"))
       imagegif($dst, $newfile);
}

$id = $_GET['id'];
$cache = file_exists('./cache/'.$heightid.'.png');
$noneCache = file_exists('./image/'.$id.'.png');

resize_image('./image/image.png', './cache/image.png', 200, 200);
if ($noneCache && !$cache) {
    resize_image('./image/'.$id.'.png', './cache/'.$id.'.png', 200, 200);
}

if ($cache) {
    header('Location: ./cache/'.$id.'.png');
}else {
    header('Location: ./cache/image.png');
}

?>
