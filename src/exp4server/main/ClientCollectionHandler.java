package exp4server.main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import exp4server.frozen.Handler;
import exp4server.frozen.Request;

/**
 * Webクライアントと通信を行うクラス
 */
public class ClientCollectionHandler extends Handler {

	protected Map<String, String> param = null;

	/**
	 * @param socket
	 * @throws IOException
	 */
	public ClientCollectionHandler(Socket socket) throws IOException {
		super(socket);
	}

	@Override
	protected void perform(Request req) throws IOException {
		// 実装してください

		// 標準出力にメソッド，ヘッダ，ボディを出力
		outputRawLog(req);

		try {
			URI uri = new URI(req.getRequestURI());
			param = splitParam(req);
			if (uri.getPath().equals("/style.css")) {
				List<String> cssList = HandlerUtils.loadCSSFile("style.css",
						"text/css");
				for (String str : cssList) {
					sendln(str);
				}
			} else if (uri.getPath().equals("/form.js")) {
				List<String> jsList = HandlerUtils.loadCSSFile("form.js",
						"application/javascript");
				for (String str : jsList) {
					sendln(str);
				}
			} else if (uri.getPath().equals("/jquery-2.0.3.min.js")) {
				List<String> jsList = HandlerUtils.loadCSSFile(
						"jquery-2.0.3.min.js", "application/javascript");
				for (String str : jsList) {
					sendln(str);
				}
			} else if (uri.getPath().equals("/auth")) {
				String authorization = req.getHeaders().get("Authorization");
				if (authorization == null || authorization.equals("")) {
					sendln("HTTP/1.0 401 Unauthorized");
					sendln("WWW-Authenticate: Basic realm='Restricted Area'");
					sendln(""); // ヘッダの終り
				} else {
					String[] tmp = authorization.split(" ");
					String auth = new String(Base64.decodeBase64(tmp[1]));
					String[] token = auth.split(":");
					if (token[0].equals("exp4") && token[1].equals("weblovers")) {
						sendln("HTTP/1.0 200 OK");
						sendln("");
					} else {
						sendln("HTTP/1.0 401 Unauthorized");
						sendln("WWW-Authenticate: Basic realm='Restricted Area'");
						sendln(""); // ヘッダの終り
					}
				}
			} else {
				// レスポンスを返す
				sendln("HTTP/1.0 200 OK");
				sendln("Content-Type: text/html; charset=utf-8");
				sendln("Content-Language: ja");
				sendln(""); // ヘッダの終り

				headHtml();
				if (uri.getPath().equals("/sex")) {
					sexHtml(param.get("sex"));
				} else if (uri.getPath().equals("/name")) {
					nameHtml(HandlerUtils.escapeString(param.get("name")));
				} else if (uri.getPath().equals("/thoughts")) {
					thoughtsHtml(HandlerUtils.escapeString(param
							.get("thoughts")));
				} else if (uri.getPath().equals("/confirm")) {
					confirmHtml(param.get("sex"),
							HandlerUtils.escapeString(param.get("name")),
							HandlerUtils.escapeString(param.get("thoughts")));
				} else if (uri.getPath().equals("/submit")) {
					System.out.println("性別：" + param.get("sex"));
					System.out.println("名前：" + param.get("name"));
					System.out.println("感想：" + param.get("thoughts"));
					submitHtml();
				} else if (uri.getPath().equals("/")) {
					homeHtml();
				}
				closeHtml();
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			System.out.println("ClientCollectionHandler.perform¥n"
					+ e.toString());
		}

	}

	/**
	 * @param req
	 * @return
	 */
	protected Map<String, String> splitParam(Request req) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("sex", "");
		map.put("name", "");
		map.put("thoughts", "");
		if (req.getBody() == null) {
			return map;
		}
		String[] params = req.getBody().split("&");
		for (String query : params) {
			if (query == null) {
				continue;
			}
			String[] param = query.split("=");
			if (param.length < 2) {
				continue;
			}
			try {
				param[1] = URLDecoder.decode(param[1], "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				System.out.println("ClientCollectionHandler.splitParam : "
						+ e.getMessage());
			}
			if (param[0] != null) {
				if (param[0].equals("sex")) {
					String sex = "";
					if (param[1].equals("male")) {
						sex = "男性";
					} else if (param[1].equals("female")) {
						sex = "女性";
					} else {
						sex = param[1];
					}
					map.put("sex", sex);
				} else if (param[0].equals("name")) {
					map.put("name", param[1]);
				} else if (param[0].equals("thoughts")) {
					map.put("thoughts", param[1]);
				}
			}
		}
		return map;
	}

	/**
	 * @throws IOException
	 */
	protected void headHtml() throws IOException {
		sendln("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\">");
		sendln("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"ja-JP\" xml:lang=\"ja-JP\">");
		sendln("<head>");
		sendln("<title>It works!</title>");
		sendln("<link rel='stylesheet' type='text/css' href='/style.css'>");
		sendln("<script type='text/javascript' src='/jquery-2.0.3.min.js'></script>");
		sendln("<script type='text/javascript' src='/form.js'></script>");
		sendln("</head>");
		sendln("<body>");
	}

