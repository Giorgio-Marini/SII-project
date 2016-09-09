import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.MalformedInputException;
import java.util.Date;
import java.util.Scanner;
import java.util.TimerTask;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;


public class PreliminarJob implements Job {
	public void execute(JobExecutionContext context) throws JobExecutionException{
		JobDetail job = JobBuilder.newJob(Get_url.class).withIdentity("2", "group2").build();
		
		FileReader readerTimining = null;
		urlDependency urlD = new urlDependency();

		try {
			readerTimining = new FileReader("timing.txt");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Scanner scannerin = new Scanner(readerTimining);
		scannerin.nextLine();
		while(scannerin.hasNextLine()){
			String timing = scannerin.nextLine();
			urlD.setValue(timing);
			
		}
		Trigger triggerB = TriggerBuilder.newTrigger().withIdentity("triggerB", "group2")
		.withSchedule(SimpleScheduleBuilder.simpleSchedule()
				.withIntervalInSeconds(urlD.getFixed_frequency()).repeatForever()).build();
		
		Scheduler schedulerB = null;
		try {
			schedulerB = new StdSchedulerFactory().getScheduler();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			schedulerB.start();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			schedulerB.scheduleJob(job,triggerB);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
