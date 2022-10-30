import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ServerCommunication extends Thread {
    private WebServer server;
    private String statusMessage;
    Socket clientSocket;
    private HashMap<String, String> files = new HashMap<>();
    private int statusreceived = 0;
    public ServerCommunication( WebServer server, String statusMessage) {
        this.server = server;
        this.statusMessage = statusMessage;
    }


    void StatusRequest(String message) {
        String NextIP = "";
        int NextPort = 0;
      try{
          if(this.server.IPList.size() > 1 && this.server.PortList.size() > 1) {
              if (server.IPList.indexOf(server.IPAddress) == server.IPList.size()-1)
                  NextIP = server.IPList.get(0);
              if (server.PortList.indexOf(server.Port) == server.PortList.size()-1)
                  NextPort = server.PortList.get(0);
              else {
                  NextIP = server.IPList.get(server.IPList.indexOf(server.IPAddress) + 1);
                  NextPort = server.PortList.get(server.PortList.indexOf(server.Port) + 1);
              }
              System.out.println("instatushandler:"+NextIP+", "+NextPort);
              Socket socket = new Socket(NextIP, NextPort);

              DataOutputStream outToClient =
                      new DataOutputStream(socket.getOutputStream());
              outToClient.writeBytes("Status\n");
              outToClient.writeBytes(message + "\n");
              socket.close();
              System.out.println("Message sent: " + message);
          }

      }catch(ConnectException e){
          if(server.IPList.indexOf(server.IPAddress) == server.IPList.size()-1)
              server.IPList.remove(0);
          if(server.PortList.indexOf(server.Port) == server.PortList.size()-1)
              server.PortList.remove(0);
          else {
              server.IPList.remove(NextIP);
              server.PortList.remove((Object)NextPort);
          }
          StatusRequest(message);

      }catch(IOException e) {

      }
    }

    String PutRequest(String filename, String data, int length, int numOfServer) {
        String NextIP = "";
        int NextPort = 0;
        try{
            if(numOfServer> this.server.IPList.size())
                return "complete";

            if(this.server.IPList.size() == 1)
                return "complete";
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

                DataOutputStream outToClient =
                        new DataOutputStream(socket.getOutputStream());
                outToClient.writeBytes("PUT\n");
                outToClient.writeBytes(filename + "\n");
                outToClient.writeBytes(String.valueOf(length) + "\n");
                outToClient.writeBytes(String.valueOf(numOfServer) + "\n");


                outToClient.writeBytes(data + "\n");
                socket.close();
            }

        }catch(ConnectException e){
            if(server.IPList.indexOf(server.IPAddress) == server.IPList.size()-1)
                server.IPList.remove(0);
            if(server.PortList.indexOf(server.Port) == server.PortList.size()-1)
                server.PortList.remove(0);
            else {
                server.IPList.remove(NextIP);
                server.PortList.remove((Object)NextPort);
            }
            PutRequest(filename,data,length,numOfServer);

        }catch(IOException e) {

        }
        return "";
    }

    void GetRequest(int numOfServers, String filename, String data) {
        String NextIP = "";
        int NextPort = 0;
        try{

            if (server.IPList.indexOf(server.IPAddress) == server.IPList.size()-1)
                NextIP = server.IPList.get(0);
            if (server.PortList.indexOf(server.Port) == server.PortList.size()-1)
                NextPort = server.PortList.get(0);
            else {
                NextIP = server.IPList.get(server.IPList.indexOf(server.IPAddress) + 1);
                NextPort = server.PortList.get(server.PortList.indexOf(server.Port) + 1);
            }

            Socket socket = new Socket(NextIP, NextPort);

            DataOutputStream outToClient =
                    new DataOutputStream(socket.getOutputStream());
            outToClient.writeBytes("Server Put\n");
            outToClient.writeBytes(String.valueOf(numOfServers) + "\n");
            outToClient.writeBytes(filename+"\n");
            outToClient.writeBytes(String.valueOf(data.length())+"\n");
            outToClient.writeBytes(data + "\n");
            socket.close();
            System.out.println("Message sent: " + statusMessage);


        }catch(ConnectException e){
            if(server.IPList.indexOf(server.IPAddress) == server.IPList.size()-1)
                server.IPList.remove(0);
            if(server.PortList.indexOf(server.Port) == server.PortList.size()-1)
                server.PortList.remove(0);
            else {
                server.IPList.remove(NextIP);
                server.PortList.remove((Object)NextPort);
            }
            GetRequest(numOfServers,filename,data);


        }catch(IOException e) {

        }

    }

    @Override
    public void run() {
        try {
            String message = "";
            ServerSocket serverSocket = new ServerSocket(server.Port);

            while(true) {
                Socket connectionSocket = serverSocket.accept();//establishes connection and waits

                BufferedReader inFromClient =
                        new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient =
                        new DataOutputStream(connectionSocket.getOutputStream());
                String requestMessageLine = inFromClient.readLine();
                System.out.println("Received "+requestMessageLine+"123");

                if(requestMessageLine.equals("Join")) {

                    System.out.println(this.server.PortList);
                    System.out.println(this.server.IPList);
                    for(int i=0; i<this.server.PortList.size(); i++) {
                        System.out.println("in");
                        outToClient.writeBytes(this.server.PortList.get(i) +"\n");
                        outToClient.writeBytes(this.server.IPList.get(i)+"\n");
                    }
                    outToClient.writeBytes("Done\n");
                    this.server.PortList.add(Integer.parseInt(inFromClient.readLine()));
                    this.server.IPList.add(inFromClient.readLine());
                    this.server.statusMessage="";
                    for(int i = 0;i<this.server.PortList.size();i++){
                        this.server.statusMessage+=" "+this.server.PortList.get(i)+" "+this.server.IPList.get(i);
                    }
                    StatusRequest(this.server.statusMessage);
                    connectionSocket.close();
                }

                if(requestMessageLine.equals("Status")) {
                    message = inFromClient.readLine();
                    System.out.println("instatusmsg:"+message);
                    System.out.println("mymsg:"+this.server.statusMessage);
                    /*if(message.equals(this.server.statusMessage)){
                        this.server.statusMessage = "";
                    }
                    else */
                    String testmessage1 = message;
                    String testmessage2 = this.server.statusMessage;
                    String[] t1 = testmessage1.split(" ");
                    String[] t2 = testmessage2.split(" ");
                    if(!message.contains(String.valueOf(this.server.Port)+" "+this.server.IPAddress) ||(message.contains(String.valueOf(this.server.Port)+" "+this.server.IPAddress) && t2.length>t1.length)){
                        if(!this.server.statusMessage.contains(message)) {
                            String[] t = message.split(" ");
                            String newmessage = "";
                            for(int i = 0;i<t.length;i++){
                                if(!t[i].equals("")&& !t[i].equals(" ")){
                                    if(!this.server.statusMessage.contains(t[i])){
                                        newmessage+=t[i]+" ";
                                    }
                                }
                            }
                            this.server.statusMessage = newmessage + " " + this.server.statusMessage;
                        }
                        StatusRequest(this.server.statusMessage);
                    }
//                    if(!message.contains(String.valueOf(this.server.Port)+" "+this.server.IPAddress)&&this.server.statusMessage.contains(String.valueOf(this.server.Port)+" "+this.server.IPAddress)){
//                        StatusRequest(this.server.statusMessage);
//                    }
//                    else if(!message.contains(String.valueOf(this.server.Port)+" "+this.server.IPAddress)) {
//                        if(message.equals("")) {
//                            statusMessage = String.valueOf(this.server.Port) + " " + this.server.IPAddress;
//                        }else {
//                            statusMessage = message + " " + String.valueOf(this.server.Port) + " " + this.server.IPAddress;
//                        }
//                        this.server.statusMessage = statusMessage;
//                        StatusRequest(statusMessage);
//                    }
                    else {
//                        if(statusreceived == 0){
//                            StatusRequest(message);
//                            statusreceived++;
//                        }
//                        this.server.statusMessage = "";
                        statusMessage = "";
//                        StatusRequest(statusMessage);
                        String[] tokenizer = message.split(" ");
                        ArrayList<String> temp = new ArrayList<>();
//                        if(tokenizer[0].equals("")){
                        for(int i = 0;i<tokenizer.length;i++){
                            if(tokenizer[i].equals("")||tokenizer[i].equals(" ")){
                                continue;
                            }else{
                                temp.add(tokenizer[i]);
                            }
                        }
                        tokenizer = new String[temp.size()];
                        for(int i = 0;i<temp.size();i++){
                            tokenizer[i] = temp.get(i);
                        }
//                        }
                        System.out.println("tokenizer:"+ Arrays.toString(tokenizer));
                        this.server.PortList.clear();
                        this.server.IPList.clear();
                        for(int i=0; i< tokenizer.length; i++) {

                            if(i%2 == 0) {
                                this.server.PortList.add(Integer.valueOf(tokenizer[i]));
                            }
                            else {
                                this.server.IPList.add(tokenizer[i]);
                            }
                        }
//                        statusreceived--;
//                        statusMessage="";
//                        this.server.statusMessage = statusMessage;
                    }
//                    System.out.println("thissstatus:"+this.server.statusMessage);
                    connectionSocket.close();
                }
                else if(requestMessageLine.equals("PUT")) {
                    String filename = inFromClient.readLine();
                    System.out.println(filename);
                    System.out.println("--");
                    String s = inFromClient.readLine();
                    System.out.println(s);
                    int length = Integer.parseInt(s);

                    int numOfServer = Integer.parseInt(inFromClient.readLine());
                    String data = "";
                    for(int i=0; i<length; i++ ) {
                        data += inFromClient.readLine()+"\n";
                    }
                    if(files.containsKey(filename)) {
                        files.remove(filename);
                        files.put(filename, data);
                    }
                    else {
                        files.put(filename, data);
                    }
                    if(numOfServer == 0)
                        clientSocket = connectionSocket;
                    numOfServer++;

                    PutRequest(filename,data,length,numOfServer);

                    if(numOfServer > this.server.IPList.size() || this.server.IPList.size() == 1) {
                        outToClient =
                                new DataOutputStream(clientSocket.getOutputStream());
                        outToClient.writeBytes("Put Completed\n");
                        clientSocket.close();
                        connectionSocket.close();
                    }else if(numOfServer < this.server.IPList.size() && numOfServer!=1){
                        connectionSocket.close();
                    }



                }
                else if(requestMessageLine.equals("GET")) {
                    System.out.println("inget");
                    String numOfServers = inFromClient.readLine();
                    String filename = inFromClient.readLine();
                    System.out.println("f:"+filename);
                    if(files.containsKey(filename)) {
                        outToClient =
                                new DataOutputStream(connectionSocket.getOutputStream());
                        outToClient.writeBytes(files.get(filename)+"\n");
                        connectionSocket.close();
                    }
                    else {
                        if(this.server.IPList.size() == 1) {
                            outToClient =
                                    new DataOutputStream(connectionSocket.getOutputStream());
                            outToClient.writeBytes("Value not Found\n");
                            connectionSocket.close();
                        }
                        if(Integer.parseInt(numOfServers)==0) {
                            clientSocket = connectionSocket;
                            int num = Integer.parseInt(numOfServers);
                            num++;
                            GetRequest(num,filename,"");
                        }else{
                            connectionSocket.close();
                        }

                    }

                }
                else if (requestMessageLine.equals("Server Put")) {
                    String numOfServers = inFromClient.readLine();
                    int num = Integer.parseInt(numOfServers)+1;
                    String filename = inFromClient.readLine();
                    String length = inFromClient.readLine();
                    int filelength = Integer.parseInt(length);
                    if(filelength == 0 ) {
                        if(num > this.server.IPList.size()) {
                            outToClient =
                                    new DataOutputStream(clientSocket.getOutputStream());
                            outToClient.writeBytes("File not Found\n");
                            clientSocket.close();
                        }
                        else {
                            if(files.containsKey(filename)) {
                                GetRequest(num,filename,files.get(filename));
                            }
                            else {
                                GetRequest(num,filename,"");
                            }
                            connectionSocket.close();
                        }
                    }
                    else {
                        if(!files.containsKey(filename)) {
                            String  data = "";
                            for (int i = 0; i < filename.length(); i++) {
                                data += inFromClient.readLine()+"\n";
                            }
                            files.put(filename, data);
                            if(num > this.server.IPList.size()) {
                                outToClient =
                                        new DataOutputStream(clientSocket.getOutputStream());
                                outToClient.writeBytes(data+"\n");
                                clientSocket.close();
                                connectionSocket.close();
                            }
                            else {
                                GetRequest(num,filename,data);
                                connectionSocket.close();
                            }
                        }
                        else {
                            GetRequest(num,filename,files.get(filename));
                            connectionSocket.close();
                        }
                    }
                }
                if(message.equals("close"))break;
                System.out.println(this.server.IPList);
                System.out.println(this.server.PortList);
            }
            serverSocket.close();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
