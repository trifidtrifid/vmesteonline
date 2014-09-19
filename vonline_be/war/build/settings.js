define("settings.min",["jquery","shop-initThrift.min"],function(a,b){function c(){function c(){var b,c="";b=f();for(var d,g=!1,i=1;b>=i;i++)d=h(i,g)?"disable":"",c+='<li class="'+d+'"><a href="#">'+i+"</a></li>";a(".shedule-item").each(function(){console.log("iter"),a(this).find(".delivery-day-dropdown .dropdown-menu").html(c)}),e(a(".delivery-day-dropdown a"))}function e(b){b.click(function(b){if(b.preventDefault(),!a(this).parent().hasClass("disable")){var d=a(this).text();a(this).closest(".btn-group").find(".btn-group-text").text(d),c()}})}function f(){var b;return b="неделя"==a(".delivery-period-dropdown .btn-group-text").text()?7:31}function g(a,b){var c,d="";c=f(),d+='<div class="shedule-item new-interval">',d+=0==q?'<a href="#" class="add-delivery-interval pull-right add-interval">+</a>':'<a href="#" class="add-delivery-interval pull-right remove-interval">&ndash;</a>',d+='<div class="shedule-confirm"><span>День доставки</span><div class="btn-group delivery-day-dropdown"><button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border"><span class="btn-group-text">'+a.orderDay+'</span><span class="icon-caret-down icon-on-right"></span></button><ul class="dropdown-menu dropdown-blue">';var e,g;g=b?!1:!0;for(var i=1;c>=i;i++)e=h(i,g)?"disable":"",d+='<li class="'+e+'"><a href="#">'+i+"</a></li>";return d+='</ul></div></div><div class="shedule-confirm"><span>подтверждать заказ за</span><input type="text" class="days-before" value="'+a.orderBefore+'"><span>дня до доставки</span></div></div>'}function h(b,c){var d=!1;if(c){for(var e=0;o>e;e++)if(n[e].orderDay==b){d=!0;break}}else a(".shedule-item").each(function(){var c=a(this).find(".delivery-day-dropdown .btn-group-text").text();console.log(c+" "+b),c==b&&(console.log("!!!!"),d=!0)});return d}a("#settings-logo").ace_file_input({style:"well",btn_choose:"Изменить логотип",btn_change:null,no_icon:"",droppable:!0,thumbnail:"large",icon_remove:null}).on("change",function(){a(".logo-container>img").hide()}).parent().addClass("settings-logo"),a("#date-picker-6").datepicker({autoclose:!0,language:"ru"});var i=a(".backoffice.dynamic").attr("id"),j=b.client.getShop(i),k=b.clientBO.getShopPages(i);if(k.aboutPageContentURL&&a("#settings-about-link").val(k.aboutPageContentURL),k.conditionsPageContentURL&&a("#settings-terms-link").val(k.conditionsPageContentURL),k.deliveryPageContentURL&&a("#settings-delivery-link").val(k.deliveryPageContentURL),k.socialNetworks)for(var l in k.socialNetworks)"vk"==l?a("#settings-socvk-link").val(k.socialNetworks[l]):"fb"==l&&a("#settings-socfb-link").val(k.socialNetworks[l]);for(var m,n=b.clientBO.getDates(),o=n.length,p="",q=0;o>q;q++)m=n[q],p+=g(m);a(".delivery-period").after(p),e(a(".delivery-day-dropdown a")),a(".delivery-period-dropdown a").click(function(b){b.preventDefault();var c,d="";"неделя"==a(this).text()?c=7:"месяц"==a(this).text()&&(c=31);for(var f,g=!1,i=1;c>=i;i++)f=h(i,g)?"disable":"",d+='<li class="'+f+'"><a href="#">'+i+"</a></li>";a(".delivery-day-dropdown").each(function(){a(this).find(".dropdown-menu").html(d)}),e(a(".delivery-day-dropdown a"))});var r,s,t=j.deliveryCostByDistance,u="",v=0;for(var l in t)r="&ndash;",s="remove-interval",0==v&&(r="+",s="add-interval"),u+='<div class="delivery-interval delivery-price-type"><input type="text" value="'+l+'"><span>км</span><input type="text" value="'+t[l]+'"><span>руб</span><a href="#" class="add-delivery-interval '+s+'">'+r+"</a></div>",v++;""==u&&(u+='<div class="delivery-interval delivery-price-type"><input type="text" placeholder="Интервал"><span>км</span><input type="text" placeholder="Стоимость"><span>руб</span><a href="#" class="add-delivery-interval add-interval">+</a></div>'),a(".delivery-interval-container").append(u);var w=j.deliveryByWeightIncrement,x="";v=0;for(var l in w)r="&ndash;",s="remove-interval",0==v&&(r="+",s="add-interval"),x+='<div class="delivery-weight delivery-price-type"><input type="text" value="'+l+'"><span>кг</span><input type="text" value="'+w[l]+'"><span>руб</span><a href="#" class="add-delivery-interval '+s+'">'+r+"</a></div>",v++;""==x&&(x+='<div class="delivery-weight delivery-price-type"><input type="text" placeholder="Интервал"><span>кг</span><input type="text" placeholder="Стоимость"><span>руб</span><a href="#" class="add-delivery-interval add-interval">+</a></div>'),a(".delivery-weight-container").append(x);var y=j.deliveryTypeAddressMasks,z="";for(var l in y){var A;2==l?A="Близко":3==l&&(A="Далеко"),z+='<div class="delivery-area  delivery-price-type"><span>'+A+'</span><input type="text" value="'+y[l]+'"><span>руб</span></div>'}""==z&&(z+='<div class="delivery-area  delivery-price-type"><span>Близко</span><input type="text" placeholder="Стоимость"><span>руб</span></div><div class="delivery-area  delivery-price-type"><span>Далеко</span><input type="text" placeholder="Стоимость"><span>руб</span></div>'),a(".delivery-area-container").append(z);var B=j.deliveryCosts,C="";for(var l in B)A="",1==l?A="Самовывоз":2==l?A="Близко":3==l&&(A="Далеко"),C+='<div class="delivery-type  delivery-price-type"><span>'+A+'</span><input type="text" value="'+B[l]+'"><span>руб</span></div>';""==C&&(C+='<div class="delivery-type  delivery-price-type"><span>Близко</span><input type="text" placeholder="Стоимость"><span>руб</span></div><div class="delivery-type  delivery-price-type"><span>Далеко</span><input type="text" placeholder="Стоимость"><span>руб</span></div>'),a(".delivery-type-container").append(C),a(".add-interval").click(function(b){b.preventDefault();var d,e=a(this).closest(".delivery-price-type").hasClass("delivery-interval"),i=a(this).closest(".shedule-item").length;if(i){var j={};j.orderDay=1;for(var k=f(),l=!1,m=!0,n=1;k>n;n++)if(!h(n,l)){j.orderDay=n;break}j.orderBefore=2,a(this).closest("#settings-shedule").find(".shedule-item").last().after(g(j,m)),c()}else d=e?'<div class="delivery-interval new-interval delivery-price-type"><input type="text" placeholder="Интервал"><span>км</span>&nbsp;<input type="text" placeholder="Стоимость"><span>руб</span>&nbsp;<a href="#" class="add-delivery-interval remove-interval">&ndash;</a></div>':'<div class="delivery-weight new-interval delivery-price-type"><input type="text" placeholder="Интервал"><span>кг</span>&nbsp;<input type="text" placeholder="Стоимость"><span>руб</span>&nbsp;<a href="#" class="add-delivery-interval remove-interval">&ndash;</a></div>',a(this).closest(".delivery-price-type").after(d);a(".new-interval .remove-interval").click(function(b){b.preventDefault(),a(this).closest(".new-interval").slideUp(200,function(){a(this).detach()})}),a(".new-interval").slideDown().removeClass(".new-interval")}),a(".remove-interval").click(function(b){b.preventDefault(),a(this).closest(".settings-delivery-container").addClass("changed"),a(this).closest(".delivery-interval").length?a(this).closest(".delivery-interval").slideUp(200,function(){a(this).detach()}):a(this).closest(".delivery-weight").length&&a(this).closest(".delivery-weight").slideUp(200,function(){a(this).detach()})}),a("#settings-shedule .remove-interval").click(function(b){b.preventDefault(),a(this).closest(".new-interval").slideUp(200,function(){a(this).detach()})}),a("#settings-delivery input").focus(function(){a(this).closest(".settings-delivery-container").addClass("changed")}),a("#settings-delivery .add-interval").click(function(){a(this).closest(".settings-delivery-container").addClass("changed")}),a(".settings-item .btn-save").click(function(c){c.preventDefault();var d=a(this).closest(".settings-item").attr("id");switch(d){case"settings-common":var e=a("#settings-common"),f=j;f.name=e.find(a("#name")).val(),f.hostName=e.find(a("#hostName")).val(),f.descr=e.find(a("#descr")).val(),f.address=b.client.createDeliveryAddress(e.find(a("#address")).val());var g=a(".logo-container .file-name img");f.logoURL=g.length?g.css("background-image"):a(".logo-container>img").attr("src"),b.clientBO.updateShop(f);break;case"settings-links":var h=new com.vmesteonline.be.shop.ShopPages;h.aboutPageContentURL=a("#settings-about-link").val(),h.conditionsPageContentURL=a("#settings-terms-link").val(),h.deliveryPageContentURL=a("#settings-delivery-link").val(),(a("#settings-socvk-link").val()||a("#settings-socfb-link").val())&&(h.socialNetworks=[],a("#settings-socvk-link").val()&&(h.socialNetworks.vk=a("#settings-socvk-link").val()),a("#settings-socfb-link").val()&&(h.socialNetworks.fb=a("#settings-socfb-link").val())),b.clientBO.setShopPages(h);break;case"settings-shedule":var k=new com.vmesteonline.be.shop.OrderDates;k.type="неделя"==a(".delivery-period-dropdown .btn-group-text").text()?com.vmesteonline.be.shop.OrderDatesType.ORDER_WEEKLY:com.vmesteonline.be.shop.OrderDatesType.ORDER_MOUNTHLY,a(".shedule-item").each(function(){var c=a(this).find(".delivery-day-dropdown .btn-group-text").text(),d=a(this).find(".days-before").val();k.orderDay=parseInt(c),k.orderBefore=parseInt(d);for(var e=!1,f=[],g=0;o>g;g++)f[g]=!1,k.orderDay==n[g].orderDay&&k.orderBefore==n[g].orderBefore&&(e=!0,f[g]=!0);e||b.clientBO.setDate(k)});for(var l=a(".shedule-item").length,m=0;o>m;m++){for(var p=!0,q=0;l>q;q++){var r=a(".shedule-item:eq("+q+")"),s=r.find(".delivery-day-dropdown .btn-group-text").text(),t=r.find(".days-before").val();k.orderDay=parseInt(s),k.orderBefore=parseInt(t),k.orderDay==n[m].orderDay&&k.orderBefore==n[m].orderBefore&&(p=!1)}console.log(p),p&&b.clientBO.removeDate(n[m])}break;case"settings-delivery":f=j;var u=!1,v=!1,w=!1,x=!1;if(a("#settings-delivery").find(".changed").each(function(){a(this).hasClass("delivery-interval-container")&&(u=!0),a(this).hasClass("delivery-area-container")&&(v=!0),a(this).hasClass("delivery-weight-container")&&(w=!0),a(this).hasClass("delivery-type-container")&&(x=!0)}),u){var y=[];a(".delivery-interval").each(function(){var b=parseInt(a(this).find("input:eq(0)").val()),c=parseFloat(a(this).find("input:eq(1)").val());b&&c&&(y[b]=c)}),b.clientBO.setShopDeliveryCostByDistance(i,y)}if(v){var z=[],A=0;a(".delivery-area").each(function(){var b;b=A?com.vmesteonline.be.shop.DeliveryType.LONG_RANGE:com.vmesteonline.be.shop.DeliveryType.SHORT_RANGE,A++;var c=a(this).find("input:eq(0)").val();c&&(z[b]=c)}),b.clientBO.setShopDeliveryTypeAddressMasks(i,z)}if(w){var B=[];a(".delivery-weight").each(function(){var b=parseInt(a(this).find("input:eq(0)").val()),c=parseInt(a(this).find("input:eq(1)").val());b&&c&&(B[b]=c)}),b.clientBO.setShopDeliveryByWeightIncrement(i,B)}if(x){var C=[];A=0,a(".delivery-type").each(function(){var b;switch(A){case 0:b=com.vmesteonline.be.shop.DeliveryType.SELF_PICKUP;break;case 1:b=com.vmesteonline.be.shop.DeliveryType.SHORT_RANGE;break;case 2:b=com.vmesteonline.be.shop.DeliveryType.LONG_RANGE}A++;var c=a(this).find("input:eq(0)").val();c&&(C[b]=c)}),b.clientBO.setDeliveryCosts(C)}}}),d=1}var d=0;return{initSettings:c,isSettingsInitSet:d}});