import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.MalformedInputException;
import java.util.Date;

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
			JobKey key = context.getJobDetail().getKey();
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			String urlpax = dataMap.getString("url");
			URL url  = new URL(urlpax);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			Date date = new Date();
			String x = date.toString();
			int code  = connection.getResponseCode();
			if(code == HttpURLConnection.HTTP_OK){
				System.out.println("url "+ urlpax +"status connection :: " + code+ " date:: " + date);
				logger.info("{},{}",urlpax,date);
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
