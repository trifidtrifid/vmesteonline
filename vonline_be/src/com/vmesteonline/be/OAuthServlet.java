package com.vmesteonline.be;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoUser;

/*import com.restfb.DefaultFacebookClient;
 import com.restfb.FacebookClient;
 import com.restfb.FacebookClient.AccessToken;
 import com.restfb.types.Album;
 import com.restfb.types.User;
 */
public class OAuthServlet extends HttpServlet {

	private static final long serialVersionUID = -6391276180341584453L;
	private static final String domain = "https://1-dot-algebraic-depot-657.appspot.com/";

	private String generatePassword() {
		StringBuilder sb = new StringBuilder();
		int n = 8; // how many characters in password
		String set = "ABCDEFJHIJKLMNOPQRSTUVWXYZabcdefjhijklmnopqrstuvwxyz1234567890"; // characters to choose from

		for (int i = 0; i < n; i++) {
			Random rand = new Random(System.nanoTime());
			int k = rand.nextInt(set.length()); // random number between 0 and set.length()-1 inklusive
			sb.append(set.charAt(k));
		}
		return sb.toString();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		resp.setContentType("text/html; charset=utf-8");

		String authCode = req.getParameter("code");

		String inviteCode = req.getParameter("state");

		resp.getWriter().println("<head><meta charset=\"utf-8\">");
		resp.getWriter().println("request " + req.toString());
		resp.getWriter().println("try authorize in " + inviteCode + " with code=" + authCode);

		try {
			String response = runUrl(new URL("https://oauth.vk.com/access_token?client_id=4429306&redirect_uri=" + domain
					+ "oauth&client_secret=oQBV8uO3tHyBONHcNsxe&code=" + authCode));
			JSONObject jsonObj = new JSONObject(response.toString());

			AuthServiceImpl authServiceImpl = new AuthServiceImpl();
			authServiceImpl.setSession(req.getSession());
			String email = jsonObj.getString("email");

			resp.getWriter().println("<br><br>" + email + " find");

			if (inviteCode == null || inviteCode.isEmpty()) {
				try {
					switch (authServiceImpl.allowUserAccess(email, "", false)) {
					case SUCCESS:
						resp.sendRedirect(domain + "main");
						break;
					case EMAIL_NOT_CONFIRMED:
						resp.sendRedirect(domain + "login.html?invalid_email=" + email);
						break;
					default:
						resp.sendRedirect(domain + "login.html?cantlogin=" + email);
						break;
					}
				} catch (Exception e) {
					resp.sendRedirect(domain + "login.html?invalid_email=" + email);
					return;
				}

			} else {
				URL url = new URL("https://api.vk.com/method/users.get?user_id=" + jsonObj.getString("user_id") + "&v=5.23&access_token="
						+ jsonObj.getString("access_token") + "&fields=first_name,last_name,sex,bdate,photo_medium");
				String resp2 = runUrl(url);
				JSONObject jsonObj2 = new JSONObject(resp2);

				resp.getWriter().println("<br><br>  sdfsdf " + resp2 + " access token " + jsonObj.getString("access_token") + " req " + url.toString());

				JSONArray vkResp = jsonObj2.getJSONArray("response");
				JSONObject o = (JSONObject) vkResp.get(0);

				if (inviteCode.startsWith("inviteCode:")) {
					inviteCode = inviteCode.substring(inviteCode.lastIndexOf(":") + 1);
					String password = generatePassword();
					authServiceImpl.registerNewUser(o.getString("first_name"), o.getString("last_name"), password, email, inviteCode, o.getInt("sex"), false);
					authServiceImpl.allowUserAccess(email, "", false);

				}

				PersistenceManager pm = PMF.getPm();
				try {
					VoUser user = authServiceImpl.getUserByEmail(email, pm);
					if (user != null) {
						resp.getWriter().println("<br>find user " + email + " avatar " + o.getString("photo_medium"));
						user.setAvatarTopic(o.getString("photo_medium"));
						user.setAvatarMessage(o.getString("photo_medium"));
						user.setAvatarProfileShort(o.getString("photo_medium"));
						user.setAvatarProfile(o.getString("photo_medium"));
						SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
						Date date = formatter.parse(o.getString("bdate"));
						long ts = date.getTime() / 1000L;
						user.setBirthday((int) ts);

					} else {
						resp.getWriter().println("<br>can't find user " + email);
					}

				} finally {
					pm.close();
				}

				if (inviteCode.startsWith("import")) {
					resp.sendRedirect(domain + "settings");
				} else
					resp.sendRedirect(domain + "main");

			}

		} catch (Exception e) {
			e.printStackTrace();
			resp.sendRedirect(domain + "login.html?error=" + e.toString());
		}

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
}
