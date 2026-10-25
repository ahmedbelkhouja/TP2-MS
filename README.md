# TP2 REST — Service web « Calculatrice » avec JAX-RS et Tomcat

## Présentation

Ce projet implémente un service web REST exposant une calculatrice via **JAX-RS (Jakarta EE 10)**, déployé sur **Apache Tomcat 10.1**.

Le service comporte deux familles d'URI :

- **Calculs directs** (`/calcul/...`) : le client demande un résultat, rien n'est enregistré.
- **Historique des opérations** (`/operations/...`) : chaque opération enregistrée est une ressource identifiée par un `id`.

**URL de base :** `http://localhost:8080/RestWebApp_war_exploded/api`

---

## Prérequis

- JDK 17+
- Maven 3.9+
- Apache Tomcat 10.1.x (paquetages `jakarta.*` — **ne pas utiliser Tomcat 9 ou antérieur**)
- Postman / curl (`curl.exe` sous PowerShell)

> ⚠️ Sous PowerShell, `curl` est un alias de `Invoke-WebRequest`. Utilisez **`curl.exe`** ou passez par **Git Bash**.

---

## A.2 — Questions de conception

1. **Pourquoi les calculs directs utilisent-ils GET et non POST ?**  
   Parce qu'un calcul direct est une **lecture** : il ne modifie aucune ressource sur le serveur, il est idempotent et sans effet de bord. GET est donc le verbe adapté (sémantique REST : GET = lecture sûre).

2. **Comment s'appelle la partie après le `?` dans `/calcul/somme?a=2&b=3` ?**  
   C'est la **query string** (chaîne de requête). Les paramètres `a` et `b` sont les *paramètres de requête* (query parameters), lus par `@QueryParam`.

3. **Différence entre `/operations` et `/operations/3` ?**  
   `/operations` désigne la **collection** (toutes les opérations) ; `/operations/3` désigne une **ressource unique**, l'opération d'identifiant 3.

4. **Code HTTP pour `/operations/99` inexistant ?**  
   **404 Not Found** — la ressource demandée n'existe pas.

---

## Partie C — Mise en œuvre

**Avec le navigateur.**  
<img width="1865" height="184" alt="Navigateur" src="https://github.com/user-attachments/assets/c0712f82-4967-4acb-ae3e-5c8a09bc9eac" />

**Avec CURL**  
<img width="1295" height="56" alt="curl" src="https://github.com/user-attachments/assets/1c69da51-b131-41e6-8cbd-7e977f21393c" />

**Avec Postman**  
<img width="1161" height="627" alt="Postman GET" src="https://github.com/user-attachments/assets/b705842e-21e1-4120-bf82-f2170555bba4" />

**Question C.1 — POST au lieu de GET sur `/calcul/somme`**  
On obtient **405 Method Not Allowed**. Le verbe POST n'est pas déclaré pour cette méthode (`@GET` seulement) : JAX-RS ne trouve aucune méthode correspondant à `POST /calcul/somme` et répond 405 (Jersey renvoie parfois 405 avec un en-tête `Allow: GET`).

---

## Partie D — Compléments calculatrice

### D.1 — Soustraction et produit

**Soustraction** (`GET /calcul/soustraction?a=10&b=4` → `6`)  
<img width="1854" height="78" alt="Soustraction" src="https://github.com/user-attachments/assets/c1dbeec0-4e7b-4aa7-9640-0e21f2a92aef" />

**Produit** (`GET /calcul/produit?a=6&b=7` → `42`)  
<img width="1914" height="101" alt="Produit" src="https://github.com/user-attachments/assets/a0becbfc-24a2-44f4-9101-e926712322ce" />

### D.2 — Division et gestion d'erreur

1. **Diviseur ≠ 0** (`GET /calcul/division?a=7&b=2`)  
   → Statut **200 OK**, corps `3.5`  
   <img width="1331" height="192" alt="Division OK" src="https://github.com/user-attachments/assets/c61ec7f7-3688-4df7-9665-587726f6a7d0" />

2. **Diviseur = 0** (`GET /calcul/division?a=7&b=0`)  
   → Statut **400 Bad Request**, corps `Erreur : division par 0 impossible`  
   <img width="1331" height="221" alt="Division par 0" src="https://github.com/user-attachments/assets/0045e68e-9cb7-4260-8e59-6d84b802b8f5" />

