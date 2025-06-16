# Etapa 1: build do app
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copia os arquivos do projeto
COPY pom.xml .
COPY src ./src

# Compila o projeto e gera o .jar
RUN mvn clean package -DskipTests

# Etapa 2: imagem final para execução
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copia o .jar gerado
COPY --from=builder /app/target/validatejwt-0.0.1-SNAPSHOT.jar validatejwt.jar

# Expõe a porta 8080 (usada pelo ECS para health checks, por exemplo)
EXPOSE 8080

# Define o comando padrão para rodar o app
ENTRYPOINT ["java", "-jar", "validatejwt.jar", "--spring.profiles.active=prod"]