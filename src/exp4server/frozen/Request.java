package exp4server.frozen;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String method;

    private final String requestURI;

    private final String version;

    private String body = null;

    private final Map<String, String> headers = new HashMap<String, String>();

    public Request(String method, String path, String version) {
        this.method = method;
        this.requestURI = path;
        this.version = version;
    }

    public void readHeader(BufferedReader in) throws IOException {
        for (;;) {
            final String line = in.readLine();
            if ("".equals(line)) {
                return;
            }

            // ヘッダは1行であることを仮定している
            final String[] s = line.split(":\\s*");
            if (s.length > 1) {
                headers.put(s[0], s[1]);
            }
        }
    }

    public void readBody(BufferedReader in) throws IOException {
        final String contentLength = headers.get("Content-Length");
        if (contentLength != null) {
            final int length = Integer.parseInt(contentLength);
            final char[] buf = new char[length];
            if (in.read(buf) != -1) {
                this.body = String.valueOf(buf);
            }
        }
    }

    /**
     * リクエストに含まれるHTTPメソッドを返す
     */
    public String getMethod() {
        return method;
    }

    /**
     * リクエストに含まれるURIを返す
     */
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * リクエストに含まれるHTTPバージョンを返す
     */
    public String getVersion() {
        return version;
    }

    /**
     * リクエストヘッダを返す
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * リクエストボディ（本体）を返す．
     * @return ボディ．存在しない場合はnull．
     */
    public String getBody() {
        return body;
    }
}
