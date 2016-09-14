

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
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
		
		index = Test_quartz.indexGroupJob.indexOf(index);
		
		try {
			schedulerB.scheduleJob( Test_quartz.groupPreliminarJob.get(index),
									Test_quartz.groupPreliminarTrigger.get(index));
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
