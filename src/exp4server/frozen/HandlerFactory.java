package exp4server.frozen;

import java.io.IOException;
import java.net.Socket;

/**
 * ClientHandlerを生成するFactory
 */
public interface HandlerFactory {
    Handler createHandler(Socket socket) throws IOException;
}
