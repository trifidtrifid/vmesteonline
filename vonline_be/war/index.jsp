
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.Group"%>
<%@ page import="com.vmesteonline.be.Rubric"%>
<%@ page import="com.vmesteonline.be.messageservice.TopicListPart"%>
<%@ page import="com.vmesteonline.be.messageservice.Topic"%>
<%@ page import="com.vmesteonline.be.ShortUserInfo"%>
<%@ page import="com.vmesteonline.be.MessageServiceImpl"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>
<%@ page import="java.util.ArrayList"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
    HttpSession sess = request.getSession();
out.print('1');

    try {
        AuthServiceImpl.checkIfAuthorised(sess.getId());
        //response.sendRedirect("/main");

    } catch (InvalidOperation ioe) {
        //return;
    }


%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="Startup Responsive Landing page Template for startup, web services, newsletter signup, lead generation etc..">
<title>ВместеОнлайн</title>

<link href="css/landing/bootstrap.css" rel="stylesheet">

<link href="css/landing/startup.css" rel="stylesheet">

<!--[if lt IE 9]>
  <script src="js/forum/landing/html5shiv.js"></script>
  <script src="js/forum/landing/respond.min.js"></script>
<![endif]-->

    <link rel="shortcut icon" href="i/landing/vmesteonline.png">
<script src="js/forum/landing/pace.js"></script>


    <link href='http://fonts.googleapis.com/css?family=Open+Sans:400italic,700italic,400,700&subset=latin,cyrillic,cyrillic-ext' rel='stylesheet' type='text/css'>

