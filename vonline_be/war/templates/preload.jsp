<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.vmesteonline.be.ShopServiceImpl"%>
<%@ page import="com.vmesteonline.be.ShopBOServiceImpl"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.ShortUserInfo"%>
<%@ page import="com.vmesteonline.be.shop.*"%>
<%@ page import="com.vmesteonline.be.shop.bo.*"%>
<%@ page import="com.google.appengine.api.utils.SystemProperty;"%>

<%
    HttpServletRequest httpReq = (HttpServletRequest)request;
    //String url1 = httpReq.getContextPath();
    String requestURI = httpReq.getRequestURI();
    String url = httpReq.getPathInfo();
    //out.print(url2);

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
    pageContext.setAttribute("URLrest",URLrest);

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
    boolean isAdminka = false;
    Shop shop = null;

    if(ArrayShops != null && ArrayShopsSize > 0){

         if(requestURI.equals("/index.jsp")){

             // LANDING

            Shop[] activeShops = new Shop[ArrayShopsSize];
            Shop[] noActiveShops = new Shop[ArrayShopsSize];
            int activeShopsCounter = 0;
            int noActiveShopsCounter = 0;

            for (int i = 0; i < ArrayShopsSize; i++) {
                if(shopService.isActivated(ArrayShops.get(i).id)){
                    activeShops[activeShopsCounter] = ArrayShops.get(i);
                    activeShopsCounter++;
                }else{
                    noActiveShops[noActiveShopsCounter] = ArrayShops.get(i);
                    noActiveShopsCounter++;
                }
            }
            pageContext.setAttribute("activeShops", activeShops);
            pageContext.setAttribute("noActiveShops", noActiveShops);

        } else if(requestURI.equals("/adminka.jsp")){

            // ADMINKA

            isAdminka = true;
            pageContext.setAttribute("shops", ArrayShops);

        } else {

             // SHOP & other

            String[] pathWords = requestURI.split("/");
            if( pathWords.length > 0 ){
            	try{
            		shop = shopService.getShop( Long.parseLong( pathWords[ pathWords.length-1]));
            	} catch (Exception nfe){
            		//There is not a Long at the end of reuqest path
            	} 
            }
                // если по hostName

			if(null==shop){                
                
                for(int i = 0; i < ArrayShopsSize; i++){

                    if(ArrayShops.get(i).hostName.equals(serverName)){
                        shop = ArrayShops.get(i);
                    }
                }
            }

        }

    }

        // COMMON

        Long shopID = null;
        UserShopRole userRole = null;
        String logoURL = null;
        List<ProductCategory> categoriesList = null;
        ShopPages shopPages = null;

        if(shop != null){
            shopID = shop.id;
            userRole = shopService.getUserShopRole(shop.id);
            logoURL = shop.logoURL;

            ShopBOServiceImpl shopBOService = new ShopBOServiceImpl(request.getSession().getId());
            shopPages = shopBOService.getShopPages(shop.id);

            // BACKOFFICE
            categoriesList = shopService.getAllCategories(shopID);

        }
        pageContext.setAttribute("shop", shop);
        pageContext.setAttribute("logoURL", logoURL);
        pageContext.setAttribute("shopID", shopID);
        pageContext.setAttribute("userRole", userRole);

        boolean isSimpleUser = false;
        String hiddenClass = "";
        if(userRole != UserShopRole.BACKOFFICER && userRole != UserShopRole.ADMIN && userRole != UserShopRole.OWNER){
            isSimpleUser = true;
            hiddenClass = "hidden";
        }
        pageContext.setAttribute("isSimpleUser",isSimpleUser);
        pageContext.setAttribute("hiddenClass",hiddenClass);

        if(categoriesList != null && categoriesList.size() > 0){
            pageContext.setAttribute("categories", categoriesList);
        }

        pageContext.setAttribute("shopPages", shopPages);


        pageContext.setAttribute("isAdminka",isAdminka);
        pageContext.setAttribute("noPhotoPic","../i/no-photo.png");

        boolean isProduction = false;
        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production){
            isProduction = true;
        }
        pageContext.setAttribute("isProduction",isProduction);
%>


