package com.github.r1j0.bugspot.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;

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
		
		for (LogEntries logEntry: logEntries) {
			Matcher matcher = ticketPattern.matcher(logEntry.getMessage());
			
			if (matcher.find()) {
				// TODO capture all ticket references, not only the first one
				String ticket = matcher.group(1);
				try {
					HttpClient c = new DefaultHttpClient();
					HttpGet req = new HttpGet(properties.getProperty("filter.RedmineFilter.base") + ticket + ".json");
					
					if (properties.containsKey("filter.RedmineFilter.username")) {
						UsernamePasswordCredentials cred = new UsernamePasswordCredentials(
							properties.getProperty("filter.RedmineFilter.username"),
							properties.getProperty("filter.RedmineFilter.password")
						);
						
						req.addHeader(BasicScheme.authenticate(cred, "US-ASCII", false));
					}
					HttpEntity entity = c.execute(req).getEntity();
					BufferedReader r = new BufferedReader(new InputStreamReader(entity.getContent()));
					JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(r.readLine());
					String tracker = jsonObject.getJSONObject("issue").getJSONObject("tracker").getString("name");
					log.trace("found Ticket " + ticket + " (" + tracker + ") in: " + logEntry.getMessage());
					
					if (!tracker.matches(properties.getProperty("filter.RedmineFilter.tracker"))) {
						log.debug(tracker + " not configured as bug tracker ( does not match " + properties.getProperty("filter.RedmineFilter.tracker") +"), filter commit");
						continue;
					}
				} catch (URISyntaxException e) {
					log.warn(e.getMessage());
				} catch (IOException e) {
					log.warn(e.getMessage());
				} catch (HttpException e) {
					log.warn(e.getMessage());
				} catch (JSONException e) {
					log.warn("processed ticket " + ticket+". Got Error: " + e.getMessage());
				}
			} 
			
			filtered.add(logEntry);
		}
		
		return filtered;
	}

}
