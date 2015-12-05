package laboratorio.pkg1.sd;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrontService extends Thread{
    
    String query;
    
    public static void socketServidorFrontServiceParaCachingService() throws Exception{    
        
        //Variables
        String desdeCachingService;        
        //Socket para el servidor en el puerto 5000
        ServerSocket socketDesdeCachingService = new ServerSocket(5002);
        
        //Socket listo para recibir 
        Socket connectionSocket = socketDesdeCachingService.accept();
        //Buffer para recibir desde el cliente
        BufferedReader inDesdeCachingService = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        //Buffer para enviar al cliente
            
        //Recibimos el dato del cliente y lo mostramos en el server
        desdeCachingService =inDesdeCachingService.readLine();
        System.out.println("Recibidos: " + desdeCachingService);
        socketDesdeCachingService.close();
    }
    
    public static void socketClienteDesdeFrontServiceHaciaCachingService(String query) throws Exception{        
        //Socket para el cliente (host, puerto)
        Socket socketHaciaCachingService = new Socket("localhost", 5001);
        
        //Buffer para enviar el dato al server
        DataOutputStream haciaCachingService = new DataOutputStream(socketHaciaCachingService.getOutputStream());
        
        haciaCachingService.writeBytes(query + '\n');
        
        socketHaciaCachingService.close();  
    }

    public static void socketClienteUsuarioaFrontService()throws Exception{
        //Variables
        String sentence;
        String fromServer;
        
        //Buffer para recibir desde el usuario
        BufferedReader entradaUsuario = new BufferedReader(new InputStreamReader(System.in));
        
        //Socket para el cliente (host, puerto)
        Socket clientSocket = new Socket("localhost", 5000);
        
        //Buffer para enviar el dato al server
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        
        //Buffer para recibir dato del servidor
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        //Leemos del cliente y lo mandamos al servidor
        sentence = entradaUsuario.readLine();
        outToServer.writeBytes(sentence + '\n');
        
        //Recibimos del servidor
        fromServer = inFromServer.readLine();
        System.out.println("Server response: " + fromServer);
        
        //Cerramos el socket
        clientSocket.close();
    }

    private FrontService(String query) {
        this.query = query;
    }
    
    @Override
    public void run(){        
        try {
            System.out.println("(Front Service) Soy el thread: " + getName() + ". Enviando la query '" + query + "' al Caching Service");
            String querySinEspacios = query.replaceAll(" ", "-");
            String httpMetodo = "GET /consulta/";
            String queryREST = new StringBuilder(httpMetodo).append(querySinEspacios).toString();
            socketClienteDesdeFrontServiceHaciaCachingService(queryREST);
            socketServidorFrontServiceParaCachingService();
        } catch (Exception ex) {
            Logger.getLogger(FrontService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws Exception {
        while(true){
          String query;  
          BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
          query = inFromUser.readLine();
          FrontService hilo = new FrontService(query);
          hilo.start();
        }
    }    
}
