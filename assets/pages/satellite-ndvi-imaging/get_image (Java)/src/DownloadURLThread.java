import java.lang.Runnable;
import java.lang.Thread;


class DownloadURLThread implements Runnable {
	private Thread t;
	private String threadName;
	private String username;
	private String password;
	private String filePath;

	DownloadURLThread(String name, String fp, String u, String p) {
		threadName = name;
		filePath = fp;
		username = u;
		password = p;
		System.out.println("\tCreating thread: " + threadName);
	}

	public void run() {
		while(Tester.activeURLs.size() > 0) {
			int i = Tester.activeURLs.size() - 1;
			String url = Tester.activeURLs.get(i);
			Tester.activeURLs.remove(i);
			System.out.println("\t"+threadName+": Downloading \""+url+"\"...");
			DownloadURL.saveImageURL(url, filePath, username, password, threadName);
			Tester.numURLsLeft--;
			System.out.println("\t"+threadName+": Process ended!");
		}
	}

	public void start() {
		System.out.println("\tStarting thread: " +  threadName);
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}
}