</head>
<body>
	<div class="preloader"></div>
    <div class="container position-relative">
        <a href="login.html" class="btn btn-success btn-lg go-to-login">Войти</a>
    </div>
	<main id="top" class="masthead" role="main">
	<div class="container">
		<div class="logo">
			<a href="#"><img src="i/landing/vmesteonline.logo.500.png" alt="startup-logo"></a>
		</div>
		<h1>
			Сайт Вашего дома. 1<br>
		</h1>

		<div class="row">
			<div class="col-md-6 col-sm-12 col-md-offset-3 subscribe">
				<form class="form-horizontal" role="form" action="subscribe.php" id="subscribeForm" method="POST">
					<div class="form-group">
						<div class="col-md-7 col-sm-6 col-sm-offset-1 col-md-offset-0">
							<input class="form-control input-lg" name="email" type="email" id="address" placeholder="Укажите Ваш email" data-validate="validate(required, email)" required="required">
                            <br>
							<input class="form-control input-lg active" name="code" type="text" id="code" placeholder="Укажите Ваш код" required="required">
                            <a href="/request.html" class="no-code-link">Нет кода?</a>
					</div>
						<div class="col-md-5 col-sm-4">
							<button type="submit" class="btn btn-success btn-lg">ПОДКЛЮЧИТЬСЯ</button>
						</div>
					</div>
				</form>
				<span id="result" class="alertMsg"></span>
			</div>
		</div>
		<a href="#explore" class="scrollto">
			<p>Подробнее...</p>
			<p class="scrollto--arrow">
				<img src="i/landing/scroll_down.png" alt="scroll down arrow">
			</p>
		</a>
	</div>
	</main>
	<div class="container" id="explore">

		<!-- Features Section -->
		<div class="section-title">
			<h2>Некоторые возможности</h2>
			<h4>конфиденциальной социальной сети ВместеОнлайн</h4>
		</div>
		<section class="row features" align="center">

			<!-- Feature 04 -->

			<div class="col-sm-6 col-md-3">
				<div class="thumbnail">
					<img src="i/landing/flat-feature-icon-4.png" alt="analytics-icon">
					<div class="caption">
						<h3>Общение</h3>
						<p>Соседи могут посоветовать хороший магазин или дать отзыв о детском саде рядом с домом</p>
					</div>
				</div>
			</div>

			<!-- Feature 01 -->
			<div class="col-sm-6 col-md-3">
				<div class="thumbnail">
					<img src="i/landing/flat-feature-icon-1.png" alt="analytics-icon">
					<div class="caption">
						<h3>Голосования и обсуждения</h3>
						<p>Принимайте участие в обсуждениях собственников жилья. Или хотя бы будьте в курсе</p>
					</div>
				</div>
			</div>


			<!-- Feature 02 -->
			<div class="col-sm-6 col-md-3">
				<div class="thumbnail">
					<img src="i/landing/flat-feature-icon-2.png" alt="analytics-icon">
					<div class="caption">
						<h3>Узнать соседей</h3>
						<p>Знакомство с соседями может быть полезным. И просто любопытно узнать как можно называть Вашего соседа?</p>
					</div>
				</div>
			</div>

		</section>

	</div>

	<!-- Container -->
	<div class="container">

		<!-- FAQ -->
		<div class="section-title">
			<h5>Подробнее о главном</h5>
		</div>
		<section class="row faq breath">
			<div class="col-md-6">
				<h6>Безопасность</h6>
				<ul>
					<li>Каждый Ваш сосед подтверждает свое местожительства</li>
					<li>Все данные передаются по зашифрованному протоколу https</li>
					<li>Вы сами выбираете тех, кто может видеть Ваши сообщения</li>
					<li>Зарегистрироваться без кода не возможно</li>
				</ul>
				<h6>Приватность</h6>
				<ul>
					<li>Мы никогда не будем предоставлять информацию для спама третьим лицам</li>
					<li>Ваши данные никогда не попадут в Google, Yandex или другие поисковые системы</li>
					<li>Вы сами выбираете, какую информацию о Вас видят остальные</li>
				</ul>

			</div>
			<div class="col-md-6">
				<h6>Комфорт</h6>
				<ul>
					<li>Отключение электричества или горячей воды - больше не будут неожиданными</li>
					<li>Вы указываете только ту информацию о себе, какую захотите</li>
					<li>Здороваясь с соседями в лифте, Вы будете знать их имя.</li>
				</ul>
				<h6>Польза</h6>
				<ul>
					<li>Соседи могут посоветовать хорошее кафе рядом с домом</li>
					<li>Совместные с соседями оптовые покупки позволят выйграть в цене и качестве</li>
					<li>Инициативу отремонтировать детскую прощадку или организовать парковку - может поддержать больше людей, чем вы думаете</li>
				</ul>
			</div>
		</section>
		<!-- // End FAQ -->

	</div>
	<!-- // Container Ends -->

	<!-- Grey Highlight Section -->
	<div class="highlight testimonials">
		<div class="container">

			<!-- Testimonials -->
			<div class="section-title">
				<h5>Что о нас говорят?</h5>
			</div>
			<section class="row breath">
				<div class="col-md-6">
			
					<div class="testblock">Ого! Наконец-то все соседи на одном сайте :) Получается, что могу написать всем в подъезде, а могу и всем во дворе!</div>
                                        <div class="clientblock">
                                                <img src="i/landing/anna.jpg" alt=".">
                                                <p>
                                                        <strong>Анна</strong>
                                                </p>
                                        </div>

				<br/><br/><br/>
				
					<div class="testblock">Хорошая идея, теперь здесь будут все наши объявления, читайте!</div>
					<div class="clientblock">
						<img src="i/landing/customer-img-1.jpg" alt=".">
						<p>
							<strong>ТСЖ</strong>
						</p>
					</div>
				</div>
				<div class="col-md-6">
					<div class="testblock">	Главное, что только для жильцов. Спам на публичных сайтах уже достал. </div>
					<div class="clientblock">
						<img src="i/landing/customer-img-2.jpg" alt=".">
						<p>
							<strong>Михаил</strong> 
						</p>
					</div>
				</div>

  				<div class="col-md-6">
                                        <div class="testblock"> Надеюсь, все подключатся. Вообще, эти постоянные объявления типа "воды не будет", "завтра чистка снега" и т.д. было бы конечно удобно получать в виде уведомлений </div>
                                        <div class="clientblock">
                                                <img src="i/landing/c4.jpg" alt=".">
                                                <p>
                                                        <strong>Александр</strong>
                                                </p>
                                        </div>
                                </div>



			</section>
			<!-- // End Testimonials -->

		</div>
	</div>
	<!-- // End Grey Highlight Section -->

	<main class="footercta" role="main">
	<div class="container">

		<h1>Сайт Вашего дома.</h1>
		<div class="row">
			<div class="col-md-12 breath text-center">
				<a href="#top" class="btn btn-success btn-lg gototop">ПОДКЛЮЧИТЬСЯ</a>
			</div>
		</div>
	</div>
	</main>

	<!-- Container -->
	<div class="container">
		<section class="row breath">
			<div class="col-md-12 footerlinks">
				<p>
					&copy; 2014 ВместеОнлайн<br><a href="mailto:info@vmesteonline.ru">info@vmesteonline.ru</a><br>Санкт-Петербург
				</p>
			</div>
		</section>
		<!-- // End Client Logos -->

	</div>
	<script src="js/forum/landing/jquery.js"></script>
	<script src="js/forum/landing/bootstrap.js"></script>
	<script src="js/forum/landing/easing.js"></script>
	<script src="js/forum/landing/typer.js"></script>
	<script src="js/forum/landing/nicescroll.js"></script>
	<script src="js/forum/landing/ketchup.all.js"></script>

    <!-- файлы thrift -->
    <script src="js/thrift.js" type="text/javascript"></script>
    <script src="gen-js/bedata_types.js" type="text/javascript"></script>
    <script src="gen-js/authservice_types.js" type="text/javascript"></script>
    <script src="gen-js/AuthService.js" type="text/javascript"></script>
    <!-- -->

	<!-- Typer -->

	<script>
		$(function() {
			$('[data-typer-targets]').typer();
		});
	</script>

	<!-- Scroll to Explore -->

	<script>
		$(function() {
			$('.scrollto, .gototop').bind('click', function(event) {
				var $anchor = $(this);
				$('html, body').stop().animate({
					scrollTop : $($anchor.attr('href')).offset().top
				}, 1500, 'easeInOutExpo');
				event.preventDefault();
			});
		});
	</script>


	<!--============== SUBSCRIBE FORM =================-->

	<script>
		$(document).ready(function() {
            var transport = new Thrift.Transport("/thrift/AuthService");
            var protocol = new Thrift.Protocol(transport);
            var authClient = new com.vmesteonline.be.AuthServiceClient(protocol);

            $('.no-code-link').click(function(e){
                $(this).addClass('clicked');
                var inputCode = document.getElementById("code");
                inputCode.removeAttribute('required');
                inputCode.setAttribute('class','form-control input-lg passive');
            });

            $('#subscribeForm').ketchup().submit(function() {
				  if ($(this).ketchup('isValid')) {
					var action = $(this).attr('action');
                    var email = $('#address').val();
                    var code = $('#code').val();
					$.ajax({
						url : action,
						type : 'POST',
						data : {
							email : email,
							code : code,
                            isOnlyAddUser: true
						},
						success : function(data) {
                            //alert("111 "+data);

                            if (data == 'Неверный код !' && !$('#code').hasClass('passive')){
                                $('#result').html(data);
                            }else{

                                var userLocation = authClient.checkInviteCode(code);
                                document.location.replace('registration.html#email='+ email +';mapUrl='+userLocation.mapUrl+";address="+
                                        userLocation.address+";code="+code);

                            /*$('body').hide(0).addClass('poll').load('poll.html .masthead',function(){
                                var marginLeft = 0;
                                var item = $('.item');
                                var itemWidth = item.width()+15;
                                var itemLen = item.length;
                                var sliderWidth = itemWidth*itemLen;
                                $('.slider').width(sliderWidth);
                                var itemActive = $('.item.active');

                                var temp = new Date().toString();
                                var date = temp.split(" ");
                                var dateStr = date[0]+" "+date[1]+" "+date[2]+" "+date[3]+" "+date[4];
                                itemActive.find('.timeAsk').val(dateStr);

                                $('.poll .btn').click(function(e){
                                    e.preventDefault();
                                    itemActive = $('.item.active');

                                    if(itemActive.find('.checkbox.active').length == 0){
                                        // юзер не выбрал ответ
                                    $('.no-answer').show();
                                    }else{
                                    $('.no-answer').hide();
                                    var ind = itemActive.index();

                                    var temp = new Date().toString();
                                    var date = temp.split(" ");
                                    var dateStr = date[0]+" "+date[1]+" "+date[2]+" "+date[3]+" "+date[4];

                                    itemActive.find('.timeAnswer').val(dateStr);
                                    itemActive.removeClass('active');

                                    if (ind == itemLen-1){
                                        // конец
                                         temp = new Date().toString();
                                         date = temp.split(" ");
                                         dateStr = date[0]+" "+date[1]+" "+date[2]+" "+date[3]+" "+date[4];
                                        $('.item:eq('+ --ind +')').find('.timeAnswer').val(dateStr);

                                        $('.poll-section, .poll .btn').fadeOut(200,function(){
                                            $('.thanks').fadeIn(200);
                                        });

                                        // отправка на сервер

                                        var answersHash="";
                                        $('.item').each(function(){
                                            var timeAsk = $(this).find('.timeAsk').val();
                                            var timeAnswer = $(this).find('.timeAnswer').val();

                                            var answer = $(this).find('.checkbox').index($(this).find('.active')) + 1;

                                            answersHash += answer+","+timeAsk+","+timeAnswer+";";
                                        });

                                        $.ajax({
                                            url : 'subscribe.php',
                                            type : 'POST',
                                            data : {
                                                email : email,
                                                code : code,
                                                answersHash : answersHash,
                                                userAgent : navigator.userAgent
                                            },
                                            success : function(data) {
                                            }
                                        });

                                    }else{
                                        ind++;
                                        temp = new Date().toString();
                                        date = temp.split(" ");
                                        dateStr = date[0]+" "+date[1]+" "+date[2]+" "+date[3]+" "+date[4];
                                        $('.item:eq('+ ind +')').addClass('active').find('.timeAsk').val(dateStr);

                                        marginLeft -= 385;
                                        $('.slider').animate({marginLeft: marginLeft},200);
                                    }
                                }
                                });

                                // заполнение анкеты вопросами и вариантаим ответов
                                    $.ajax({
                                        url : 'getPoll.php',
                                        type : 'POST',
                                        data : {
                                        },
                                        success : function(data) {
                                        var questions = data.split('#'),
                                                question = [],
                                                answer = [];
                                        var qLen = questions.length;
                                            for(var i = 0; i < qLen; i++){
                                                var parts = questions[i].split('\\');
                                                question[i] = parts[0];
                                                answer[i] = parts[1];
                                            }

                                            var count = 0;
                                        $('.item').each(function(){
                                            $(this).find('h3').text(question[count]);
                                            var answersVariables = answer[count].split(';');
                                            var answLen = answersVariables.length;
                                            var answersHtml = "<ul>";
                                            for(var i = 0; i < answLen; i++){
                                                answersHtml += "<li><label>" +
                                                        "<input class='checkbox' type='checkbox'>"+
                                                        "<span>"+ answersVariables[i] +"</span>"+
                                                        "</label>"+
                                                        "</li>";
                                            }
                                            answersHtml += "</ul>";
                                            $(this).find('h3').after(answersHtml);
                                            count++;

                                        });
                                            $('.item .checkbox').change(function(){
                                                $(this).toggleClass('active');
                                            });
                                        }
                                    });

                            }).show(0);*/
                            }

						},
						error : function() {
							$('#result').html('Sorry, an error occurred.');
						}
					});
				}

				return false;
			});


		});
	</script>

    <script>
        (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
            (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
                m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
        })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

        ga('create', 'UA-51483969-1', 'vmesteonline.ru');
        ga('send', 'pageview');

    </script>


</body>
</html>
