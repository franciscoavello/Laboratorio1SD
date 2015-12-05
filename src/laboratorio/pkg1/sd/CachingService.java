package laboratorio.pkg1.sd;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class CachingService {
    
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
    
    public static void socketServidorCachingServiceParaFrontService(int numParticiones) throws Exception{    
        
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
            System.out.println("(Caching Service) HTTP METHOD: " + metodoHTTP);
            System.out.println("(Caching Service) Resource: " + tokens_parametros[1]);
            System.out.println("-----------------------------------------------------------------");
            
            String consulta = tokens_parametros[2].replaceAll("-", " ");
            
            int particionAEnviar= hashParticionCache(consulta,numParticiones);
            System.out.println("Enviando la consulta '"+ consulta + "' a la particion numero " + particionAEnviar + " del cache");
            
            respuestaAFrontService="MISS";
            socketClienteDesdeCachingServiceHaciaFrontService(respuestaAFrontService);
        }
    }
    
    public static void main(String args[]) throws Exception{        
        int numParticiones=7;
        int tamCache=21;
        CacheLRU[] particionesCache = new CacheLRU[numParticiones];
        for(int i=0; i<3;i++){
            particionesCache[i]= new CacheLRU(tamCache/numParticiones);
        }        
        socketServidorCachingServiceParaFrontService(numParticiones);
    }
    
}