### D.3 — Observations

1. **Paramètre `b` absent** (`GET /RestWebApp/api/calcul/somme?a=2`)  
   <img width="1272" height="193" alt="b absent" src="https://github.com/user-attachments/assets/0ee6d306-27e5-4055-9901-b8f3136ae875" />

2. **Paramètre `a` non entier** (`GET /calcul/somme?a=abc&b=3`)  
   <img width="1801" height="314" alt="a non entier" src="https://github.com/user-attachments/assets/39bd1a38-594b-4bcd-9ed4-aa795ab220ed" />

**Question D.1 — Quelle valeur JAX-RS donne-t-il à un paramètre entier absent ?**  
`GET /calcul/somme?a=2` (b absent) : JAX-RS donne **0** à un paramètre entier absent (valeur par défaut du type primitif `int`). Le résultat renvoyé est donc `2 + 0 = 2`.

**Question D.2 — Quel statut obtenez-vous lorsque le paramètre n'est pas un entier ? La méthode `somme` a-t-elle été exécutée ?**  
`GET /calcul/somme?a=abc&b=3` : JAX-RS renvoie **404 Not Found** (échec de conversion en `int` du paramètre `a`). La méthode `somme` **n'est pas exécutée** : l'échec de conversion se produit avant l'appel.

### D.4 — Paramètres dans le chemin : `@PathParam`

`GET /calcul/produit/6/7` → `42`  
<img width="1282" height="180" alt="@PathParam" src="https://github.com/user-attachments/assets/3a1421cd-e910-4cbc-832b-a63640643d18" />

**Question D.3 — `@QueryParam` vs `@PathParam`**  
`@QueryParam` lit un paramètre dans la **query string** (`?a=2`) ; `@PathParam` lit un segment du **chemin** de l'URI (`/produit/6/7`). Pour lire l'identifiant dans `/operations/{id}`, on utilise **`@PathParam("id")`**.

---

## Partie E — JSON

### E.2 — Méthode qui produit du JSON

`GET /calcul/somme/detail?a=2&b=3`  
<img width="1378" height="185" alt="JSON detail" src="https://github.com/user-attachments/assets/4694c895-18f6-4439-b7ae-bd322eaf2bfd" />

**Question E.1 — Qui a produit le JSON ?**  
C'est **Jersey** (`jersey-media-json-binding`, implémentation de JSON-B) qui a effectué la sérialisation automatique de l'objet `Resultat` en JSON, grâce à l'annotation `@Produces(MediaType.APPLICATION_JSON)`. Les noms `a`, `b`, `operation`, `resultat` proviennent des **noms des propriétés Java** (getters/setters) de la classe `Resultat` — JSON-B les déduit par introspection (convention JavaBeans : `getA()` → `"a"`).

### E.3 — L'en-tête `Accept`

1. **`Accept: application/json`** → **200 OK**  
   <img width="1695" height="200" alt="Accept JSON" src="https://github.com/user-attachments/assets/927d7d31-abca-43b4-85d0-2b05f28370cb" />

2. **`Accept: application/xml`** → **406 Not Acceptable**  
   <img width="1804" height="338" alt="Accept XML" src="https://github.com/user-attachments/assets/661ce5a6-377e-441c-9ae9-8580e5a3030d" />

**Question E.2 — Pourquoi la seconde requête est-elle refusée ?**  
La méthode déclare `@Produces(MediaType.APPLICATION_JSON)` : elle ne peut produire que du JSON. Le client demandant `application/xml`, aucune représentation compatible n'existe → **406 Not Acceptable**. (Le JSON-B de Jersey ne fournit pas de provider XML.)

---

## Partie F — La ressource « operations »

### F.2 — Classe `OperationRest` : créer et lire

<img width="1566" height="255" alt="OperationRest code" src="https://github.com/user-attachments/assets/dda49fdb-d240-4940-adba-592616d91467" />

### F.3 — Tests de création et lecture

**Création avec Postman**

<img width="1159" height="847" alt="POST 1" src="https://github.com/user-attachments/assets/27e152b2-5abc-4d89-88d8-a6ab65157118" />

<img width="815" height="754" alt="POST 2" src="https://github.com/user-attachments/assets/6e7a6879-07dd-4814-99d7-ce457339610a" />

