define("bo-common",["jquery","shop-initThrift"],function(a){function b(b){var c=document.createElement("div");c.appendChild(document.createElement("div")),c.style.overflow="auto",c.style.overflowY="hidden",c.firstChild.style.width=b.scrollWidth+"px",c.firstChild.style.paddingTop="1px",c.firstChild.style.marginTop="-5px",c.firstChild.appendChild(document.createTextNode(" ")),c.onscroll=function(){b.scrollLeft=c.scrollLeft},b.onscroll=function(){c.scrollLeft=b.scrollLeft},b.parentNode.insertBefore(c,b),a(b).prev().addClass("top-scroll")}function c(b){try{var c=a(".main-content"),d=b?b:c.height(),e=h.height();d>e?(d=b?b+100:c.height()+45,a("#sidebar").css("height",d)):a("#sidebar").css("height",e-45)}catch(f){alert(f+" Функция setSidebarHeight")}}function d(a){var b;switch(a){case"Самовывоз":b="1";break;case"Курьер рядом":b="2";break;case"Курьер далеко":b="3"}return b}function e(b,c,d,e){b.find(".dropdown-toggle").click(function(b){b.preventDefault();var f=a(this).closest("td").index(),g=0,h=0;c.find("table").find("td").each(function(){f>h&&(g+=a(this).width()),h++});var i=e||0;g-=c.scrollLeft()+i,a(this).parent().find(".dropdown-menu").css({left:g,top:d})})}function f(){a(".show-full-text").click(function(b){if(b.preventDefault(),a(this).closest(".export-table").length>0)var c=b.pageX-330,d=b.pageY-90;else c=b.pageX-200,d=b.pageY-200;a(".full-text").hide(),a(this).parent().find(".full-text").css({left:c,top:d}).show()})}function g(){a(".full-text .close").click(function(b){b.preventDefault(),a(this).parent().hide()})}var h=a(window);return{DoubleScroll:b,setSidebarHeight:c,getDeliveryTypeByText:d,setDropdownWithoutOverFlow:e,initShowFullText:f,initCloseFullText:g}});