package org.levelup.client;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private static boolean exitFlag;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(InetAddress.getByName("localhost"), 7878);

        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        Thread clientSend = new Thread(new ClientSend(toServer, consoleReader));
        Thread clientReceive = new Thread(new ClientReceive(fromServer));
        clientReceive.setDaemon(true);
        clientSend.setDaemon(true);
        clientSend.start();
        clientReceive.start();
        while (!getExitFlag()){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static void setExitFlag(boolean exitFlagLoc) {
        exitFlag = exitFlagLoc;
    }

    private static boolean getExitFlag() {
        return exitFlag;
    }

    static class ClientSend implements Runnable {

        private BufferedReader consoleReader;
        private BufferedWriter toServer;

        ClientSend(BufferedWriter toServer, BufferedReader consoleReader) {
            this.toServer = toServer;
            this.consoleReader = consoleReader;
        }

        @Override
        public void run() {
            while (true) {
                System.out.println("Enter string: ");
                try {
                    String inputString = consoleReader.readLine();
                    toServer.write(inputString + '\n');
                    toServer.flush();
                    if (inputString.equals("exit")) {
                        setExitFlag(true);
                        Thread.sleep(200);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    static class ClientReceive implements Runnable {
        private BufferedReader fromServer;

        ClientReceive(BufferedReader fromServer) {
            this.fromServer = fromServer;
        }

        @Override
        public void run() {
            String stringFromServer = null;
            while (true) {
                try {
                    stringFromServer = fromServer.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("From server: " + stringFromServer);
            }
        }
    }

}