<img width="825" height="853" alt="GET all" src="https://github.com/user-attachments/assets/a05c2718-99a1-413f-b12a-653e4f80104e" />

<img width="819" height="553" alt="GET id" src="https://github.com/user-attachments/assets/78a4e11b-8e04-4c26-a40f-e73592edede8" />

<img width="812" height="657" alt="GET 99 → 404" src="https://github.com/user-attachments/assets/9d38b7d5-4aef-443f-8a86-8d516a4b03b1" />

<img width="813" height="686" alt="POST division par 0 → 400" src="https://github.com/user-attachments/assets/4f07f7bc-4518-402d-a5bf-cf569b195d01" />

### F.4 — Modifier une opération : PUT

**PUT** `/operations/1` `{"operateur":"produit","a":8,"b":2}` → **200**, `resultat=16.0`  
<img width="840" height="749" alt="PUT" src="https://github.com/user-attachments/assets/91c95dc6-1230-4fdc-ac69-a66fa5c27169" />

### F.5 — Supprimer une opération : DELETE

**DELETE** `/operations/1` → **204 No Content**  
<img width="1337" height="80" alt="DELETE 204" src="https://github.com/user-attachments/assets/73fe5a5f-7931-45e0-9045-d77375085140" />

**GET après DELETE** → **404**  
<img width="1803" height="304" alt="GET after delete" src="https://github.com/user-attachments/assets/f8833964-ca3d-4332-954f-6bdc7ddacd87" />

**DELETE une seconde fois** → **404**  
<img width="1795" height="302" alt="DELETE 404" src="https://github.com/user-attachments/assets/088906bb-a542-4e7e-8fdc-57614687900d" />

---

## Questions de synthèse

### 1. Tableau A.1 enrichi des codes de statut HTTP

**Famille 1 — calculs directs**

| Verbe | URI | Rôle | Codes de statut possibles |
|---|---|---|---|
| GET | `/calcul/somme?a=&b=` | Retourne `a + b` | **200 OK** ; **404** si `a` ou `b` n'est pas un entier |
| GET | `/calcul/soustraction?a=&b=` | Retourne `a - b` | **200 OK** ; **404** si conversion impossible |
| GET | `/calcul/produit?a=&b=` | Retourne `a × b` | **200 OK** ; **404** si conversion impossible |
| GET | `/calcul/division?a=&b=` | Retourne `a ÷ b` | **200 OK** ; **400 Bad Request** si `b = 0` ; **404** si conversion impossible |
| GET | `/calcul/somme/detail?a=&b=` | Retourne un objet `Resultat` en JSON | **200 OK** ; **406 Not Acceptable** si `Accept` incompatible |
| GET | `/calcul/produit/{a}/{b}` | Produit via `@PathParam` | **200 OK** ; **404** si segment non entier |

**Famille 2 — historique des opérations**

| Verbe | URI | Rôle | Codes de statut possibles |
|---|---|---|---|
| POST | `/operations` | Crée une opération (calcul + enregistrement) | **201 Created** ; **400 Bad Request** si opérateur inconnu ou division par 0 ; **415** si `Content-Type` incorrect |
| GET | `/operations` | Retourne toutes les opérations | **200 OK** |
| GET | `/operations/{id}` | Retourne l'opération `id` | **200 OK** ; **404 Not Found** si `id` inexistant |
| PUT | `/operations/{id}` | Modifie l'opération `id` | **200 OK** ; **400 Bad Request** si calcul impossible ; **404 Not Found** si `id` inexistant |
| DELETE | `/operations/{id}` | Supprime l'opération `id` | **204 No Content** ; **404 Not Found** si `id` inexistant |

### 2. Annotations JAX-RS utilisées dans ce TP et leur rôle

