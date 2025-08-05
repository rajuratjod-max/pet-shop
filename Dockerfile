FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy all Java files and libraries
COPY backend/Main.java .
COPY backend/RestApiServer.java .
COPY backend/lib ./lib

# Compile Java files
RUN javac -cp "lib/*" Main.java RestApiServer.java

# Expose port
EXPOSE 8080

# Run the Java server
CMD ["java", "-cp", ".:lib/*", "RestApiServer"]
