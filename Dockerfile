FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy all files
COPY backend/ ./backend/
COPY backend/lib ./backend/lib/

# Compile Java files
RUN javac -cp "./backend/lib/*" backend/Main.java backend/RestApiServer.java

# Expose the port your server runs on
EXPOSE 8080

# Run the server
CMD ["java", "-cp", "./backend:./backend/lib/*", "backend.RestApiServer"]
