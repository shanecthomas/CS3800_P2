import java.io.*;
import java.net.*;
import java.util.*;

class TCPClient {

    public static void main(String argv[]) throws Exception
    {
        String sentence;
        String modifiedSentence;

        String getRequest = "GET /" + argv[4] + " HTTP/1.1\nHost: " + argv[1];
        String putRequest = "PUT /" + argv[4] + " HTTP/1.1\nHost: " + argv[1];

        Socket clientSocket = new Socket("localhost", Integer.parseInt(argv[2]));
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());

        //GET
        if (argv[3].equals("GET")) {
            //Send to server
            sentence = getRequest;
            outToServer.writeBytes(sentence + '\n');

            //Receive from server
            int bytes = 0;
            File file = new File(argv[4]);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            long size = inFromServer.readLong();
            byte[] buffer = new byte[4 * 1024];
            while (size > 0 && (bytes = inFromServer.read(buffer, 0,(int)Math.min(buffer.length, size)))!= -1) {
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes;
            }
            fileOutputStream.close();

            //Print file
            System.out.println("Received file!");
        }
        //PUT
        else if (argv[3].equals("PUT")) {
            //Send to server
            sentence = putRequest;
            outToServer.writeBytes(sentence + '\n');

            int bytes = 0;
            File file = new File(argv[4]);
            FileInputStream fileInputStream = new FileInputStream(file);
            outToServer.writeLong(file.length());
            byte[] buffer = new byte[4 * 1024];
            while ((bytes = fileInputStream.read(buffer))!= -1) {
                outToServer.write(buffer, 0, bytes);
                outToServer.flush();
            }
            fileInputStream.close();

            //Receive from server
            modifiedSentence = inFromServer.readLine();
            System.out.println("FROM SERVER: " + modifiedSentence);
        }
        //INVALID
        else {
            System.out.println("Invalid request.");clientSocket.close();System.exit(0);
        }

        clientSocket.close();
    }
} 