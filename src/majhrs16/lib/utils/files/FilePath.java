package majhrs16.lib.utils.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePath {
    public static String getFileNameFromURL(String url) {
        int lastIndexOfSlash = url.lastIndexOf('/');
        if (lastIndexOfSlash != -1) {
            return url.substring(lastIndexOfSlash + 1);
        }
        return null;
    }
    
    public static void makedirs(String folderName) throws IOException {
	    Path folderPath = Paths.get(folderName);
        if (!Files.exists(folderPath)) {
            // Crea la carpeta en el sistema de archivos, incluyendo directorios padres si es necesario
            Files.createDirectories(folderPath);
        }
    }
}
