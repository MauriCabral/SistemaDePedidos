package org.example.kaos.manager;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Update {
    private static final String VERSION_URL = "https://raw.githubusercontent.com/MauriCabral/SistemaDePedidos/branch-1/src/main/resources/org/example/kaos/version.txt";
    private static final String VERSION_ACTUAL = "0.0.1";

    public static void main(String[] args) {
        if (isNuevaVersionDisponible()) {
            System.out.println("Hay una nueva versión disponible.");
            actualizarAplicacion();
        } else {
            System.out.println("Ya tienes la última versión.");
        }
    }

    public static boolean isNuevaVersionDisponible() {
        try {
            URL url = new URL(VERSION_URL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String versionRemota = reader.readLine().trim();
            reader.close();

            System.out.println("Versión remota: " + versionRemota);
            System.out.println("Versión actual: " + VERSION_ACTUAL);

            return compararVersiones(VERSION_ACTUAL, versionRemota);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean compararVersiones(String actual, String remota) {
        String[] vActual = actual.split("\\.");
        String[] vRemota = remota.split("\\.");

        for (int i = 0; i < Math.min(vActual.length, vRemota.length); i++) {
            int numActual = Integer.parseInt(vActual[i]);
            int numRemota = Integer.parseInt(vRemota[i]);
            if (numRemota > numActual) {
                return true;
            } else if (numRemota < numActual) {
                return false;
            }
        }
        return vRemota.length > vActual.length;
    }

    public static void actualizarAplicacion() {
        String urlNuevaVersion = "https://github.com/MauriCabral/SistemaDePedidos/releases/download/v0.0.2/mi-aplicacion-v0.0.2.exe";
        try {
            System.out.println("Descargando nueva versión...");
            URL url = new URL(urlNuevaVersion);
            InputStream inputStream = url.openStream();
            Files.copy(inputStream, Paths.get("mi-aplicacion-v0.0.2.exe"), StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
            System.out.println("Nueva versión descargada exitosamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}