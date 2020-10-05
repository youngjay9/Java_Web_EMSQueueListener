
<!DOCTYPE html>
<html>
<head>	
	
<!--for ie-->
<meta http-equiv="X-UA-Compatible" content="IE=EDGE; IE=10; IE=9; IE=8;" />
<meta charset="UTF-8">

<!-- <meta name="viewport" content="width=device-width,initial-scale=1.0"> -->
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />

</head>

<body>

<h2>開發環境 CPC模擬中油APP 使用 form post 方式呼叫OB 小板</h2>

<form action="http://10.242.136.114:8080/api/cpc/init-bind-card" method="post" >


  <input type="button" value="Test" onClick="decodeURIComponentSafe()">
</form> 

</body>

<script>
  function decodeURIComponentSafe() {
    var s = 'OUQ2ODQ2QTM4RDdENTg3RENFOEYzMzM0RTMwMTlEOUI5MTNCRjAzOTMzMkM0QzgzQTM0MDc0OTgyRDBFMTYyOTlENTlFRjdBRUIxMUJCMDlFMUVGQzhCOUQ5QTg2NDIwQTkxOTBFN0JBOUQzRDVCNjgzMjhERkJDQjUxMDk4N0IwQjlEMzI0RTk2RjI1QUQ2NUM4MDQxRDlGNUI4RjdCQw==';
	
    console.log(decodeURIComponent(s.replace(/%(?![0-9][0-9a-fA-F]+)/g, '%25')));
	
	console.log(encodeURIComponent(s))
}
</script>

</html>
