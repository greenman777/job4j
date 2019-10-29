package ru.job4j.filedownloader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author John Ivanov (johnivo@mail.ru)
 * @since 29.10.2019
 */
public class HttpDownloader implements Runnable {

    private String url;
    private String target;
    private  int speedKbSec;

    public HttpDownloader(String url, String target, int speedKbSec) {
        this.url = url;
        this.target = target;
        this.speedKbSec = speedKbSec;
    }

    @Override
    public void run() {

        try {

            URL urlObj = new URL(url);
            HttpURLConnection httpCon = (HttpURLConnection) urlObj.openConnection();

            int responseCode = httpCon.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream inputStream = httpCon.getInputStream();
                BufferedInputStream reader = new BufferedInputStream(inputStream);

                BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(target));

                byte[] buffer = new byte[speedKbSec * 1024];
                int bytesRead = -1;
                long mark = System.nanoTime();

                while ((bytesRead = reader.read(buffer)) != -1) {

                    writer.write(buffer, 0, bytesRead);

                    long iterationTime = (System.nanoTime() - mark) / 1000L;
                    long pause = 1000L - iterationTime;
                    if (1000L - iterationTime > 0) {
                        try {
                            Thread.currentThread().sleep(pause);
                            System.out.println(String.format("pause %d ms", pause));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mark = System.nanoTime();

                }

                writer.close();
                reader.close();

                System.out.println("File saved.");

            } else {
                System.out.println("Server returned response code " + responseCode + ". Download failed.");
            }

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("An I/O error occurs: " + e.getMessage());
        }

    }

    public static void main(String[] args) throws IOException {

        //String url = "https://docs.oracle.comcom";
        String url = "https://docs.oracle.com/en/java/javase/13/core/java-core-libraries-developer-guide.pdf";

        int speed = 200;

        String target = System.getProperty("java.io.tmpdir")
                + System.getProperty("file.separator")
                + "test.pdf";
        //new File(target).createNewFile();

        HttpDownloader wget = new HttpDownloader(url, target, speed);
        new Thread(wget).start();
    }
}
