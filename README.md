# ☁️ Cloud Gallery - AWS Project

![Java](https://img.shields.io/badge/Java-22-orange?style=flat&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=flat&logo=springboot)
![AWS](https://img.shields.io/badge/AWS-Cloud-orange?style=flat&logo=amazon-aws)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat&logo=mysql)

## 📋 Descrição

**Cloud Gallery** é uma aplicação web moderna de galeria de imagens desenvolvida em **Java Spring Boot**, totalmente hospedada na **AWS Cloud**. O sistema permite upload, visualização e exclusão de imagens, utilizando uma arquitetura serverless e escalável com integração de múltiplos serviços AWS.

A aplicação roda no **AWS Elastic Beanstalk**, armazena imagens no **Amazon S3**, persiste metadados no **Amazon RDS (MySQL)** e utiliza **AWS Lambda** para auditoria automática de uploads.

---

## 🏗️ Arquitetura

O projeto segue uma arquitetura moderna baseada em microserviços e computação em nuvem:

```mermaid
graph TD
    User[Usuário] -->|HTTPS| EB[Elastic Beanstalk<br/>(Spring Boot App)]
    subgraph "AWS Cloud (us-east-2)"
        EB -->|Salva Arquivo| S3[Amazon S3 Bucket]
        EB -->|Salva Dados| RDS[Amazon RDS MySQL]
        EB -->|Auditoria| Lambda[AWS Lambda]
    end
```

### Fluxo de Dados

#### 📤 **Upload de Imagem**

1. **Usuário** envia uma imagem via interface web (Thymeleaf)
2. **Elastic Beanstalk** recebe a requisição HTTP POST
3. **Spring Boot Application** processa o upload:
   - Gera UUID único para o arquivo
   - Faz upload do arquivo para o **S3 Bucket**
   - Salva metadados (título, descrição, URL, data) no **RDS MySQL**
   - Dispara função **Lambda** para auditoria assíncrona
4. **Página é recarregada** exibindo a galeria atualizada

#### 🗑️ **Exclusão de Imagem**

1. **Usuário** clica no botão de lixeira (🗑️) no card da imagem
2. **Elastic Beanstalk** recebe a requisição POST `/image/delete/{id}`
3. **Spring Boot Application**:
   - Busca o registro no banco de dados
   - Deleta o arquivo físico do **S3 Bucket**
   - Remove o registro do **RDS MySQL**
4. **Página é recarregada** sem a imagem deletada

---

## 🚀 Tecnologias Utilizadas

### **Backend**

- ☕ **Java 22**
- 🍃 **Spring Boot 3.2.0**
  - Spring Web (MVC)
  - Spring Data JPA
  - Thymeleaf Template Engine
- 📦 **Maven** (Gerenciamento de dependências)

### **Cloud (AWS)**

- 🌐 **Elastic Beanstalk**: Hospedagem da aplicação
- 🗄️ **RDS MySQL**: Banco de dados relacional
- 🪣 **S3**: Armazenamento de objetos (imagens)
- ⚡ **Lambda**: Auditoria serverless de uploads
- 🔐 **IAM**: Gerenciamento de credenciais e permissões

### **Frontend**

- 🎨 **Thymeleaf** (Server-Side Rendering)
- 📱 **Bootstrap 5.3** (Framework CSS)
- 🌐 **HTML5 + CSS3**

---

## 📁 Estrutura do Projeto

```
CloudGallery/
├── src/
│   ├── main/
│   │   ├── java/com/example/cloudgallery/
│   │   │   ├── CloudGalleryApplication.java    # Classe principal
│   │   │   ├── GalleryController.java          # Controller REST/MVC
│   │   │   ├── Image.java                      # Entidade JPA
│   │   │   ├── ImageRepository.java            # Repository JPA
│   │   │   ├── S3Service.java                  # Serviço AWS S3
│   │   │   └── LambdaService.java              # Serviço AWS Lambda
│   │   └── resources/
│   │       ├── application.properties          # Configurações AWS/DB
│   │       └── templates/
│   │           └── list.html                   # Interface da galeria
├── pom.xml                                     # Dependências Maven
└── README.md
```

---

## ⚙️ Configuração

### **1. Pré-requisitos**

- ✅ Java 22 JDK instalado
- ✅ Maven 3.8+ instalado
- ✅ Conta AWS ativa
- ✅ AWS CLI configurado (opcional)

### **2. Configurar Credenciais AWS**

Edite o arquivo `src/main/resources/application.properties`:

```properties
# AWS Credentials
aws.accessKey=SUA_ACCESS_KEY_AQUI
aws.secretKey=SUA_SECRET_KEY_AQUI
aws.region=us-east-2

# S3 Bucket
s3.bucket.name=seu-bucket-name

# Lambda Function
aws.lambda.function=ImageAuditor

# RDS Database
spring.datasource.url=jdbc:mysql://seu-endpoint-rds.us-east-2.rds.amazonaws.com:3306/cloud_gallery
spring.datasource.username=admin
spring.datasource.password=sua_senha
```

### **3. Criar Recursos AWS**

#### **S3 Bucket**

```bash
aws s3 mb s3://seu-bucket-name --region us-east-2
```

#### **RDS MySQL**

- Crie uma instância RDS MySQL 8.0
- Configure Security Group para permitir conexão da aplicação
- Crie o database `cloud_gallery`

#### **Lambda Function**

- Crie uma função Lambda chamada `ImageAuditor`
- Configure permissões IAM apropriadas

---

## 🛠️ Como Executar

### **Localmente (Desenvolvimento)**

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/cloud-gallery.git
cd cloud-gallery

# 2. Configure as credenciais AWS no application.properties

# 3. Execute a aplicação
mvn spring-boot:run

# 4. Acesse no navegador
http://localhost:5000
```

### **Deploy no AWS Elastic Beanstalk**

```bash
# 1. Gere o JAR da aplicação
mvn clean package

# 2. O arquivo será gerado em:
target/cloudgallery-0.0.1-SNAPSHOT.jar

# 3. Faça upload no Elastic Beanstalk via Console AWS
# ou use o EB CLI:
eb init
eb create cloud-gallery-env
eb deploy
```

---

## 📸 Funcionalidades

### ✨ **Upload de Imagens**

- Interface intuitiva com formulário de upload
- Suporte a qualquer formato de imagem
- Campos: Título, Descrição e Arquivo
- Armazenamento seguro no S3
- Metadados persistidos no RDS

### 🖼️ **Galeria de Imagens**

- Visualização em grid responsivo (3 colunas)
- Cards com título, descrição e data de upload
- Imagens carregadas diretamente do S3
- Design moderno com Bootstrap 5

### 🗑️ **Exclusão de Imagens**

- Botão de lixeira (🗑️) em cada card
- Confirmação antes de deletar
- Remove arquivo do S3 E registro do banco
- Feedback visual imediato

### 🔍 **Auditoria (Lambda)**

- Registro automático de cada upload
- Processamento assíncrono
- Logs centralizados no CloudWatch

---

## 🔒 Segurança

- 🔐 Credenciais AWS armazenadas em `application.properties`
- 🛡️ Security Groups configurados no RDS
- 🔑 IAM Roles com princípio de menor privilégio
- 🚫 Validação de upload de arquivos

> ⚠️ **IMPORTANTE**: Nunca commite credenciais AWS no Git! Use variáveis de ambiente ou AWS Secrets Manager em produção.

---

## 📊 Banco de Dados

### **Tabela: `images`**

| Campo         | Tipo         | Descrição            |
| ------------- | ------------ | -------------------- |
| `id`          | BIGINT (PK)  | ID auto-incrementado |
| `title`       | VARCHAR(255) | Título da imagem     |
| `description` | TEXT         | Descrição (opcional) |
| `s3_url`      | VARCHAR(500) | URL completa do S3   |
| `upload_date` | DATETIME     | Timestamp do upload  |

---

## 🧪 Testando a Aplicação

### **1. Upload de Imagem**

```
POST http://localhost:5000/upload
Content-Type: multipart/form-data

file: [arquivo.jpg]
title: "Minha Foto"
description: "Teste de upload"
```

### **2. Visualizar Galeria**

```
GET http://localhost:5000/
```

### **3. Deletar Imagem**

```
POST http://localhost:5000/image/delete/1
```

---

## 🐛 Troubleshooting

### **Erro de Conexão com RDS**

- Verifique o Security Group do RDS (porta 3306 aberta)
- Confirme credenciais no `application.properties`
- Teste conectividade: `telnet seu-endpoint-rds 3306`

### **Erro no Upload S3**

- Verifique permissões do IAM User/Role
- Confirme o nome do bucket no `application.properties`
- Bucket deve estar na mesma região (`us-east-2`)

### **Lambda não dispara**

- Verifique o nome da função (`ImageAuditor`)
- Confirme permissões de invocação no IAM
- Cheque logs no CloudWatch

---

## 📈 Melhorias Futuras

- [ ] Implementar autenticação (Spring Security + Cognito)
- [ ] Adicionar paginação na galeria
- [ ] Upload múltiplo de imagens
- [ ] Geração automática de thumbnails (Lambda)
- [ ] Sistema de tags/categorias
- [ ] API REST pública
- [ ] Frontend React/Vue.js separado
- [ ] Implementar CloudFront para CDN

---

## 👨‍💻 Autor

**Gustavo**  
📧 Email: contato@exemplo.com  
🔗 GitHub: [@GUSTH01](https://github.com/GUSTH01)

---

## 📄 Licença

Este projeto é open-source e está disponível sob a licença MIT.

---

## 🙏 Agradecimentos

- AWS Documentation
- Spring Boot Community
- Bootstrap Team

---

<div align="center">

**⭐ Se este projeto foi útil, deixe uma estrela!**

</div>
