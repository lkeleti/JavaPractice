# 1. lépés: Építés
FROM node:20 AS builder

WORKDIR /app

# package.json és package-lock.json bemásolása
COPY package*.json ./

# függőségek telepítése
RUN npm install

# a teljes frontend forrás bemásolása
COPY . .

# Angular build (termelési build, így kis méretű, optimalizált)
RUN npm run build -- --configuration production

# 2. lépés: Szolgálat
FROM nginx:alpine

# Angular build output átmásolása az Nginx webszerverbe
COPY --from=builder /app/dist/invotrax /usr/share/nginx/html

# Opcionálisan: egyedi nginx.conf, ha akarod szabályozni az átirányításokat (pl. SPA routing)
# COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
