# Use a minimal base image
FROM node:20-alpine 

# Set working directory
WORKDIR /app

# Copy package files first to leverage Docker caching
COPY package.json package-lock.json ./

# Install dependencies in a clean environment
RUN npm ci --omit=dev

# Copy application files separately (after installing dependencies)
COPY . .

# Use a non-root user for security
RUN addgroup -S app && adduser -S app -G app && chown -R app:app .
USER app

# Expose the application port
EXPOSE 3000

# Define the startup command
CMD ["npm", "run", "start"]
