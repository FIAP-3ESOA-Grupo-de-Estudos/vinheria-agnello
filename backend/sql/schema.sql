CREATE TABLE usuario (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NULL UNIQUE,
    telefone VARCHAR(20) NULL,
    tipo_usuario ENUM('cliente', 'admin', 'gerente') DEFAULT 'cliente' NOT NULL,
    status ENUM('ativo', 'inativo', 'bloqueado') DEFAULT 'ativo' NOT NULL,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_email (email),
    INDEX idx_status (status)
);

CREATE TABLE endereco (
    id_endereco INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    cep VARCHAR(10) NOT NULL,
    logradouro VARCHAR(150) NOT NULL,
    numero VARCHAR(10) NOT NULL,
    complemento VARCHAR(100) NULL,
    bairro VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    principal BOOLEAN DEFAULT FALSE NOT NULL,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    INDEX idx_usuario (id_usuario),
    INDEX idx_principal (principal)
);

CREATE TABLE categoria (
    id_categoria INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    INDEX idx_nome (nome)
);

CREATE TABLE vinho (
    id_vinho INT PRIMARY KEY AUTO_INCREMENT,
    id_categoria INT NOT NULL,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT NOT NULL,
    tipo ENUM('tinto', 'branco', 'rose', 'espumante', 'fortificado') NOT NULL,
    regiao VARCHAR(100) NOT NULL,
    pais VARCHAR(50) NOT NULL,
    safra INT NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    teor_alcoolico DECIMAL(4, 2) NOT NULL,
    volume_ml INT NOT NULL DEFAULT 750,
    harmonizacao TEXT NULL,
    nota_degustacao TEXT NULL,
    estoque INT NOT NULL DEFAULT 0,
    imagem_url VARCHAR(500) NULL,
    ativo BOOLEAN DEFAULT TRUE NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria) ON DELETE RESTRICT,
    INDEX idx_categoria (id_categoria),
    INDEX idx_tipo (tipo),
    INDEX idx_ativo (ativo),
    INDEX idx_preco (preco),
    INDEX idx_nome (nome)
);

CREATE TABLE avaliacao (
    id_avaliacao INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    id_vinho INT NOT NULL,
    nota INT NOT NULL CHECK (nota >= 1 AND nota <= 5),
    comentario TEXT NULL,
    status_aprovacao ENUM('pendente', 'aprovado', 'rejeitado') DEFAULT 'pendente' NOT NULL,
    data_avaliacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    data_aprovacao TIMESTAMP NULL,

    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_vinho) REFERENCES vinho(id_vinho) ON DELETE CASCADE,
    UNIQUE KEY unique_user_vinho (id_usuario, id_vinho),
    INDEX idx_usuario (id_usuario),
    INDEX idx_vinho (id_vinho),
    INDEX idx_status (status_aprovacao)
);

CREATE TABLE carrinho (
    id_carrinho INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    status ENUM('ativo', 'convertido', 'abandonado') DEFAULT 'ativo' NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    INDEX idx_usuario (id_usuario),
    INDEX idx_status (status)
);

CREATE TABLE item_carrinho (
    id_item_carrinho INT PRIMARY KEY AUTO_INCREMENT,
    id_carrinho INT NOT NULL,
    id_vinho INT NOT NULL,
    quantidade INT NOT NULL CHECK (quantidade > 0),
    preco_unitario DECIMAL(10, 2) NOT NULL,
    data_adicao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    FOREIGN KEY (id_carrinho) REFERENCES carrinho(id_carrinho) ON DELETE CASCADE,
    FOREIGN KEY (id_vinho) REFERENCES vinho(id_vinho) ON DELETE CASCADE,
    UNIQUE KEY unique_carrinho_vinho (id_carrinho, id_vinho),
    INDEX idx_carrinho (id_carrinho),
    INDEX idx_vinho (id_vinho)
);

