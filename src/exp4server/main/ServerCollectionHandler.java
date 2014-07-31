package exp4server.main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;

import exp4server.frozen.Handler;
import exp4server.frozen.Request;
import exp4server.sample.Data;
import exp4server.sample.SampleRandom;
import exp4server.sample.SampleSerializer;

/**
 * Webクライアントと通信を行うクラス
 */
public class ServerCollectionHandler extends Handler {

	final private static String Filename = "session.bin";
	private String sessionKey;
	private boolean isCookieEnabled;

	/**
	 * @param socket
	 * @throws IOException
	 */
	public ServerCollectionHandler(Socket socket) throws IOException {
		super(socket);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see exp4server.frozen.Handler#perform(exp4server.frozen.Request)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void perform(Request req) throws IOException {
		// 実装してください
		Map<String, Data> dataMap = null;
		Object loadObject = SampleSerializer.load(Filename);
		if (loadObject != null && HashMap.class == loadObject.getClass()) {
			dataMap = (Map<String, Data>) loadObject;
		} else {
			dataMap = new HashMap<String, Data>();
		}

		// 標準出力にメソッド，ヘッダ，ボディを出力
		outputRawLog(req);

		String cookie = req.getHeaders().get("Cookie");
		if (cookie != null && cookie.split("=").length > 1) {
			sessionKey = cookie.split("=")[1];
		}

		URI uri = null;
		Data data = null;
		isCookieEnabled = true;
		try {
			uri = new URI(req.getRequestURI());
		} catch (URISyntaxException e) {
			System.out.println("ServerCollectionHandler.preform "
					+ e.getLocalizedMessage());
		}

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
		} else if (uri.getPath().equals("/sessionKey.json")) {
			sendln("HTTP/1.0 200 OK");
			sendln("Content-Type: application/json; charset=utf-8");
			sendln(""); // ヘッダの終り
			try {
				JSONObject obj = new JSONObject();
				sessionKey = SampleRandom.generateRandomId();
				obj.put("key", sessionKey);
				sendln(obj.toString());
				dataMap.put(sessionKey, new Data());
			} catch (Exception e) {
				System.out.println("ServerCollectionHanlder.perform "
						+ e.getLocalizedMessage());
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
			if (sessionKey == null || sessionKey.equals("")
					|| uri.getPath().equals("/")) {
				if (req.getBody() != null
						&& req.getBody().contains("sessionKey")) {
					String[] params = req.getBody().split("&");
					for (String param : params) {
						String[] query = param.split("=");
						if (query.length > 1 && query[0].equals("sessionKey")) {
							sessionKey = query[1];
							isCookieEnabled = false;
						}
					}
					data = dataMap.get(sessionKey);
				} else {
					sessionKey = SampleRandom.generateRandomId();
					data = new Data();
				}
			} else {
				data = dataMap.get(sessionKey);
			}
			data = splitParam(req, data);
			dataMap.put(sessionKey, data);

			// レスポンスを返す
			sendln("HTTP/1.0 200 OK");
			sendln("Content-Type: text/html; charset=utf-8");
			sendln("Content-Language: ja");
			sendln("Set-Cookie: key=" + sessionKey);
			sendln(""); // ヘッダの終り

			headHtml();
			if (uri != null) {
				if (uri.getPath().equals("/")) {
					homeHtml();
				} else if (uri.getPath().equals("/sex")) {
					sexHtml(data);
				} else if (uri.getPath().equals("/name")) {
					nameHtml(data);
				} else if (uri.getPath().equals("/thoughts")) {
					thoughtsHtml(data);
				} else if (uri.getPath().equals("/confirm")) {
					confirmHtml(data);
				} else if (uri.getPath().equals("/submit")) {
					System.out.println("性別：" + data.gender);
					if (data.name == null) {
						System.out.println("名前：");
					} else {
						System.out.println("名前：" + data.name);
					}
					if (data.description == null) {
						System.out.println("感想：");
					} else {
						System.out.println("感想：" + data.description);
					}
					submitHtml();
				}
			}
			closeHtml(req);
		}
		SampleSerializer.save(Filename, dataMap);
	}

