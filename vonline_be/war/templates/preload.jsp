<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.vmesteonline.be.ShopServiceImpl"%>
<%@ page import="com.vmesteonline.be.ShopBOServiceImpl"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.ShortUserInfo"%>
<%@ page import="com.vmesteonline.be.shop.*"%>
<%@ page import="com.vmesteonline.be.shop.bo.*"%>

<%
    HttpServletRequest httpReq = (HttpServletRequest)request;
    /*String url1 = httpReq.getContextPath();
    String url2 = httpReq.getRequestURI();*/
    String url = httpReq.getPathInfo();

    String serverName = request.getServerName();
    int port = request.getServerPort();

    String URLrest = "";

    if (serverName.endsWith(".local")){
        URLrest = ".local";

        int serverNameLength = serverName.length()-6;
        serverName = serverName.substring(0,serverNameLength);
    }

    if(port != 0){
        URLrest = URLrest + ":"+port;
    }

    HttpSession sess = request.getSession();
    boolean isAuth = true;

    try {
        //AuthServiceImpl.checkIfAuthorised(sess.getId());
        UserServiceImpl userService = new UserServiceImpl(request.getSession());
        ShortUserInfo ShortUserInfo = userService.getShortUserInfo();
        if( null == ShortUserInfo){
            sess.invalidate();
            throw new InvalidOperation( com.vmesteonline.be.VoError.NotAuthorized, "");
        }
        pageContext.setAttribute("firstName",ShortUserInfo.firstName);
        pageContext.setAttribute("lastName",ShortUserInfo.lastName);
    } catch (InvalidOperation ioe) {
        isAuth = false;
    }
    pageContext.setAttribute("isAuth",isAuth);


    ShopServiceImpl shopService = new ShopServiceImpl(request.getSession().getId());

    List<Shop> ArrayShops = shopService.getShops();
    int ArrayShopsSize = ArrayShops.size();

    if(ArrayShops != null && ArrayShopsSize > 0){
        Shop shop = shopService.getShop(ArrayShops.get(0).id);
        for(int i = 0; i < ArrayShopsSize; i++){

            if(ArrayShops.get(i).hostName.equals(serverName)){
                shop = ArrayShops.get(i);
            }
        }

        /*if(ArrayShops.size() > 1 && url != null && url.length() >= 17){
            char buf[] = new char[16];
            url.getChars(1, 17, buf, 0);
            String shopIdStr = "";
            // 15 - кол-во символов в id магазина
            for (int i = 0; i <= 15; i++) {
                shopIdStr = shopIdStr+buf[i];
            }

            Long shopId = new Long(shopIdStr);

            shop = shopService.getShop(shopId);
        }else{
            shop = shopService.getShop(ArrayShops.get(0).id);
        }*/
        //out.print(shop.id);

        UserShopRole userRole = shopService.getUserShopRole(shop.id);
        pageContext.setAttribute("logoURL", shop.logoURL);
        pageContext.setAttribute("shopID", shop.id);
        pageContext.setAttribute("userRole", userRole);

         // for BO
        List<ProductCategory> categoriesList = shopService.getAllCategories(shop.id);

        if(categoriesList != null && categoriesList.size() > 0){
            pageContext.setAttribute("categories", categoriesList);
        }

        pageContext.setAttribute("shop", shop);

         // for Adminka
        pageContext.setAttribute("shops", ArrayShops);
        pageContext.setAttribute("isEmptyURL",url == null);

        // shopPages Links

        ShopBOServiceImpl shopBOService = new ShopBOServiceImpl(request.getSession().getId());

        ShopPages shopPages = shopBOService.getShopPages(shop.id);
        pageContext.setAttribute("shopPages", shopPages);

    }

%>


