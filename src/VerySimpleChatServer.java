import java.io.*;
import java.net.*;
import java.util.*;

public class VerySimpleChatServer {

    ArrayList clientOutputStreams;

    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;
        String[] userList;
        int userID;
        String userName;

        public ClientHandler(Socket clientSocket, int usr) {
            try {
                sock = clientSocket;
                userID = usr;
                userName = "user" + userID;
//                userList[userID] ="user" +usr;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void run() {
            String message;
//            String name = userList[userID];
            try {
                while ((message = reader.readLine()) != null) {

                    if(message.startsWith("%")){
                        userName = message.substring(1);
                    }

                    message = userName + ": " + message;
                    System.out.println(message);
                    tellEveryone(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new VerySimpleChatServer().go();
    }

    public void go(){
        clientOutputStreams = new ArrayList();
        try {
            ServerSocket serverSock = new ServerSocket(5000);

            while (true){
                Socket clientSocket = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);
                int i = clientOutputStreams.indexOf(writer);
                Thread t = new Thread(new ClientHandler(clientSocket,i ));
                t.start();
                System.out.println("got a connection");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void tellEveryone(String message){
        Iterator it = clientOutputStreams.iterator();
        while (it.hasNext()){
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}


