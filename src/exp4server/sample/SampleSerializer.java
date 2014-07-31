package exp4server.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * シリアライザのサンプル
 */
public class SampleSerializer {

    /**
     * 入力オブジェクトをファイルにシリアライズして保存する
     * @param filename 保存ファイル名
     * @param object 保存すべきオブジェクト
     */
    public static void save(String filename, Object object) {
        try {
            final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
            oos.writeObject(object);
            oos.close();
        }
        catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ファイルにシリアライズされたオブジェクトを復元する
     * @param filename ファイル名
     * @return オブジェクト
     */
    public static Object load(String filename) {
        try {
            final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
            final Object result = ois.readObject();
            ois.close();
            return result;
        }
        catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
        catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * オブジェクトをシリアライズし，ファイルに保存するサンプルのmain
     */
    public static void main(String[] args) {
        final Data d1 = new Data();
        d1.gender = "male";
        d1.name = "hayashi";
        d1.description = "Shinpei Hayashi";

        System.out.println(d1);
        SampleSerializer.save("test.bin", d1);

        final Data d2 = (Data) SampleSerializer.load("test.bin");
        System.out.println(d2);

        // ファイルを削除
        System.out.println(new File("test.bin").delete());
    }
}
