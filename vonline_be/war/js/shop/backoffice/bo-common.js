define(
    'bo-common',
    ['jquery','shop-initThrift'],
    function( $,thriftModule ){
        var w = $(window);

        function DoubleScroll(element) {
            var scrollbar= document.createElement('div');
            scrollbar.appendChild(document.createElement('div'));
            scrollbar.style.overflow= 'auto';
            scrollbar.style.overflowY= 'hidden';
            scrollbar.firstChild.style.width= element.scrollWidth+'px';
            scrollbar.firstChild.style.paddingTop= '1px';
            scrollbar.firstChild.style.marginTop= '-5px';
            scrollbar.firstChild.appendChild(document.createTextNode('\xA0'));
            scrollbar.onscroll= function() {
                element.scrollLeft= scrollbar.scrollLeft;
            };
            element.onscroll= function() {
                scrollbar.scrollLeft= element.scrollLeft;
            };
            element.parentNode.insertBefore(scrollbar, element);
            $(element).prev().addClass('top-scroll');
        }

        function setSidebarHeight(contentH){
            try{

                var mainContent = $('.main-content');
                var contH = (contentH) ? contentH : mainContent.height(),
                    wHeight = w.height();
                //alert(contH+" "+w.height());

                if (contH > wHeight){
                    contH = (contentH) ? contentH+100 : mainContent.height()+45;
                    $('#sidebar').css('height', contH);
                    //alert('1 '+contH);
                }else{
                    //alert('2 '+wHeight);
                    $('#sidebar').css('height', wHeight-45);
                }
            }catch(e){
                alert(e+" Функция setSidebarHeight");
            }
        }

        function getDeliveryTypeByText(deliveryText){
            var deliveryType;

            switch (deliveryText){
                case "Самовывоз":
                    deliveryType = "1";
                    break;
                case "Курьер рядом":
                    deliveryType = "2";
                    break;
                case "Курьер далеко":
                    deliveryType = "3";
                    break;
            }
            return deliveryType;
        }

        function setDropdownWithoutOverFlow(dropdownSelector,tableContainerSelector,coordY,coordXOffset){
            dropdownSelector.find('.dropdown-toggle').click(function(e){
                e.preventDefault();

                var ind = $(this).closest('td').index();
                var coordX = 0, tdIndex = 0;
                tableContainerSelector.find('table').find('td').each(function(){
                    if (tdIndex < ind){
                        coordX += $(this).width();
                    }
                    tdIndex ++;
                });

                var offset = coordXOffset || 0;
                coordX -= tableContainerSelector.scrollLeft() + offset;

                $(this).parent().find('.dropdown-menu').css({'left':coordX,'top':coordY});
            });
        }

        function initShowFullText(){
            $('.show-full-text').click(function(e){
                e.preventDefault();
                if($(this).closest('.export-table').length > 0){
                    var coordX = e.pageX-330;
                    var coordY = e.pageY-90;
                }else{
                    coordX = e.pageX-200;
                    coordY = e.pageY-200;
                }
                $('.full-text').hide();
                $(this).parent().find('.full-text').css({'left':coordX,'top':coordY}).show();
            });
        }

        function initCloseFullText(){
            $('.full-text .close').click(function(e){
                e.preventDefault();
                $(this).parent().hide();
            });
        }

        return {
            DoubleScroll : DoubleScroll,
            setSidebarHeight: setSidebarHeight,
            getDeliveryTypeByText: getDeliveryTypeByText,
            setDropdownWithoutOverFlow : setDropdownWithoutOverFlow,
            initShowFullText : initShowFullText,
            initCloseFullText : initCloseFullText
        }

    });