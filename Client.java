package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
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
    private static DataOutputStream out;
    private static DataInputStream in;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here

        Scanner input = new Scanner(System.in);
        String send = null;
        String line;
        String FileName;
        FileInputStream IS;
        OutputStream OS;
        InputStream is;
        FileOutputStream fos;
        String ht = "01111110";
        String payload;
        String frame = "";
        byte num = 1;
        byte ack_no = 0;
        String stuffed;
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

                  
                    int cc = 0;
                    
                        IS = new FileInputStream(f);
                        OS = socket.getOutputStream();
                        byte[] buffer = new byte[1];
                        int chunk = 1;
                        int count = 0;
                        byte[] arr = new byte[20];
                        while (count != FileSize) {
                            if (FileSize - count >= chunk) {
                                IS.read(buffer, 0, chunk);
                                count += chunk;
                                String seq = Integer.toBinaryString(num & 255 | 256).substring(1);
                                String ack = Integer.toBinaryString(ack_no & 255 | 256).substring(1);
                                payload = Integer.toBinaryString(buffer[0] & 255 | 256).substring(1);
                                stuffed = bitStuff(payload);
                                int sum = 0;
                                for (int i = 0; i < 8; i++) {
                                    if (payload.charAt(i) == '1') {
                                        sum++;
                                    }
                                }
                                String c = Integer.toBinaryString((byte) sum & 255 | 256).substring(1);
                                frame = "01111110" + "00000001" + seq + ack + stuffed + c + "01111110";
                                int len = frame.length();
                               // pr.println(len);
                              //  pr.flush();
                                byte[] abc = new byte[(len + Byte.SIZE - 1) / Byte.SIZE];
                                char ch;
                                for (int i = 0; i < len; i++) {
                                    if ((ch = frame.charAt(i)) == '1') {
                                        abc[i / Byte.SIZE] = (byte) (abc[i / Byte.SIZE] | (0x80 >>> (i % Byte.SIZE)));
                                    } else if (ch != '0') {
                                        throw new IllegalArgumentException();
                                    }
                                }
                                OS.write(abc);
                                System.out.println("Frame sent: " + frame);
                               // pr.println("");
                                
                            } else {
                                chunk = (int) (FileSize - count);
                                IS.read(buffer, 0, chunk);
                                String seq = Integer.toBinaryString(num & 255 | 256).substring(1);
                                String ack = Integer.toBinaryString(ack_no & 255 | 256).substring(1);
                                payload = Integer.toBinaryString(buffer[0] & 255 | 256).substring(1);
                                stuffed = bitStuff(payload);
                                int sum = 0;
                                for (int i = 0; i < 8; i++) {
                                    if (payload.charAt(i) == '1') {
                                        sum++;
                                    }
                                }
                                String c = Integer.toBinaryString((byte) sum & 255 | 256).substring(1);
                                frame = "01111110" + "00000001" + seq + ack + stuffed + c + "01111110";
                                int len = frame.length();
                                byte[] abc = new byte[(len + Byte.SIZE - 1) / Byte.SIZE];
                                char ch;
                                for (int i = 0; i < len; i++) {
                                    if ((ch = frame.charAt(i)) == '1') {
                                        abc[i / Byte.SIZE] = (byte) (abc[i / Byte.SIZE] | (0x80 >>> (i % Byte.SIZE)));
                                    } else if (ch != '0') {
                                        throw new IllegalArgumentException();
                                    }
                                }
                                OS.write(abc);
                                System.out.println("Frame sent: " + frame);
                                count = (int) FileSize;
                            }
                        }
                        OS.flush();
                        //System.out.println("Frame Up");

                    
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

    public static String bitStuff(String s) {
        int count = 0;
        String res = "";
        for (int i = 0; i < 8; i++) {
            if (s.charAt(i) == '1') {
                count++;
                res += s.charAt(i);
            } else {
                count = 0;
                res += s.charAt(i);

            }
            if (count == 5) {
                res += '0';
                count = 0;
            }
        }
        return res;
    }
}
