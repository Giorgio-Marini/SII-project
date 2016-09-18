

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Browser
{  
	private static String get_default_name( String browser_directory )
	{		
		if ( 	browser_directory.equals("firefox") || 
				browser_directory.equals("Mozilla Firefox"))
			return "Mozilla Firefox";

		if ( 	browser_directory.equals("Google"))
			return "Google Chrome";

		if ( 	browser_directory.equals("chromium-browser"))
			return "Google Chromium";

		if ( 	browser_directory.equals("Microsoft.MicrosoftEdge_8wekyb3d8bbwe"))
			return "Microsoft Edge";		

		if ( 	browser_directory.equals("Internet Explorer"))
			return "Microsoft Internet Explorer";		

		return browser_directory;
	}

	
	private static ArrayList<String> find_installed_browser( String[] list_files )
	{
		String[] list_browser = { "firefox", "Mozilla Firefox", "Google", "chromium-browser","Opera","opera","Microsoft.MicrosoftEdge_8wekyb3d8bbwe","Internet Explorer","netsurf-gtk","qupzilla","konqueror"};

		ArrayList<String> list_installed_browser = new ArrayList<String>();
		
		Arrays.sort(list_files);
		
		for ( String browser : list_browser )
		{
			if ( ( Arrays.binarySearch(list_files, browser) >= 0 ) &&
				 !( list_installed_browser.contains(get_default_name( browser ))) )
			{
				list_installed_browser.add( get_default_name( browser ));
			}
		}
		
		return list_installed_browser;
	}
	
	private static void writeFile( String pathBrowserFile, ArrayList<String> browser_founded ) throws IOException
	{
		PrintStream pw = new PrintStream( new FileOutputStream( pathBrowserFile ));
		
		for( String browser : browser_founded )
			pw.println(browser);

		pw.close();
	}
   
	private static void check_supportOperatingSystem( String os ) throws IOException
	{
		String[] list_supportedOS = { "Linux", "Windows 7", "Windows 10" };
      
		if ( Arrays.binarySearch(list_supportedOS, os) < 0 )
		{
			System.out.println("[ERROR] The retrieving browser function is not supported on this Operating System!");
			System.exit(1);
		}
	}   

	private static ArrayList<String> retrieve_browser_system( File directory )
	{
		if ( directory.list() != null )
		{
			String[] list_files = directory.list();
			
			return find_installed_browser( list_files );
		}
		else
			return new ArrayList<String>();
	}
	
	private static ArrayList<String> retrieve_browser_windows()
	{
		ArrayList<String> browser = new ArrayList<String>();
		
		browser.addAll( retrieve_browser_system( new File("C:/Program Files") ));
		browser.addAll( retrieve_browser_system( new File("C:/Program Files (x86)") ));
		browser.addAll( retrieve_browser_system( new File("C:/Windows/SystemApps") ));
		
		return browser;
	}	
	
	private static ArrayList<String> search_browser( String operating_system )
	{
		ArrayList<String> browser = new ArrayList<String>();
		
		if ( operating_system.contains("Linux") )
			browser = retrieve_browser_system( new File("/usr/bin/") );
		
		if ( operating_system.contains("Windows"))
			browser = retrieve_browser_windows();

		if ( browser.size() == 0 )
		{
			browser.add("There aren't installed browser on this system!");
		}
		
		Collections.sort(browser, String.CASE_INSENSITIVE_ORDER);
		
		return browser;
	}
	
	public static void find_browser_system(String pathBrowserFile) throws IOException
	{	
		String operating_system = System.getProperty("os.name");
		
		System.out.println(operating_system);
		
		check_supportOperatingSystem( operating_system );
      
		ArrayList<String> browser_founded = search_browser( operating_system );
		
		writeFile( pathBrowserFile, browser_founded );
	}		
}