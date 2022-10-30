import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class HealthCheck extends Thread {
    private WebServer server;

    public HealthCheck(WebServer server) {
        this.server = server;
    }

    public void HandleConnectionException() {
        String NextIP = "";
        int NextPort = 0;

        if (server.IPList.indexOf(server.IPAddress) == server.IPList.size()-1)
            NextIP = server.IPList.remove(0);
        if (server.PortList.indexOf(server.Port) == server.PortList.size()-1)
            NextPort = server.PortList.remove(0);
        else {
            NextIP = server.IPList.remove(server.IPList.indexOf(server.IPAddress) + 1);
            NextPort = server.PortList.remove(server.PortList.indexOf(server.Port) + 1);
        }
        try {
            if(this.server.IPList.size() > 1 && this.server.PortList.size() > 1) {
                if (server.IPList.indexOf(server.IPAddress) == server.IPList.size()-1)
                    NextIP = server.IPList.get(0);
                if (server.PortList.indexOf(server.Port) == server.PortList.size()-1)
                    NextPort = server.PortList.get(0);
                else {
                    NextIP = server.IPList.get(server.IPList.indexOf(server.IPAddress) + 1);
                    NextPort = server.PortList.get(server.PortList.indexOf(server.Port) + 1);
                }

                Socket socket = new Socket(NextIP, NextPort);

                if (!(this.server.statusMessage.contains(String.valueOf(this.server.Port)) && this.server.statusMessage.contains(this.server.IPAddress))) {

                    this.server.statusMessage += " "+this.server.Port + " " + this.server.IPAddress;
                    System.out.println("in if msg"+this.server.statusMessage);
                }else{
                    this.server.statusMessage = this.server.Port+" "+this.server.IPAddress;
                }

                DataOutputStream outToClient =
                        new DataOutputStream(socket.getOutputStream());
                outToClient.writeBytes("Status\n");
                outToClient.writeBytes(this.server.statusMessage +"\n");
                socket.close();
                System.out.println("Message sent: " + this.server.statusMessage);
            }

        }catch (ConnectException e) {
            HandleConnectionException();

        }catch (IOException e) {

        }
    }

    @Override
    public void run() {
        String NextIP = "";
        int NextPort = 0;
        while(true) {
//            System.out.println(this.server.IPList.size()+", "+this.server.PortList.size());
            try {
                if (this.server.IPList.size() > 1 && this.server.PortList.size() > 1) {
                    if (server.IPList.indexOf(server.IPAddress) == server.IPList.size()-1)
                        NextIP = server.IPList.get(0);
                    if (server.PortList.indexOf(server.Port) == server.PortList.size()-1)
                        NextPort = server.PortList.get(0);
                    else {
                        NextIP = server.IPList.get(server.IPList.indexOf(server.IPAddress) + 1);
                        NextPort = server.PortList.get(server.PortList.indexOf(server.Port) + 1);
                    }
                    System.out.println("ipandport:"+NextIP+", "+NextPort);
                    System.out.println("m:"+this.server.statusMessage);
                    Socket socket = new Socket(NextIP, NextPort);

                    if (!(this.server.statusMessage.contains(String.valueOf(this.server.Port)) && this.server.statusMessage.contains(this.server.IPAddress))) {

                        this.server.statusMessage += " "+this.server.Port + " " + this.server.IPAddress;
                        System.out.println("in if msg"+this.server.statusMessage);
                    }else{
                        this.server.statusMessage = this.server.Port+" "+this.server.IPAddress;
                    }
//                    }else{
//                        this.server.statusMessage = this.server.Port + " " + this.server.IPAddress;
//                    }

                    DataOutputStream outToClient =
                            new DataOutputStream(socket.getOutputStream());
                    outToClient.writeBytes("Status\n");
                    outToClient.writeBytes(this.server.statusMessage + "\n");
                    socket.close();
                    System.out.println("Message sent: " + this.server.statusMessage);
                }
                if(this.server.IPList.size()==0)break;

            } catch (ConnectException e) {
                HandleConnectionException();

            } catch (IOException e) {

            }
            try{
                Thread.sleep(5000);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
