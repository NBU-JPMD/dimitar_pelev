package com.server;

import com.sun.org.apache.bcel.internal.generic.Select;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by J-Mitko on 16.3.2016 г..
 */
public class Server{

    private int port = 9999;


    public Server()
    {
        try {
            Selector selector = Selector.open();

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking( false );

            ServerSocket serverSocket = serverSocketChannel.socket();

            InetSocketAddress address = new InetSocketAddress( port );
            serverSocket.bind(new InetSocketAddress("127.0.0.1", port));


            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

            SelectionKey key = serverSocketChannel.register( selector, SelectionKey.OP_ACCEPT );

            while (true) {
                int num = selector.select();

                Set selectedKeys = selector.selectedKeys();
                Iterator it = selectedKeys.iterator();

                while (it.hasNext()) {
                    key = (SelectionKey) it.next();

                    if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {

                        serverSocketChannel = (ServerSocketChannel) key.channel();// Accept the new connection
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);


                        SelectionKey newKey = socketChannel.register(selector, SelectionKey.OP_READ);// Add the new connection to the selector
                        it.remove();

                        System.out.println("Got connection from " + socketChannel);
                    } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {

                        SocketChannel socketChannel = (SocketChannel) key.channel();// Read the data


                        while (true) {
                            buffer.clear();

                            int n = socketChannel.read(buffer);//RECEIVE

                            if (n <= 0) {
                                break;
                            }
                            buffer.flip();

                            socketChannel.write(buffer);//SEND BACK
                        }

                        System.out.println(buffer + " from " + socketChannel);

                        it.remove();
                    }
                }
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
