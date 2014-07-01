package com.vmesteonline.be;

import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.store.DataStoreFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

class Utilites {

	public static String clnScrts = "{" + "\"web\": {" + "\"client_id\": \"290786477692.apps.googleusercontent.com\","
			+ "\"client_secret\": \"IiK6TuzttYzupLD7vlAVWr5P\" " + "}" + "}";

	/**
	 * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single globally shared instance across your application.
	 */
	private static final AppEngineDataStoreFactory DATA_STORE_FACTORY = AppEngineDataStoreFactory.getDefaultInstance();

	private static GoogleClientSecrets clientSecrets = null;
//	private static final Set<String> SCOPES = Collections.singleton(PlusScopes.PLUS_ME);
	static final String MAIN_SERVLET_PATH = "/plussampleservlet";
	static final String AUTH_CALLBACK_SERVLET_PATH = "/oauth";
	static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static GoogleClientSecrets getClientSecrets() throws IOException {
		if (clientSecrets == null) {
			clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new StringReader(clnScrts));
		}
		return clientSecrets;
	}

	static GoogleAuthorizationCodeFlow initializeFlow(String id, String secret) throws IOException {
		return null;//new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,  id, secret, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
	}

	static String getRedirectUri(HttpServletRequest req) {
		GenericUrl requestUrl = new GenericUrl(req.getRequestURL().toString());
		requestUrl.setRawPath(AUTH_CALLBACK_SERVLET_PATH);
		return requestUrl.build();
	}
}
