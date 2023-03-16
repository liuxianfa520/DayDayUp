package bio;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.StandardCharsets;

public class BioServer {

    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(8899));


        while (true) {
            // 接收客户端的连接
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> {
                try {
                    ByteBuffer allocate = ByteBuffer.allocate(1024);
                    int read = clientSocket.getChannel().read(allocate);
                    if (read > 0) {
                        // 接收数据
                        String s = new String(allocate.array());
                        System.out.println("从客户端读取到数据:" + s);
                        // 给client发送数据
                        clientSocket.getChannel().write(ByteBuffer.wrap(("server read message :" + s).getBytes(StandardCharsets.UTF_8)));

                        // 关闭连接
                        clientSocket.getChannel().close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
