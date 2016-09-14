import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Get_url implements Job{

	public void execute(JobExecutionContext context) throws JobExecutionException{
	   
		Logger logger = LoggerFactory.getLogger(Get_url.class);
		
		try{
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			
			String urlpax = dataMap.getString("url");
			String userAgent = dataMap.getString("userAgent");
			
			long maxContact = dataMap.getLong("maxContact");
			Integer index = dataMap.getInt("index");
			Integer c = Test_quartz.maxC.get(index);
			
				/* German Proxy */
			System.setProperty("http.proxyHost", "85.10.235.253");
			System.setProperty("http.proxyPort", "8080");
			
			URL url  = new URL(urlpax);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			if( !userAgent.equals("default"))
				connection.setRequestProperty("User-Agent", userAgent);
			
			
			if(c < maxContact){
				Test_quartz.maxC.set(index,c = c + 1);
				
				DateTime date = new DateTime();
				
				int code  = connection.getResponseCode();
				
				if(code == HttpURLConnection.HTTP_OK){
					System.out.println("url "+ urlpax +"status connection :: " + code+ " date:: " + date.toString("dd-MM-yyyy HH:mm:ss"));
					logger.info("{},{}",urlpax,date);
					}
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
