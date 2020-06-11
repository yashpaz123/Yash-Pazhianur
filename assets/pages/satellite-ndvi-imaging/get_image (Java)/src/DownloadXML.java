import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DownloadXML {
    public static String[] getXML(String url, String username, String password) {
    	ArrayList<String> xml = new ArrayList<String>();
    	
        URL urlDat;
        InputStream is = null;
        BufferedReader br;
        String line;

        ServerAuthenticator.setPasswordAuthentication(username, password);
        Authenticator.setDefault(new ServerAuthenticator());

        try {
            urlDat = new URL(url);
            is = urlDat.openStream();
            br = new BufferedReader(new InputStreamReader(is));
            while((line = br.readLine()) != null) {
               xml.add(line);
            }
        }
        catch(MalformedURLException mue) {
             mue.printStackTrace();
        }
        catch(IOException ioe) {
             ioe.printStackTrace();
        }
        finally {
            try {
                if(is != null) {
                	is.close();
                }
            }
            catch (IOException ioe) {
                
            }
        }
        
        return xml.toArray(new String[0]);
    }
}
