**A.2 — Questions de conception**
1. **Pourquoi les calculs directs utilisent-ils GET et non POST ?**  
   Parce qu'un calcul direct est une **lecture** : il ne modifie aucune ressource sur le serveur, il est idempotent et sans effet de bord. GET est donc le verbe adapté (sémantique REST : GET = lecture sûre).
   2. **Comment s'appelle la partie après le `?` dans `/calcul/somme?a=2&b=3` ?**  
   C'est la **query string** (chaîne de requête). Les paramètres `a` et `b` sont les *paramètres de requête* (query parameters), lus par `@QueryParam`.

3. **Différence entre `/operations` et `/operations/3` ?**  
   `/operations` désigne la **collection** (toutes les opérations) ; `/operations/3` désigne une **ressource unique**, l'opération d'identifiant 3.

4. **Code HTTP pour `/operations/99` inexistant ?**  
   **404 Not Found** — la ressource demandée n'existe pas.
**C.7 Tester le service**
