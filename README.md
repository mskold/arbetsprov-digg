# Markus Skölds arbetsprov för DIGG

Det här är mitt arbetsprov för tjänsten som systemutvecklare.

## TLDR

Applikationen byggs och startas med kommandot:

```
docker-compose up --build
```

För att populera tjänsten med kunder kan man använda den tillhandahållna json-filen `customers.json` och POST:a till batch-endpointen.

```
curl -X POST -H "Content-type: application/json" -d@customers.json http://localhost:8080/customers/batch
```

### Länkadresser

| Tjänst            | URL                                |
|-------------------|------------------------------------|
| VUE3-Gränssnittet | http://localhost:3000              |
| Backend           | http://localhost:8080/customers    |
| Swagger           | http://localhost:8080/q/swagger-ui |
| OpenAPI           | http://localhost:8080/q/openapi    |
| Health            | http://localhost:8080/q/health     |


## Dokumentation

Jag har valt att separarera frontend och backend i olika kataloger och i sina respektive Docker-containers.

På så sätt får man en mikroservice-arkitektur där varje del kan vidareutvecklas och distribueras oberoende av den andra.
I en verklig situation hade jag också lagt de respektive tjänsterna i olika git-repon.

### Backend

Jag använde Quarkus för att skapa en backendtjänst som byggs med en Maven Wrapper.

Om man t.ex. vill köra testerna utan att bygga containern:
```
./mvnw clean test
```

Dockerfilen för backend innehåller två steg.

1. Ett bygg-steg som bygger applikationen och kör tester med Rest Assured
2. Ett kör-steg som startar upp applikationen och exponerar port 8080.

### Frontend

Frontend är byggd i Vue3. Jag har valt att paginera i frontend med 5 kunder i taget, eftersom mitt exempeldata enbart innehåller 17 kunder.  

Frontendapplikationen har även den har en Dockerfile som innehåller två steg.

1. Bygg-steg som bygger med NPM.
2. Kör-steget driftsätter applikationen i en nginx-container som konfigurerar en proxy mot backend för att undvika CORS-problematik.
