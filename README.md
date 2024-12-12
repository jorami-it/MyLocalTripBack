# StarterJoramiAppJwt
## Remplacer StarterJwt par le nom du projet dans:
* (Arborescence de fichier) com.jorami.StarterJoramiAppJwt en com.jorami.projectName.
* (Arborescence de fichier) com.jorami.StarterJoramiAppJwt en com.jorami.ProjectNameApplication (idem dans le dossier test!).
* (pom.xml) dans les balises artifactId (l. 12) et name (l. 14).
* [StarterJoramiAppJwt] sur le dossier racine.

Enfin, File > Invalidate Caches...

## Docker
* Adapter les différents noms avec le nom du projet dans le fichier docker-compose.yaml
* Lancer Docker Desktop
* Cliquer sur la double flèche verte au niveau de la première ligne (services:) ou, dans le Terminal, écrire la commande ````docker-compose up````
* Vérifier que le container et les images soient bien créées sur Docker Desktop

## .env
* Adapter l'URL de la base de données avec ce qui est présent dans le docker-compose.

## Créer une base de données pour le projet
* Lancer pgAdmin4
* Clic droit sur Databases -> Create > Database
* Dans General, dans le champ Database, encoder le nom de la base de données (voir dans le fichier docker-compose.yml -> postgres -> POSTGRES_DB)
* Le nom de la base de données doit correspondre dans le fichier docker-compose.yml et dans le .env

## Tester le mailing
* Lancer Docker Desktop
* Lancer le container Docker (soit via l'application Docker Desktop, soit via le fichier docker-compose du projet)
* Sur le navigateur, accéder à l'url [localhost:1080](http://localhost:1080/#/) (voir port dans le fichier docker-compose -> mail-dev -> ports))
* Créer un role USER en base de données (manuellement ou dans le code)
* Lancer l'application
* Lancer Postman et tester l'url POST localhost:8088/api/v1/auth/register avec comme body (raw (JSON))

-> Dans Postman, cliquer sur Import et y glisser le fichier Jorami.postman_collection.json, présent à la racine de ce projet
```
{
    "firstname": "John",
    "lastname": "Doe",
    "email": "j.doe@jorami.be",
    "password": "MyP@22word"
}
```
    

## Contributeurs
- [Jérémi Dupont](https://github.com/jeremid)
- [Khaled Hammou](https://github.com/khaled2510)
- [Romain Chariot](https://github.com/NidalosMorphine)
