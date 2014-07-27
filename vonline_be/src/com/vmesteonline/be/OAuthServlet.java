package com.vmesteonline.be;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.vmesteonline.be.utils.EMailHelper;

/*import com.restfb.DefaultFacebookClient;
 import com.restfb.FacebookClient;
 import com.restfb.FacebookClient.AccessToken;
 import com.restfb.types.Album;
 import com.restfb.types.User;
 */
public class OAuthServlet extends HttpServlet {

	private static final long serialVersionUID = -6391276180341584453L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		resp.setContentType("text/html; charset=utf-8");

		String authCode = req.getParameter("code");
		String state = req.getParameter("state");
		resp.getWriter().println("<head><meta charset=\"utf-8\">");

		resp.getWriter().println("request " + req.toString());

		resp.getWriter().println("try authorize in " + state + " with code=" + authCode);

		URL obj = new URL(
				"https://oauth.vk.com/access_token?client_id=4463293&redirect_uri=https://3-dot-vmesteonline.appspot.com/oauth&client_secret=S8wYzpGUtzomnv1Pvcpv&code="
						+ authCode);
		String response = runUrl(obj);

		try {
			JSONObject jsonObj = new JSONObject(response.toString());
			AuthServiceImpl authServiceImpl = new AuthServiceImpl();
			authServiceImpl.setSession(req.getSession());
			resp.getWriter().println("<br><br>" + jsonObj.getString("email") + " find");

			String email = jsonObj.getString("email");

			if (!authServiceImpl.allowUserAccess(email, "", false)) {
				String resp2 = runUrl(new URL("https://api.vk.com/method/users.get?user_id=" + jsonObj.getString("user_id") + "&v=5.23&access_token="
						+ jsonObj.getString("access_token")));
				JSONObject jsonObj2 = new JSONObject(resp2);

				resp.getWriter().println("<br><br>  sdfsdf " + resp2);

				JSONArray vkResp = jsonObj2.getJSONArray("response");
				JSONObject o = (JSONObject) vkResp.get(0);

				authServiceImpl.registerNewUser(o.getString("first_name"), o.getString("last_name"), "123456", email, null);
				authServiceImpl.allowUserAccess(email, "", false);
				EMailHelper.sendSimpleEMail(email, "Регистрация на сайте voclub.co",
						"вы зарегистрировались на сайте http://voclub.co. Ваш логин для входа: " + email
								+ ". Ваш пароль: 123456. Рекомендуем изменить.");
			}

		} catch (Exception e) {
			resp.getWriter().println("<br><br>  sdfsdf " + e.toString());
			e.printStackTrace();
		}

		resp.sendRedirect(state + "?rt=ci&bu="+URLEncoder.encode("https://3-dot-vmesteonline.appspot.com/"));

	}

	private String runUrl(URL obj) throws IOException {
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}

	/*
	 * else if (state.equals("facebook")) {
	 * 
	 * 
	 * AccessToken accessToken = new DefaultFacebookClient().obtainAppAccessToken("293608184137183", "0b93bf9f2c099da8503497d908c5aabd");
	 * 
	 * 
	 * URL obj = new URL(
	 * "https://graph.facebook.com/oauth/access_token?client_id=293608184137183&redirect_uri=https://1-dot-vmesteonline.appspot.com/oauth&client_secret=0b93bf9f2c099da8503497d908c5aabd&code="
	 * + authCode); HttpURLConnection con = (HttpURLConnection) obj.openConnection(); int responseCode = con.getResponseCode();
	 * resp.getWriter().println("Response Code : " + responseCode);
	 * 
	 * BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())); String inputLine; StringBuffer response = new
	 * StringBuffer();
	 * 
	 * while ((inputLine = in.readLine()) != null) { response.append(inputLine); } in.close();
	 * 
	 * String[] pairs = response.toString().split("&"); HashMap<String, String> respVals = new HashMap<String, String>(); for (String data : pairs) {
	 * String[] p = data.split("="); if (p.length == 2) { respVals.put(p[0], p[1]); } } resp.getWriter().println("token is '" + response.toString() +
	 * "'");
	 * 
	 * resp.getWriter().println("token is '" + respVals.get("access_token") + "'");
	 * 
	 * FacebookClient facebookClient = new DefaultFacebookClient(respVals.get("access_token")); User user = facebookClient.fetchObject("me",
	 * User.class); resp.getWriter().println("email is " + user.getEmail()); resp.getWriter().println("name is " + user.getName() + " " +
	 * user.getLastName()); resp.getWriter().println("avatar is https://graph.facebook.com/" + user.getId() + "/picture?type=large"); }
	 */

	/*
	 * if (state.equals("google")) { GoogleAuthorizationCodeFlow flow = Utils.initializeFlow("290786477692.apps.googleusercontent.com",
	 * "IiK6TuzttYzupLD7vlAVWr5P"); GoogleTokenResponse response = flow.newTokenRequest
	 * (authCode).setRedirectUri("https://1-dot-vmesteonline.appspot.com/oauth" ).execute(); final Credential credential =
	 * flow.createAndStoreCredential(response, "userid"); final HttpRequestFactory requestFactory =
	 * Utils.HTTP_TRANSPORT.createRequestFactory(credential); // Make an authenticated request final GenericUrl url = new
	 * GenericUrl("https://www.googleapis.com/oauth2/v1/userinfo"); final HttpRequest request = requestFactory.buildGetRequest(url);
	 * request.getHeaders().setContentType("application/json"); final String jsonIdentity = request.execute().parseAsString();
	 * 
	 * resp.getWriter().println("Hello, world " + jsonIdentity); try { JSONObject jsonObj = new JSONObject(jsonIdentity);
	 * resp.getWriter().println("<br><br>" + jsonObj.getString("email"));
	 * 
	 * } catch (JSONException e) { // TODO Auto-generated catch block e.printStackTrace(); } }
	 */
}
