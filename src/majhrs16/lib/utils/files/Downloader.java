package majhrs16.lib.utils.files;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Downloader {
    public interface DownloadListener {
        void onBytesDownloaded(int bytesDownloaded);
    }

    private List<DownloadListener> listeners;

    public Downloader() {
        this.listeners = new ArrayList<>();
    }

    public void addDownloadListener(DownloadListener listener) {
        listeners.add(listener);
    }

    public void removeDownloadListener(DownloadListener listener) {
        listeners.remove(listener);
    }

    public void downloadFile(String fileUrl, String destinationPath) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(destinationPath)) {

            byte[] buffer = new byte[1024 * 16];
            int bytesRead;
            int totalBytesRead = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                notifyBytesDownloaded(totalBytesRead);
            }

        } finally {
            connection.disconnect();
        }
    }

    private void notifyBytesDownloaded(int bytesDownloaded) {
        for (DownloadListener listener : listeners) {
            listener.onBytesDownloaded(bytesDownloaded);
        }
    }
}
