
import org.joda.time.DateTime;

public class urlDependency {
	
	private String url;
	private int fixed_frequency;
	private int min_interval_random;
	private int max_interval_random;
	private long max_contact;
	private int sleep_mode;
	private String interval_hour;
	private String interval_day;
	private int hour_start;
	private int hour_stop;
	private int frequency;
	private String user_agent;
	private String CronExpression;
	public int isFrequency() {
		return frequency;
	}
	
	public int getHour_start() {
		return hour_start;
	}
	public void setHour_start(int hour_start) {
		this.hour_start = hour_start;
	}
	public int getHour_stop() {
		return hour_stop;
	}
	public void setHour_stop(int hour_stop) {
		this.hour_stop = hour_stop;
	}
	
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public int getFixed_frequency() {
		return fixed_frequency;
	}
	public void setFixed_frequency(int fixed_frequency) {
		this.fixed_frequency = fixed_frequency;
	}
	public int getMin_interval_random() {
		return min_interval_random;
	}
	public void setMin_interval_random(int min_interval_random) {
		this.min_interval_random = min_interval_random;
	}
	public int getMax_interval_random() {
		return max_interval_random;
	}
	public void setMax_interval_random(int max_interval_random) {
		this.max_interval_random = max_interval_random;
	}
	public long getMax_contact() {
		return max_contact;
	}
	public void setMax_contact(long max_contact) {
		this.max_contact = max_contact;
	}
	public int isSleep_mode() {
		return sleep_mode;
	}
	public void setSleep_mode(int sleep_mode) {
		this.sleep_mode = sleep_mode;
	}
	public String getInterval_hour() {
		return interval_hour;
	}
	public void setInterval_hour(String interval_hour) {
		this.interval_hour = interval_hour;
	}
	public String getInterval_day() {
		return interval_day;
	}
	public void setInterval_day(String interval_day) {
		this.interval_day = interval_day;
	}
	public String getCronExpression() {
		return CronExpression;
	}
	private void setCronExpression(String cronExpression) {
		CronExpression = cronExpression;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public urlDependency() {
	};
	public urlDependency(int fixed_frequency, int min_interval_random,
			int max_interval_random, long max_contact, int sleep_mode,
			String interval_hour, String interval_day, int frequency) {
		this.fixed_frequency = fixed_frequency;
		this.min_interval_random = min_interval_random;
		this.max_interval_random = max_interval_random;
		this.max_contact = max_contact;
		this.sleep_mode = sleep_mode;
		this.interval_hour = interval_hour;
		this.interval_day = interval_day;
		this.frequency = frequency;
	};
	public void setValue(String timing) throws InterruptedException{
		
		boolean validate = checkdate( timing );
		
		if(validate){
			calculateCronExpression();
		}
		else 
		{
			System.out.println("data is not valid!");
			System.exit(1);
		}
		
	}
	
	private boolean checkdate( String timing )
	{
		boolean well_form_data = true;
		
		String [] splits = timing.split(" ");
		
		try
		{
			frequency = Integer.parseInt(splits[0]);
		}
		catch( NumberFormatException e )
		{
			System.out.println("[ERROR] The frequency mode in not an integer value!");
			return false;
		}
		
		if ( ( frequency != 0 ) && ( frequency != 1 ) )
		{
			System.out.println("[ERROR] The frequency mode is different from the 0 and 1 values!");
			return false;
		}
		
		try
		{
			fixed_frequency = Integer.parseInt(splits[1]);
		}
		catch( NumberFormatException e )
		{
			System.out.println("[ERROR] The frequency value in not an integer!");
			return false;			
		}
		
		if ( !( ( fixed_frequency > 0 ) && ( fixed_frequency < 60)) )
		{
			System.out.println("[ERROR] The frequency value is over the range [0:60] !");
			return false;			
		}
		
		if ( frequency == 1 )
		{
		
			try
			{
				max_interval_random = Integer.parseInt(splits[2]);
			}
			catch( NumberFormatException e )
			{
				System.out.println("[ERROR] The frequency value in not an integer!");
				return false;
			}
		
			if ( !( ( max_interval_random > 0 ) && ( max_interval_random <= 60))  )
			{
				System.out.println("[ERROR] The max value of interval is over the range [0:60] !");
				return false;
			}
		
			try
			{
				min_interval_random = Integer.parseInt(splits[3]);
			}
			catch( NumberFormatException e )
			{
				System.out.println("[ERROR] The min value of interval is over the range [0:60] !");			
				return false;
			}
			
			if ( !( ( min_interval_random >= 0 ) && ( min_interval_random < 60))  )
			{
				System.out.println("[ERROR] The min value of interval is over the range [0:60] !");
				return false;
			}
			
			if ( min_interval_random > max_interval_random )
			{
				System.out.println("[ERROR] The min value of interval is greater than the max!");
				return false;			
			}
		}
		
		try
		{
			max_contact = Integer.parseInt(splits[4]);
		}
		catch( NumberFormatException e )
		{
			System.out.println("[ERROR] The max_contact value is not an integer value !");
			return false;	
		}
		
		if ( !( ( max_contact > 0 ) && ( max_contact <= 50 )) )
		{
			System.out.println("[ERROR] The max_contact value is too large ( >50 ) or negative ( < 0 ) or zero ( == 0)!");
			return false;			
		}
				
		try
		{
			sleep_mode =Integer.parseInt(splits[5]);
		}
		catch( NumberFormatException e )
		{
			System.out.println("[ERROR] The sleep_mode value is not an integer!");
			return false;			
		}
		
		if ( !( ( sleep_mode == 0 ) || ( sleep_mode == 1 )) )
		{
			System.out.println("[ERROR] The sleep_mode value is different from the 0 and 1 values!");
			return false;			
		}
		
		interval_hour = splits[6];
		
		interval_day = splits[7];
		
		return well_form_data;
	}
	
	private void calculateCronExpression() throws InterruptedException{
		String fs = null, fm= null, fo = null, dayofmonth = null, month = null, dayofweek = null;
		//System.out.println(sleep_mode);
		
					/* random integer in a interval */
		if ( frequency == 1)
		{
			fixed_frequency = Test_quartz.extract_int_range_minute(min_interval_random, max_interval_random);
		}
		
		if(fixed_frequency > 0 && fixed_frequency < 60){
			fs = "0/"+fixed_frequency;
			fm = "*";
		}else if(fixed_frequency >59 && fixed_frequency<3600){
			fm = ""+fixed_frequency/60;
			fs = ""+fixed_frequency%60;
			}else if(fixed_frequency == 0){
				fs = "*";
			}
	
		if(sleep_mode == 1)
		{
						/*
						 * the fixed frequency IS DIVISIBLE for 60
						 */
			if ( checkValueFrequencySeconds(fixed_frequency)){
				fo = calcintervalTask();
				month= "*";
				dayofweek = calcTaskDay();
				if(!dayofweek.equals("*") || !dayofweek.equals("0")){
					dayofmonth = "?";
				}else dayofmonth = "*";
			}
						/*
						 * the fixed frequency IS NOT DIVISIBLE for 60 
						 */
			else{
				calcintervalTask();
				
				DateTime d = new DateTime();				
				
				int hour_now = Integer.parseInt(d.toString("HH"));
				
				fo = hour_start+"-"+hour_stop;
				
				if ( ( hour_now < hour_stop ) || ( hour_now > hour_start ) )
				{						
					fs = ""+d.toString("ss");
					fm = ""+d.toString("mm");
				}
				else
				{
					fs = "0";
					fm = "0";
				}
				
				dayofmonth = "*";
				month = "*";
				dayofweek = "?";
			}
		}else if (sleep_mode == 0)
		{			
			fo = "*";
			dayofmonth = "*";
			month = "*";
			dayofweek = "?";
		}
		
		CronExpression = fs+" "+fm+" "+ fo +" "+dayofmonth+" "+month+" "+dayofweek;
	};
	
	
												/*
												 *  Check if the value of frequency ( expressed in seconds ) 
												 *  
												 *  	is divisible for 60 ( 1, 2, 3, ...  it returns a true value )
												 *  
												 * 		or not ( 17, ... it returns a false value ).
												 *
												 */
	public static boolean checkValueFrequencySeconds( int value )
	{
		if ( 60 % value == 0 )
			return true;
		else
			return false;
	}
	
	private String calcintervalTask(){
		String split[] = interval_hour.split("-");
		String result = null;
		
		if(split.length > 1){
			
			int x1 = Integer.parseInt(split[0]);
			int x2 = Integer.parseInt(split[1]);		
			
			int tmp;
			
			if(x1 == 0 && x2!=23){
				x1=x2+1;
				x2 =23;
			}else if(x1!=0 && x2!=23){
				tmp = x1;
				x1 = x2+1;
				x2 = tmp - 1;
			}else if(x1!=0 && x2==23){
				tmp = x1;
				x1=0;
				x2 = tmp - 1;
			}
			
			hour_start = x1;
			hour_stop = x2;
			
			result = x1+"-"+x2;

		}else if(split[0].equals("AM")){
			result = "12-23";
			hour_start = 12;
			hour_stop = 0;

		}else if(split[0].equals("PM")){
			result = "0-11";
			hour_start = 0;
			hour_stop = 12;

		}
		
		return result;
	}
	private String calcTaskDay(){
		String day[] = {"MON","TUE","WED","THU","FRI","SAT","SUN"};
		String split[] = interval_day.split(",");
		String log = "";
		int i = 0;
		int j;
		if(split[0].equals("*")){
			System.out.println("condition denied, the process will continue with stardard setting");
			return split[0];
		}else if(split[0].equals("0")){
			return "*";
		}else{
			for(i= 0; i<split.length; i++){
				for(j=0 ; j<day.length; j++){
					if(split[i].equals(day[j])){
						day[j]=" ";
						break;
					}
				}
			}
			for(i= 0; i<day.length; i++){
				if(!day[i].equals(" ")){
					log = log + day[i]+",";
				}
			}
			if(log.charAt(log.length()-1) == ','){
				log = log.substring(0, log.length()-1);
			}
		}

		return log;
		
	}

	public String getUser_agent() {
		return user_agent;
	}

	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}	

}
