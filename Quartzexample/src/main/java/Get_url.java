import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.MalformedInputException;

import javax.net.ssl.HttpsURLConnection;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Get_url implements Job{

															/*
															 * 	connect to a resource by HTTP protocol
															 * 
															 */
	private void conn_http( URL url, Proxy proxy_web_server, String userAgent, 
							int index, int c, long maxContact, Logger logger ) throws IOException
	{
		HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy_web_server);

		String urlpax = url.toString();
		
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

															/*
															 * 	connect to a resource by HTTPs protocol
															 * 
															 */	
	private void conn_https( URL url, Proxy proxy_web_server, String userAgent, 
							 int index, int c, long maxContact, Logger logger ) throws IOException
	{
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection(proxy_web_server);

		String urlpax = url.toString();
		
				/*
				 * Setup the user-agent field into http header packet 
				 */
		if( !( userAgent.equals("default") ) )
			connection.setRequestProperty("User-Agent", userAgent);
		
		if(c < maxContact)
		{
			Test_quartz.maxC.set(index,c = c + 1);
		
			DateTime date = new DateTime();
		
			int code = connection.getResponseCode();
		
			if(code == HttpURLConnection.HTTP_OK){
				System.out.println("url "+ urlpax +"status connection :: " + code+ " date:: " + date.toString("dd-MM-yyyy HH:mm:ss"));
				logger.info("{},{}",urlpax,date);
			}
		}
	}

															/*
															 * 	retrieve the data to the connection.
															 * 
															 */	
	private void connect( JobDataMap dataMap, URL url, Logger logger ) throws IOException
	{
		String userAgent = dataMap.getString("userAgent");
		String proxy_hostname = dataMap.getString("proxy");
		int proxy_port = Integer.parseInt(dataMap.getString("proxy_port"));
		
		long maxContact = dataMap.getLong("maxContact");
		Integer index = dataMap.getInt("index");
		Integer c = Test_quartz.maxC.get(index);

		Proxy proxy_web_server = Proxy.NO_PROXY;
		
				/*
				 * Setup a connection to a public proxy web-server.
				 */
		if ( !( proxy_hostname.equals("nope") ) )
		{
			proxy_web_server = new Proxy( Proxy.Type.HTTP, 
							   new InetSocketAddress(proxy_hostname, proxy_port) );
		}

		if ( url.getProtocol().equals("http") )
			conn_http( url, proxy_web_server, userAgent, index, c, maxContact, logger);
		else 
		{
			if ( url.getProtocol().equals("https") )
				conn_https( url, proxy_web_server, userAgent, index, c, maxContact, logger);
			else
			{
				System.out.println("[ERROR] The protocol is unknown! ( only allowed \"http\" or \"https\")");
				System.exit(1);
			}
		}
	}	
	
	public void execute(JobExecutionContext context) throws JobExecutionException{
	   
		Logger logger = LoggerFactory.getLogger(Get_url.class);
		
		try
		{	
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();

			URL url  = new URL( dataMap.getString("url") );
			
			connect( dataMap, url, logger );

		}catch(MalformedInputException ex){
				ex.printStackTrace();
		}catch (UnknownHostException ex) {
				ex.printStackTrace();
		}catch(IOException ex){
				ex.printStackTrace();
		}
	}
}
