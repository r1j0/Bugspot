package com.github.r1j0.bugspot.filter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import com.github.r1j0.bugspot.repository.LogEntries;

/**
 * filter out commits with ticket references in the commit message where those tickets were
 * not marked as bug (e.g. look at the corresponding tracker).
 * 
 * @author 0x20h
 */
public class RedmineFilter implements Filter {
	private static Log log = LogFactory.getLog(FilterChain.class);

	
	public List<LogEntries> filter(List<LogEntries> logEntries, Properties properties) {
		List<LogEntries> filtered = new ArrayList<LogEntries>();
		Pattern ticketPattern = Pattern.compile(properties.getProperty("filter.RedmineFilter.ticket"));
		HttpClient client = null;
		
		if (properties.getProperty("filter.RedmineFilter.insecure", "false").equals("true")) {
			// more or less got that from 
			// http://stackoverflow.com/questions/2703161/how-to-ignore-ssl-certificate-errors-in-apache-httpclient-4-0
			SSLContext sslContext;

			try {
				sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, new TrustManager[] { new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {return null;}
					public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
					public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
				}}, new SecureRandom());
				
				SSLContext.setDefault(sslContext);
				SSLSocketFactory sf = SSLSocketFactory.getSocketFactory();
				Scheme httpsScheme = new Scheme("https", sf, 443);
				SchemeRegistry schemeRegistry = new SchemeRegistry();
				schemeRegistry.register(httpsScheme);
				
				HttpParams params = new BasicHttpParams();
				ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);
				client = new DefaultHttpClient(cm, params);
			} catch (Exception e) {
				log.fatal(e.getStackTrace());
				System.exit(1);
			} 
		} else {
			client = new DefaultHttpClient();
		}
		
		for (LogEntries logEntry: logEntries) {
			Matcher matcher = ticketPattern.matcher(logEntry.getMessage());
			
			if (matcher.find()) {
				// TODO capture all ticket references, not only the first one
				String ticket = matcher.group(1);
				
				try {
					URI uri = new URI(properties.getProperty("filter.RedmineFilter.base") + ticket + ".json");
					HttpGet req = new HttpGet(uri);

					if (properties.containsKey("filter.RedmineFilter.username")) {
						UsernamePasswordCredentials cred = new UsernamePasswordCredentials(
							properties.getProperty("filter.RedmineFilter.username"),
							properties.getProperty("filter.RedmineFilter.password")
						);
						
						req.addHeader(BasicScheme.authenticate(cred, "US-ASCII", false));
					}
					
					HttpEntity entity = client.execute(req).getEntity();
					BufferedReader r = new BufferedReader(new InputStreamReader(entity.getContent()));
					JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(r.readLine());
					String tracker = jsonObject.getJSONObject("issue").getJSONObject("tracker").getString("name");
					log.trace("Found ticket " + ticket + " (" + tracker + ") in: " + logEntry.getMessage());
					
					if (!tracker.matches(properties.getProperty("filter.RedmineFilter.tracker"))) {
						log.debug(tracker + " not configured as bug tracker ( does not match " + properties.getProperty("filter.RedmineFilter.tracker") +"), filter commit");
						continue;
					}
				} catch (Exception e) {
					log.warn(e.getMessage() + ":"+ e.getStackTrace().toString());
				}
			} 
			
			filtered.add(logEntry);
		}
		
		return filtered;
	}

}
