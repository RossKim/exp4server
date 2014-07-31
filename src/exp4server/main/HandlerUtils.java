package exp4server.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HandlerUtils {
	/**
	 * @param str
	 * @return
	 */
	protected static String escapeString(String str) {
		if (str == null || str.equals("")) {
			return str;
		}
		String[] escapeStr = { "&amp;", "&lt;", "&gt;", "&quot;", "&#039;" };
		String escapeChangeStr = "&amp;";
		for (String escape : escapeStr) {
			if (str.contains(escape)) {
				str = str.replace(escape,
						escapeChangeStr.concat(escape.substring(1)));
			}
		}
		return str;
	}

	/**
	 * @param filename
	 * @return
	 */
	protected static List<String> loadCSSFile(String filename, String type) {
		List<String> cssList = new ArrayList<String>();

		// レスポンスを返す
		cssList.add("HTTP/1.0 200 OK");
		cssList.add("Content-Type: " + type + "; charset=utf-8");
		cssList.add(""); // ヘッダの終り

		File file = new File(filename);
		if (file != null && file.exists()) {
			InputStream is = null;
			Reader r = null;
			BufferedReader br = null;
			try {
				is = new FileInputStream(file);
				r = new InputStreamReader(is, "UTF-8");
				br = new BufferedReader(r);
				for (;;) {
					String text = br.readLine();
					if (text == null) {
						break;
					}
					cssList.add(text);
				}
			} catch (FileNotFoundException e) {
				System.out.println("HandlerUtils.loadCSSFile¥n"
						+ e.getLocalizedMessage());
			} catch (UnsupportedEncodingException e) {
				System.out.println("HandlerUtils.loadCSSFile¥n"
						+ e.getLocalizedMessage());
			} catch (IOException e) {
				System.out.println("HandlerUtils.loadCSSFile¥n"
						+ e.getLocalizedMessage());
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
					}
				}
				if (r != null) {
					try {
						r.close();
					} catch (IOException e) {
					}
				}
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return cssList;
	}
}