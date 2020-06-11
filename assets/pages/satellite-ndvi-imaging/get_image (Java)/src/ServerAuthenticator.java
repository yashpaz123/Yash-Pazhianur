import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ServerAuthenticator extends Authenticator {  
    private static String username = "";
    private static String password = "";

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication (ServerAuthenticator.username, 
        		ServerAuthenticator.password.toCharArray());
    }

    public static void setPasswordAuthentication(String username, String password) {
    	ServerAuthenticator.username = username;
    	ServerAuthenticator.password = password;
    }
}
