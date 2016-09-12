
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.MalformedInputException;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.TimerTask;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
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
		
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		int index = dataMap.getIntValue("index");
		
		//System.out.println("[index preliminar job] "+index);
		
		index = Test_quartz.indexGroupJob.indexOf(index);

		//System.out.println("[real index preliminar job] "+index);
		
		try {
			schedulerB.scheduleJob( Test_quartz.groupPreliminarJob.get(index),
									Test_quartz.groupPreliminarTrigger.get(index));
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/*
		while( i < Test_quartz.groupPreliminarJob.size() )
		{
			//JobDetail job = (Test_quartz.groupPreliminarJob.get(i));
			
			System.out.println("["+(i+1)+"] ");
			
			//Trigger trg = ();
			
			try {
				schedulerB.scheduleJob( Test_quartz.groupPreliminarJob.get(i),
										Test_quartz.groupPreliminarTrigger.get(i));
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			++i;
		}*/
		
		/*
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		int in = dataMap.getIntValue("index");
		
		System.out.println("[JOB DESCRIPTION] "+Test_quartz.groupPreliminarJob.get(in).toString());
		
		System.out.println("[ JOB ] "+in);
		
		JobDetail job = JobBuilder.newJob(PrintOKJob.class).withIdentity(""+in, "group2 "+in).build();
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY)+1);
		
		System.out.println("[PRELIMINAR JOB SCHEDULED] "+cal.get(Calendar.HOUR_OF_DAY));
		
		Trigger triggerB = TriggerBuilder.newTrigger()
										 .withIdentity("triggerB "+in, "group2 "+in)
										 .withSchedule( SimpleScheduleBuilder.simpleSchedule()
												 		 .repeatSecondlyForever(17))
												 		 .endAt(cal.getTime()).build();
		*/
		//Scheduler schedulerB = null;
		/*try {
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
		}*/
		/*try {
			schedulerB.scheduleJob(job,triggerB);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

}
