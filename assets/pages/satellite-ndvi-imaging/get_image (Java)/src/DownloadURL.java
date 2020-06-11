import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.net.Authenticator;
import java.net.URL;

//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.nio.channels.Channels;
//import java.nio.channels.FileChannel;
//import java.nio.channels.ReadableByteChannel;
//import java.nio.file.Files;
//import java.nio.file.StandardCopyOption;

//import java.io.BufferedInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.URL;

public class DownloadURL {
	
	// Temporary buffers allow program to download gigabytes of data without storing all in RAM.
	// 2 GB file takes as little as 100 MB RAM.
	public static void saveImageURL(String url, String path, String username, String password, String threadName) {
		System.out.println("\t\t"+threadName+": Authenticating credentials...");
		ServerAuthenticator.setPasswordAuthentication(username, password);
        Authenticator.setDefault(new ServerAuthenticator());
		try {
			System.out.println("\t\t"+threadName+": Opening buffered input stream...");
			BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
			byte data[] = new byte[1024];
			int count;
			FileOutputStream out = new FileOutputStream(path);
			System.out.println("\t\t"+threadName+": Writing data...");
			while((count = in.read(data,0,1024)) != -1) {
				out.write(data, 0, count);
			}
			// If we get this far, then add to list of downloaded URLs...
			Tester.downloadedPaths.add(path);
			System.out.println("\t\t"+threadName+": Closing buffered input stream...");
			out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		// Other ideas for improved efficiency...
		
//		FileOutputStream fos = null;
//		try {
//			URL website = new URL(url);
//			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
//			fos = new FileOutputStream(path);
//			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
//		}
//		catch(IOException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			if(fos != null) {
//				fos.close();
//			}
//		}
//		catch(IOException e) {
//			e.printStackTrace();
//		}

		
		
//		try(
//			BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
//			FileOutputStream fileOutputStream = new FileOutputStream(path) 
//		) {
//			byte dataBuffer[] = new byte[1024];
//			int bytesRead;
//			while((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
//				fileOutputStream.write(dataBuffer, 0, bytesRead);
//			}
//		}
//		catch(IOException e) {
//			e.printStackTrace();
//		}
		
//		try {
//			BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
//			ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
//			FileOutputStream fileOutputStream = new FileOutputStream(path);
//			FileChannel fileChannel = fileOutputStream.getChannel();
//			fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
//			
//			byte dataBuffer[] = new byte[1024];
//			int bytesRead;
//			while((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
//				fileOutputStream.write(dataBuffer, 0, bytesRead);
//			}
//		}
//		catch(IOException e) {
//			e.printStackTrace();
//		}
		
		
	}
}

