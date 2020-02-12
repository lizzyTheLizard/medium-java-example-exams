FROM node:latest
RUN mkdir /home/node/.npm-global
ENV PATH=/home/node/.npm-global/bin:$PATH
ENV NPM_CONFIG_PREFIX=/home/node/.npm-global
WORKDIR /app
RUN npm install -g @angular/cli
CMD ["ng","serve","--host", "0.0.0.0"]
