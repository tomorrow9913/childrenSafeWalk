<?php
include('./library/connect.php');

$conn = connect();

$rawPOST = file_get_contents("php://input");
$decode = json_decode($rawPOST, true);

$result = array();
if ($decode == null){
    $json["result"] = "fail";
    $json["comment"] = "json error";
    echo(json_encode($json));
    disconnect($conn);
    return;
}

$session = $decode['session'];
$locationLat = $decode['latitude'];
$locationLong = $decode['longitude'];

$level = $decode['level'];

$tag = $decode['tag'];
$tag = implode(',', $tag);

if (checkSession($conn, $session)) {
    $sql = "INSERT INTO pingInfo(level, tag, latitude, longitude) values('$level', '$tag', $locationLat, $locationLong)";
    if (!mysqli_query($conn, $sql)) {
        $result["result"] = "fail";
        $result["comment"] = mysqli_error($conn);
    }else {
        $sql = "SELECT id from pingInfo ORDER BY id DESC";
        $query = mysqli_query($conn, $sql);
        $row = (int)mysqli_fetch_array($query)[0];
        $result["result"] = "success";
        $result["id"] = $row;
    }
}else {
    $result["result"] = "fail";
    $result["comment"] = "session is expired";
}

echo(json_encode($result));

disconnect($conn);
?>