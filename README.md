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