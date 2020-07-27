<?php
function GenerateString($length)  
{  
    $characters  = "0123456789";
    $characters .= "abcdefghijklmnopqrstuvwxyz";
    $characters .= "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
    
    $string_generated = "";  
    
    $nmr_loops = $length;  
    while ($nmr_loops--)  
    {  
        $string_generated .= $characters[mt_rand(0, strlen($characters) - 1)];  
    } 
    return $string_generated;  
}


function makePolygon($pos) {
    $result = "POLYGON((";
    $length = count($pos);
    if ($length <= 2) {
        return "";
    }

    if ($pos[0]['latitude'] != $pos[$length - 1]['latitude'] ||
        $pos[0]['longitude'] != $pos[$length - 1]['longitude']) {
        array_push($pos, $pos[0]);
    }

    foreach ($pos as $value) {
        $lat = $value['latitude'];
        $long = $value['longitude'];
        $result .= "$long $lat,";
    }
    $result = substr($result, 0, -1);
    $result .= "))";
    return $result;
}

function checkJson($json) { 

}

?>