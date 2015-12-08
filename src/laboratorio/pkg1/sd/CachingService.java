package laboratorio.pkg1.sd;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static laboratorio.pkg1.sd.FrontService.socketClienteDesdeFrontServiceHaciaCachingService;
import static laboratorio.pkg1.sd.FrontService.socketServidorFrontServiceParaCachingService;

public class CachingService extends Thread{
    
    static ArrayList<String> consultas = new ArrayList<String>();
    static ArrayList<String> respuestas = new ArrayList<String>();
    
    public static String getEntry(String query) {
        for (int i = 0; i < consultas.size(); i++) {            
            if (consultas.get(i).equals(query)) {
                return respuestas.get(i);
            }
        }
        return null;
    }
    
    public static int hashParticionCache(String consulta, int numParticiones){
        int resParticion=0;
        for ( int i = 0; i < consulta.length(); ++i ) {
            char c = consulta.charAt( i );
            int j = (int) c;
            resParticion=resParticion+j;
        }
        return (resParticion/consulta.length())%numParticiones;
    }
    
    public static void socketClienteDesdeCachingServiceHaciaFrontService(String respuestaAFrontService) throws Exception{        
        //Socket para el cliente (host, puerto)
        Socket socketHaciaFrontService = new Socket("localhost", 5002);
        
        //Buffer para enviar el dato al server
        DataOutputStream haciaElFrontService = new DataOutputStream(socketHaciaFrontService.getOutputStream());
        
        haciaElFrontService.writeBytes(respuestaAFrontService + '\n');
        
        socketHaciaFrontService.close();  
    }
    
    public static void socketServidorCachingServiceParaFrontService(int numParticiones, CacheLRU[] particionesCache) throws Exception{    
        
        //Variables
        String desdeFrontService;
        String respuestaAFrontService="";    
        
        //Socket para el servidor en el puerto 5000
        ServerSocket socketDesdeFrontService = new ServerSocket(5001);
        
        while(true){
            //Socket listo para recibir 
            Socket connectionSocket = socketDesdeFrontService.accept();
            //Buffer para recibir desde el cliente
            BufferedReader inDesdeFrontService = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            //Buffer para enviar al cliente
            DataOutputStream haciaElFrontService = new DataOutputStream(connectionSocket.getOutputStream());
            
            //Recibimos el dato del cliente y lo mostramos en el server
            desdeFrontService =inDesdeFrontService.readLine();
            
            String[] tokens = desdeFrontService.split(" ");
            String metodoHTTP = tokens[0];
            String[] tokens_parametros = tokens[1].split("/");
            System.out.println("-----------------------------------------------------------------");
            System.out.println("(Caching Service) Recibí la consulta HTTP desde el Front Service:");           
            System.out.println("(Caching Service) Consulta: " + tokens_parametros[2]);
            System.out.println("(Caching Service) Metodo HTTP: " + metodoHTTP);
            System.out.println("(Caching Service) Resource: " + tokens_parametros[1]);
            System.out.println("-----------------------------------------------------------------");
            
            String consulta = tokens_parametros[2].replaceAll("-", " ");
            
            int particionAEnviar= hashParticionCache(consulta,numParticiones);
            System.out.println("Enviando la consulta '"+ consulta + "' a la particion numero " + particionAEnviar + " del cache");
            if(particionesCache[particionAEnviar].revisarCacheEstatico(consulta)!= null){
                System.out.println("(Particion "+ particionAEnviar + " Cache) HIT! en el cache estatico.");
                socketClienteDesdeCachingServiceHaciaFrontService(particionesCache[particionAEnviar].revisarCacheEstatico(consulta));
            }
            else{
                System.out.println("(Particion "+ particionAEnviar + " Cache) MISS! en el cache estatico");
                String resultadoCacheDinamico = particionesCache[particionAEnviar].revisarCacheDinamico(consulta);
                if (resultadoCacheDinamico == null) { // MISS
                    System.out.println("(Particion "+ particionAEnviar + " Cache) MISS! en el cache dinamico");                    
                    String respuesta = getEntry(consulta);
                    if(respuesta!=null){
                        System.out.println("(Particion "+ particionAEnviar + " Cache) Agregando consulta y respuesta al cache dinamico");
                    }
                    particionesCache[particionAEnviar].addEntryToCache(consulta, respuesta);
                    socketClienteDesdeCachingServiceHaciaFrontService("MISS!");
                }else{
                    System.out.println("(Particion "+ particionAEnviar + " Cache) HIT! en el cache dinamico");
                    socketClienteDesdeCachingServiceHaciaFrontService(resultadoCacheDinamico);
                }
            }
        }
    }
      
    public static void main(String args[]) throws Exception{
        File archivo = new File ("entrada.txt");
        FileReader fr = new FileReader (archivo);
        BufferedReader br = new BufferedReader(fr);
        String linea = br.readLine();
        int cantRespuestas= Integer.parseInt(linea);        
        linea = br.readLine();
        int tamCache=Integer.parseInt(linea);
        linea = br.readLine();
        int numParticiones=Integer.parseInt(linea);
        fr.close();
        if(numParticiones<tamCache){
            if(tamCache%numParticiones==0){
                System.out.println("Auto generando preguntas y respuestas...");
                for(int i=0; i<cantRespuestas; i++){
                    consultas.add(i, "consulta"+(i+1));
                    respuestas.add(i, "respuesta"+(i+1));
                }
                System.out.println("Preguntas y respuestas generadas."); 
                CacheLRU[] particionesCache = new CacheLRU[numParticiones];
                for(int i=0; i<numParticiones;i++){
                    particionesCache[i] = new CacheLRU(tamCache/numParticiones);
                    particionesCache[i].cacheEstatico.put(consultas.get(0), respuestas.get(0));
                }        
                socketServidorCachingServiceParaFrontService(numParticiones,particionesCache);
            }
            else{
                System.out.println("Los tamaños de cache y numero de particiones no son multiplos entre si. Ingrese valores correctos en el archivo de entrada");
            }         
        }
        else{
            System.out.println("El numero de particiones es menor al tamaño del cache. Ingrese valores correctos en el archivo de entrada");
        }
        
    }
    
}
