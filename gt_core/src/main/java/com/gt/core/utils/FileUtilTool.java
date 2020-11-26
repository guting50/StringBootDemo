package com.gt.core.utils;

import com.alibaba.druid.util.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class FileUtilTool {

    public static final boolean windowsEnv = System.getProperty("os.name").toLowerCase().contains(
            "Windows".toLowerCase());
    public static final String fileRootHead = "/usr/local";//清除服务器图片时候使用
    public static final String fileRoot = "/usr/local/images/";

    public static String charset = "0123456789abcdefghijklmnopqrstuvwxyz";

    public static String head_img_file = "headImg";

    public static final String userHeadFileName = "headImg.jpg";

    public static void removeFile(String source) {
        try {
            File file = new File(source);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean writeToFile(String path, String output) {
        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            writer.print(output);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("fail to write to file " + path);
            return false;
        }
    }

    public static String normalizeFileName(String filename) {
        System.out.println("old file name:" + filename);

        filename = filename.replaceAll("\\s+", "");
        filename = filename.replace("(", "");
        filename = filename.replace(")", "");

        System.out.println("new file name:" + filename);
        return filename;
    }

    public static boolean ensureDirectory(String directory) {
        boolean ret = true;

        try {
            File f = new File(directory);
            if (!f.exists()) {
                f.mkdirs();

            } else if (f.isFile()) {
                System.out.println("file " + directory + " is already taken by some human error");
                f.delete();
                f.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Path path = Paths.get(directory);
                Files.createDirectories(path);
                System.out.println("successfully create the directory " + directory);
            } catch (IOException e1) {
                e1.printStackTrace();
                ret = false;
            }
        }

        return ret;
    }

    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static String readFile(String file) throws IOException {
        if (StringUtils.isEmpty(file)) {
            return null;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            reader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            if (reader != null) {
                reader.close();
            }

            e.printStackTrace();

            throw e;
        }
    }


    /**
     * full version to get the string from input stream
     *
     * @param is
     * @return string
     */
    public static String readFromInputStream(final InputStream is) {
        final char[] buffer = new char[1024];
        final StringBuilder out = new StringBuilder();
        Reader in = null;
        boolean hasError = false;
        try {
            in = new InputStreamReader(is);
            for (; ; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0) {
                    break;
                }

                out.append(buffer, 0, rsz);
            }
        } catch (Exception e) {
            e.printStackTrace();

            hasError = true;
        }

        if (in != null) {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return hasError ? null : out.toString();
    }


    /**
     * path the file name without path
     *
     * @param filenameIn -- full file name with path
     */
    public static String getFileName(String filenameIn) {
        if (filenameIn == null || filenameIn.length() == 0) {
            return null;
        }

        String filename = filenameIn;
        int index = filename.lastIndexOf('\\');
        if (index == -1) {
            index = filename.lastIndexOf('/');
        }

        if (index == -1) {
            index = filename.lastIndexOf(':');
        }

        if (index != -1) {
            filename = filename.substring(index + 1);
        }

        return filename;
    }

    public static String toBase64(String fileName) throws Exception {
        File f = new File(fileName);
        long size = f.length();
        if (size <= 0 || (size != (int) size))
            return null;

        FileInputStream fis = new FileInputStream(f);
        byte byteArray[] = new byte[(int) f.length()];
        fis.read(byteArray);

        String result = Base64.encodeBase64String(byteArray);

        fis.close();

        return result;
    }

    public static void removeFileOrDirectory(String files, String fileName) {
        System.out.println("fileName=" + fileName);
        File file = new File(fileName);
        try {
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                } else {
                    FileUtils.deleteDirectory(file);
                }
            }
            File file2 = new File(files);
            file2.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String ensureFileDirectory(String name) {
        try {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String location = ensureEndSeparator(fileRoot) + name;
            location = ensureEndSeparator(location);
            File file = new File(location);
            if (!file.exists() || !file.isDirectory()) {
                file.mkdirs();
            }

            location += date;

            file = new File(location);
            if (!file.exists() || !file.isDirectory()) {
                file.mkdirs();
            }

            location = ensureEndSeparator(location);
            Random rng = new Random();
            String path = ensureEndSeparator(location + generateString(rng, charset, 6));
            while (true) {
                file = new File(path);
                if (file.exists()) {
                    path = ensureEndSeparator(location + generateString(rng, charset, 6));
                } else {
                    file.mkdirs();
                    return path;
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String generateString(Random rng, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }

        return new String(text);
    }

    public static String saveToDisk(InputStream is1, String filePath, String userFileImageName) {
        String location = ensureEndSeparator(filePath) + userFileImageName;

        try {
            OutputStream out = new FileOutputStream(new File(location));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = is1.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            out.flush();

            out.close();

            return location;
        } catch (Exception e) {
            return null;
        }
    }

    public static String ensureEndSeparator(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }

        char separator = windowsEnv ? '\\' : '/';
        if (str.charAt(str.length() - 1) != separator) {
            str = str + separator;
        }

        return str;
    }

    public static void deleteImg(String url, String name) {
        if (!StringUtils.isEmpty(url)) {
            File file = new File(url);
            if (file.exists() && file.isDirectory()) {
                removeFileOrDirectory(url, url + name);
            }
        }
    }
}