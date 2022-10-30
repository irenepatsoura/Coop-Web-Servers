import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;

public class ServerJoin extends Thread {
    public WebServer myServer;
    public String connectionServerIP;
    public int connectionServerPort;

    public ServerJoin(WebServer myServer, String connectionServerIP, int connectionServerPort) {
        this.connectionServerIP = connectionServerIP;
        this.connectionServerPort = connectionServerPort;
        this.myServer = myServer;
    }

    @Override
    public void run() {
        try {
            String line = "";
            Socket socket = new Socket(connectionServerIP, connectionServerPort);

            DataOutputStream outToClient =
                    new DataOutputStream(socket.getOutputStream());
            outToClient.writeBytes("Join\n");
            outToClient.writeBytes(String.valueOf(this.myServer.Port) + "\n");
            outToClient.writeBytes(this.myServer.IPAddress + "\n");


            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            line = inFromClient.readLine();
            int c = 0;
            while (!line.equals("Done")) {
                System.out.println("1"+line);
                this.myServer.PortList.add(this.myServer.PortList.indexOf(this.myServer.Port)+1+c,Integer.parseInt(line));
                line = inFromClient.readLine();
                System.out.println("2"+line);
                this.myServer.IPList.add(this.myServer.IPList.indexOf(this.myServer.IPAddress)+1+c,line);
                line = inFromClient.readLine();
                c++;

            }
            for(int i = 0;i<this.myServer.PortList.size();i++){
                this.myServer.statusMessage+=" "+this.myServer.PortList.get(i)+" "+this.myServer.IPList.get(i);
            }

           socket.close();
        } catch (IOException e){
            System.out.println(e);
        }
    }
}
