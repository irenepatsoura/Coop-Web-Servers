

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WebServer {
    public int Port;
    public String ID;
    public String IPAddress;
    public String statusMessage;
    public List<String> IPList = new ArrayList<String>();
    public List<Integer> PortList = new ArrayList<Integer>();

    public WebServer(int Port, String ID, String IPAddress, String statusMessage) {
        this.Port = Port;
        this.ID = ID;
        this.IPAddress = IPAddress;
        this.statusMessage = statusMessage;
    }

    public static void main(String[] args) throws IOException {
        WebServer webServer;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Please give me your port: ");
        int port = Integer.parseInt(inFromUser.readLine());

        System.out.println("Please give me your ID: ");
        String id = inFromUser.readLine();

        System.out.println("Please give me your IP Address: ");
        String ip = inFromUser.readLine();


        webServer = new WebServer(port,id,ip,"");

        webServer.PortList.add(port);
        webServer.IPList.add(ip);

        System.out.println("Please give me the Port of the server you want to connect to: ");
        int connectionPort = Integer.parseInt(inFromUser.readLine());

        System.out.println("Please give me the IP Address of the server you want to connect with: ");
        String connectionIP = inFromUser.readLine();

        ServerCommunication serverCommunication = new ServerCommunication(webServer, "");
        serverCommunication.start();

        ServerJoin serverJoin;
        if(connectionPort != 0 && !connectionIP.equals("0")) {
            serverJoin = new ServerJoin(webServer, connectionIP, connectionPort);
            serverJoin.start();
        }

        HealthCheck healthCheck = new HealthCheck(webServer);
        healthCheck.start();

    }


}
