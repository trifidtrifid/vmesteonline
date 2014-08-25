<?

// пример запроса
// vmesteonline.ru/send.php?from=a@a.ru&password=123&to=b@b.ru&cc=asd&subject=asd&body=asdfgh

$message = '<html><head>
 <title>'.$_GET['subject'].'</title>
</head>
<body>'.$_GET['body'].'<br> Пароль: '.$_GET['password'].'</body>
</html>';

$headers  = "Content-type: text/html; charset=utf-8 \r\n";
$headers .= "From: ".$_GET['from']."\r\n";
$headers .= "Bcc: ".$_GET['cc']."\r\n";

mail($_GET['to'], $_GET['subject'], $message, $headers);

?>