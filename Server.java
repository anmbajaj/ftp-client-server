import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private int port = 8080;
    private ServerSocket serverSocket;
    private Socket clientConnection;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public void receiveFile(String message) throws IOException {
        String[] messages = message.split(" ");
        long size = Long.valueOf(messages[2]);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("New" + messages[1]);
            int bytes = 0;
            byte[] buffer = new byte[1000];
            while(size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1){
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes;
            }
            fileOutputStream.close();
        } catch(FileNotFoundException fileNotFoundException) {
            System.out.println("SERVER ERROR: File not found");
        }
    }

    public void run(){
        try{
            serverSocket = new ServerSocket(port, 10);
            System.out.println("Waiting for connection");

            clientConnection = serverSocket.accept();
            System.out.println("Connection request received from " + clientConnection.getInetAddress());

            dataOutputStream = new DataOutputStream(clientConnection.getOutputStream());
            dataInputStream = new DataInputStream(clientConnection.getInputStream());
            try{
                while(true){
                    String message = dataInputStream.readUTF();
                    if(message.startsWith("upload"))
                        receiveFile(message);
                }
            } catch (Exception exception){
                System.out.println("SERVER ERROR: Error while reading the message type");
            }

        } catch (IOException ioException){
            System.out.println("SERVER ERROR: error while initiating the server socket");
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
