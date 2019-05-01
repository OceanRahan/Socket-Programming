package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USER
 */
public class ClientConnected implements Runnable {

    private Socket socket;
    private InputStream IS;
    private OutputStream OS;
    private int id;
    static int FileSize;
    FileOutputStream fos;
    FileInputStream fis;
    private static byte[] array;
    static String FileName;

    public ClientConnected(Socket s, int id) {
        this.socket = s;
        this.id = id;

        try {
            this.IS = s.getInputStream();
            this.OS = s.getOutputStream();
        } catch (Exception e) {
            System.err.println("Problem connecting with client [" + id + "] .");
        }

    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(this.IS));
        PrintWriter pr = new PrintWriter(this.OS);
        String str;
        Scanner input = new Scanner(System.in);
        DataOutputStream outStream;
        try {
            outStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientConnected.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {
            try {

                if ((str = br.readLine()) != null) {
                    if (str.equalsIgnoreCase("Exit")) {
                        System.out.println("Client " + id + " wants to EXIT now. Connection will be terminated....");
                        break;
                    } else if (str.equalsIgnoreCase("send")) {

                        /* try {
                            String s = br.readLine();
                            int Rid = Integer.parseInt(s);
                            if (Server.OnlineClient[Rid % 120] == "") {
                                pr.println("Not");
                                pr.flush();
                                break;
                            } 
                         */
                        FileName = br.readLine();
                        String length = br.readLine();
                        FileSize = Integer.parseInt(length);
                        System.out.println("Enter buffer size: ");
                        int i = input.nextInt();
                        if (FileSize > i) {
                            // System.err.println("Buffer Overflow!");
                            pr.println("Not");
                            pr.flush();
                            continue;
                        }

                        /*      try{
                            str=br.readLine();
                            System.out.println(str);
                            }catch(Exception ex)
                            {
                                System.err.println("Could not receive data");
                            }
                         */
                        int count = 0;

                        IS = socket.getInputStream();
                        array = new byte[i];
                        IS.read(array, 0, FileSize);

                    } else if (str.equalsIgnoreCase("receive")) {
                        pr.println(FileName);
                        pr.flush();
                        OS = socket.getOutputStream();
                        byte[] buffer = new byte[1024];
                        int bytes = 0;
                        OS.write(array, 0, FileSize);

                        OS.flush();
                        System.out.println("File sent");

                        if (IS != null) {
                            IS.close();
                        }
                        if (OS != null) {
                            OS.close();
                        }
                        if (socket != null) {
                            socket.close();
                        }

                    }
                }
            } catch (IOException ex) {
                //  System.err.println("Could not connect to the client");
            }
        }
        Server.ClientCount--;
        Server.OnlineClient[id % 120] = "";
        System.out.println("Client [" + id + "] is now terminating....");
        System.out.println("Number Of Connected Client :" + Server.ClientCount);
    }

}
