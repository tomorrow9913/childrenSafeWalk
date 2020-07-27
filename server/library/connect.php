<?php  
include('library/library.php');

function connect() {
    $servername = "localhost";
    $username = "powerpower";
    $password = "hwansuya!";
    $dbname = "powerpower";
    
    // Create connection
    $conn = new mysqli($servername, $username, $password, $dbname);
    
    // Check connection
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
        echo "error!";
    } 
    mysqli_set_charset($conn, "utf8");  

    return $conn;
}


function disconnect($conn) {
    mysqli_close($conn); 
}

function checkSession($conn, $session) {
    $sql = "SELECT * from session where session='$session'";
    $result = mysqli_query($conn, $sql);
    
    $count = mysqli_num_rows($result);
    if ($count > 0) {
        return true;
    }else{
        return false;
    }
}

function checkLogin($conn, $id, $pwd) {
    $sql = "SELECT EXISTS (SELECT * from user where userId='$id' and userPwd='$pwd')";
    $result = mysqli_query($conn, $sql);

    $login = mysqli_fetch_array($result)[0];
    if ($login == 0){
        return false;
    }else {
        return true;
    }
}

function makeSession($conn, $id, $pwd) {
    if (!checkLogin($conn, $id, $pwd)) {
        return "";
    }

    $sql = "select session from session where userId='$id'";
    $result = mysqli_query($conn, $sql);
    $datas = mysqli_num_rows($result);
    
    if ($datas > 0){
        $row = mysqli_fetch_array($result);
        removeSession($conn, $id, $row[0]);
    }
    // echo $datas;

    $session = GenerateString(32);
    $sql = "INSERT INTO session(userId, session) values('$id', '$session')";
    $result = mysqli_query($conn, $sql);
    // echo mysqli_error($conn);
    // echo $session;
    return $session;
}


function removeSession($conn, $id, $session) {
    if (checkSession($conn, $session)) {
        $sql = "DELETE FROM session WHERE userId = '$id'";
        mysqli_query($conn, $sql);
        return true;
    }else {
        return false;
    }
}

?>