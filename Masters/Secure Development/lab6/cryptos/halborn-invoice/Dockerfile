FROM nikolaik/python-nodejs:latest

# Install deps + add Chrome Stable + purge all the things
RUN apt-get update && apt-get install -y \
 apt-transport-https \
 ca-certificates \
 curl \
 gnupg \
 --no-install-recommends \
 && curl -sSL https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
 && echo "deb https://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
 && apt-get update && apt-get install -y \
 google-chrome-stable \
 fontconfig \
 fonts-ipafont-gothic \
 fonts-wqy-zenhei \
 fonts-thai-tlwg \
 fonts-kacst \
 fonts-symbola \
 fonts-noto \
 fonts-freefont-ttf \
 libcap2-bin \
 --no-install-recommends \
 && apt-get purge --auto-remove -y curl gnupg \
 && rm -rf /var/lib/apt/lists/*

# # Add application dependencies
WORKDIR /app
COPY src/package.json .
RUN npm i


# Copy application
COPY src .
CMD [ "node", "app.js" ]
