import javax.sound.midi.Soundbank;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private BufferedReader bufferedReader;

    public void uploadFile(String filename){

    }

    public void getFile(){
    }

    public void executeCommand(String command){
        if(command.startsWith("get"))
            getFile();
        else if(command.startsWith("upload")){
            String filename = command.split(" ")[1];
            if(filename.contains(" "))
                System.out.println("Please enter valid filename");
            else
                uploadFile(filename);
        }
        else
            System.out.println("Unsupported command: try get <filename> | upload <filename> ");
    }

    public void run(String hostname, String port) throws IOException {
        try {
            socket = new Socket(hostname, Integer.valueOf(port));
            System.out.println("Connected to" + hostname + " at port " + port);

            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            while (true){
                System.out.print("Please issue the command: ");
                String command = bufferedReader.readLine();
                executeCommand(command);
            }

        } catch (UnknownHostException e) {
            System.out.println("ERROR OCCURRED: Unknown Host");
        } catch (IOException e) {
            System.out.println("ERROR OCCURRED: I/O error");
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