| Annotation | Rôle en une phrase |
|---|---|
| `@ApplicationPath("/api")` | Fixe le préfixe d'URI commun à toutes les ressources de l'application. |
| `@Path("...")` | Définit l'URI d'une classe ressource ou d'une méthode (segment de chemin, éventuellement avec `{param}`). |
| `@GET` | Associe une méthode au verbe HTTP GET (lecture). |
| `@POST` | Associe une méthode au verbe HTTP POST (création). |
| `@PUT` | Associe une méthode au verbe HTTP PUT (remplacement / modification). |
| `@DELETE` | Associe une méthode au verbe HTTP DELETE (suppression). |
| `@Produces(MediaType.APPLICATION_JSON)` | Annonce le(s) type(s) MIME produit(s) par la méthode (ici JSON). |
| `@Consumes(MediaType.APPLICATION_JSON)` | Annonce le(s) type(s) MIME accepté(s) dans le corps de la requête (ici JSON). |
| `@QueryParam("a")` | Lit un paramètre de la query string (`?a=2`) et le convertit dans le type Java attendu. |
| `@PathParam("id")` | Lit un segment du chemin (`/operations/{id}`) et le convertit dans le type Java attendu. |
| `@Context` | Injecte un objet du contexte JAX-RS (ici `UriInfo` pour construire l'URI de la ressource créée). |

### 3. Différence entre JAX-RS et Jersey

**JAX-RS** est la **spécification** (l'API standard Jakarta EE) : elle définit les annotations (`@Path`, `@GET`, …), les interfaces (`Response`, `UriInfo`, …) et les règles de fonctionnement d'un service REST en Java.  
**Jersey** est une **implémentation** concrète de cette spécification : c'est le moteur qui exécute réellement le service (routage, sérialisation JSON, gestion des statuts, etc.).  
Autres implémentations possibles : RESTEasy, Apache CXF. On peut changer d'implémentation sans modifier le code annoté JAX-RS.

### 4. Rôle de Tomcat / pourquoi un `.war` et non un `.jar`

**Tomcat** est un **conteneur de servlets** : il écoute sur le port 8080, reçoit les requêtes HTTP, les transmet à Jersey via le servlet `ServletContainer`, gère le cycle de vie de l'application web (déploiement, démarrage, arrêt) et renvoie les réponses HTTP au client.

On produit un **`.war`** (Web ARchive) et non un `.jar` exécutable parce que le projet est une **application web** destinée à être **déployée dans un serveur d'applications existant** (Tomcat), et non un programme autonome. Un `.war` contient :
- les classes compilées,
- les dépendances (Jersey, JSON-B…),
- les ressources web (`WEB-INF/`, `META-INF/`).

Tomcat fournit l'infrastructure HTTP et le conteneur ; l'application n'a donc pas à embarquer un serveur. Un `.jar` exécutable, au contraire, embarquerait un serveur (ex. Spring Boot avec Tomcat embarqué) — ce n'est pas le modèle demandé ici.

### 5. Pourquoi conserver la liste des opérations ne contredit pas le principe *stateless*

Le principe *stateless* impose que le **serveur ne mémorise aucun état de session propre à un client** entre deux requêtes : chaque requête doit être **auto-suffisante** et contenir toutes les informations nécessaires (identifiant, corps JSON, etc.). Le serveur ne doit pas se souvenir « qui est le client » ni « où il en était ».

Or la liste des opérations est une **ressource métier partagée et persistante** (un historique global visible par tous), et non un état de session lié à un client particulier. Chaque requête est traitée indépendamment :
- `POST /operations` crée une ressource à partir du corps JSON fourni ;
- `GET /operations/{id}` identifie la ressource par son `id` dans l'URI ;
- `PUT` / `DELETE` ciblent explicitement la ressource par son `id`.

Aucune requête ne dépend d'une requête précédente du même client pour être interprétée. Conserver des **données métier** n'est donc pas contradictoire avec le *stateless* : c'est simplement un **état de ressource**, pas un **état de session**.

### 6. Effet de la répétition : POST vs PUT

**Deux `POST /operations` identiques :**
- Chaque appel **crée une nouvelle ressource** avec un **nouvel `id`** (le compteur `COMPTEUR` est incrémenté à chaque appel).
- Résultat : **deux opérations distinctes** dans l'historique.
- POST n'est **pas idempotent** : `POST × 2` ≠ `POST × 1`.

**Deux `PUT /operations/1` identiques :**
- Chaque appel **remplace** la ressource d'identifiant 1 par le même contenu.
- Après le premier appel, l'opération 1 a le nouveau contenu ; après le second, elle a **exactement le même contenu**.
- Résultat : l'état final est **identique** à celui obtenu après un seul appel.
- PUT est **idempotent** : `PUT × 2` = `PUT × 1`.

**Conclusion :** la répétition d'un POST crée des doublons (effet cumulatif), tandis que la répétition d'un PUT est sans effet supplémentaire (effet stable). C'est exactement la différence sémantique entre les deux verbes en REST.