	/**
	 * @throws IOException
	 */
	protected void closeHtml() throws IOException {
		sendln("</form>");
		sendln("</div>");
		sendln("</body></html>");
	}

	/**
	 * @throws IOException
	 */
	protected void sidebarHtml() throws IOException {
		sendln("<div id='sidebar'><br>");
		String[] forms = { "sex", "name", "thoughts" };
		String[] titles = { "性別", "名前", "感想" };
		for (int i = 0; i < forms.length; i++) {
			sendln("<form action='/" + forms[i] + "' method='POST'>");
			formDataHtml("");
			sendln("<button type='submit' id='submitbutton'>" + titles[i]
					+ "</button>");
			sendln("</form>");
		}
		sendln("</div>");
	}

	/**
	 * @throws IOException
	 */
	protected void homeHtml() throws IOException {
		sendln("<div>");
		sendln("<form action='/sex' method='GET'>");
		sendln("<p>アンケートにようこそ！</p>");
		sendln("<input type='submit' value='送信' />");
	}

	/**
	 * @throws IOException
	 */
	protected void sexHtml(String sex) throws IOException {
		sidebarHtml();
		sendln("<div id='main'>");
		sendln("<form action='/name' method='POST'>");
		formDataHtml("sex");
		sendln("<p>性別</p>");
		if (sex != null && !sex.equals("")) {
			if (sex.equals("男性")) {
				sendln("<dd><input type='radio' name='sex' value='male' checked='checked'>男性</input> <input type='radio' name='sex' value='female'>女性</input></dd>");
			} else {
				sendln("<dd><input type='radio' name='sex' value='male'>男性</input> <input type='radio' name='sex' value='female' checked='checked'>女性</input></dd>");
			}
		} else {
			sendln("<dd><input type='radio' name='sex' value='male'>男性</input> <input type='radio' name='sex' value='female'>女性</input></dd>");
		}
		sendln("<dt><input type='submit' value='送信' /></dt>");
	}

	/**
	 * @param sex
	 * @throws IOException
	 */
	protected void nameHtml(String name) throws IOException {
		sidebarHtml();
		sendln("<div id='main'>");
		sendln("<form action='/thoughts' method='POST'>");
		formDataHtml("name");
		sendln("<p>名前</p>");
		sendln("<dd><input type='text' name='name' value='" + name + "'></dd>");
		sendln("<dt><input type='submit' value='送信' /></dt>");
	}

	/**
	 * @param sex
	 * @param name
	 * @throws IOException
	 */
	protected void thoughtsHtml(String thoughts) throws IOException {
		sidebarHtml();
		sendln("<div id='main'>");
		sendln("<form action='/confirm' method='POST'>");
		formDataHtml("thoughts");
		sendln("<p>感想</p>");
		sendln("<dd><textarea name='thoughts' rows='4' cols='40'>" + thoughts
				+ "</textarea></dd>");
		sendln("<dt><input type='submit' value='送信' /></dt>");
	}

	/**
	 * @param sex
	 * @param name
	 * @param thoughts
	 * @throws IOException
	 */
	protected void confirmHtml(String sex, String name, String thoughts)
			throws IOException {
		sidebarHtml();
		sendln("<div id='main'>");
		sendln("<form action='/submit' method='POST'>");
		sendln("<p><b>確認</b></p>");
		sendln("<p>性別</p>");
		sendln("<dd><input type='text' name='sex' value='" + sex
				+ "' readonly /></dd>");
		sendln("<p>名前</p>");
		sendln("<dd><input type='text' name='name' value='" + name
				+ "' readonly /></dd>");
		sendln("<p>感想</p>");
		sendln("<dd><textarea rows='4' cols='40' name='thoughts' readonly>"
				+ thoughts + "</textarea></dd>");
		sendln("<dt><input type='submit' value='送信' /></dt>");
	}

	/**
	 * @throws IOException
	 */
	protected void submitHtml() throws IOException {
		sendln("<div>");
		sendln("<p>ありがとうございました。</p>");
	}

	/**
	 * @param currentPageUrl
	 * @throws IOException
	 */
	protected void formDataHtml(String currentPageUrl) throws IOException {
		List<String> dataList = new ArrayList<String>();
		if (param.get("sex") != null && !param.get("sex").equals("")
				&& !"sex".equals(currentPageUrl)) {
			dataList.add("sex");
		}
		if (param.get("name") != null && !param.get("name").equals("")
				&& !"name".equals(currentPageUrl)) {
			dataList.add("name");
		}
		if (param.get("thoughts") != null && !param.get("thoughts").equals("")
				&& !"thoughts".equals(currentPageUrl)) {
			dataList.add("thoughts");
		}
		for (String str : dataList) {
			sendln("<input type='hidden' name='" + str + "' value='"
					+ HandlerUtils.escapeString(param.get(str)) + "' />");
		}
	}

}
