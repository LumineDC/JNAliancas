# Front-end JN Alianças

As páginas desta pasta são servidas pelo Spring Boot em `static/` e usam `js/app.js` para consumir a API REST do projeto. Os dados são persistidos pelo back-end em tabelas MySQL com JPA; não são armazenados no `localStorage`.

Para executar, abra o projeto Maven na pasta raiz (a que contém `pom.xml`), configure o MySQL conforme `banco/jnaliancas.sql` e inicie a aplicação. Acesse `http://localhost:8080/index.html`. Login local de demonstração: `admin` / `123456`.

As validações da interface ajudam o preenchimento, e o serviço Java também valida os campos recebidos pela API. As gravações exigem autenticação HTTP Basic; as leituras das listas são públicas.
