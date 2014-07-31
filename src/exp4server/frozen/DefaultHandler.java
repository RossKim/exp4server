package exp4server.frozen;

import java.io.IOException;
import java.net.Socket;



/**
 * Webクライアントと通信を行うクラス
 */
public class DefaultHandler extends Handler {
    public DefaultHandler(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    protected void perform(Request req) throws IOException {
        // 標準出力にメソッド，ヘッダ，ボディを出力
        outputRawLog(req);

        // レスポンスを返す
        sendln("HTTP/1.0 200 OK");
        sendln("Content-Type: text/html; charset=utf-8");
        sendln(""); // ヘッダの終り

        sendln("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\">");
        sendln("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"ja-JP\" xml:lang=\"ja-JP\">");
        sendln("<head><title>It works!</title></head>");
        sendln("<body>");
        sendln("<p>このページは，exp4server.frozen.DefaultHandler によって出力されています．</p>");
        sendln("<p>下記のフォームを（適当な入力を伴って）submitしたときの標準出力を観察してみましょう．</p>");
        sendln("<form action=\"/form\" method=\"post\"><dl>");
        sendln("<dt>text</dt>");
        sendln("<dd><input name=\"input1\" type=\"text\" /></dd>");
        sendln("<dt>radio buttons</dt>");
        sendln("<dd><label><input name=\"input2\" type=\"radio\" value=\"A\" /> A</label></dd>");
        sendln("<dd><label><input name=\"input2\" type=\"radio\" value=\"B\" /> B</label></dd>");
        sendln("<dt>textarea</dt>");
        sendln("<dd><textarea name=\"input3\"></textarea></dd>");
        sendln("<dt>and...</dt>");
        sendln("<dd><input type=\"submit\" /></dd>");
        sendln("</dl></form>");
        sendln("</body></html>");
    }
}
