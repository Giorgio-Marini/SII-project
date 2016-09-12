
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class Test_quartz{
	private static ArrayList<JobDetail> groupJob = new ArrayList<JobDetail>();
	private static ArrayList<Trigger> groupTrigger = new ArrayList<Trigger>();
	public static ArrayList<urlDependency> connectUrl = new ArrayList<urlDependency>();	
	public static  ArrayList<Integer> maxC = new ArrayList<Integer>();

	public static ArrayList<JobDetail> groupPreliminarJob = new ArrayList<JobDetail>();
	public static ArrayList<Trigger> groupPreliminarTrigger = new ArrayList<Trigger>();
	public static ArrayList<Integer> indexGroupJob = new ArrayList<Integer>();
	
	public static int extract_int_range_minute( int min_interval, int max_interval ) throws InterruptedException
	{
		return ThreadLocalRandom.current().nextInt(min_interval, max_interval + 1);
	}
	
	
	public static int count_row_file( String filename ) throws IOException, InterruptedException
	{
		FileReader readFile = new FileReader(filename);
		Scanner scannerFile = new Scanner( readFile );
		
		int i = 0;
		
		try
		{
			while( scannerFile.nextLine() != null )
			{
				++i;
			}
		}
		catch( NoSuchElementException e )
		{
			scannerFile.close();
			readFile.close();
		}
		
		return i;
	}
	
	
	public static boolean check_row_file( String urlFileName, String timingFileName) throws IOException, InterruptedException
	{
		int num_row_url_file = 0,
			num_row_timing_file = 0;
		
		num_row_url_file = count_row_file( urlFileName );
		
		num_row_timing_file = count_row_file( timingFileName );
		
		if ( num_row_url_file == num_row_timing_file )
			return false;
		else
			return true;
	}
	
	//read config file of the url and timing
	public static boolean readConfigFile(String configPath) throws IOException, InterruptedException{
		
		FileReader readerConfig = new FileReader(configPath);
		Scanner scanner = new Scanner(readerConfig);
		
		String urlFileName = "";
		String timingFileName = "";
		
		try
		{
			urlFileName = scanner.nextLine();		
		}
		catch( NoSuchElementException e)
		{
			System.out.println("[WARNING] The config file is empty!");
			return true;			
		}

		try
		{
			timingFileName = scanner.nextLine();		
		}
		catch( NoSuchElementException e)
		{
			System.out.println("Error missed configuration time file");
			return true;			
		}		
		
		scanner.close();
		
		boolean error_config_file = false;
		
		error_config_file = check_row_file(urlFileName, timingFileName);
		
		if ( error_config_file )
		{	
			System.out.println("[WARNING] The URL file and the timing file haven't got the same size");
			return true;
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
			
		return false;
	};
	
	
	private static JobDetail config_job( int i )
	{
		JobDetail job, preliminarJob;
	
		System.out.println("OK FUNCTION config_job");
		
		//int c = i;
		
		if ( ( connectUrl.get(i).isSleep_mode() == 1 ) &&
				 ( !urlDependency.checkValueFrequencySeconds(connectUrl.get(i).getFixed_frequency()))){
				
				System.out.println("- PJ -"+i);
			
				//c = c + 2;				
				
				job = JobBuilder.newJob(PreliminarJob.class).withIdentity("job"+i,"group"+i)
										.usingJobData("url", connectUrl.get(i).getUrl())
										.usingJobData("index", i)
										.usingJobData("maxContact", connectUrl.get(i).getMax_contact())
										.build();
				
				preliminarJob = JobBuilder.newJob(Get_url.class).withIdentity("preliminar"+i,"preliminarGroup"+i)
										  .usingJobData("url", connectUrl.get(i).getUrl())
										  .usingJobData("index", i)
										  .usingJobData("maxContact", connectUrl.get(i).getMax_contact())
										  .build();
				
				groupPreliminarJob.add(preliminarJob); 
						
				indexGroupJob.add(i);
				//groupPreliminarJob.get()
				
				System.out.println("GPJ index - "+groupPreliminarJob.indexOf(preliminarJob));
				
				//System.out.println("["+(i+1)+"] "+groupPreliminarJob.get(i).getDescription());
				
				/*job2 = JobBuilder.newJob(Get_url.class).withIdentity("job"+i,"group"+i)
						.usingJobData("url", connectUrl.get(i).getUrl())
						.usingJobData("index", i)
						.usingJobData("maxContact", connectUrl.get(i).getMax_contact())
						.build();
					
				  add job2 into a preliminar job array list idem the trigger
				*/
				System.out.println("[PRELIMINAR JOB CREATED]");
			}
			else{
				job = JobBuilder.newJob(Get_url.class).withIdentity("job"+i, "group"+i)
					.usingJobData("url", connectUrl.get(i).getUrl())
					.usingJobData("index", i)
					.usingJobData("maxContact", connectUrl.get(i).getMax_contact())
					.build();
			}
		
		return job; 
	}
	
	
	private static Trigger config_trigger( int i )
	{
		Trigger trg, preliminarTrg;
		
		System.out.println("OK FUNCTION config_trigger");
		
		if(connectUrl.get(i).isSleep_mode() == 1)
		{
			trg =TriggerBuilder.newTrigger().withIdentity("cronT" + i, "group"+ i)
					.withSchedule(CronScheduleBuilder.cronSchedule("0 59 10 * * ?"/*(connectUrl.get(i)).getCronExpression()*/))
					.build();
			
			preliminarTrg = TriggerBuilder.newTrigger().withIdentity("simple"+i,"group" +i)
							.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(connectUrl.get(i).getFixed_frequency()).repeatForever())
							.build();
			
			groupPreliminarTrigger.add(preliminarTrg);
			}
		else{
			trg = TriggerBuilder.newTrigger().withIdentity("simple"+i,"group" + i)
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(connectUrl.get(i).getFixed_frequency()).repeatForever())
				.build();
			}		
		
		return trg;
	}
	
	
	//set the value of job and trigger, we can init the values for maxContact list
	public static void settingJobTrigger(){
		int i = 0;

			while(i < connectUrl.size()){
				
				System.out.println("- JOB -"+i);
											/*
											 * Set up the job
											 * 
											 */
				JobDetail job = config_job(i);
				
											/*
											 * Set up trigger
											 * 
											 */
				Trigger trigger = config_trigger(i);
				
				/*if ( ( connectUrl.get(i).isSleep_mode() == 1 ) &&
					 ( !urlDependency.checkValueFrequencySeconds(connectUrl.get(i).getFixed_frequency()))){
					job = JobBuilder.newJob(PreliminarJob.class).withIdentity("job"+i,"group"+i)
											.usingJobData("url", connectUrl.get(i).getUrl())
											.usingJobData("index", i)
											.usingJobData("maxContact", connectUrl.get(i).getMax_contact())
											.build();
					
					/*job2 = JobBuilder.newJob(Get_url.class).withIdentity("job"+i,"group"+i)
							.usingJobData("url", connectUrl.get(i).getUrl())
							.usingJobData("index", i)
							.usingJobData("maxContact", connectUrl.get(i).getMax_contact())
							.build();
						
					  add job2 into a preliminar job array list idem the trigger
					*/
					/*System.out.println("[PRELIMINAR JOB CREATED]");
				}
				else{
					job = JobBuilder.newJob(Get_url.class).withIdentity("job"+i, "group"+i)
						.usingJobData("url", connectUrl.get(i).getUrl())
						.usingJobData("index", i)
						.usingJobData("maxContact", connectUrl.get(i).getMax_contact())
						.build();
				}*/
					
											/*
											 * Set up trigger
											 */
				/*Trigger trigger;
				System.out.println("sleep mode :" + connectUrl.get(i).isSleep_mode() );
					
					
				if(connectUrl.get(i).isSleep_mode() == 1)
				{
					trigger =TriggerBuilder.newTrigger().withIdentity("cronT" + i, "group"+ i)
							.withSchedule(CronScheduleBuilder.cronSchedule("0 56 16 * * ?"/*(connectUrl.get(i)).getCronExpression()*///))
					/*		.build();
					}
				else{
					trigger = TriggerBuilder.newTrigger().withIdentity("simple"+i,"group" + i)
						.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(connectUrl.get(i).getFixed_frequency()).repeatForever())
						.build();
					}*/
				maxC.add(0);
				groupJob.add(job);
				groupTrigger.add(trigger);
				i++;
			}
			System.out.println(groupPreliminarJob.size());
	}
	
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
		
		boolean error_config_file = false;
		
		error_config_file = readConfigFile("config.txt");
		
		if( !error_config_file )
		{
			settingJobTrigger();
			runJob();
		}
	}

}
