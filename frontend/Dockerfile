FROM node:20-alpine as build

WORKDIR /app

# Copy package files
COPY package*.json ./

# Use regular install instead of ci
RUN npm install

# Copy the rest of the code
COPY . .

# Set environment variable for production build
ARG REACT_APP_API_URL
ENV REACT_APP_API_URL=$REACT_APP_API_URL

# Build the app
RUN npm run build

# Start a new stage for serving
FROM node:20-alpine

WORKDIR /app

# Install serve
RUN npm install -g serve

# Copy build from previous stage
COPY --from=build /app/build ./build

EXPOSE 3000

CMD ["serve", "-s", "build", "-l", "3000"]