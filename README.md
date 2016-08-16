################################################################################
###################### Proyecto de  Sistemas Distribuidos ######################
################################################################################

Tema: Sistema distribuido para un conjunto de dispensador de alimentos para animales domestico.

La idea consiste en que en  un conjunto  de dispensadores de  alimentos, puedan notificarse entre 
ellos por paso de mensajes que  se ha  acabado ciertos alimentos (agua, comida) en el dispensador, 
para un animal,  debido que  son una  cantidad considerable  de  dispensadores la idea general es, 
usar un solo sistema para todos los dispensadores envíe y reciba notificaciones de mensajes entre 
ellos, y  notifique al encargado del grupo de dispensadores.

Entonces el  middleware  que  nos  servirá  para  implementar  el  dispensador de alimentos  será, 
Publish/subscribe: Apache  ActiveMQ, lo  elegimos  porque es  un  sistema de  mensajería de código 
abierto y  también por que se  puede  usar en  varios lenguajes de programación, y el lenguaje que 
vamos  a utilizar es JAVA con variante  de APache  Cameil, interface JMS(Java  Message Service), y  
como protocolo OpenWire. 

Metodología a utilizar

El sistema  distribuido  para el  conjunto de  dispensadores  de alimentos es  que se pueda asignar 
entre  los dispensadores a un “dispensador master”, que  distribuya la  carga de trabajo a los demás 
dispensadores,  “dispensadores  esclavo”,  quienes se encargaran de ejecutar  la carga de trabajo de 
manera distribuida,  sincronizada y que avisen al “dispensador  master” que han terminado su trabajo, 
de igual forma  que avisen al  “dispensador master”  enviando una notificación cuando se les acaba la 
ración de alimentos, para que de esta manera los dispensadores siempre puedan ofrecer la alimentación.

Pasos para una correcta instalación en el sistiema operativo Linux- Ubuntu 15.10, Linux - Ubuntu 16.04

1.- descargar apache activmq en el siguiente enlace:
    link: http://activemq.apache.org/activemq-5140-release.html

2.- Nos dirigimos a la carpeta de descarga para descomprimir el archivo.
    abrimos el terminal en está ubicación y escribimos el siguiente comando.
	"tar -zxvf apache-activemq-5.5.0-bin.tar.gz

3.- luego de eso podemos mover la carpeta descomprimida a otro lugar puede ser en la carpeta documentos, entonces abrimos y nos ubicamos dentro de la carpeta bin, y procedemos a darle permiso al archivo activemq. con el siguiente comando.
	chmod 755 activemq

4.- El siguiente paso es iniciarlo lo haremos con el siguiente comando.
	sudo sh activemq start 

5.- para comprobar la instalacion lo verficamos con el siguiente comando.
	nestat -an|grep 61616

6.- finalmente entramos al browser escribimos la siguiente dirección 
	localhost:8161/admin
    pedira usario y contraseña ambas son admin.

7.- luego de eso para agregar la libreria al proyecto, abrimos netbeans 
    y agregamos la libreria .jar .  
	 

	

