
# TP2 - Portail d'inscription UdeM

TP2 du cours d'IFT1025 - Créer une application Java client-serveur qui permet aux étudiants de s'inscrire à des cours.


## Getting Started

### Prerequisites

- Java 17 LTS
```
https://www.java.com/en/download/help/download_options.html
```

### Installation

Le programme considère que la structure du directory contenant les .jar est comme suit:

```bash
.
|____client_fx.jar
|____client_simple.jar
|____data
| |____cours.txt
| |____inscription.txt
|____server.jar

```

### Usage

Il est important de lancer le serveur local avant de pouvoir faire des demandes à partir du client. À l'aide d'un invite de commande:

```bash
cd project-dir
java -jar server.jar
```

Similaire pour client_simple et client_fx:

```bash
cd project-dir
java -jar client_simple.jar
```

```bash
cd project-dir
java -jar client_fx.jar
```

Pour joindre de nouveaux cours à la liste, ajoutez-les dans le fichier data/cours.txt en suivant ce format:

No. de cours|Nom du cours|Session
---|---|---
ABC1234|Cours|Session
IFT1025|Programmation2|Hiver

Les espaces entre les champs doivent être des tabulations -> \t



    
## Démo

Démo - client_fx.jar

https://user-images.githubusercontent.com/43102929/232609556-411dd3af-9a50-4dc3-8274-c7cf6381f35b.mp4

Démo - Bonus

https://user-images.githubusercontent.com/43102929/232612957-9b8fd1cf-bb42-4a8c-a83f-0be9cebab4ee.mp4

## Bonus

### Server.java

Les changements suivants ont été apportés à Server.java dans le but de pouvoir recevoir et gérer plusieurs requêtes de Client simultanément:

- La classe extend java.lang.Thread afin de pouvoir créer une requête server unique à chaque nouveau Thread
```Java
public class Server extends Thread {
...
}
```

- Le constructeur requiert désormais le Socket du Client, ainsi que ses input et output streams
```Java
public Server(Socket client, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) throws IOException {
        this.client = client;
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
    }
```

- La méthode run() @Override celle de Thread, et n'a donc plus besoin de définir le client et ses différents streams, puisqu'ils se trouvent dans le constructeur de la classe

```Java
public void run() {
        try {
            listen();
            disconnect();
        }
        ...
}
```

### ClientRequest.java
- Une nouvelle classe gère les demandes des Clients, son constructeur ne requiert que le port utilisé

```Java
public ClientRequest(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
    }
```

- Une fois lancé ( run() ), une nouvelle requête Server sera créée sur un nouveau Thread, puis démarée avec Thread.start()

```Java
public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());

                Thread serverRequest = new Server(client, objectOutputStream, objectInputStream);
                serverRequest.start();
            }
            ...
        }
}
```


## Authors

[![Alexandre Carrier](https://avatars.githubusercontent.com/u/43102929?v=3&s=144)](https://github.com/alxcar)  | [![Léo Marotte]()](https://github.com/leomarotte)
---|---
[Alexandre Carrier](https://github.com/alxcar) |[Léo Marotte](https://github.com/leomarotte)
20246542|20247116



## Acknowledgements

 - [TP2 Server Template](https://github.com/OussamaSghaier/IFT1025-TP2-server/)

