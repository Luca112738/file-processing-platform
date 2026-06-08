-- ============================================================
--  TACO EXPRESS — Script completo para XAMPP (MySQL/MariaDB)
--  Banco: taco_express_db
--  Compatível com: ConnectionFactory.java → taco_express_db
-- ============================================================

-- Cria e seleciona o banco
CREATE DATABASE IF NOT EXISTS taco_express_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE taco_express_db;

-- ============================================================
-- TABELAS
-- ============================================================

-- 1. USUARIO (entidade forte — sem dependência)
CREATE TABLE IF NOT EXISTS usuario (
    id       INT          NOT NULL AUTO_INCREMENT,
    email    VARCHAR(150) NOT NULL UNIQUE,
    senha    VARCHAR(255) NOT NULL,
    endereco VARCHAR(300) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. CATEGORIA (entidade forte — sem dependência)
CREATE TABLE IF NOT EXISTS categoria (
    id   INT         NOT NULL AUTO_INCREMENT,
    nome VARCHAR(80) NOT NULL UNIQUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. PRODUTO (relacionamento 1:N com categoria)
--    Um produto pertence a uma categoria → ComboBox de categorias na tela
CREATE TABLE IF NOT EXISTS produto (
    id           INT            NOT NULL AUTO_INCREMENT,
    id_categoria INT            NOT NULL,
    nome         VARCHAR(150)   NOT NULL,
    descricao    VARCHAR(500)   DEFAULT NULL,
    preco        DECIMAL(10,2)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_produto_categoria
        FOREIGN KEY (id_categoria) REFERENCES categoria(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. PEDIDO (relacionamento 1:N com usuario)
--    Um usuário pode ter vários pedidos
--    Campos: id_usuario, data_hora, metodo_pagamento, endereco, total, status
--    (compatível com PedidoDAO.inserir e Pedido.java)
CREATE TABLE IF NOT EXISTS pedido (
    id               INT            NOT NULL AUTO_INCREMENT,
    id_usuario       INT            NOT NULL,
    data_hora        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodo_pagamento VARCHAR(30)    NOT NULL,
    endereco         VARCHAR(300)   NOT NULL,
    total            DECIMAL(10,2)  NOT NULL,
    status           TINYINT        NOT NULL DEFAULT 0,
    -- 0=Pedido confirmado | 1=Pagamento aprovado | 2=Preparando | 3=Saiu para entrega | 4=Entregue
    PRIMARY KEY (id),
    CONSTRAINT fk_pedido_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. ITEM_PEDIDO (associativa: pedido N:M produto)
--    Campos: id_pedido, id_produto, quantidade, preco_unit
--    (compatível com PedidoDAO — batch insert dos itens)
CREATE TABLE IF NOT EXISTS item_pedido (
    id          INT           NOT NULL AUTO_INCREMENT,
    id_pedido   INT           NOT NULL,
    id_produto  INT           NOT NULL,
    quantidade  INT           NOT NULL DEFAULT 1,
    preco_unit  DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_item_pedido
        FOREIGN KEY (id_pedido)  REFERENCES pedido(id)  ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_item_produto
        FOREIGN KEY (id_produto) REFERENCES produto(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- DADOS INICIAIS
-- ============================================================

-- Categorias (IDs fixos usados no CardapioController)
INSERT INTO categoria (id, nome) VALUES
    (1, 'Tacos'),
    (2, 'Burritos'),
    (3, 'Quesadillas')
ON DUPLICATE KEY UPDATE nome = VALUES(nome);

-- Produtos do cardápio (exatamente os exibidos nas telas do Canva)
INSERT INTO produto (id_categoria, nome, descricao, preco) VALUES
    -- Tacos
    (1, 'Combo de Tacos (3 un.)',
        'Tacos crocantes de milho recheados com carne, vegetais frescos e queijo.',
        29.90),
    (1, 'Combo de Tacos Soft',
        'Tacos de tortillas macias (soft shell) recheados com carne, vegetais frescos e queijo.',
        32.90),

    -- Burritos
    (2, 'Burrito Supremo',
        'Tortilha de trigo recheada com carne bem temperada, arroz, feijão e molhos especiais.',
        28.90),
    (2, 'Burrito Aberto (Bowl)',
        'Todo o sabor do burrito tradicional servido no prato, combinando arroz mexicano, feijão, carne, vegetais e molhos.',
        29.90),

    -- Quesadillas
    (3, 'Quesadilla de Queijo',
        'Tortilha de trigo grelhada na chapa e recheada com muito queijo derretido.',
        19.90),
    (3, 'Quesadilla de Carne',
        'Tortilha de trigo grelhada na chapa e recheada com carne e muito queijo derretido.',
        22.90);

-- Usuário de teste (para logar sem precisar cadastrar)
-- email: teste@taco.com | senha: 123456
INSERT INTO usuario (email, senha) VALUES
    ('teste@taco.com', '123456')
ON DUPLICATE KEY UPDATE senha = VALUES(senha);

-- ============================================================
-- VERIFICAÇÃO (opcional — rode para confirmar)
-- ============================================================
-- SELECT * FROM categoria;
-- SELECT p.id, c.nome AS categoria, p.nome, p.preco FROM produto p JOIN categoria c ON c.id = p.id_categoria;
-- SELECT * FROM usuario;
