package laboratorio.pkg1.sd;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class CacheLRU {
    
    int tamaño;
    LinkedHashMap<String, String> cacheEstatico;
    LinkedHashMap<String, String> cacheDinamico;
    
    public CacheLRU(int size) {
        this.tamaño = size;
        this.cacheEstatico = new LinkedHashMap<>();
        this.cacheDinamico = new LinkedHashMap<>();
    }
 
}
