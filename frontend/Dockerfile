# Bygg-steg
FROM docker.io/library/node:20-alpine AS build

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Körsteg (static file server)
FROM docker.io/library/nginx:stable-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
# Kör i förgrunden för att hålla containern vid liv.
CMD ["nginx", "-g", "daemon off;"]