CREATE TABLE pedido (
    id_pedido INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    id_endereco INT NOT NULL,
    numero_pedido VARCHAR(20) NOT NULL UNIQUE,
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    valor_subtotal DECIMAL(10, 2) NOT NULL,
    valor_desconto DECIMAL(10, 2) DEFAULT 0 NOT NULL,
    valor_frete DECIMAL(10, 2) DEFAULT 0 NOT NULL,
    valor_total DECIMAL(10, 2) NOT NULL,
    status_pedido ENUM('pendente', 'processando', 'enviado', 'entregue', 'cancelado') DEFAULT 'pendente' NOT NULL,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_endereco) REFERENCES endereco(id_endereco) ON DELETE RESTRICT,
    INDEX idx_usuario (id_usuario),
    INDEX idx_status (status_pedido),
    INDEX idx_numero (numero_pedido)
);

CREATE TABLE item_pedido (
    id_item_pedido INT PRIMARY KEY AUTO_INCREMENT,
    id_pedido INT NOT NULL,
    id_vinho INT NOT NULL,
    quantidade INT NOT NULL CHECK (quantidade > 0),
    preco_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,

    FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido) ON DELETE CASCADE,
    FOREIGN KEY (id_vinho) REFERENCES vinho(id_vinho) ON DELETE RESTRICT,
    INDEX idx_pedido (id_pedido),
    INDEX idx_vinho (id_vinho)
);

CREATE TABLE pagamento (
    id_pagamento INT PRIMARY KEY AUTO_INCREMENT,
    id_pedido INT NOT NULL,
    tipo_pagamento ENUM('credito', 'debito', 'boleto', 'pix', 'transferencia') NOT NULL,
    status_pagamento ENUM('pendente', 'processando', 'aprovado', 'recusado', 'reembolsado') DEFAULT 'pendente' NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    parcelas INT DEFAULT 1 NOT NULL CHECK (parcelas >= 1 AND parcelas <= 12),
    codigo_transacao VARCHAR(100) NULL,
    data_pagamento TIMESTAMP NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido) ON DELETE CASCADE,
    UNIQUE KEY unique_pedido (id_pedido),
    INDEX idx_status (status_pagamento),
    INDEX idx_tipo (tipo_pagamento)
);

CREATE TABLE plano_assinatura (
    id_plano INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT NOT NULL,
    valor_mensal DECIMAL(10, 2) NOT NULL,
    quantidade_garrafas INT NOT NULL,
    premium_only BOOLEAN DEFAULT FALSE NOT NULL,
    ativo BOOLEAN DEFAULT TRUE NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    INDEX idx_nome (nome),
    INDEX idx_ativo (ativo)
);

CREATE TABLE beneficio_plano (
    id_beneficio_plano INT PRIMARY KEY AUTO_INCREMENT,
    id_plano INT NOT NULL,
    tipo_beneficio VARCHAR(100) NOT NULL,
    descricao TEXT NOT NULL,
    valor_beneficio DECIMAL(10, 2) NULL,
    ativo BOOLEAN DEFAULT TRUE NOT NULL,

    FOREIGN KEY (id_plano) REFERENCES plano_assinatura(id_plano) ON DELETE CASCADE,
    INDEX idx_plano (id_plano)
);

CREATE TABLE assinatura (
    id_assinatura INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    id_plano INT NOT NULL,
    data_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    data_fim TIMESTAMP NULL,
    status_assinatura ENUM('ativa', 'pausada', 'cancelada', 'expirada') DEFAULT 'ativa' NOT NULL,
    data_proxima_cobranca TIMESTAMP NOT NULL,
    data_cancelamento TIMESTAMP NULL,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_plano) REFERENCES plano_assinatura(id_plano) ON DELETE RESTRICT,
    INDEX idx_usuario (id_usuario),
    INDEX idx_status (status_assinatura),
    INDEX idx_proxima_cobranca (data_proxima_cobranca)
);

CREATE TABLE preferencia_assinatura (
    id_preferencia INT PRIMARY KEY AUTO_INCREMENT,
    id_assinatura INT NOT NULL,
    faixa_preco_min DECIMAL(10, 2) NULL,
    faixa_preco_max DECIMAL(10, 2) NULL,
    observacoes TEXT NULL,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (id_assinatura) REFERENCES assinatura(id_assinatura) ON DELETE CASCADE,
    UNIQUE KEY unique_assinatura (id_assinatura)
);

