package kr.co.woobi.tomorrow99.safewalk.library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageHelper {
    private final static String host = "http://210.107.245.192:400/";

    public interface Event {
        void ImageUploading(int id);

        void ImageUploadPercent(int id, float status);

        void ImageUploaded(int id, int code, String message);

        void ImageConveting(int id, Bitmap.CompressFormat convFormat);

        void ImageConveted(int id, Bitmap.CompressFormat convFormat);

        void Error(int id, Exception e);
    }

    Event eventHandler = null;

    protected HttpURLConnection generateHttpUrl(String path, WebMethod method, String fileName) throws Exception {
        URL url = new URL(host + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        conn.setRequestMethod(method.toString().toUpperCase());
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");
        conn.setRequestProperty("uploaded_file", fileName);

        return conn;
    }

    protected byte[] ImageConvert(int id, byte[] image, Bitmap.CompressFormat format) throws Exception {
        if (eventHandler != null) {
            eventHandler.ImageConveting(id, Bitmap.CompressFormat.PNG);
        }
        Bitmap b = BitmapFactory.decodeByteArray(image, 0, image.length);
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, blob);
        if (eventHandler != null) {
            eventHandler.ImageConveted(id, Bitmap.CompressFormat.PNG);
        }
        return blob.toByteArray();
    }

    public int sendPicture(byte[] image, int id) throws Exception {
        if (eventHandler != null) {
            eventHandler.ImageUploading(id);
        }

        String fileName = id + ".png";
        HttpURLConnection conn = generateHttpUrl("addImagePing.php", WebMethod.POST, fileName);

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(image);

        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
        dos.writeBytes(lineEnd);

        int bytesAvailable = fileInputStream.available();
        int maxBufferSize = 1 * 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            if (eventHandler != null) {
                eventHandler.ImageUploadPercent(id, 1 - (fileInputStream.available() / image.length));
            }
            dos.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        }

        dos.writeBytes(lineEnd);
        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        if (eventHandler != null) {
            eventHandler.ImageUploadPercent(id, 1);
        }

        int serverResponseCode = conn.getResponseCode();
        String serverResponseMessage = conn.getResponseMessage();
        if (eventHandler != null) {
            eventHandler.ImageUploaded(id, serverResponseCode, serverResponseMessage);
        }

        fileInputStream.close();
        dos.flush();
        dos.close();

        return serverResponseCode;
    }

    public void ImageUpdate(byte[] image, int id) {
        try {
            byte[] conv = ImageConvert(id, image, Bitmap.CompressFormat.PNG);
            sendPicture(conv, id);
        } catch (Exception e) {
            if (eventHandler != null) {
                eventHandler.Error(id, e);
            }
        }
    }
}