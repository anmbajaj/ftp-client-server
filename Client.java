import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private static final int CHUNK_SIZE = 1000;

    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private BufferedReader bufferedReader;

    public void uploadFile(String command, String filename) {
        File file = new File(filename);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                dataOutputStream.writeUTF(command + " " + file.length());
            } catch(IOException exception) {
                System.out.println("ERROR OCCURRED: Issue sending the upload command type");
            }

            System.out.println("Sending " + filename + " to server");
            int bytes = 0;
            byte[] buffer = new byte[CHUNK_SIZE];
            while((bytes = fileInputStream.read(buffer)) > 0){
                dataOutputStream.write(buffer, 0, bytes);
                dataOutputStream.flush();
            }
            System.out.println("File sent successfully");
        } catch (FileNotFoundException fileNotFoundException){
            System.out.println("ERROR OCCURRED: File not found. Please specify the correct file name");
        } catch (IOException e) {
            System.out.println("ERROR OCCURRED: Issue occurred with file input stream, while checking the availability");
        }
    }

    public void downloadFile(String command, String filename){
        try {
            dataOutputStream.writeUTF(command);
            String reply = dataInputStream.readUTF();
            if(reply.equals("No such file exists"))
                System.out.println("Please enter a valid file name for download");
            else{
                long size = Long.valueOf(reply);
                FileOutputStream fileOutputStream = new FileOutputStream("New" + filename);

                System.out.println("Downloading " + filename);
                int bytes = 0;
                byte[] buffer = new byte[CHUNK_SIZE];
                while(size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1){
                    fileOutputStream.write(buffer, 0, bytes);
                    size -= bytes;
                }
                fileOutputStream.close();
                System.out.println(filename + " download complete");
            }
        } catch(IOException exception) {
            System.out.println("ERROR OCCURRED: Issue sending the upload command type");
        }
    }

    public void executeCommand(String command){
        if(command.startsWith("get")) {
            String filename = command.split(" ")[1];
            if(filename.contains(" "))
                System.out.println("Please enter valid filename");
            else {
                downloadFile(command, filename);
            }

        }
        else if(command.startsWith("upload")){
            String filename = command.split(" ")[1];
            if(filename.contains(" "))
                System.out.println("Please enter valid filename");
            else {
                uploadFile(command, filename);
            }
        }
        else
            System.out.println("Unsupported command: try get <filename> | upload <filename> ");
    }

    public void run(String hostname, String port) throws IOException {
        try {
            socket = new Socket(hostname, Integer.valueOf(port));
            System.out.println("Connected to " + hostname + " at port " + port);

            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            while (true){
                System.out.print("Please issue the command: ");
                String command = bufferedReader.readLine();
                executeCommand(command);
            }

        } catch (UnknownHostException e) {
            System.out.println("ERROR OCCURRED: Unknown Host.");
        } catch (IOException e) {
            System.out.println("ERROR OCCURRED: I/O error while creating the socket");
        } finally {
            try {
                dataInputStream.close();
                dataOutputStream.close();
                socket.close();
            } catch (Exception exception){

            }
        }
    }

    public static void main(String[] args) throws IOException {
        if(args.length != 2){
            System.out.println("Syntax is: java Client <hostname> <port>");
            System.exit(1);
        }
        Client client = new Client();
        client.run(args[0], args[1]);
    }
}
