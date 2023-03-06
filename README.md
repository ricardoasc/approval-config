# Geral

Template para criação de novos projetos Spring Boot para ECS via DevConsole.

## Estrutura do Projeto

Abaixo mostramos uma represenção da árvore de diretórios e arquivos disponíveis no projeto.

```
├── build.gradle
├── dependencies.gradle
├── Dockerfile
├── ecs-meta.json
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradle.properties
├── gradlew
├── gradlew.bat
├── Jenkinsfile
├── src
│   ├── main
│   │   ├── java
│   │   │   └── io
│   │   │       └── sicredi
│   │   │           └── changeme
│   │   │               ├── ChangeMeApplication.java
│   │   │               └── controller
│   │   │                   └── ChangeMeController.java
│   │   └── resources
│   │       ├── application-dev.properties
│   │       ├── application-prd.properties
│   │       ├── application-tst.properties
│   │       ├── application-uat.properties
│   │       └── bootstrap.yml
│   └── test
│       ├── java
│       │   └── io
│       │       └── sicredi
│       │           └── changeme
│       │               └── spec
│       │                   └── ChangeMeControllerSpec.java
│       └── resources
│           └── application.properties
```

### Pacotes

Como esta aplicação é apenas um teplate inicial e deve ser alterada, o pacote `io.sicredi.changeme` deve ter seu nome alterado para refletir o domínio do serviço.

### Controller

O pacote `io.sicredi.changeme.controller` contém um `controller` chamado `ChangeMeController`. Este `controller` expõe dois `endpoints` a fim de demonstrar o uso do Spring WebFlux e as funcionalidades disponibilizadas pelo conjunto de bibliotecas `services-platform`. Estes `endpoints` são: 


- `greetings/{name}` - Retorna uma mensagem no formato `application/plain-text`; 

- `throw` - Lança uma exceção de negócio que é convertida em `HTTP Status Code 422`. 

### Resources

Este pacote contém os arquivos de propriedades e estão pós-fixados com os ambientes disponíveis para instalação. O nome deste arquivo segue o padrão `application-{environment}.properties`. Como estes arquivos são persistidos no `SCM` (Git), durante a instalação é possível aplicar as mesmas propriedades de um determinado `commit` ou `tag` no `Consul`, mantendo a consistência da configuração. 

Além dos arquivos de propriedades temos o `bootstrap.yml` que mantém configurações que não irão ser alteradas com frequência. Nestas configurações temos as declarações do `Vault` e `Consul` já pré-configurados. 

> A dependência responsável por acessar o `Vault` não foi adicionada neste template, portanto mesmo tendo a configuração no arquivo `bootstrap.yml` o Spring Boot não criará os `beans` para isso.

### ECS Descriptor

O arquivo `ecs-meta.json` está contido na raiz do projeto. Neste arquivo temos a definição do atributo `service_name` que é usado pelo sistema de instalação para determinar o nome da aplicação bem como as entradas de `DNS`. Este arquivo é obrigatório para todas as aplicações que serão instaladas no `ECS`. 

### Build

A construção da aplicação está dividida em dois arquivos, respectivamente `build.gradle` e `dependencies.gradle`. Este último contém a declaração de todas as dependências utilizadas no projeto e é importado pelo primeiro arquivo.

> Para maiores informações sobre as denpendências adicionadas no projeto leia os comentários disponíveis no arquivo [dependencies.gradle](`./dependencies.gradle`). 

### CI

O arquivo `Jenkinsfile` contém as funções necessárias para integração com o `Jenkins` e para construir a aplicação. 