package compresor;

import java.io.File;
import java.util.List;

/**
 * @author www.borjaro2000.tk
 */
public class Compresor {
    //Main - Muestra como funciona la clase

    public static void main(String[] args) {
        //Descomprimir zip
        //Si se escribe por comandos la palabra descomprimir seguido del archivo zip y la ruta de salida
        if (args[0].equalsIgnoreCase("descomprimir")) {
            if (args.length > 0) {
                String archivoEntradaZip = args[1];
                File archivo = new File(archivoEntradaZip);
                //Si el archivo existe procedor a descomprimir
                if (archivo.exists()) {
                    String rutaSalida = null;
                    File salida;

                    if (args.length > 1) {
                        //Si la carpeta de salida no existe la creo
                        salida = new File(args[2]);
                        if (!salida.exists()) {
                            salida.mkdir();
                            rutaSalida = salida.getAbsolutePath() + System.getProperties().getProperty("file.separator");;
                        } else if (salida.exists()) {
                            //Si existe y en un directorio procedo a descomprimir
                            if (salida.isDirectory()) {
                                rutaSalida = salida.getAbsolutePath() + System.getProperties().getProperty("file.separator");
                            }
                        }
                    } else {
                        //Si no dan salida descomprimo en el directorio del archivo zip
                        rutaSalida = System.getProperties().getProperty("user.dir") + System.getProperties().getProperty("file.separator") + archivo.getName().substring(0, archivo.getName().lastIndexOf(".")) + System.getProperties().getProperty("file.separator");
                        salida = new File(rutaSalida);
                        salida.mkdir();
                    }

                    //Ejecutar función
                    OperarZip.leerZip(archivo.getAbsolutePath(), rutaSalida);
                }
            }
            //Si quiero comprimir
        } else {
            if (args.length >= 3) {
                String archivoSalidaZip = args[1];
                File archivoSalida = new File(archivoSalidaZip);

                //Ruta del directorio a comprimirZip
                File rutaAComprimir = new File(args[2]);
                //Compruebo que existe el directorio
                if (rutaAComprimir.exists()) {
                    //Nos ponemos a trabajar en el directorio dado
                    String anterior = OperarZip.cambiarDirectorioDeTrabajo(rutaAComprimir.getAbsolutePath());
                    //Si existe la recorremos sacando la lista de archivos a comprimir
                    List<String> listado = OperarZip.recorrerDirectorio(rutaAComprimir.getPath());
                    //Comprimo
                    OperarZip.comprimirZip(archivoSalida.getPath(), listado, 9);
                }
            }
        }
    }
}
