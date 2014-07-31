package exp4server.frozen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;


/**
 * Webクライアントと通信を行うクラス
 */
public abstract class Handler {
    /**
     * Webクライアントと通信するためのソケット
     */
    private final Socket client;

    /**
     * Webクライアントからの読み込みに使うストリーム
     */
    private final BufferedReader in;

    /**
     * Webクライアントへの出力に使うストリーム
     */
    private final OutputStream out;

    /**
     * コンストラクタ
     * @param socket クライアントとの間で保持されているソケット
     */
    public Handler(Socket socket) throws IOException {
        this.client = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = socket.getOutputStream();
    }

    /**
     * 1行の文字列をクライアントへ送信する．
     * 行末コードも同時に出力される．
     * @param line CRLFを含まない文字列
     */
    protected void sendln(String line) throws IOException {
        send(line + "\r\n");
    }

    /**
     * 文字列をクライアントへ送信する．
     * @param s 文字列
     */
    protected void send(String s) throws IOException {
        out.write(s.getBytes());
    }

    /**
     * フォーマット付き send．
     * @param format 入力フォーマット
     * @param args 埋め込む引数
     * @throws IOException
     */
    protected void send(final String format, final Object... args) throws IOException {
        send(String.format(format, args));
    }

    /**
     * フォーマット付き sendln．
     * @param format 入力フォーマット
     * @param args 埋め込む引数
     * @throws IOException
     */
    protected void sendln(final String format, final Object... args) throws IOException {
        sendln(String.format(format, args));
    }

    /**
     * ステータスコードとエラーページを送信する．
     * @param message 出力するエラーメッセージ
     */
    private void sendStatusCode(String message) throws IOException {
        sendln("HTTP/1.0 " + message);
        sendln("Content-Type: text/plain");
        sendln(message);
    }

    private Request parseHeader() throws IOException {
        final String line = in.readLine();
        if (line == null) {
            return null;
        }

        // リクエスト行を空白" "で区切って解析する
        final StringTokenizer st = new StringTokenizer(line, " ");
        String method = null; // メソッド
        String path = null; // パス
        String version = null; // HTTPのバージョン

        try {
            method = st.nextToken();
            path = st.nextToken();
            version = st.nextToken();
        }
        catch (final NoSuchElementException e) {
            // リクエスト行が「メソッド パス HTTPのバージョン」の形式ではなかった
            return null;
        }

        final Request req = new Request(method, path, version);
        req.readHeader(in);
        req.readBody(in);
        return req;
    }

    /**
     * クライアントとの通信を行う.
     */
    public void doService() {
        try {
            try {
                final Request req = parseHeader();
                if (req == null) {
                    sendStatusCode("400 Bad Request");
                    return;
                }
                perform(req);
            }
            finally {
                out.close();
                client.close();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void perform(Request req) throws IOException;

    /**
     * 標準出力にメソッドその他を出力する
     */
    protected void outputRawLog(Request req) {
        System.out.println("[Method] " + req.getMethod());
        System.out.println("[Request-URI] " + req.getRequestURI());
        System.out.println("[HTTP-Version] " + req.getVersion());
        System.out.println("[Headers]");
        for (final Map.Entry<String, String> e: req.getHeaders().entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }
        System.out.println("[Body]");
        System.out.println(req.getBody());
        System.out.println("-----------------------------");
    }
}
