import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Get_url implements Job{

	
	public void conn_http( JobDataMap dataMap, URL url, String urlpax, Logger logger ) throws IOException
	{
		String userAgent = dataMap.getString("userAgent");
		String proxy_hostname = dataMap.getString("proxy");
		int proxy_port = Integer.parseInt(dataMap.getString("proxy_port"));
		
		long maxContact = dataMap.getLong("maxContact");
		Integer index = dataMap.getInt("index");
		Integer c = Test_quartz.maxC.get(index);

		HttpURLConnection connection = null;
		
				/*
				 * Setup a connection to a public proxy web-server.
				 */
		if ( !( proxy_hostname.equals("nope") ) )
		{
			Proxy proxy_web_server = new Proxy( Proxy.Type.HTTP, 
								 	 new InetSocketAddress(proxy_hostname, proxy_port) );
		
			connection = (HttpURLConnection) url.openConnection(proxy_web_server);
		}
		else
		{			
			connection = (HttpURLConnection) url.openConnection();
		}		

				/*
				 * Setup the user-agent field into http header packet 
				 */
		if( !( userAgent.equals("default") ) )
			connection.setRequestProperty("User-Agent", userAgent);
		
		if(c < maxContact)
		{
			Test_quartz.maxC.set(index,c = c + 1);
		
			DateTime date = new DateTime();
		
			int code  = connection.getResponseCode();
		
			if(code == HttpURLConnection.HTTP_OK){
				System.out.println("url "+ urlpax +"status connection :: " + code+ " date:: " + date.toString("dd-MM-yyyy HH:mm:ss"));
				logger.info("{},{}",urlpax,date);
			}
		}
	}	
	
	public void conn_https( JobDataMap dataMap, URL url, String urlpax, Logger logger ) throws IOException
	{	
		String userAgent = dataMap.getString("userAgent");
	
		String proxy_hostname = dataMap.getString("proxy");
		
		int proxy_port = Integer.parseInt(dataMap.getString("proxy_port"));
	
		System.out.println("OK HTTPS");
		long maxContact = dataMap.getLong("maxContact");
		Integer index = dataMap.getInt("index");
		Integer c = Test_quartz.maxC.get(index);

		HttpsURLConnection connection = null;
		

				/*
				 * Setup a connection to a public proxy web-server.
				 */
		if ( !( proxy_hostname.equals("nope") ) )
		{
			Proxy proxy_web_server = new Proxy( Proxy.Type.HTTP, 
								 	 new InetSocketAddress(proxy_hostname, proxy_port) );
		
			connection = (HttpsURLConnection) url.openConnection(proxy_web_server);
		}
		else
		{			
			connection = (HttpsURLConnection) url.openConnection();
		}		

				/*
				 * Setup the user-agent field into http header packet 
				 */
		if( !( userAgent.equals("default") ) )
			connection.setRequestProperty("User-Agent", userAgent);
		
		if(c < maxContact)
		{
			Test_quartz.maxC.set(index,c = c + 1);
		
			DateTime date = new DateTime();
		
			int code  = connection.getResponseCode();
		
			if(code == HttpURLConnection.HTTP_OK){
				System.out.println("url "+ urlpax +"status connection :: " + code+ " date:: " + date.toString("dd-MM-yyyy HH:mm:ss"));
				logger.info("{},{}",urlpax,date);
			}
		}		
	}
	
	public void execute(JobExecutionContext context) throws JobExecutionException{
	   
		Logger logger = LoggerFactory.getLogger(Get_url.class);
		
		try
		{	
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();

			String urlpax = dataMap.getString("url");

			URL url  = new URL(urlpax);
			
			String []spliturl = urlpax.split(":");
			
			if ( spliturl[0].equals("http") )
			{
				conn_http( dataMap, url, urlpax, logger );
			}
			else if ( spliturl[0].equals("https"))
			{
				conn_https( dataMap, url, urlpax, logger );				
			}

		}catch(MalformedInputException ex){
				ex.printStackTrace();
		}catch (UnknownHostException ex) {
				ex.printStackTrace();
		}catch(IOException ex){
				ex.printStackTrace();
		}
	}
}
