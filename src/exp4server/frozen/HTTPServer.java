package exp4server.frozen;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

/**
 * 簡単なWebサーバ
 */
public class HTTPServer {
    /**
     * サーバソケット
     */
    private final ServerSocket server;

    /**
     * Handlerを生成するFactory
     */
    private final HandlerFactory factory;
    
    /**
     * コンストラクタ
     * @param port このサーバが受け付けるポート番号
     */
    public HTTPServer(int port, HandlerFactory factory) throws IOException {
        server = new ServerSocket(port);
        this.factory = factory;
    }

    /**
     *  実際に通信を受け付けサービスを行う
     */
    public void doService() {
        System.out.println("Starting Server Service!!");
        for (;;) {
            try {
                final Socket client = server.accept();
                final Handler handler = factory.createHandler(client);
                handler.doService();
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}
