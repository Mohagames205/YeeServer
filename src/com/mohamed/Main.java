package com.mohamed;

import com.mollin.yapi.YeelightDevice;
import com.mollin.yapi.enumeration.YeelightProperty;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    public static void main(String[] args) throws Exception {
        run(6969);
    }

    public static void run(int port) throws Exception {

        ServerSocket serverSocket = new ServerSocket(port);
        boolean running = true;
        YeelightDevice device = null;
        try {
            device = new YeelightDevice("192.168.1.30");
            System.out.println("Connected to bulb!");
        }
        catch(Exception e){
            System.out.println("Could not connect to bulb\nExiting...");
            System.exit(0);
        }
        while (running) {
            Socket newSocket = serverSocket.accept();
            BufferedReader bf = new BufferedReader(new InputStreamReader(newSocket.getInputStream()));
            String text = bf.readLine();
            PrintWriter out = new PrintWriter(newSocket.getOutputStream());
            if (text != null) {
                String state;
                if (text.toLowerCase().equals("toggle")) {
                    device.toggle();
                    state = "The bulb is now " + device.getProperties().get(YeelightProperty.POWER);
                } else if (text.toLowerCase().contains("setcolor")) {
                    try {
                        text = text.replace(" ", "");
                        String[] splittext = text.split(":");
                        String rgbcode = splittext[1];
                        String[] rgbarray = rgbcode.split(",");
                        Integer r = Integer.parseInt(rgbarray[0]);
                        Integer g = Integer.parseInt(rgbarray[1]);
                        Integer b = Integer.parseInt(rgbarray[2]);
                        device.setRGB(r, g, b);
                        state = "The bulb is now set to the color: <b>" + rgbcode + "</b>";
                    } catch (Exception e) {
                        state = "Invalid color input!";
                    }
                } else {
                    state = "Invalid command.";
                }

                out.println("[OUTBOUND\\COMMAND][" + serverSocket.getInetAddress().getHostAddress() + "] Message received successfully. " + state);
                out.flush();
                System.out.println(ANSI_CYAN + "[INBOUND\\COMMAND]" + ANSI_WHITE + "[" + newSocket.getInetAddress().toString() + "] " + ANSI_YELLOW + text + ANSI_RESET);
            } else {
                out.println("[OUTBOUND\\CONNECTION][" + newSocket.getInetAddress().getCanonicalHostName() + "] Connection with server established.");
                out.flush();
                System.out.println(ANSI_CYAN + "[INBOUND\\CONNECTION] " + ANSI_YELLOW + "Client " + newSocket.getInetAddress().toString() + " connected to server." + ANSI_RESET);
            }

        }
    }
}
