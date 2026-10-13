CREATE DATABASE IF NOT EXISTS jnaliancas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'jnaliancas_app'@'localhost' IDENTIFIED BY 'JnAliancas@2026';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES ON jnaliancas.* TO 'jnaliancas_app'@'localhost';
FLUSH PRIVILEGES;

USE jnaliancas;
CREATE TABLE IF NOT EXISTS cliente (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(120) NOT NULL, nascimento VARCHAR(10) NOT NULL,
  cpf VARCHAR(14) NOT NULL, telefone VARCHAR(25) NOT NULL, rua VARCHAR(120) NOT NULL, numero VARCHAR(12) NOT NULL,
  bairro VARCHAR(100) NOT NULL, cidade VARCHAR(100) NOT NULL, estado VARCHAR(2) NOT NULL, cep VARCHAR(9) NOT NULL,
  complemento VARCHAR(120)
);
CREATE TABLE IF NOT EXISTS produto (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(120) NOT NULL, material VARCHAR(80) NOT NULL,
  quantidade INT NOT NULL, preco DECIMAL(12,2) NOT NULL, categoria VARCHAR(80) NOT NULL, observacoes VARCHAR(500)
);
CREATE TABLE IF NOT EXISTS pedido (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, cliente_id BIGINT NOT NULL, produto_id BIGINT NOT NULL,
  quantidade INT NOT NULL, data VARCHAR(10) NOT NULL, observacoes VARCHAR(500), concluido VARCHAR(3) NOT NULL,
  total DECIMAL(12,2) NOT NULL, CONSTRAINT fk_pedido_cliente FOREIGN KEY(cliente_id) REFERENCES cliente(id),
  CONSTRAINT fk_pedido_produto FOREIGN KEY(produto_id) REFERENCES produto(id)
);
CREATE TABLE IF NOT EXISTS pagamento (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, cliente_id BIGINT NOT NULL, valor DECIMAL(12,2) NOT NULL,
  tipo VARCHAR(20) NOT NULL, pago DECIMAL(12,2) NOT NULL, pendente DECIMAL(12,2) NOT NULL,
  CONSTRAINT fk_pagamento_cliente FOREIGN KEY(cliente_id) REFERENCES cliente(id)
);
CREATE TABLE IF NOT EXISTS entrega (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, cliente_id BIGINT NOT NULL, tipo VARCHAR(12) NOT NULL,
  frete DECIMAL(12,2), prazo VARCHAR(40) NOT NULL, observacoes VARCHAR(500),
  CONSTRAINT fk_entrega_cliente FOREIGN KEY(cliente_id) REFERENCES cliente(id)
);
CREATE TABLE IF NOT EXISTS agendamento (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, cliente_id BIGINT NOT NULL, data VARCHAR(10) NOT NULL,
  hora VARCHAR(5) NOT NULL, observacoes VARCHAR(500), CONSTRAINT fk_agendamento_cliente FOREIGN KEY(cliente_id) REFERENCES cliente(id)
);

INSERT INTO cliente (nome,nascimento,cpf,telefone,rua,numero,bairro,cidade,estado,cep,complemento)
SELECT 'Maria Souza','12/05/1990','123.456.789-09','+55 (27) 99999-1234','das Flores','120','Centro','Vitória','ES','29000-000',''
WHERE NOT EXISTS (SELECT 1 FROM cliente);
INSERT INTO produto (nome,material,quantidade,preco,categoria,observacoes)
SELECT 'Aliança Tradicional','Ouro 18k',10,1200.00,'Aliança',''
WHERE NOT EXISTS (SELECT 1 FROM produto);
INSERT INTO produto (nome,material,quantidade,preco,categoria,observacoes)
SELECT 'Aliança Anatômica','Prata 950',6,350.00,'Aliança',''
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE nome='Aliança Anatômica');
