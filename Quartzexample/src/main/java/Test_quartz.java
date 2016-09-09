import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class Test_quartz{
	private static ArrayList<JobDetail> groupJob = new ArrayList<JobDetail>();
	private static ArrayList<Trigger> groupTrigger = new ArrayList<Trigger>();
	public static ArrayList<urlDependency> connectUrl = new ArrayList<urlDependency>();
	public static  ArrayList<Integer> maxC = new ArrayList<Integer>();

	//read config file of the url and timing
	public static void readConfigFile(String configPath) throws FileNotFoundException{
		FileReader readerConfig = new FileReader(configPath);
		Scanner scanner = new Scanner(readerConfig);
		String urlFileName = "";
		String timingFileName = "";
		if(scanner.hasNextLine()){
			urlFileName = scanner.nextLine();
			if(scanner.hasNextLine()){
				timingFileName = scanner.nextLine();
			}else 
				System.out.println("Error missed configuration time file");
		}
		FileReader readerTimining = new FileReader(timingFileName);
		FileReader readerUrl = new FileReader(urlFileName);
		Scanner scannerUrl = new Scanner(readerUrl);
		Scanner scannerTime = new Scanner(readerTimining);
		scannerTime.nextLine();
		scannerUrl.nextLine();
		//scanner of url and timing
		while(scannerTime.hasNextLine()){
			urlDependency urlD = new urlDependency();
			String timing = scannerTime.nextLine();
			String own_url = scannerUrl.nextLine();
			urlD.setUrl(own_url);
			System.out.println(urlD.getUrl());
			urlD.setValue(timing);
			System.out.println("this is the cron Expression:: "+ urlD.getCronExpression());
			connectUrl.add(urlD);
			//System.out.println("max contact = " + urlD.getMax_contact());
		}
		
	};
	//set the value of job and trigger, we can init the values for maxContact list
	public static void settingJobTrigger(){
		int i = 0;

			while(i < connectUrl.size()){
				JobDetail job = JobBuilder.newJob(Get_url.class).withIdentity("job"+i, "group"+i)
						.usingJobData("url", connectUrl.get(i).getUrl())
						.usingJobData("index", i)
						.usingJobData("maxContact", connectUrl.get(i).getMax_contact())
						.build();
					Trigger trigger;
					System.out.println("sleep mode :" + connectUrl.get(i).isSleep_mode() );
				if(connectUrl.get(i).isSleep_mode() == 1){
					trigger =TriggerBuilder.newTrigger().withIdentity("cronT" + i, "group"+ i)
							.withSchedule(CronScheduleBuilder.cronSchedule((connectUrl.get(i)).getCronExpression()))
							.build();
					}else{
						trigger = TriggerBuilder.newTrigger().withIdentity("simple"+i,"group" + i)
						.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(connectUrl.get(i).getFixed_frequency()).repeatForever())
						.build();
					}
				maxC.add(0);
				groupJob.add(job);
				groupTrigger.add(trigger);
				i++;
				
			}
		
	};
	//schedule job
	public static void runJob() throws SchedulerException{
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.start();
		int i = 0;
		while(i<groupJob.size() && i<groupTrigger.size()){
			scheduler.scheduleJob(groupJob.get(i),groupTrigger.get(i));
			i++;
		}
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println("OUR BOT IS STARTING");
		
		readConfigFile("config.txt");
		settingJobTrigger();
		runJob();
	}

}
