package exp4server.frozen;

import java.io.IOException;
import java.net.Socket;

import exp4server.main.ServerCollectionHandler;
import exp4server.main.ClientCollectionHandler;

public class Main {
    /**
     * サーバ起動のためのmainメソッド
     * 起動方法
     * <pre>
     * java pro4server.HTTPServer type
     * type: "-c": ClientCollectionHandler, "-s": ServerCollectionHandler, それ以外: DefaultHander を起動
     * </pre>
     */
    public static void main(String[] args) {
        try {
            String type = args.length == 0 ? null : args[0];
            final HTTPServer server = new HTTPServer(8081, createHandlerFactory(type));
            server.doService();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static HandlerFactory createHandlerFactory(String type) {
        if (type == null) {
            return new DefaultHandlerFactory();
        }
        else if (type.equals("-s")) {
            return new ServerCollectionHandlerFactory();
        }
        else if (type.equals("-c")) {
            return new ClientCollectionHandlerFactory();
        }
        else {
            return new DefaultHandlerFactory();
        }
    }
}

class ServerCollectionHandlerFactory implements HandlerFactory {
    @Override
    public Handler createHandler(Socket socket) throws IOException {
        return new ServerCollectionHandler(socket);
    }
}

class ClientCollectionHandlerFactory implements HandlerFactory {
    @Override
    public Handler createHandler(Socket socket) throws IOException {
        return new ClientCollectionHandler(socket);
    }
}

class DefaultHandlerFactory implements HandlerFactory {
    @Override
    public Handler createHandler(Socket socket) throws IOException {
        return new DefaultHandler(socket);
    }
}
