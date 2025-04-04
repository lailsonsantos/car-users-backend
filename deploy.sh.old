#!/bin/bash
echo "🔐 Login no Heroku..."
heroku container:login

echo "📦 Buildando e subindo a imagem Docker para o Heroku..."
heroku container:push web --app seu-app

echo "🚀 Fazendo release da imagem..."
heroku container:release web --app seu-app

echo "🌐 Abrindo no navegador..."
heroku open --app seu-app
