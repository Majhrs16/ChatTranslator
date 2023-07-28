package majhrs16.lib.shell.commandline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Executor {
    public static String execute(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
            processBuilder.redirectErrorStream(true); // Redirige la salida de error al mismo flujo de entrada

            Process process = processBuilder.start();

            // Leer la salida del proceso
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }

            // Esperar a que el proceso termine
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("El proceso ha finalizado con código de salida: " + exitCode);
            }

            return output.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
        	return null;
        }
    }
}