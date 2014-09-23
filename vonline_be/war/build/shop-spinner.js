define("shop-spinner",["jquery","ace_spinner","shop-initThrift","shop-common","shop-basket"],function(a,b,c){function d(a,b,c,d){var f=1;d&&(f=d),a.ace_spinner({value:+b,min:f,max:1e5,step:f,on_sides:!0,hold:!1,btn_up_class:"btn-info",btn_down_class:"btn-info"});var g=a.closest(".catalog-confirm").length>0;c||g?e(a):l(a)}function e(b){var c=b.data("step");b.on("focusout",function(){var b=a(this).val(),d=a(this).data("step");if(b!=c){var e=b%d;e&&(b=b-e+d),a(this).closest(".ace-spinner").spinner("value",b),a(this).trigger("change")}c=b}),b.on("change",function(){var b=a(this).closest(".ace-spinner").spinner("value");if((0==b||void 0===b)&&(b=a(this).data("step")?a(this).data("step"):1),b&&(b=parseFloat(b).toFixed(1)),a(this).closest(".ace-spinner").spinner("value",b),c!=b){var d=a(this).closest(".product");if(d.addClass("wasChanged"),f(a(this),d,0),0==a(this).closest(".modal").length){var e=d.find(".td-price").text(),g=d.find(".td-spinner .ace-spinner").spinner("value");e=parseFloat(e),d.find(".td-summa").text((e*g).toFixed(1))}}c=b})}function f(b,c,d,e){var f=b.closest(".ace-spinner").spinner("value").toFixed(1),h=b.closest(".modal").length>0;if(h){var i=!1,j=b.closest(".modal-footer").hasClass("with-prepack");if(j){var k,l,m=b.closest(".modal-footer").find(">.packs .ace-spinner").spinner("value"),n=b.closest(".modal-footer").find(">.qnty .ace-spinner").spinner("value"),o=[];o[n]=m,f=m*n;var p,q,r=a(".error-prepack");r.text("Товар не возможно добавить: вы создали две линни с одинаковым количеством продукта"),r.hide();var s=[],t=0;b.closest(".modal-footer").find(".prepack-line").each(function(){p=a(this).find(".packs .ace-spinner").spinner("value"),q=a(this).find(".qnty .ace-spinner").spinner("value"),f+=p*q,s[t++]=q,q==n&&(r.show(),i=!0);for(var b=0;t-1>b;b++)q==s[b]&&(r.show(),i=!0)}),f=f.toFixed(1);var u=b.closest(".packs").length>0;u?(k=parseFloat(b.closest(".prepack-item").next().next().find(".ace-spinner").spinner("value")).toFixed(1),l=b.closest(".ace-spinner").spinner("value"),d[k]=l):(d=o,b.closest(".modal-footer").find(".prepack-line").each(function(){p=a(this).find(".packs .ace-spinner").spinner("value"),q=a(this).find(".qnty .ace-spinner").spinner("value"),q=parseFloat(q).toFixed(1),d[q]=p}))}if(!i){if(e){c.find(".td-spinner .ace-spinner").spinner("value",f);var v=c.find(".td-price").text();v=parseFloat(v),c.find(".td-summa").text((v*f).toFixed(1))}c.closest(".catalog-confirm").length&&a(".catalog-order .product").each(function(){if(a(this).data("productid")==c.data("productid")){a(this).find(".td-spinner .ace-spinner").spinner("value",f),g(b,a(this));var d=a(this).find(".td-price").text();a(this).find(".td-summa").text((d*f).toFixed(1)),a(this).find(".modal-body").length&&a(this).find(".modal-body").remove()}})}}else{if(d){l=0;for(var w in d)l=d[w];d=[],l&&(d[f]=l,f*=l)}var x=c.find(".modal-body").length>0;x&&(c.data("prepack")?c.find(".modal-footer>.qnty .ace-spinner").spinner("value",f):c.find(".modal-footer .ace-spinner").spinner("value",f))}return g(b,c),{packs:d,qnty:f,errorFlag:i}}function g(a,b){var c=a.closest(".modal-footer").find(".prepack-line"),d=a.closest(".modal-footer").find(">.packs .ace-spinner").spinner("value");b.find(".td-spinner .ace-spinner").spinner(c.length>0||d>1?"disable":"enable")}function h(){a(".refresh").click(function(){var b=a(this).closest(".basket-bottom").length>0,c=a(b?".catalog-order":".catalog-confirm");if(c.find(".product.wasChanged").length){var d,e=a(".tab-pane.active"),f=e.data("orderid");c.find(".product.wasChanged").each(function(){var c=a(this).data("productid"),e=a(this);if(d=j(e,f,c),!b){var g=e.find(".td-price").text(),h=require("shop-common");a(".catalog-order .product").each(function(){a(this).data("productid")==e.data("productid")&&(a(this).find(".td-spinner .ace-spinner").spinner("value",d.qnty),d.packs&&h.getPacksLength(d.packs)>1&&a(this).find(".td-spinner .ace-spinner").spinner("disable"),a(this).find(".qnty .ace-spinner").spinner("value",d.qnty),a(this).find(".td-summa").text((g*d.qnty).toFixed(1)))})}a(this).removeClass("wasChanged")});var g=d?d.updateInfo:0;k(f,c,g)}return!1})}function i(b,c,d){var e,f=15,g=a(".weight-right span");if(e=parseFloat(g.length?g.text():a(".weight span").text()),q){if(e>=f){var h=require("shop-basket");h.setDeliveryCost(b,c,d),q=0}}else f>e&&(h=require("shop-basket"),h.setDeliveryCost(b,c,d),q=1)}function j(b,d,e,g){var h;b.data("prepack")?(m(b),h=b.find(".modal-footer").find(">.qnty .ace-spinner .spinner1")):g?(m(b),h=b.find(".modal-footer").find(".ace-spinner .spinner1")):h=b.find(".td-spinner .spinner1");var i=f(h,b,0,g);return i.packs||(i.packs=0),e&&!i.errorFlag?(a(".error-prepack").hide(),i.updateInfo=c.client.setOrderLine(d,e,i.qnty,"",i.packs)):a(".error-prepack").show(),i}function k(b,d,e){var f=e?e:c.client.getOrderDetails(b),g=require("shop-common"),h=a(".tab-pane.active"),j=g.getOrderWeight(b,f),k=g.countAmount(d,f);h.find(".weight span").text(j),h.find(".amount span").text(k),a(".weight-right span").text(j),a(".itogo-right span").text(k),i(b,f,d)}function l(b){var c=b.data("step");b.on("focusout",function(){var b=a(this).closest(".ace-spinner").spinner("value"),d=a(this).data("step");if(b!=c){var e=b%d;e&&(b=b-e+parseFloat(d)),a(this).closest(".ace-spinner").spinner("value",b),a(this).trigger("change")}c=b}),b.on("change",function(){var b=a(this).closest(".ace-spinner").spinner("value"),d=a(this).data("step");if((0==b||void 0===b)&&(b=d?d:1),b&&(b=parseFloat(b).toFixed(1)),a(this).closest(".ace-spinner").spinner("value",b),c!=b){var e=a(this).closest(".product"),f=a(this).val(),g=a(this).closest(".modal").length>0;if(g){var h=a(this).closest(".modal-footer").hasClass("with-prepack");if(h){var i=a(this).closest(".modal-footer").find(".prepack-line"),j=a(this).closest(".modal-footer").find(">.packs .ace-spinner").spinner("value");if(0==i.length&&1==j)a(this).closest(".prepack-item").hasClass("packs")&&(f=a(this).closest(".modal-footer").find(">.qnty .ace-spinner").spinner("value")),e.find(".td-spinner .ace-spinner").spinner("enable"),e.find(".td-spinner .ace-spinner").spinner("value",parseFloat(f).toFixed(1));else{e.find(".td-spinner .ace-spinner").spinner("disable");var k=a(this).closest(".modal-footer").find(">.packs .ace-spinner").spinner("value"),l=a(this).closest(".modal-footer").find(">.qnty .ace-spinner").spinner("value"),m=k*l;e.find(".prepack-line").each(function(){var b=a(this).find(".packs .ace-spinner").spinner("value"),c=a(this).find(".qnty .ace-spinner").spinner("value");m+=b*c}),e.find(".td-spinner .ace-spinner").spinner("value",parseFloat(m).toFixed(1))}}else e.find(".td-spinner .ace-spinner").spinner("value",parseFloat(f).toFixed(1))}else e.data("prepack")?(e.find(".modal .qnty .ace-spinner").spinner("value",f),e.find(".modal .packs .ace-spinner").spinner("value",1)):e.find(".modal .ace-spinner").spinner("value",f)}})}function m(a){a.find(".modal-body").length||(a.find(".product-link").trigger("click"),a.find(".modal .close").trigger("click"))}function n(b){b.click(function(b){b.preventDefault();var c=parseFloat(a(this).closest(".prepack-line").find(".qnty .ace-spinner").spinner("value")).toFixed(1),d=0;a(this).closest(".modal-footer").find(".qnty .ace-spinner").each(function(){parseFloat(a(this).spinner("value")).toFixed(1)==c&&d++}),a(this).closest(".prepack-line").slideUp(function(){var b=a(this).closest(".modal").height();a(this).closest(".modal").height(b-53),2>=d&&a(".error-prepack").hide(),a(this).remove()})})}function o(b){var c;c=b?b.find(".spinner1"):a(".catalog table .spinner1"),c.each(function(){var b=a(this).data("step");d(a(this),b,0,b)})}function p(b,e,f,g,h){var i;i=void 0===h?!0:h;var j=b.closest(".tab-pane").data("orderid");j||(j=a(".tab-pane.active").data("orderid"));var k=b.closest(".product").data("productid"),l=b.closest(".product").find(".unit-name").text(),m=b.closest(".order-products").length>0;if(m&&(j=b.closest(".order-item").data("orderid")),g){var o=e.height();if("disabled"!=f.find(".td-spinner .spinner1").attr("disabled")){var p=b.closest(".product").find(".td-spinner .ace-spinner").spinner("value");d(e.find(".packs .spinner1"),1,i,1),d(e.find(".qnty .spinner1"),p,i,f.find(".td-spinner .spinner1").data("step"))}else{for(var q,r=c.client.getOrderDetails(j),s=r.odrerLines.length,t=0;s>t;t++)r.odrerLines[t].product.id==k&&(q=r.odrerLines[t].packs);var u,v=0;for(var w in q)if(w&&q[w]){if(0==v)g?(d(e.find(".packs .spinner1"),q[w],i,1),d(e.find(".qnty .spinner1"),w,i,f.find(".td-spinner .spinner1").data("step"))):(e.find(".packs .ace-spinner").spinner("value",q[w]),e.find(".qnty .ace-spinner").spinner("value",w));else if(0!=q[w]){u='<div class="prepack-line no-init"><div class="prepack-item packs"><input type="text" class="input-mini spinner1 prepack" /><span>упаковок</span></div><div class="prepack-item">по</div><div class="prepack-item qnty"><input type="text" data-step="'+f.find(".spinner1").data("step")+'" class="input-mini spinner1" /><span>'+l+'</span></div><div class="prepack-item"><a href="#" class="remove-prepack-line" title="Удалить">&times;</a></div></div>',e.find(".prepack-list").append(u),d(e.find(".no-init .packs .spinner1"),q[w],i),d(e.find(".no-init .qnty .spinner1"),w,i,f.find(".td-spinner .spinner1").data("step"));var x=e.find(".prepack-line.no-init");n(x.find(".prepack-item .remove-prepack-line"),k,f),x.removeClass("no-init"),o+=53,alert(o),e.height(o)}v++}}}}var q=1;return a(".ace-spinner").click(function(b){var c=b.target;a(c).hasClass("spinner1")?"disabled"==a(c).attr("disabled")&&(a(this).closest("tr").length>0?a(this).closest("tr").find(".product-link").trigger("click"):a(this).closest("li").find(".product-link").trigger("click")):a(c).hasClass("spinner-buttons")&&"disabled"==a(c).prev().attr("disabled")&&(a(this).closest("tr").length>0?a(this).closest("tr").find(".product-link").trigger("click"):a(this).closest("li").find(".product-link").trigger("click"))}),{InitSpinner:d,InitSpinnerChangeInBasket:e,InitSpinnerChange:l,initRemovePrepackLine:n,initProductsSpinner:o,initPrepackRequiredInModal:p,initRefresh:h,changePacks:f,singleSetOrderLine:j,updateWeightAndAmount:k}});