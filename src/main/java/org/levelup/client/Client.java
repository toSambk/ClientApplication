package org.levelup.client;
import org.level.up.json.JsonService;
import org.level.up.json.configuration.JsonConfiguration;
import org.level.up.json.impl.JsonServiceImpl;
import org.level.up.json.test.Cat;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import static java.lang.Integer.parseInt;


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
        while (!getExitFlag()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
        public static String returnJsonFromCat(String line) {
            JsonService service = new JsonServiceImpl();
            String[] arr = line.split(" ");
            Cat cat = new Cat(arr[0], parseInt(arr[1]));
            String json = service.toJson(cat);
            System.out.println(json);
            return json;
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

                    toServer.write(returnJsonFromCat(inputString) + '\n');
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
                if (stringFromServer != null) System.out.println("From server: " + stringFromServer);
            }
        }
    }
}

