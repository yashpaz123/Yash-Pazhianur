import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Tester {
	
	// if rows is > 100, split process into multiple threads, since max rows per request is 100 (only for getting xml)
	public static int start = 800;
	public static int rows = 1;
	public static int numThreads = 4;
	public static int timeThreadWait = 100;
	public static String directory = "path";
	public static String username = "yashpaz123";
	public static String password = "neXmut-hersy8-qifbuw";
	public static int processIDLength = 16;
	
	public static String processID;
	public static ArrayList<String> activeURLs = new ArrayList<String>();
	public static int numURLsLeft;
	public static ArrayList<String> downloadedPaths = new ArrayList<String>();
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
		System.out.println("--> STARTING SENTINEL DATA DOWNLOAD <--");
		processID = getRandomHexString(processIDLength);
		System.out.println("Process ID: "+processID+"\n");
		
		Runtime rt = Runtime.getRuntime();
		System.out.println("System Runtime Specs:");
		System.out.println("\tVersion: "+rt.version());
		System.out.println("\tRAM Location: "+rt);
		System.out.println("\tAvailable Processors: "+rt.availableProcessors());
		System.out.println("\tFree Memory: "+(rt.freeMemory()/1000000.0)+" MB");
		System.out.println("\tTotal Memory: "+(rt.totalMemory()/1000000.0)+" MB");
		System.out.println("\tMaximum Memory: "+(rt.maxMemory()/1000000.0)+" MB");
		System.out.println("\tHash Code: "+rt.hashCode());
		
		System.out.println("\nDownloading XML URL list...");
		String[] XMLURLList = DownloadXML.getXML("https://scihub.copernicus.eu/dhus/search?start="+start+"&rows="+rows+"&q=*", username, password);
		String XMLURLString = String.join("", XMLURLList);
		
		System.out.println("Parsing XML URL list...");
		ArrayList<String> imageURLs = ParseXML.parseGetURLs(XMLURLString);
		
		System.out.println("Here are the URLs ("+imageURLs.size()+"):");
		for(String s : imageURLs) {
			System.out.println("\t"+s);
		}
		activeURLs = imageURLs;
		
		directory += "/SENTINEL-DOWNLOADED-DATA-"+processID;
		System.out.println("Creating directory \""+directory+"\"");
		boolean folderCreationSuccess = (new File(directory)).mkdirs();
		if(!folderCreationSuccess) {
			System.out.println("Unable to create directory \""+directory+"\"\nTERMINATED");
			System.exit(0);
		}
		
		System.out.println("\nDownloading image data...");
		numURLsLeft = activeURLs.size();
		ArrayList<DownloadURLThread> threads = new ArrayList<DownloadURLThread>();
		System.out.println("Creating threads:");
		for(int i = 0; i < numThreads; i++) {
			threads.add(new DownloadURLThread("[Thread-"+(i+1)+"]", directory+"/SENTINEL-TEMP-ZIP-"+processID+"-"+i+".zip", username, password));
		}
		System.out.println("Starting threads:");
		for(DownloadURLThread thread : threads) {
			thread.start();
		}
		System.out.println("\t[System] Waiting for thread(s) to complete ["+timeThreadWait+"ms]...");
		while(numURLsLeft > 0) {
			try {
				Thread.sleep(timeThreadWait);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("SUCCESS: Image data downloaded!");
		
		
		
		
		
		
		
//		System.out.println("\nUnzipping image files...");
//		for(int i = 0; i < imageURLs.length; i++) {
//			System.out.println("\tUnzipping \""+directory+"/Sentinel-"+i+".zip"+"\"...");
//			UnzipFiles.unzip(directory+"/Sentinel-"+i+".zip", directory);
//		}
//		System.out.println("SUCCESS: Image data unzipped!");
		
	}

	private static String getRandomHexString(int numchars){
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		while(sb.length() < numchars){
			sb.append(Integer.toHexString(r.nextInt()));
		}

		return sb.toString().substring(0, numchars);
	}
}

//Make github website (markdown), share your projects on them.

