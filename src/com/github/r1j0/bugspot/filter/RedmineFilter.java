package com.github.r1j0.bugspot.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.github.r1j0.bugspot.repository.LogEntries;

/**
 * filter out commits with ticket references in the commit message where those tickets were
 * not marked as bug
 * 
 * @author 0x20h
 */
public class RedmineFilter implements Filter {

	public List<LogEntries> filter(List<LogEntries> logEntries, Properties properties) {
		List<LogEntries> filtered = new ArrayList<LogEntries>();
		
		for (LogEntries logEntry: logEntries) {
			Matcher matcher = Pattern.compile(properties.getProperty("filter.RedmineFilter.ticket")).matcher(logEntry.getMessage());
			
			if (matcher.find()) {
				// TODO capture all ticket references
				String ticket = matcher.group(1);
				
				try {
					HttpClient c = new DefaultHttpClient();
					HttpGet req = new HttpGet(properties.getProperty("filter.RedmineFilter.base") + ticket + ".json");
					HttpResponse rsp = c.execute(req);
					HttpEntity entity = rsp.getEntity();
					InputStream in = entity.getContent();
					BufferedReader r = new BufferedReader(new InputStreamReader(in));
					String raw = r.readLine();
					System.err.println(raw);
					JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(raw);
					String tracker = jsonObject.getJSONObject("issue").getJSONObject("tracker").getString("name");
					
					if (tracker.matches(properties.getProperty("filter.RedmineFilter.tracker"))) {
						continue;
					}
				} catch (URISyntaxException e) {
					System.err.println(e.getMessage());
				} catch (IOException e) {
					System.err.println(e.getMessage());
				} catch (HttpException e) {
					System.err.println(e.getMessage());
				}
			} 
			
			filtered.add(logEntry);
		}
		
		return filtered;
	}

}
