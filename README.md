# JN Alianças Web — Etapa 9

Aplicação web Java 17 com Spring Boot, Spring MVC/REST, Spring Data JPA e MySQL. Os módulos clientes, produtos, pedidos, pagamentos, entregas e agendamentos são persistidos em tabelas próprias, com relações entre pedidos e produtos/clientes. As páginas fornecidas na Etapa 8 são servidas pelo Spring e consomem a API.

## Abrir e executar no NetBeans

Abra como projeto Maven a pasta que contém este `pom.xml`. Antes de iniciar a aplicação, execute `banco/jnaliancas.sql` no MySQL 8+ para criar o banco, o usuário e as tabelas. Depois execute o projeto no NetBeans ou rode `mvn spring-boot:run`. Acesse `http://localhost:8080/index.html`.

Credenciais locais de demonstração: `admin` / `123456`. O Spring Security armazena a senha com BCrypt e exige HTTP Basic nas operações de gravação. Para outros ambientes, configure `DB_URL`, `DB_USER`, `DB_PASSWORD`, `API_USER` e `API_PASSWORD` como variáveis de ambiente.

## Testes

- Testes de regras JUnit: execute **Test** no projeto Maven do NetBeans ou rode `mvn test`. Os relatórios ficam em `target/surefire-reports`.
- Testes de API: importe `postman/JN_Aliancas_API.postman_collection.json` no Postman. A coleção inclui login, consultas, gravação, validação e autorização. Use o banco de teste.
- Testes de interface: acesse o sistema no navegador e execute os casos do plano de testes da Etapa 7; registre resultados, falhas e capturas no documento de evidências.

## API

`GET /api/registros/{clientes|produtos|pedidos|pagamentos|entregas|agendamentos}` lista registros. `POST /api/registros/{entidade}` cria; `PUT /api/registros/{entidade}/{id}` atualiza; `DELETE /api/registros/{entidade}/{id}` exclui. Consultas são públicas; alterações exigem autenticação HTTP Basic. `GET /api/sessao` confirma as credenciais.

## Observações

O material fornecido da Etapa 6 contém um relatório de melhorias, mas não as fontes do sistema desktop nem o esquema MySQL original. Este projeto modela o domínio web a partir dos formulários da Etapa 8 e dos testes previstos. O acesso padrão é apenas demonstrativo: altere as credenciais antes de qualquer implantação real.