CREATE TABLE preferencia_assinatura_tipo (
    id_pref_tipo INT PRIMARY KEY AUTO_INCREMENT,
    id_preferencia INT NOT NULL,
    tipo_vinho ENUM('tinto', 'branco', 'rose', 'espumante', 'fortificado') NOT NULL,

    FOREIGN KEY (id_preferencia) REFERENCES preferencia_assinatura(id_preferencia) ON DELETE CASCADE,
    UNIQUE KEY unique_pref_tipo (id_preferencia, tipo_vinho),
    INDEX idx_preferencia (id_preferencia)
);

CREATE TABLE artigo (
    id_artigo INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(200) NOT NULL,
    resumo VARCHAR(500) NULL,
    conteudo TEXT NOT NULL,
    autor VARCHAR(100) NOT NULL,
    categoria_artigo VARCHAR(50) NOT NULL,
    status_publicacao ENUM('rascunho', 'publicado', 'arquivado') DEFAULT 'rascunho' NOT NULL,
    imagem_capa VARCHAR(500) NULL,
    data_publicacao TIMESTAMP NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_status (status_publicacao),
    INDEX idx_autor (autor),
    INDEX idx_data_publicacao (data_publicacao)
);

CREATE TABLE beneficio_fidelidade (
    id_beneficio_fidelidade INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT NOT NULL,
    tipo_beneficio ENUM('desconto', 'brinde', 'envio_gratis', 'acesso_exclusivo') NOT NULL,
    pontos_necessarios INT NOT NULL,
    ativo BOOLEAN DEFAULT TRUE NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    INDEX idx_tipo (tipo_beneficio),
    INDEX idx_ativo (ativo)
);

CREATE TABLE pontuacao_usuario (
    id_movimentacao INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    pontos INT NOT NULL,
    tipo_movimentacao ENUM('compra', 'avaliacao', 'referencia', 'promocao', 'resgate', 'ajuste_admin') NOT NULL,
    descricao VARCHAR(255) NULL,
    data_movimentacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    referencia_id INT NULL,

    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    INDEX idx_usuario (id_usuario),
    INDEX idx_tipo (tipo_movimentacao),
    INDEX idx_data (data_movimentacao)
);

CREATE INDEX idx_vinho_tipo_preco ON vinho(tipo, preco);
CREATE INDEX idx_vinho_categoria_ativo ON vinho(id_categoria, ativo);
CREATE INDEX idx_pedido_usuario_data ON pedido(id_usuario, data_pedido);
CREATE INDEX idx_item_pedido_pedido ON item_pedido(id_pedido);
CREATE INDEX idx_assinatura_usuario_status ON assinatura(id_usuario, status_assinatura);

DROP VIEW IF EXISTS vw_vendas_por_vinho;

CREATE VIEW vw_vendas_por_vinho AS
SELECT
    v.id_vinho,
    v.nome,
    v.preco,
    COALESCE(vendas.total_itens_vendidos, 0) AS total_itens_vendidos,
    COALESCE(vendas.quantidade_garrafas, 0) AS quantidade_garrafas,
    COALESCE(vendas.valor_total_vendas, 0.00) AS valor_total_vendas,
    avaliacoes.avaliacao_media
FROM vinho v
LEFT JOIN (
    SELECT
        ip.id_vinho,
        COUNT(ip.id_item_pedido) AS total_itens_vendidos,
        SUM(ip.quantidade) AS quantidade_garrafas,
        SUM(ip.subtotal) AS valor_total_vendas
    FROM item_pedido ip
    GROUP BY ip.id_vinho
) vendas ON v.id_vinho = vendas.id_vinho
LEFT JOIN (
    SELECT
        a.id_vinho,
        AVG(a.nota) AS avaliacao_media
    FROM avaliacao a
    WHERE a.status_aprovacao = 'aprovado'
    GROUP BY a.id_vinho
) avaliacoes ON v.id_vinho = avaliacoes.id_vinho;

CREATE VIEW vw_clientes_assinatura_ativa AS
SELECT
    u.id_usuario,
    u.nome,
    u.email,
    pa.nome AS plano,
    a.data_proxima_cobranca,
    a.id_assinatura
FROM usuario u
JOIN assinatura a ON u.id_usuario = a.id_usuario
JOIN plano_assinatura pa ON a.id_plano = pa.id_plano
WHERE a.status_assinatura = 'ativa';
