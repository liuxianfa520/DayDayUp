package bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class BioClient {

    public static void main(String[] args) throws IOException {
        while (true) {
            run();
        }
    }

    private static void run() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8899));

        socketChannel.write(ByteBuffer.wrap("[hello,i am client]".getBytes(StandardCharsets.UTF_8)));
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        int read = socketChannel.read(allocate);
        if (read>0) {
            System.out.println(new String(allocate.array()));
        }

        socketChannel.close();
    }
}