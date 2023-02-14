import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 8080;

    private static final int CHUNK_SIZE = 1000;

    private static final String PATH = "/Users/anmbajaj/UF/Spring 2023/CN/ftp-client-server/";

    private ServerSocket serverSocket;
    private Socket clientConnection;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public void receiveFile(String message) throws IOException {
        String[] messages = message.split(" ");
        long size = Long.valueOf(messages[2]);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(PATH + "New" + messages[1]);
            System.out.println("Receiving file " + messages[1]);

            int bytes = 0;
            byte[] buffer = new byte[CHUNK_SIZE];
            while(size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1){
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes;
            }

            System.out.println("File received successfully");
            fileOutputStream.close();
        } catch(FileNotFoundException fileNotFoundException) {
            System.out.println("SERVER ERROR: File not found");
        }
    }

    public void sendFile(String message){
        String[] messages = message.split(" ");
        try {
            FileInputStream fileInputStream = new FileInputStream(PATH + messages[1]);
            dataOutputStream.writeUTF(String.valueOf(fileInputStream.available()));

            System.out.println("Sending file ");
            int bytes = 0;
            byte[] buffer = new byte[CHUNK_SIZE];
            while((bytes = fileInputStream.read(buffer)) > 0){
                dataOutputStream.write(buffer, 0, bytes);
                dataOutputStream.flush();
            }

            fileInputStream.close();
            System.out.println("File sent successfully");
        } catch (FileNotFoundException fileNotFoundException) {
            try {
                System.out.println("No such file");
                dataOutputStream.writeUTF("No such file exists");
            } catch (IOException ioException){
                System.out.println("SERVER ERROR: IO Exception occurred while notifying the non-existence of file");
            }
        } catch (IOException ioException){
            System.out.println("SERVER ERROR: While closing the File input stream");
        }
    }

    public void run(){
        try{
            serverSocket = new ServerSocket(PORT, 10);
            System.out.println("Server started at port " + PORT);
            System.out.println("Waiting for connection");

            clientConnection = serverSocket.accept();
            System.out.println("Connection request received from " + clientConnection.getInetAddress());

            dataOutputStream = new DataOutputStream(clientConnection.getOutputStream());
            dataInputStream = new DataInputStream(clientConnection.getInputStream());
            try{
                while(true){
                    System.out.println("Waiting for message from client");
                    String message = dataInputStream.readUTF();
                    System.out.println("Received message from Client: " + message);

                    if(message.startsWith("upload"))
                        receiveFile(message);
                    if(message.startsWith("get"))
                        sendFile(message);
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
