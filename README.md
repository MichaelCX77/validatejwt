# validatejwt

Projeto para validação de tokens JWT

---

## Índice

- [Introdução](#introdução)
- [Desafio](#desafio)
- [Documentação técnica](#documentação-técnica)
- [Relatório de Cobertura](#relatório-de-cobertura)
- [Estrutura Hierárquica do Projeto](#estrutura-hierárquica-do-projeto)
- [Descrição das Bibliotecas](#descrição-das-bibliotecas)
- [Configuração e Execução](#configuração-e-execução)
- [Features Disponíveis](#features-disponíveis)
- [Exemplos de Logs da Aplicação](#exemplos-de-logs-da-aplicação)
- [Infraestrutura como Código (Terraform)](#infraestrutura-como-código-terraform)
- [Integração e Automação via GitHub Actions](#integração-e-automação-via-github-actions)
- [Informações Adicionais](#informações-adicionais)
- [Referências e Fontes Externas](#referências-e-fontes-externas)

---

## Introdução

O `validatejwt` é um serviço de API REST construído em Spring Boot, projetado para validar tokens JWT (JSON Web Tokens) conforme regras estritas de formato, claims e negócios. Ele garante que os tokens recebidos estejam devidamente estruturados, possuam claims obrigatórios e válidos, e rejeita tokens malformados, com claims inválidas ou valores fora do padrão estabelecido.

---

## Desafio

Este projeto foi desenvolvido como parte de um desafio técnico proposto em:  
[https://github.com/99h58f2qe/backend-challenge](https://github.com/99h58f2qe/backend-challenge)

---

## Documentação técnica

A documentação completa da API está disponível em:  
[https://michaelcx77.github.io/validatejwt/](https://michaelcx77.github.io/validatejwt/)

---

## Relatório de Cobertura

![Jacoco Coverage Report](assets/images/jacoco_coverage_report.jpg)

---

## Estrutura Hierárquica do Projeto

```
/validatejwt
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.api.validatejwt
│   │   │       ├── v1.config           # Configurações gerais e beans customizados
│   │   │       ├── v1.controller       # Controllers REST (ex: JwtController)
│   │   │       ├── v1.enums            # Enumerações (ex: EnumRole)
│   │   │       ├── v1.exception        # Classes customizadas de exceção
│   │   │       ├── v1.model            # Models/DTOs usados na API (ex: Jwt, JwtDTO, Claim)
│   │   │       ├── v1.service          # Serviços de negócio (ex: JwtService)
│   │   │       └── v1.util             # Utilitários de apoio (ex: ReflectionUtils)
│   │   └── resources
│   │       ├── application.properties  # Configurações da aplicação Spring Boot
│   │       └── logback-spring.xml      # Configuração de logging estruturado
│   └── test
│       └── java                        # Testes automatizados
├── assets
│   └── images                          # Imagens utilizadas na documentação
├── docs
│   ├── output_log_examples             # Exemplos reais de logs gerados pela aplicação
│   └── collections
│       └── output_log_examplesInsomnia_*.yaml  # Coleções de requests para Insomnia
├── infra
│   ├── ecs_app
│   │   ├── backend.tf
│   │   ├── main.tf
│   │   └── variables.tf
│   └── ecs_infra
│       ├── backend.tf
│       ├── main.tf
│       ├── outputs.tf
│       └── variables.tf
├── pom.xml                             # Arquivo de configuração do Maven
└── README.md                           # (Este arquivo)
```

### Descrição dos principais diretórios e arquivos

- **com.api.validatejwt**: Pacote base do projeto.
- **v1.config**: Beans, configurações e adaptações para o Spring.
- **v1.controller**: Controllers REST. Expõe os endpoints da API.
- **v1.enums**: Enumerações usadas nas validações (ex: roles permitidas).
- **v1.exception**: Exceções customizadas para tratamento de erros.
- **v1.model**: Modelos de dados do domínio e DTOs de entrada/saída.
- **v1.service**: Lógica central de validação do JWT e regras de negócio.
- **v1.util**: Utilitários auxiliares para reflexão, manipulação, etc.
- **resources/application.properties**: Configura propriedades da aplicação, como porta, logs, etc.
- **resources/logback-spring.xml**: Configuração de logging estruturado (Logback e Logstash).
- **docs/output_log_examples**: Exemplos reais de logs de sucesso/erro.
- **docs/Insomnia_*.yaml**: Exemplos prontos para testar a API com Insomnia.
- **infra/**: Infraestrutura como código (Terraform), pronta para deploy na AWS via ECS Fargate.

---

## Descrição das Bibliotecas

Principais dependências utilizadas:

- **spring-boot-starter:** Inicialização rápida do Spring Boot, gerenciamento de contexto e recursos básicos do framework.
- **spring-boot-starter-web:** Permite criação de APIs REST usando Spring MVC.
- **spring-boot-starter-actuator:** Exposição de métricas e endpoints de monitoramento de saúde da aplicação.
- **spring-boot-starter-validation:** Suporte a validações de dados no padrão Bean Validation (JSR-380).
- **jakarta.validation-api:** API de validação para beans Java, integrada ao Spring.
- **lombok:** Automatiza geração de métodos comuns (getters, setters, builders etc), reduzindo boilerplate.
- **logstash-logback-encoder:** Geração de logs estruturados em JSON, facilitando integração com ELK Stack (Elasticsearch, Logstash, Kibana).
- **spring-boot-devtools:** Hot reload e melhorias no ciclo de desenvolvimento (somente ambiente local).
- **spring-boot-starter-test:** Ferramentas para testes unitários e de integração, incluindo JUnit, Hamcrest e Mockito.
- **jacoco-maven-plugin:** Plugin Maven para geração e validação de cobertura de código nos testes (mínimo exigido de 80%).

---

## Configuração e Execução

1. **Pré-requisitos**
   - Java 17+
   - Maven 3.8+

2. **Clonando o repositório**
   ```bash
   git clone https://github.com/MichaelCX77/validatejwt.git
   cd validatejwt
   ```

3. **Instalando dependências e compilando**
   ```bash
   mvn clean install
   ```

4. **Executando a aplicação**
   ```bash
   mvn spring-boot:run
   ```
   A aplicação irá rodar por padrão em http://localhost:8080

---

## Features Disponíveis

### ✅ Válido  
- **JWT válido:** Campo jwt contendo 3 claims válidas: `Name`, `Role` e `Seed`.  

### ⚠️ Casos de erro e validação
- **Claims extras**: JWT possui mais de 3 claims.
- **Claim Name com números**: Exemplo: `"Toninho 123"`.
- **Claim Role inválido**: Valor diferente dos permitidos: `Admin`, `Member`, `External`.
- **Claim Seed não primo**: Valor não é um número primo.
- **Name com mais de 256 caracteres**.
- **Campo extra no payload**: JWT contém campo adicional, ex: `ExtraClaim`.
- **Qualquer dos 3 claims nulos (`null`) ou String vazia (`""`)**.
- **JSON vazio**: Body sem nenhum campo.
- **Campo jwt ausente**.

> Para cada caso acima, a resposta conterá detalhes do erro de validação identificado. Exemplos completos estão no arquivo [docs/HANDSON.md](docs/HANDSON.md).

---

## Exemplos de Logs da Aplicação

Logs estruturados são gerados em formato JSON e podem ser encontrados em:

- [exemple_client_error.json](docs/output_log_examples/exemple_client_error.json)  
- [exemple_failed_validate_payload.json](docs/output_log_examples/exemple_failed_validate_payload.json)  
- [exemple_server_error.json](docs/output_log_examples/exemple_server_error.json)  
- [exemple_success.json](docs/output_log_examples/exemple_success.json)  

Esses arquivos exemplificam logs de requisições bem-sucedidas, falhas de validação de payload, erros do cliente e falhas internas do servidor, respectivamente.

---

## Infraestrutura como Código (Terraform)

O projeto já inclui uma estrutura pronta para deploy em nuvem usando **Terraform** e AWS ECS Fargate.  
A pasta `/infra` contém módulos para provisionamento de infraestrutura e aplicação, **baseados no repositório [@MichaelCX77/infra-base](https://github.com/MichaelCX77/infra-base)**.

### Sobre o repositório de infraestrutura ([infra-base](https://github.com/MichaelCX77/infra-base))

O [infra-base](https://github.com/MichaelCX77/infra-base) é um projeto de infraestrutura reutilizável em Terraform, focado em deployment de APIs no AWS ECS Fargate, combinando baixo custo, escalabilidade e facilidade operacional.  
Ele automatiza todo o provisionamento necessário para aplicações containerizadas, incluindo rede, cluster, balanceador de carga, logs, auto scaling e integração com ECR.

**Principais módulos:**
- `ecs_infra`: Provisionamento de rede, cluster ECS, ALB, security groups, auto scaling etc.
- `ecs_app`: Deploy da aplicação/container e ligação com a infraestrutura criada.

**Estrutura típica:**
```
/validatejwt/infra
├── ecs_app/
│   ├── backend.tf
│   ├── main.tf
│   └── variables.tf
└── ecs_infra/
    ├── backend.tf
    ├── main.tf
    ├── outputs.tf
    └── variables.tf
```

### Como funciona

- **State remoto:** O arquivo `backend.tf` utiliza backend S3 para versionamento e colaboração segura do estado do Terraform.
- **Módulos separados:** `ecs_infra` para infraestrutura base e `ecs_app` para o deploy do serviço/container.
- **Infra as Code:** Permite escalar, destruir e versionar sua infraestrutura com facilidade, de acordo com o padrão definido no [infra-base](https://github.com/MichaelCX77/infra-base).

### Como usar

1. Ajuste as variáveis conforme seu contexto.
2. Inicialize e aplique cada módulo:
   ```bash
   cd infra/ecs_infra
   terraform init
   terraform apply

   cd ../ecs_app
   terraform init
   terraform apply
   ```
3. Os recursos serão provisionados automaticamente na AWS.

> Consulte também a documentação e exemplos no próprio repositório [infra-base](https://github.com/MichaelCX77/infra-base).

---

## Integração e Automação via GitHub Actions

O projeto conta com um pipeline CI/CD robusto, utilizando **GitHub Actions** para automação de testes, build, cobertura, documentação e deploy. Os principais workflows são:

### **deploy_ecs**

- CI/CD para deploy da aplicação no ECS, disparado em push na branch `develop` ou manualmente.
- Executa:
  - Provisionamento de infraestrutura via Terraform (reutilizando workflow da base infra).
  - Build e push da imagem Docker para ECR.
  - Deploy automatizado no ECS Fargate.

**Como usar:**  
Push na branch `develop` ou acione manualmente o workflow nas Actions do GitHub.

---

### **deploy_javadoc**

- Gera e publica automaticamente a documentação JavaDoc do projeto no GitHub Pages.
- Executado a cada push na branch `main`.
- Utiliza Maven para buildar o Javadoc e [peaceiris/actions-gh-pages](https://github.com/peaceiris/actions-gh-pages) para publicar.

**Como usar:**  
Push na branch `main` do repositório.

---

### **push_to_develop**

- Executa testes automatizados, gera relatório de cobertura Jacoco e pode ser ajustado para criar PRs para a branch develop.
- Disparado para todas as branches, exceto `main` e `develop`.
- Valida se a cobertura mínima foi atingida e apresenta feedback nos logs.

**Como usar:**  
Push em qualquer branch de feature/fix.

---

### Como funciona a automação

- Cada workflow é disparado de acordo com eventos do GitHub (push, PR, etc).
- Os jobs são segmentados em etapas: build/teste, geração de cobertura, publicação de documentação e deploy.
- O state remoto do Terraform garante independência e rastreabilidade dos recursos.
- A automação cobre todo o ciclo: infra, build, testes, cobertura, documentação e deploy.

---

## Informações Adicionais

- **Gerar documentação Javadoc**
  ```bash
  mvn javadoc:javadoc
  ```
  Os arquivos gerados estarão em `target/site/apidocs`.
- **Javadoc é publicado automaticamente pelo GitHub Actions e pode ser acessado em:**  
  [https://michaelcx77.github.io/validatejwt/](https://michaelcx77.github.io/validatejwt/)

- **Gerar relatório de cobertura de testes**
  ```bash
  mvn test
  mvn jacoco:report
  ```
  O relatório estará disponível em `target/site/jacoco/index.html`.

---

## Referências e Fontes Externas

- [Spring Boot - Documentação Oficial](https://spring.io/projects/spring-boot)
- [JWT.io - Introdução a JSON Web Tokens](https://jwt.io/introduction)
- [Jakarta Bean Validation](https://beanvalidation.org/)
- [Logback e Logstash Encoder](https://github.com/logstash/logstash-logback-encoder)
- [JavaDoc para projetos Maven](https://maven.apache.org/plugins/maven-javadoc-plugin/)
- [JaCoCo - Java Code Coverage](https://www.jacoco.org/jacoco/)
- [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [Deploy ECS Fargate + ALB (AWS Hands-on)](https://aws.amazon.com/getting-started/hands-on/deploy-docker-containers/)
- [@MichaelCX77/infra-base — Source da Infraestrutura](https://github.com/MichaelCX77/infra-base)
- [Actions: peaceiris/actions-gh-pages](https://github.com/peaceiris/actions-gh-pages)

---

Contribuições são bem-vindas! Abra issues ou pull requests com sugestões ou melhorias.