
import java.io.FileNotFoundException;
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
	
	
	public static void check_numb_rows_files( String urlFileName, 
										 String timingFileName,
										 String userAgentFileName) throws IOException, InterruptedException
	{
		int num_row_url_file = 0,
			num_row_timing_file = 0,
			num_row_userAgent_file = 0;
		
		num_row_url_file = count_row_file( urlFileName );
		
		num_row_timing_file = count_row_file( timingFileName );

		num_row_userAgent_file = count_row_file( userAgentFileName );		
		
		if ( !(( num_row_url_file == num_row_timing_file ) && 
			   ( num_row_timing_file == num_row_userAgent_file )))
		{
			System.out.println("[ERROR] the file haven't got the same size!");
			System.exit(1);
		}
	}
	
	
	private static ArrayList<String> read_filename(String configPath) throws IOException, InterruptedException
	{
		ArrayList<String> list_config_file = new ArrayList<String>();

		FileReader readerConfig = new FileReader(configPath);
		Scanner scanner = new Scanner(readerConfig);
	
		while( scanner.hasNextLine() )
		{
			list_config_file.add(scanner.nextLine());
		}
		
		scanner.close();
		
		return list_config_file;
	}
	
	private static ArrayList<String> check_config_file(String configPath) throws IOException, InterruptedException
	{
		ArrayList<String> list_config_file = new ArrayList<String>();
		
		list_config_file = read_filename(configPath);
		
		if ( list_config_file.size() != 3 )
		{
			System.out.println("[ERROR] The config file hasn't got the path of all files to run the application! ");
			System.exit(1);
		}
		
		return list_config_file;
	}
	
	private static void create_request_job( String urlFileName, 
			 								String timingFileName,
			 								String userAgentFileName ) throws InterruptedException, FileNotFoundException
	{
		Scanner scannerUrl         = new Scanner( new FileReader(urlFileName) );
		Scanner scannerTime 	   = new Scanner( new FileReader(timingFileName));
		Scanner scannerUserAgent   = new Scanner( new FileReader(userAgentFileName)); 
		
								/*
								 *  jump the header of the three files because 
								 *  	it contains the description of the contents
								 */
		scannerTime.nextLine();
		scannerUrl.nextLine();		
		scannerUserAgent.nextLine();
		
			//scanner of url and timing
		while(scannerTime.hasNextLine()){
				urlDependency urlD = new urlDependency();
				String timing = scannerTime.nextLine();
				String own_url = scannerUrl.nextLine();
				String userAgent = scannerUserAgent.nextLine();
				
				urlD.setUrl(own_url);
				
				System.out.println(urlD.getUrl());
				
				urlD.setValue(timing);
				System.out.println("this is the cron Expression:: "+ urlD.getCronExpression());
				
				urlD.setUser_agent(userAgent);
				
				System.out.println("[useragent] "+urlD.getUser_agent());
				
				connectUrl.add(urlD);
			}
			
		scannerUrl.close();
		scannerTime.close();
		scannerUserAgent.close();
	}
	
	
	//read config file of the url and timing
	public static void readConfigFile(String configPath) throws IOException, InterruptedException{
		
		ArrayList<String> list_config_file = new ArrayList<String>();
		
		String path_url_filename = "",
			   path_timing_filename = "",
			   path_userAgent_filename = "";
		
		list_config_file = check_config_file(configPath);
		
		path_url_filename 		= list_config_file.get(0);
		path_timing_filename 	= list_config_file.get(1);
		path_userAgent_filename = list_config_file.get(2);		
		
		check_numb_rows_files(path_url_filename, 
						 path_timing_filename,
						 path_userAgent_filename);
		
		create_request_job( path_url_filename, 
							path_timing_filename,
							path_userAgent_filename );
	}
	
	
	private static JobDetail config_job( int i )
	{
		JobDetail job, preliminarJob;
	
		System.out.println("OK FUNCTION config_job");
		
		if ( ( connectUrl.get(i).isSleep_mode() == 1 ) &&
				 ( !urlDependency.checkValueFrequencySeconds(connectUrl.get(i).getFixed_frequency()))){				
				
				System.out.println(" ------> "+connectUrl.get(i).getUser_agent());
			
				job = JobBuilder.newJob(PreliminarJob.class).withIdentity("job"+i,"group"+i)
										.usingJobData("url", connectUrl.get(i).getUrl())
										.usingJobData("index", i)
										.usingJobData("maxContact", connectUrl.get(i).getMax_contact())
										.usingJobData("userAgent", connectUrl.get(i).getUser_agent())
										.build();
				
				preliminarJob = JobBuilder.newJob(Get_url.class).withIdentity("preliminar"+i,"preliminarGroup"+i)
										  .usingJobData("url", connectUrl.get(i).getUrl())
										  .usingJobData("index", i)
										  .usingJobData("maxContact", connectUrl.get(i).getMax_contact())
										  .usingJobData("userAgent", connectUrl.get(i).getUser_agent())
										  .build();
				
				groupPreliminarJob.add(preliminarJob); 
						
				indexGroupJob.add(i);
				
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
		
		if(connectUrl.get(i).isSleep_mode() == 1)
		{
			System.out.println(connectUrl.get(i).getCronExpression());
			
			trg =TriggerBuilder.newTrigger().withIdentity("cronT" + i, "group"+ i)
					.withSchedule(CronScheduleBuilder.cronSchedule(/*"0 31 22 * * ?"*/(connectUrl.get(i)).getCronExpression()))
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

			System.out.println("[ TOT URL ] "+connectUrl.size());
		
			while(i < connectUrl.size()){
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
				
				maxC.add(0);
				groupJob.add(job);
				groupTrigger.add(trigger);
				i++;
			}
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
		
		readConfigFile("config.txt");
		
		settingJobTrigger();
			
		runJob();
	}

}
