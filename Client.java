package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author USER
 */
public class Client {

    private static Socket socket = null;
    private static BufferedReader br = null;
    private static PrintWriter pr = null;
    private static int ID;
    static long FileSize;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here

        Scanner input = new Scanner(System.in);
        String send = null;
        String i;
        String Id;
        String line;
        String FileName;
        FileInputStream IS;
        OutputStream OS;
        InputStream is;
        FileOutputStream fos;
        String ht = "01111110";
        String payload;

        try {
            socket = new Socket("localhost", 1558);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pr = new PrintWriter(socket.getOutputStream());
            System.out.println("Connected to server");

        } catch (Exception e) {
            System.err.println("Problem connecting to the server");
            System.exit(1);
        }

        System.out.println("Enter your ID: ");
        while (true) {
            try {

                send = input.nextLine();
            } catch (Exception ex) {
                continue;
            }
            pr.println(send);
            pr.flush();

            line = br.readLine();
            if (!(line.equalsIgnoreCase("yes"))) {
                System.out.println("You are already logged in with IP address: " + line);
                if (socket != null) {
                    socket.close();
                    break;
                }
            }
            ID = Integer.parseInt(send);
            while (true) {
                System.out.println("Type : Exit/Send/Receive");
                try {
                    send = input.nextLine();
                } catch (Exception ex) {
                    continue;
                }
                pr.println(send);
                pr.flush();
                if (send.equalsIgnoreCase("Exit")) {
                    System.out.println("Client wants to exit the connection. Exiting......");
                    break;
                } else if (send.equalsIgnoreCase("send")) {
                    
                    File f = new File("light.jpg");
                    FileName = f.getName();
                    pr.println(FileName);
                    pr.flush();
                    long FileSize = f.length();
                    pr.println(FileSize);
                    pr.flush();
                  
                    line = br.readLine();
                    System.out.println(line);
                    if (line.equalsIgnoreCase("Not")) {
                        System.err.println("Buffer Overflow");
                        continue;
                    }

                    System.out.println("I am entering into loop");
                    IS = new FileInputStream(f);
                    OS = socket.getOutputStream();
                    byte[] buffer = new byte[1];
                    int chunk = 1;
                    int count = 0;

                    while (count != FileSize) {
                        if (FileSize - count >= chunk) {
                            IS.read(buffer, 0, chunk);
                            count += chunk;
                            payload = buffer.toString();
                            System.out.println(payload);
                            /*    try {
                                    pr.print(payload);
                                    pr.flush();

                                } catch (Exception ex) {
                                    System.err.println("Could not load payload");
                                }
                             */
                            OS.write(buffer, 0, chunk);
                        } else {
                            chunk = (int) (FileSize - count);
                            IS.read(buffer, 0, chunk);
                            OS.write(buffer, 0, chunk);
                            count = (int) FileSize;
                        }
                    }
                    OS.flush();
                    System.out.println("File Uploaded");

                } else if (send.equalsIgnoreCase("Receive")) {
                    line = br.readLine();

                    try {
                        is = socket.getInputStream();
                        fos = new FileOutputStream("Copy.jpg");

                        byte[] buffer = new byte[1024];
                        int count;

                        while ((count = is.read(buffer)) >= 0) {
                            fos.write(buffer, 0, count);
                        }

                        fos.flush();

                        System.out.println("Received Filename: " + line);
                        System.out.println("Saved To: " + "Copy.jpg");

                        System.out.println("File Received");

                    } catch (Exception ex) {
                        System.err.print("Error Receiving file");
                    }
                }
            }
        }
    }
}

                                
                              
                       
