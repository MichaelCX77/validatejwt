# Etapa 1: build do app
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copia os arquivos do projeto
COPY pom.xml .
COPY src ./src

# Compila o projeto e gera o .jar executável
RUN mvn clean package -DskipTests

# Etapa 2: imagem final para execução
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copia o .jar gerado para a imagem final
COPY --from=builder /app/target/validatejwt-0.0.1-SNAPSHOT.jar validatejwt.jar

# Expõe a porta usada pela aplicação
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "validatejwt.jar"]
