<?

// пример запроса
// vmesteonline.ru/send.php?from=a@a.ru&password=123&to=b@b.ru&cc=asd&subject=asd&body=asdfgh


$urlArray = explode("?",$_SERVER[REQUEST_URI]);
$partsArray = explode('&',$urlArray[1]);
$len = count($partsArray);
for($i = 0; $i < $len ; $i++){
	$valuePartsArray = explode('=',$partsArray[$i]);
	switch($valuePartsArray[0]){
		case 'from':
			$from = $valuePartsArray[1];
		case 'password':
			$password = $valuePartsArray[1];
		case 'to':
			$to = $valuePartsArray[1];
		case 'cc':
			$cc = $valuePartsArray[1];
		case 'subject':
			$subject = $valuePartsArray[1];
		case 'body':
			$body = $valuePartsArray[1];	
	}
	
}
//echo "123 ".$from." ".$password." ".$to." ";

$message = '<html><head>
 <title>'.$subject.'</title>
</head>
<body>'.$body.'<br> Пароль: '.$password.'</body>
</html>';

$headers  = "Content-type: text/html; charset=utf-8 \r\n";
$headers .= "From: ".$from."\r\n";
$headers .= "Bcc: ".$cc."\r\n";

mail($to, $subject, $message, $headers);

?>