	/**
	 * q
	 * 
	 * @param req
	 * @param data
	 * @return
	 */
	protected Data splitParam(Request req, Data data) {
		if (req.getBody() == null) {
			return data;
		}
		String[] params = req.getBody().split("&");
		for (String query : params) {
			if (query == null) {
				continue;
			}
			String[] param = query.split("=");
			if (param.length < 2) {
				if (param[0].equals("name")) {
					data.name = null;
				} else if (param[0].equals("thoughts")) {
					data.description = null;
				}
				continue;
			}
			try {
				param[1] = URLDecoder.decode(param[1], "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				System.out.println("ServerCollectionHandler.splitParam : "
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
					data.gender = sex;
				} else if (param[0].equals("name")) {
					data.name = param[1];
				} else if (param[0].equals("thoughts")) {
					data.description = param[1];
				}
			}
		}
		return data;
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
		sendln("<body onload='checkCookie();'>");
	}

	/**
	 * @throws IOException
	 */
	protected void closeHtml(Request req) throws IOException {
		if (isCookieEnabled) {
			sendln("<div id='key'></div>");
		} else {
			sendln("<div id='key'><input type='hidden' name='sessionKey' value='"
					+ sessionKey + "' /></div>");
		}
		sendln("</form>");
		sendln("</div>");
		sendln("</body></html>");
	}

	/**
	 * @throws IOException
	 */
	protected void homeHtml() throws IOException {
		sendln("<form action='/sex' method='POST'>");
		sendln("<p>アンケートにようこそ！</p>");
		sendln("<input type='submit' value='送信' />");
	}

	/**
	 * @throws IOException
	 */
	protected void sexHtml(Data data) throws IOException {
		sidebarHtml();
		sendln("<div id='main'>");
		sendln("<form action='/name' method='POST'>");
		sendln("<p>性別</p>");
		if (data.gender != null && !data.gender.equals("")) {
			if (data.gender.equals("男性")) {
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
	 * @throws IOException
	 */
	protected void nameHtml(Data data) throws IOException {
		sidebarHtml();
		sendln("<div id='main'>");
		sendln("<form action='/thoughts' method='POST'>");
		sendln("<p>名前</p>");
		send("<dd><input type='text' name='name' value='");
		if (data.name != null && !data.name.equals("")) {
			send(HandlerUtils.escapeString(data.name));
		}
		sendln("' /></dd>");
		sendln("<dt><input type='submit' value='送信' /></dt>");
	}

	/**
	 * @throws IOException
	 */
	protected void thoughtsHtml(Data data) throws IOException {
		sidebarHtml();
		sendln("<div id='main'>");
		sendln("<form action='/confirm' method='POST'>");
		sendln("<p>感想</p>");
		send("<dd><textarea name='thoughts' rows='4' cols='40'>");
		if (data.description != null && !data.description.equals("")) {
			send(HandlerUtils.escapeString(data.description));
		}
		sendln("</textarea></dd>");
		sendln("<dt><input type='submit' value='送信' /></dt>");
	}

	/**
	 * @param data
	 * @throws IOException
	 */
	protected void confirmHtml(Data data) throws IOException {
		sidebarHtml();
		sendln("<div id='main'>");
		String name = HandlerUtils.escapeString(data.name);
		String description = HandlerUtils.escapeString(data.description);
		if (name == null) {
			name = "";
		}
		if (description == null) {
			description = "";
		}
		sendln("<form action='/submit' method='POST'>");
		sendln("<p><b>確認</b></p>");
		sendln("<p>性別</p>");
		sendln("<dd>" + data.gender + "</dd>");
		sendln("<p>名前</p>");
		sendln("<dd><input type='text' value='" + name + "' /></dd>");
		sendln("<p>感想</p>");
		sendln("<dd><textarea rows='4' cols='40' readonly>" + description
				+ "</textarea></dd>");
		sendln("<dt><input type='submit' value='送信' /></dt>");
	}

	/**
	 * @throws IOException
	 */
	protected void submitHtml() throws IOException {
		sendln("<p>ありがとうございました。</p>");
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
			sendln("<button type='submit' id='submitbutton'>" + titles[i]
					+ "</button>");
			if (isCookieEnabled) {
				sendln("<div id='key'></div>");
			} else {
				sendln("<div id='key'><input type='hidden' name='sessionKey' value='"
						+ sessionKey + "' /></div>");
			}
			sendln("</form>");
		}
		sendln("</div>");
	}

}
