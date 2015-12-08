# Laboratorio1SD
Cache Service. Laboratorio 1 Sistemas distribuidos.

Instrucciones de ejecución.

- El proyecto debe ser abierto en el IDE NetBeans.
- Dentro de la carpeta del proyecto se encuentra el archivo "entrada.txt" el cual posee los datos de entrada.
  - La primera linea del archivo corresponde al numero de preguntas y respuestas que se quieren autogenerar.
  - La segunda linea corresponde al numero del tamaño del cache.
  - La tercera linea corresponde al numero de particiones del cache.
- Para la correcta ejecución se deje iniciar primero, dentro de NetBeans, el archivo "CachingService.java" y seguidamente el archivo "FrontService.java"
- Una vez realizado, se pueden comenzar a ingresar las consultas al FrontService.

- Considere que el sistema posee consultas con sus respectivas respuestas, en caso de que no estén en el cache. Las consultas siguen el estándar "consultaNº" y las respuestas "respuestaNº". Aparte cada cache estático de las particiones posee al menos una consulta con su respuesta en ella.
