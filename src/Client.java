import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        System.out.println("Please give me the IP Address of the server you want to connect to: ");
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        String IP = inFromUser.readLine();
        System.out.println("Please give me the Port of the server you want to connect to: ");
        int Port = Integer.parseInt(inFromUser.readLine());

        Socket clientSocket = new Socket(IP, Port);
        DataOutputStream outToServer =
                new DataOutputStream(clientSocket.getOutputStream());
        System.out.println("What's your type of request? ");
        String request = inFromUser.readLine();
        outToServer.writeBytes(request+"\n");


        if(request.equals("GET")) {
            int numOfServers = 0;
            System.out.println("Give me the name of the file you want to read from: ");
            String filename = inFromUser.readLine();
            outToServer =
                    new DataOutputStream(clientSocket.getOutputStream());
            outToServer.writeBytes(String.valueOf(numOfServers)+"\n");
            outToServer.writeBytes(filename+"\n");

            BufferedReader inFromServer =
                    new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String answer = inFromServer.readLine();
            while(answer != null) {
                System.out.println(answer);
                answer = inFromServer.readLine();
            }

            clientSocket.close();
        }

        else if(request.equals("PUT")) {
            System.out.println("Give me the name of the file you want to write to: ");
            String filename = inFromUser.readLine();
            System.out.println("f:"+filename);
            System.out.println("The length of that file: ");
            int length = Integer.parseInt(inFromUser.readLine());
            System.out.println("l:"+length);
            int numOfServer = 0;
            System.out.println("Give me the data of that file: ");
            String data = "";
            for(int i=0; i<length; i++) {
                data += inFromUser.readLine()+"\n";
            }
            outToServer =
                    new DataOutputStream(clientSocket.getOutputStream());
            outToServer.writeBytes(filename+"\n");
            outToServer.writeBytes(String.valueOf(length)+"\n");
            outToServer.writeBytes(String.valueOf(numOfServer)+"\n");
            outToServer.writeBytes(data+"\n");


            BufferedReader inFromServer =
                    new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String answer = inFromServer.readLine();
            System.out.println(answer);
            clientSocket.close();

        }

    }
}
