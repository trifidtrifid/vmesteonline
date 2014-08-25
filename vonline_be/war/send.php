<?

// пример запроса
// vmesteonline.ru/send.php?from=a@a.ru&to=b@b.ru&cc=asd&subject=asd&body=asdfgh

$message = '<html><head>
 <title>'.$_GET['subject'].'</title>
</head>
<body>'.$_GET['body'].'</body>
</html>';

$headers  = "Content-type: text/html; charset=utf-8 \r\n";
$headers .= "From: ".$_GET['from']."\r\n";
$headers .= "Bcc: ".$_GET['cc']."\r\n";
$headers .= "To: ".$_GET['to']."\r\n";
$newsubject = '=?UTF-8?B?'.base64_encode($_GET['subject']).'?=';

mail($_GET['to'], $newsubject, $message, $headers);

?>OK