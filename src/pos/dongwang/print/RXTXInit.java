package pos.dongwang.print;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wenjing on 2017/5/19.
 */
public class RXTXInit {


    public synchronized static void loadLib()  {
        String os = System.getProperty("os.name").toLowerCase();
        String osName;
        String version = System.getProperty("sun.arch.data.model");
        String libFullName;
        String nativeTempDir;

        if (os.indexOf("win") != -1) {
            try {
                osName = "windows";
                libFullName = "rxtxSerial.dll";
                String libName = "rxtxSerial";
                // nativeTempDir = System.getProperty("user.dir") + File.separator + "printSDK" + File.separator + osName + File.separator + version + File.separator + libFullName;
                nativeTempDir = System.getProperty("java.io.tmpdir");
                InputStream in = null;
                BufferedInputStream reader = null;
                FileOutputStream writer = null;

                File extractedLibFile = new File(nativeTempDir + File.separator + libFullName);
                if (!extractedLibFile.exists()) {
                    try {
                        File file = new File(System.getProperty("user.dir") + File.separator + "printSDK" + File.separator + osName + File.separator + version + File.separator + libFullName);
                        in = new FileInputStream(file);
                        if (in == null)
                            in = RXTXInit.class.getResourceAsStream(libFullName);
                        RXTXInit.class.getResource(libFullName);
                        reader = new BufferedInputStream(in);
                        writer = new FileOutputStream(extractedLibFile);

                        byte[] buffer = new byte[1024];

                        while (reader.read(buffer) > 0) {
                            writer.write(buffer);
                            buffer = new byte[1024];
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (in != null)
                            in.close();
                        if (writer != null)
                            writer.close();
                    }
                }

              //  System.out.println(extractedLibFile.toString());
                System.setProperty("java.library.path", System.getProperty("java.library.path")
                        + ";" + nativeTempDir);
                //System.out.println("sssssssssssssss" + System.getProperty("java.library.path"));
                //System.load(extractedLibFile.toString());
                Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
                fieldSysPath.setAccessible(true);
                fieldSysPath.set(null, null);
                System.loadLibrary(libName);

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (os.indexOf("linux") != -1) {
            osName = "linux";
            libFullName = "librxtxSerial.so";
            nativeTempDir = System.getProperty("user.dir") + File.separator + "printSDK" + File.separator + osName + File.separator + version + File.separator + libFullName;
            System.load(nativeTempDir);
        } else {
            osName = "mac";
            libFullName = "librxtxSerial.jnilib";
            nativeTempDir = System.getProperty("user.dir") + File.separator + "printSDK" + File.separator + osName + File.separator + libFullName;
            System.load(nativeTempDir);

        }


    }


    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        System.out.println("当前时间：" + sdf.format(now));

        Date afterDate = new Date(now .getTime() + 3000);
        System.out.println(sdf.format(afterDate ));


    }
}
