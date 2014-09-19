define("shop-orders",["jquery","shop-initThrift","shop-basket","shop-common","shop-spinner"],function(a,b,c,d,e){function f(a,c){var d='<section class="catalog"><table><thead><tr><td>Название</td><td>Производитель</td><td>Цена (руб)</td><td>Количество</td><td>Стоимость</td><td>Ед.изм</td><td></td></tr></thead>',e=a.odrerLines,f=e.length;r=r?r:b.client.getProducers(),s=s?s:r.length;for(var g=0;f>g;g++){for(var h,i,j,k="",l=0;s>l;l++)if(r[l].id==e[g].product.producerId){i=r[l].name,j=r[l].id;break}if(m=require("shop-common"),h=e[g].product.imageURL?e[g].product.imageURL:m.noPhotoPic,e[g].product.unitName&&(k=e[g].product.unitName),d+='<tr class="product" data-prepack="'+e[g].product.prepackRequired+'" data-productid="'+e[g].product.id+'"><td><a href="#" class="product-link"><div class="product-pic"><img src="'+h+'?w=40&h=40" alt="'+e[g].product.name+'"/></div><span><span class="product-name">'+e[g].product.name+"</span>"+e[g].product.shortDescr+'</span></a><div class="modal"></div></td><td class="td-producer" data-producerid="'+j+'">'+i+'</td><td class="product-price">'+e[g].product.price+'</td><td class="td-spinner">',c)d+=e[g].quantity;else{d+='<input type="text"';var m=require("shop-common");e[g].packs&&m.getPacksLength(e[g].packs)>1&&(d+='disabled="disabled"'),d+=' data-step="'+e[g].product.minClientPack+'" class="input-mini spinner1" />'}d+='<span class="added-text">добавлен</span></td><td class="orderLine-amount"><span>'+(e[g].product.price*e[g].quantity).toFixed(1)+'</span></td><td><span class="unit-name">'+k+"</span></td>",c||(d+='<td><a href="#" title="Добавить в корзину" class="fa fa-shopping-cart"></a><span href="#" title="Продукт уже у вас в корзине" class="fa fa-check"></span></td>'),d+="</tr>"}return d+="</table></section>"}function g(b,c){try{var d="",e=b.length,f=0,g=10;c&&(g=u,e-=t),e>g&&(f=e-g);for(var h=e-1;h>=f;h--){var i,j=new Date(1e3*b[h].date);switch(b[h].status){case 0:i="Неизвестен";break;case 1:i="Не подтвержден";break;case 2:i="Подтвержден";break;case 3:i="Уже едет";break;case 4:i="Доставлен";break;case 5:i="Закрыт";break;case 6:i="Отменен"}var k=j.getDate();k=10>k?"0"+k:k;var l=j.getMonth()+1;l=10>l?"0"+l:l;var m=require("shop-basket"),n=m.getWeekDay(j.getDay());d+='<div class="order-item orders-no-init" data-orderid="'+b[h].id+'"><table class="orders-tbl"><tbody><tr><td class="td1"><a class="fa fa-plus plus-minus" href="#"></a></td><td class="td2">'+b[h].id+'</td><td class="td3">'+k+"."+l+" ("+n+')</td><td class="td4"><div class="order-status">'+i+'</div></td><td class="td9"></td><td class="td8"></td><td class="td6">'+b[h].totalCost.toFixed(1)+'</td></tr></tbody></table><div class="order-bottom">',"Подтвержден"!=i&&(d+='<a href="#" title="Удалить" class="delete-order-from-history">&times;</a>'),d+='<button class="btn btn-sm btn-primary no-border repeat-order-btn">Повторить</button><button class="btn btn-sm btn-primary no-border add-order-btn">Добавить</button><div class="order-delivery"></div></div><div class="order-products"></div></div>'}var o=e%g;o&&o!=e?d+='<div class="more-orders"><a href="#">Показать еще</a></div>':a(".more-orders").hide()}catch(p){}return d}function h(a,c,d){var e=d?d:b.client.getOrderDetails(c);a.find(".td9").text(e.deliveryCost),a.find(".td8:not(.user-name)").text((e.weightGramm/1e3).toFixed(1));var f;switch(e.delivery){case 0:f="Неизвестно";break;case 1:f="Самовывоз";break;case 2:f="Курьер рядом";break;case 3:f="Курьер далеко"}a.find(".order-delivery").html("<span><b>Доставка:</b> "+f+",  "+e.deliveryTo.city.name+", "+e.deliveryTo.street.name+" "+e.deliveryTo.building.fullNo+", кв."+e.deliveryTo.flatNo+"</span>")}function i(){a(".delete-order-from-history").click(function(c){c.preventDefault();var d=a(this).closest(".order-item"),e=d.data("orderid"),f=a(".tab-pane.active");f.data("orderid")==e?f.find(".btn-cancel").trigger("click",!0):b.client.deleteOrder(e),a(this).closest(".order-item").slideUp()})}function j(c){c.find(".plus-minus").click(function(c){c.preventDefault();var d=a(this).closest(".order-item"),g=d.find(".order-products"),i=d.data("orderid");if(0==g.find(".catalog").length){var j=b.client.getOrderDetails(i),k=j.odrerLines,l=k.length;h(d,i,j),g.append(f(j));for(var m=0;l>m;m++){var n=g.find("tbody tr:eq("+m+") .spinner1"),o=g.find("tbody tr:eq("+m+") .spinner1").data("step");e.InitSpinner(n,k[m].quantity,0,o),"disabled"==n.attr("disabled")&&n.closest(".ace-spinner").spinner("disable")}var p=require("shop-basket"),q=require("shop-common");p.InitAddToBasket(g.find(".fa-shopping-cart")),q.InitProductDetailPopup(g.find(".product-link")),q.markAddedProduct()}g.slideToggle(200,function(){a(".main-content").height()>a(window).height()?a("#sidebar, .shop-right").css("height",a(".main-content").height()+45):a("#sidebar, .shop-right").css("height","100%")}),a(this).hasClass("fa-plus")?a(this).removeClass("fa-plus").addClass("fa-minus"):a(this).removeClass("fa-minus").addClass("fa-plus")})}function k(b,c){var d,e=a(".tab-pane.active"),f=e.data("orderid"),g=require("shop-basket");if(f)l(b,c,d);else{var g=require("shop-basket");g.initChooseDatepicker(!0,b,null,null,null,null,null,c)}}function l(c,d,e){var f,g,h=a(".tab-pane.active"),i=h.data("orderid"),j=require("shop-basket");if("replace"==d){e=b.client.getOrderDetails(c);for(var k=e.odrerLines,l=k.length,m=0;l>m;m++){f=k[m].product,g=k[m].quantity;var n=k[m].packs;b.client.setOrderLine(i,f.id,g,"",n)}}else"append"==d&&(e=b.client.appendOrder(i,c));k=e.odrerLines,l=k.length;for(var o,m=0;l>m;m++){f=k[m].product,g=k[m].quantity,o=!1;var p=require("shop-common");k[m].packs&&p.getPacksLength(k[m].packs)>1&&(o=!0),j.AddSingleProductToBasket(f,g,o,e)}var p=require("shop-common");p.markAddedProduct(),h.find(".weight span").text(p.getOrderWeight(c,e))}function m(b){var c;c=b.itsAppend?"append":"replace",a(".catalog-order").html(""),k(b.orderId,c)}function n(b){b.find(".repeat-order-btn").click(function(){var b={itsOrder:!0,itsAppend:!1,orderId:a(this).closest(".order-item").data("orderid")};m(b)}),b.find(".add-order-btn").click(function(){var b={itsOrder:!0,itsAppend:!0,orderId:a(this).closest(".order-item").data("orderid")};a(".additionally-order").hasClass("hide")||m(b)})}function o(){t=10,u=10}function p(b){a(".more-orders").click(function(c){c.preventDefault();var d=a(".orders-list");d.find(".more-orders").remove();var e=!0;d.append(g(b,e)),p(b);var f=a(".orders-no-init");j(f),n(f),f.removeClass("orders-no-init"),t+=u;var h=require("shop-common");h.setSidebarHeight()})}function q(){a(".go-to-orders").trigger("click")}var r,s,t=10,u=10;return{createOrdersProductHtml:f,createOrdersHtml:g,initOrderPlusMinus:j,addSingleOrderToBasket:k,AddOrdersToBasket:m,initOrderBtns:n,initVarForMoreOrders:o,initShowMoreOrders:p,GoToOrdersTrigger:q,showOrderDetails:h,deleteOrderFromHistory:i,addOrderTo:l}});