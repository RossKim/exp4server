package exp4server.sample;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

public class SampleRandom {

	/**
	 * 乱数生成器
	 */
	private static Random RANDOM;
	static {
		try {
			RANDOM = SecureRandom.getInstance("SHA1PRNG");
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
			RANDOM = new Random(new Date().getTime());
		}
	}

	/**
	 * 乱数文字列を生成する
	 */
	public static String generateRandomId() {
		final byte b[] = new byte[16];
		RANDOM.nextBytes(b);
		return bytesToHexString(b);
	}

	/**
	 * バイト列を16進文字列に変換する
	 * 
	 * @param bytes
	 *            入力バイト列
	 * @return
	 */
	public static String bytesToHexString(byte[] bytes) {
		final StringBuffer sb = new StringBuffer();
		for (final byte b : bytes) {
			final String s = Integer.toHexString(0xff & b);
			sb.append(s.length() == 1 ? "0" + s : s);
		}
		return sb.toString();
	}

	/**
	 * ランダムなキーを100個生成し，標準出力に出力する
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			System.out.println(generateRandomId());
		}
	}
}
