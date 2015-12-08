package laboratorio.pkg1.sd;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static laboratorio.pkg1.sd.CachingService.hashParticionCache;
import static laboratorio.pkg1.sd.CachingService.socketClienteDesdeCachingServiceHaciaFrontService;

public class CacheLRU extends Thread{
    
    int tamaño;
    LinkedHashMap<String, String> cacheEstatico;
    LinkedHashMap<String, String> cacheDinamico;
    
    public CacheLRU(int size) {
        this.tamaño = size;
        this.cacheEstatico = new LinkedHashMap<>();
        this.cacheDinamico = new LinkedHashMap<>();
    }
    
    public String revisarCacheEstatico(String query) {
        String result = cacheEstatico.get(query);
        return result;
    }
    
    public String revisarCacheDinamico(String query) {
        String result = cacheDinamico.get(query);
        if(result != null) {
            cacheDinamico.remove(query);
            cacheDinamico.put(query, result);
        }
        return result;
    }
    
    public void addEntryToCache(String query, String answer) {
        if (cacheDinamico.containsKey(query)) { // HIT
            // Bring to front
            cacheDinamico.remove(query);
            cacheDinamico.put(query, answer);
        } else { // MISS
            if(cacheDinamico.size() == this.tamaño) {
                String first_element = cacheDinamico.entrySet().iterator().next().getKey();
                System.out.println("Removiendo: '" + first_element + "'");
                cacheDinamico.remove(first_element);
            }
            cacheDinamico.put(query, answer);
        }
    }
    
     @Override
    public void run(){        
        try {
            System.out.println("(Cache)("+ getName() +") Soy una particion del cache");
        } catch (Exception ex) {
            Logger.getLogger(CacheLRU.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
