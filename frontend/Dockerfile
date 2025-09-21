FROM node:20-alpine

WORKDIR /app

# Copy package files first
COPY package.json package-lock.json ./

# Install dependencies normally
RUN npm ci

# Copy rest of the code
COPY . .

# Expose Vite dev server
EXPOSE 5173

# Run dev server
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
