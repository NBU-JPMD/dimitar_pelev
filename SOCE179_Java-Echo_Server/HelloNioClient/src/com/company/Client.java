package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by J-Mitko on 18.3.2016 г..
 */
public class Client {

    public Client() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 9999));

        BufferedReader br = new BufferedReader(new
                InputStreamReader(System.in));

        String string;

        ByteBuffer buf = ByteBuffer.allocateDirect(1024);
        buf.clear();

        try {
            while (true) {

                System.out.print("input: ");

                string = (String) br.readLine();

                if (string.length() <= 1024) {
                    for (int i = 0; i < string.length(); i++) {
                        buf.put((byte) string.charAt(i));//READ MESSAGE
                    }
                } else {
                    System.out.println("Message too long");
                }

                buf.flip();

                while (buf.hasRemaining()) {
                    socketChannel.write(buf);//SEND
                }
                buf.clear();

                int n = socketChannel.read(buf);//RECEIVE
                buf.flip();

                System.out.print("echo: ");
                while (buf.hasRemaining()) {
                    System.out.print((char) buf.get());//PRINT ECHO
                }

                System.out.println();
                buf.clear();

            }
        }finally {
            socketChannel.close();
        }

    }
}
