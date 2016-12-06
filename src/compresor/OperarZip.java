package compresor;

import java.util.*;
import java.util.zip.*;
import java.io.*;

/**
 * @author Borjaro2000.tk
 */
public class OperarZip {

    /**
     * Dado un archivo .zip lo descomprime.    
     * @param rutaZip Ruta del archivo que queremos descomprimir en zip.
     * @param rutaDescompresion Ruta de la carpeta, ya creada (terminada en / o \), donde vamos a descomprimir el/los archivos.    
     */
    public static void leerZip(String rutaZip, String rutaDescompresion) {
        //Descomprimiendo
        //Indico que es un zip y lo asocio a su objeto
        ZipFile archivoZip = null;
        try {
            archivoZip = new ZipFile(new File(rutaZip));
        } catch (ZipException e) {
            System.err.println("Excepción Zip " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Excepción IO " + e.getMessage());
        }

        //Lista de archivos en el zip
        Enumeration archivosADescomprimir= archivoZip.entries();
        
        //Los recorro
        while (archivosADescomprimir.hasMoreElements()) {
            //Elemento que toca, lo saco y lo descomprimo
            ZipEntry tmpArchivoComprimido = (ZipEntry) archivosADescomprimir.nextElement();
            
            //Si es un directorio lo creo
            if (tmpArchivoComprimido.isDirectory()) {
                //Ruta donde descomprimo - Ruta de descompresión+nombre del archivo (ruta dentro del zip )
                File temp = new File(rutaDescompresion + tmpArchivoComprimido.getName());
                //Creo el directorio
                temp.mkdir();
                
                //Si es un archivo lo descomprimo
            } else {
                //Info del archivo
                /*System.out.println("Nombre: " + tmpArchivoComprimido.getName() + " Tamaño comprimido: "
                + tmpArchivoComprimido.getCompressedSize() + " Tamaño real: "
                + tmpArchivoComprimido.getSize() + " CRC: " + tmpArchivoComprimido.getCrc());*/
                
                try {
                    //Flujo de entrada - comprimido - Para cada archivo
                    InputStream flujoEntradaComprimido = archivoZip.getInputStream(tmpArchivoComprimido);
                    //Flujo de salida - Para cada archivo
                    FileOutputStream flujoSalida = new FileOutputStream(rutaDescompresion + tmpArchivoComprimido.getName());


                    //Voy guardando
                    //Leo de la entrada y escribo la salida
                    int leidos = 0;
                    byte[] buffer = new byte[256 * 1024];
                    while (leidos != -1) {
                        try {
                            leidos = flujoEntradaComprimido.read(buffer);
                            if (leidos != -1) {
                                flujoSalida.write(buffer, 0, leidos);
                            }
                        } catch (IOException ex) {
                        }

                    } //Fin while leerZip archivo
                    
                    //Cierro los flujos
                    flujoSalida.close();
                    flujoEntradaComprimido.close();
                } catch (IOException e) {
                    System.err.println("Excepción al descomprimir: " + e.getMessage());
                }
            }
        }//Fin while cada archivo
    }

    /**
     * Dado un archivo .zip y una lista de archivos comprime todos los archivos dados dentro de ese zip.    
     * @param rutaZip Ruta del archivo donde queremos comprimirZip en zip.
     * @param estructuraComprimir Listado de archivos a comprimirZip en valores relativos. Se debe hacer que el programa cambie el directorio de trabajo.
     * @param nivelCompresion 0=Compresión mínima 9=Compresión máxima
     */
    public static void comprimirZip(String rutaZip, List<String> estructuraComprimir, int nivelCompresion) {
        byte[] buffer = new byte[256 * 1024];
        //Comprobación del nivel en los valores correctos
        if (nivelCompresion < 0) {
            nivelCompresion = 0;
        } else if (nivelCompresion > 9) {
            nivelCompresion = 9;
        }

        try {
            //Archivo zip que crearemos
            ZipOutputStream archivoSalidaComprimido = new ZipOutputStream(new FileOutputStream(rutaZip, false));
            archivoSalidaComprimido.setLevel(nivelCompresion);

            //Cada una de las entradas en el archivo zip. Cada archivo que estará dentro
            ZipEntry temp;
            
            //Bucle que incluirá cada archivo en la lista dada
            for (int i = 0; i < estructuraComprimir.size(); i++) {                
                //Crear la entrada para cada nuevo archivo en el zip
                temp = new ZipEntry(estructuraComprimir.get(i));
                
                //Añadir cada entrada zip al archivo zip
                archivoSalidaComprimido.putNextEntry(temp);
                
                //Abrir el archivo a comprimirZip
                FileInputStream entradaTemp = new FileInputStream(estructuraComprimir.get(i));
                //Escribo cada archivo en el flujo que comprime al zip
                int leidos = 0;
                while (leidos != -1) {
                    try {
                        leidos = entradaTemp.read(buffer);
                        if (leidos != -1) {
                            archivoSalidaComprimido.write(buffer, 0, leidos);
                        }
                    } catch (IOException ex) {
                    }
                }
                //Se cierra el lector temporal de archivo antes de leerZip un nuevo archivo
                entradaTemp.close();
            }//Fin recorrer todos los archivos a comprimir
            
            //Se cierra el archivo zip creado
            archivoSalidaComprimido.close();
            
        } catch (FileNotFoundException ex) {
            System.err.println("Error al crear el zip." + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Error al comprimir en el zip." + ex.getMessage());
        }
    }

    /**
     * Cambia el directorio de trabajo del programa.    
     * @param nuevoDirectorio Nuevo directorio de trabajo.
     * @return  Anterior directorio de trabajo o null si no había.    
     */
    public static String cambiarDirectorioDeTrabajo(String nuevoDirectorio) {
        return System.setProperty("user.dir", nuevoDirectorio);
    }

    /**
     * Devuelve una lista de archivos dentro de un directorio y subdirectorios.    
     * @param ruta Directorio raíz.
     * @return  Listado de archivos.
     */
    public static List<String> recorrerDirectorio(String ruta) {
        List<String> temp = new ArrayList();

        //Para la ruta dada voy mirando si es directorio o un archivo
        //Si es un archivo lo meto en la lista
        //Si es un directorio llamo de nuevo a la función para recorrer los subdirectorios
        File direccion = new File(ruta);
        if (direccion.isFile()) {
            temp.add(direccion.getPath());
        } else if (direccion.isDirectory()) {
            //Si es un directorio saco una lista con lo que hay dentro y la recorro
            File[] listado = direccion.listFiles();
            List<String> temp2;
            for (int i = 0; i < listado.length; i++) {
                temp2 = recorrerDirectorio(listado[i].getPath());
                for (int j = 0; j < temp2.size(); j++) {
                    temp.add(temp2.get(j));
                }
            }
        }
        return temp;
    }
}
