package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App
{

    private static SimpleServer server;
    public static void main( String[] args ) throws IOException, InterruptedException {
        server = new SimpleServer(3000);



        server.listen();
    }
}
