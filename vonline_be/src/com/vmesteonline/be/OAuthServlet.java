package com.vmesteonline.be;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

/*import com.restfb.DefaultFacebookClient;
 import com.restfb.FacebookClient;
 import com.restfb.FacebookClient.AccessToken;
 import com.restfb.types.Album;
 import com.restfb.types.User;
 */
public class OAuthServlet extends HttpServlet {
	private static final Iterable<String> SCOPE = Arrays
			.asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email".split(";"));

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		resp.setContentType("text/plain");

		String authCode = req.getParameter("code");
		String state = req.getParameter("state");
		resp.getWriter().println("request " + req.toString());

		resp.getWriter().println("try authorize in " + state + " with code=" + authCode);

		URL obj = new URL(
				"https://oauth.vk.com/access_token?client_id=4429306&redirect_uri=https://1-dot-vmesteonline.appspot.com/oauth&client_secret=oQBV8uO3tHyBONHcNsxe&code="
						+ authCode);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		int responseCode = con.getResponseCode();
		resp.getWriter().println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		resp.getWriter().println("Hello, world " + response);
		try {
			JSONObject jsonObj = new JSONObject(response.toString());
			// resp.getWriter().println("<br><br>" +
			// jsonObj.getString("access_token"));
			resp.getWriter().println("<br><br>" + jsonObj.getString("email"));

		} catch (JSONException e) {
			resp.getWriter().println("<br><br>  sdfsdf " + e.toString());

			e.printStackTrace();
		}

	}

	/*
	 * else if (state.equals("facebook")) {
	 * 
	 * 
	 * AccessToken accessToken = new
	 * DefaultFacebookClient().obtainAppAccessToken("293608184137183",
	 * "0b93bf9f2c099da8503497d908c5aabd");
	 * 
	 * 
	 * URL obj = new URL(
	 * "https://graph.facebook.com/oauth/access_token?client_id=293608184137183&redirect_uri=https://1-dot-vmesteonline.appspot.com/oauth&client_secret=0b93bf9f2c099da8503497d908c5aabd&code="
	 * + authCode); HttpURLConnection con = (HttpURLConnection)
	 * obj.openConnection(); int responseCode = con.getResponseCode();
	 * resp.getWriter().println("Response Code : " + responseCode);
	 * 
	 * BufferedReader in = new BufferedReader(new
	 * InputStreamReader(con.getInputStream())); String inputLine; StringBuffer
	 * response = new StringBuffer();
	 * 
	 * while ((inputLine = in.readLine()) != null) { response.append(inputLine);
	 * } in.close();
	 * 
	 * String[] pairs = response.toString().split("&"); HashMap<String, String>
	 * respVals = new HashMap<String, String>(); for (String data : pairs) {
	 * String[] p = data.split("="); if (p.length == 2) { respVals.put(p[0],
	 * p[1]); } } resp.getWriter().println("token is '" + response.toString() +
	 * "'");
	 * 
	 * resp.getWriter().println("token is '" + respVals.get("access_token") +
	 * "'");
	 * 
	 * FacebookClient facebookClient = new
	 * DefaultFacebookClient(respVals.get("access_token")); User user =
	 * facebookClient.fetchObject("me", User.class);
	 * resp.getWriter().println("email is " + user.getEmail());
	 * resp.getWriter().println("name is " + user.getName() + " " +
	 * user.getLastName());
	 * resp.getWriter().println("avatar is https://graph.facebook.com/" +
	 * user.getId() + "/picture?type=large"); }
	 */

	/*
	 * if (state.equals("google")) { GoogleAuthorizationCodeFlow flow =
	 * Utils.initializeFlow("290786477692.apps.googleusercontent.com",
	 * "IiK6TuzttYzupLD7vlAVWr5P"); GoogleTokenResponse response =
	 * flow.newTokenRequest
	 * (authCode).setRedirectUri("https://1-dot-vmesteonline.appspot.com/oauth"
	 * ).execute(); final Credential credential =
	 * flow.createAndStoreCredential(response, "userid"); final
	 * HttpRequestFactory requestFactory =
	 * Utils.HTTP_TRANSPORT.createRequestFactory(credential); // Make an
	 * authenticated request final GenericUrl url = new
	 * GenericUrl("https://www.googleapis.com/oauth2/v1/userinfo"); final
	 * HttpRequest request = requestFactory.buildGetRequest(url);
	 * request.getHeaders().setContentType("application/json"); final String
	 * jsonIdentity = request.execute().parseAsString();
	 * 
	 * resp.getWriter().println("Hello, world " + jsonIdentity); try {
	 * JSONObject jsonObj = new JSONObject(jsonIdentity);
	 * resp.getWriter().println("<br><br>" + jsonObj.getString("email"));
	 * 
	 * } catch (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 */
}
