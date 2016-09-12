import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Get_url implements Job{
	// passiamo come paramentro di jobdatamap solo l'indice, con l'indice si va a prendere il valore dell'url dall'array
	//sono commentati al momento, non so se necessitano di qualche lock o cmq di qualche controllo sull'accesso visto che comunque
	// parliamo di memoria condivisa. funziona in tutti e due i casi.
	public void execute(JobExecutionContext context) throws JobExecutionException{
	    Logger logger = LoggerFactory.getLogger(Get_url.class);
		try{
			JobKey key = context.getJobDetail().getKey();
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			
			String urlpax = dataMap.getString("url");
			String userAgent = dataMap.getString("userAgent");
			
			System.out.println(" ----> UA : "+userAgent);
			
			long maxContact = dataMap.getLong("maxContact");
			Integer index = dataMap.getInt("index");
			
//			String urlpax = Test_quartz.connectUrl.get(index).getUrl();
//			long maxContact = Test_quartz.connectUrl.get(index).getMax_contact();
			Integer c = Test_quartz.maxC.get(index);
			
			if(c < maxContact){
				Test_quartz.maxC.set(index,c = c + 1);
				URL url  = new URL(urlpax);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				
				if( !userAgent.equals("default"))
					connection.setRequestProperty("User-Agent", userAgent);
				
				Date date = new Date();
				String x = date.toString();
				int code  = connection.getResponseCode();
				
				//System.out.println( connection.getHeaderField("User-Agent") );
				
				if(code == HttpURLConnection.HTTP_OK){
					System.out.println("url "+ urlpax +"status connection :: " + code+ " date:: " + date);
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
