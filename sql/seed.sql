USE vinheria_agnello;

-- =========================================================
-- SEED / POPULAÇÃO INICIAL
-- Projeto: Vinheria Agnello
-- Compatível com MySQL / MariaDB
-- =========================================================

-- ---------------------------------------------------------
-- RESET INICIAL
-- ---------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE pontuacao_usuario;
TRUNCATE TABLE avaliacao;
TRUNCATE TABLE pagamento;
TRUNCATE TABLE item_pedido;
TRUNCATE TABLE pedido;
TRUNCATE TABLE item_carrinho;
TRUNCATE TABLE carrinho;
TRUNCATE TABLE preferencia_assinatura_tipo;
TRUNCATE TABLE preferencia_assinatura;
TRUNCATE TABLE assinatura;
TRUNCATE TABLE beneficio_plano;
TRUNCATE TABLE plano_assinatura;
TRUNCATE TABLE beneficio_fidelidade;
TRUNCATE TABLE artigo;
TRUNCATE TABLE vinho;
TRUNCATE TABLE categoria;
TRUNCATE TABLE endereco;
TRUNCATE TABLE usuario;

SET FOREIGN_KEY_CHECKS = 1;


-- ---------------------------------------------------------
-- USUÁRIOS
-- ---------------------------------------------------------
INSERT INTO usuario (nome, email, senha, cpf, telefone, tipo_usuario, status) VALUES
('Cliente Teste 01', 'cliente01@vinheria.com', 'senha123', '000.000.000-01', '(11) 90000-0001', 'cliente', 'ativo'),
('Cliente Teste 02', 'cliente02@vinheria.com', 'senha123', '000.000.000-02', '(11) 90000-0002', 'cliente', 'ativo'),
('Cliente Teste 03', 'cliente03@vinheria.com', 'senha123', '000.000.000-03', '(11) 90000-0003', 'cliente', 'ativo'),
('Cliente Teste 04', 'cliente04@vinheria.com', 'senha123', '000.000.000-04', '(11) 90000-0004', 'cliente', 'ativo'),
('Cliente Teste 05', 'cliente05@vinheria.com', 'senha123', '000.000.000-05', '(11) 90000-0005', 'cliente', 'ativo'),
('Cliente Teste 06', 'cliente06@vinheria.com', 'senha123', '000.000.000-06', '(11) 90000-0006', 'cliente', 'ativo'),
('Cliente Teste 07', 'cliente07@vinheria.com', 'senha123', '000.000.000-07', '(11) 90000-0007', 'cliente', 'ativo'),
('Cliente Teste 08', 'cliente08@vinheria.com', 'senha123', '000.000.000-08', '(11) 90000-0008', 'cliente', 'ativo'),
('Admin Sistema', 'admin@vinheria.com', 'admin123', '000.000.000-09', '(11) 90000-0009', 'admin', 'ativo'),
('Gerente Sistema', 'gerente@vinheria.com', 'senha123', '000.000.000-10', '(11) 90000-0010', 'gerente', 'ativo');

-- ---------------------------------------------------------
-- ENDEREÇOS
-- ---------------------------------------------------------
INSERT INTO endereco (id_usuario, cep, logradouro, numero, complemento, bairro, cidade, estado, principal) VALUES
(1, '01310-100', 'Avenida Paulista', '1000', 'Apto 81', 'Bela Vista', 'São Paulo', 'SP', TRUE),
(2, '22041-001', 'Rua Barata Ribeiro', '230', 'Bloco B', 'Copacabana', 'Rio de Janeiro', 'RJ', TRUE),
(3, '30130-110', 'Avenida Afonso Pena', '850', NULL, 'Centro', 'Belo Horizonte', 'MG', TRUE),
(4, '40020-000', 'Rua Chile', '150', NULL, 'Centro', 'Salvador', 'BA', TRUE),
(5, '80010-000', 'Rua XV de Novembro', '420', 'Sala 12', 'Centro', 'Curitiba', 'PR', TRUE),
(6, '90010-150', 'Rua dos Andradas', '890', NULL, 'Centro Histórico', 'Porto Alegre', 'RS', TRUE),
(7, '50030-230', 'Avenida Guararapes', '300', 'Apto 45', 'Santo Antônio', 'Recife', 'PE', TRUE),
(8, '69005-070', 'Rua 10 de Julho', '120', NULL, 'Centro', 'Manaus', 'AM', TRUE),
(9, '01311-200', 'Alameda Santos', '1500', 'Conj. 101', 'Jardins', 'São Paulo', 'SP', TRUE),
(10, '30140-071', 'Rua da Bahia', '780', NULL, 'Lourdes', 'Belo Horizonte', 'MG', TRUE);

-- ---------------------------------------------------------
-- CATEGORIAS
-- ---------------------------------------------------------
INSERT INTO categoria (nome, descricao) VALUES
('Tintos', 'Vinhos tintos encorpados, equilibrados e aromáticos'),
('Brancos', 'Vinhos brancos leves, refrescantes e gastronômicos'),
('Rosés', 'Vinhos rosés delicados, frutados e versáteis'),
('Espumantes', 'Espumantes para celebrações e harmonizações leves'),
('Fortificados', 'Vinhos fortificados com maior teor alcoólico'),
('Premium', 'Rótulos selecionados de alto padrão'),
('Orgânicos', 'Vinhos produzidos com práticas mais naturais');

-- ---------------------------------------------------------
-- VINHOS
-- ---------------------------------------------------------
INSERT INTO vinho (
    id_categoria, nome, descricao, tipo, regiao, pais, safra, preco,
    teor_alcoolico, volume_ml, harmonizacao, nota_degustacao, estoque, imagem_url, ativo
) VALUES
(1, 'Cabernet Sauvignon Reserva', 'Vinho tinto estruturado com notas de frutas negras e carvalho.', 'tinto', 'Vale dos Vinhedos', 'Brasil', 2020, 89.90, 13.50, 750, 'Carnes vermelhas e massas intensas', 'Taninos elegantes e final persistente', 35, 'src/main/webapp/assets/images/wine-image-1.png', TRUE),
(1, 'Merlot Especial', 'Vinho tinto macio e frutado, excelente para o dia a dia.', 'tinto', 'Serra Gaúcha', 'Brasil', 2021, 69.90, 12.80, 750, 'Massas, pizzas e queijos semiduros', 'Boa acidez e corpo médio', 42, 'src/main/webapp/assets/images/wine-image-2.png', TRUE),
(1, 'Malbec Andes', 'Rótulo argentino intenso e aromático.', 'tinto', 'Mendoza', 'Argentina', 2019, 109.90, 14.00, 750, 'Churrasco e cortes bovinos', 'Frutas maduras, especiarias e toque amadeirado', 28, 'src/main/webapp/assets/images/wine-image-3.png', TRUE),
(2, 'Chardonnay Premium', 'Branco elegante com notas amanteigadas e frutadas.', 'branco', 'Serra Gaúcha', 'Brasil', 2022, 79.90, 12.30, 750, 'Peixes, risotos e massas leves', 'Notas cítricas e final cremoso', 31, 'src/main/webapp/assets/images/wine-image-4.png', TRUE),
(2, 'Sauvignon Blanc Vale', 'Branco fresco e vibrante.', 'branco', 'Vale de Casablanca', 'Chile', 2023, 74.90, 12.10, 750, 'Saladas, frutos do mar e pratos leves', 'Aromas herbáceos e frutas tropicais', 25, 'src/main/webapp/assets/images/wine-image-5.png', TRUE),
(3, 'Rosé Summer', 'Rosé leve, refrescante e muito versátil.', 'rose', 'Provence', 'França', 2023, 84.90, 11.80, 750, 'Entradas, canapés e culinária mediterrânea', 'Morangos, flores e boa acidez', 19, 'src/main/webapp/assets/images/wine-image-6.png', TRUE),
(4, 'Espumante Brut Imperial', 'Espumante sofisticado e equilibrado.', 'espumante', 'Campanha Gaúcha', 'Brasil', 2022, 99.90, 11.90, 750, 'Celebrações, aperitivos e sobremesas leves', 'Perlage fina e notas florais', 50, 'src/main/webapp/assets/images/wine-image-7.png', TRUE),
(4, 'Espumante Moscatel Doce', 'Espumante aromático e delicadamente doce.', 'espumante', 'Serra Gaúcha', 'Brasil', 2023, 59.90, 7.50, 750, 'Sobremesas e frutas frescas', 'Notas de flores brancas e mel', 46, 'src/main/webapp/assets/images/wine-image-8.png', TRUE),
(5, 'Porto Ruby Selection', 'Vinho fortificado intenso e marcante.', 'fortificado', 'Douro', 'Portugal', 2018, 129.90, 19.50, 750, 'Queijos azuis e sobremesas com chocolate', 'Doçura equilibrada e final longo', 17, 'src/main/webapp/assets/images/wine-image-9.png', TRUE),
(6, 'Syrah Gran Reserva', 'Rótulo premium de grande complexidade.', 'tinto', 'Vale do São Francisco', 'Brasil', 2020, 149.90, 14.20, 750, 'Carnes nobres e pratos robustos', 'Pimenta, frutas negras e barrica bem integrada', 14, 'src/main/webapp/assets/images/wine-image-10.png', TRUE),
(6, 'Pinot Noir Prestige', 'Vinho elegante, delicado e refinado.', 'tinto', 'Patagônia', 'Argentina', 2021, 139.90, 13.00, 750, 'Cogumelos, aves e queijos leves', 'Frutas vermelhas, toque terroso e suavidade', 12, 'src/main/webapp/assets/images/wine-image-11.png', TRUE),
(7, 'Orgânico Branco Natural', 'Branco de produção orgânica com perfil leve.', 'branco', 'Serra Catarinense', 'Brasil', 2023, 87.90, 11.70, 750, 'Saladas, peixes e pratos vegetarianos', 'Fresco, cítrico e delicado', 22, 'src/main/webapp/assets/images/wine-image-12.png', TRUE);

-- ---------------------------------------------------------
-- ARTIGOS
-- ---------------------------------------------------------
INSERT INTO artigo (
    titulo, resumo, conteudo, autor, categoria_artigo, status_publicacao, imagem_capa, data_publicacao
) VALUES
('Harmonização: vinhos tintos e carnes', 'Dicas para combinar vinhos tintos com diferentes cortes.', 'Conteúdo sobre harmonização de vinhos tintos com carnes vermelhas, aves e pratos mais intensos.', 'Equipe Vinheria Agnello', 'harmonizacao', 'publicado', 'src/main/webapp/assets/images/article-image-1.png', NOW()),
('Como escolher um bom vinho para iniciantes', 'Um guia básico para quem está começando no universo dos vinhos.', 'Conteúdo completo do artigo sobre escolha de vinhos para iniciantes, tipos, harmonizações e dicas práticas.', 'Equipe Vinheria Agnello', 'educacao', 'publicado', 'src/main/webapp/assets/images/article-image-2.png', NOW()),
('Espumantes: quando servir e como apreciar', 'Um conteúdo sobre estilos de espumantes e ocasiões ideais.', 'Conteúdo completo sobre espumantes brut, moscatel, harmonizações e temperatura de serviço.', 'Equipe Vinheria Agnello', 'degustacao', 'publicado', 'src/main/webapp/assets/images/article-image-3.png', NOW()),
('Entendendo os vinhos orgânicos, biodinâmicos e naturais', 'O que muda no cultivo e na produção desses vinhos que estão conquistando o mundo.', 'Explicação das diferenças entre métodos de cultivo sustentável, certificações orgânicas, práticas biodinâmicas e a filosofia dos vinhos naturais (mínima intervenção).', 'Equipe Vinheria Agnello', 'educacao', 'publicado', 'src/main/webapp/assets/images/article-image-4.png', NOW()),

-- ---------------------------------------------------------
-- PLANOS DE ASSINATURA
-- ---------------------------------------------------------
INSERT INTO plano_assinatura (
    nome, descricao, valor_mensal, quantidade_garrafas, premium_only, ativo
) VALUES
('Iniciante', 'Para quem está descobrindo o mundo dos vinhos.', 89.00, 2, FALSE, TRUE),
('Explorador', 'Para quem deseja variedade, personalização e benefícios extras.', 179.00, 4, FALSE, TRUE),
('Sommelier', 'Experiência premium para quem busca rótulos mais exclusivos.', 299.00, 6, TRUE, TRUE);

-- ---------------------------------------------------------
-- BENEFÍCIOS DOS PLANOS
-- ---------------------------------------------------------
INSERT INTO beneficio_plano (id_plano, tipo_beneficio, descricao, valor_beneficio, ativo) VALUES
(1, 'garrafas', 'Receba 2 garrafas selecionadas por mês.', NULL, TRUE),
(1, 'revista_digital', 'Acesso à revista digital com conteúdos sobre vinhos.', NULL, TRUE),
(1, 'harmonizacao', 'Guia de harmonização para ajudar nas combinações.', NULL, TRUE),

(2, 'garrafas', 'Receba 4 garrafas selecionadas + 1 surpresa.', NULL, TRUE),
(2, 'sommelia', 'Recomendações personalizadas com a SomelIA.', NULL, TRUE),
(2, 'comunidade_desconto', 'Acesso à comunidade exclusiva + 10% off em compras.', 10.00, TRUE),

(3, 'garrafas_premium', 'Receba 6 garrafas premium selecionadas.', NULL, TRUE),
(3, 'degustacao', 'Acesso a degustações com Giulio.', NULL, TRUE),
(3, 'frete_beneficios', 'Frete grátis + benefícios exclusivos.', NULL, TRUE);

-- ---------------------------------------------------------
-- ASSINATURAS
-- ---------------------------------------------------------
INSERT INTO assinatura (
    id_usuario, id_plano, data_inicio, data_fim, status_assinatura, data_proxima_cobranca, data_cancelamento
) VALUES
(1, 1, NOW(), NULL, 'ativa', DATE_ADD(NOW(), INTERVAL 30 DAY), NULL),
(2, 2, NOW(), NULL, 'ativa', DATE_ADD(NOW(), INTERVAL 30 DAY), NULL),
(3, 3, NOW(), NULL, 'ativa', DATE_ADD(NOW(), INTERVAL 30 DAY), NULL),
(4, 1, NOW(), NULL, 'pausada', DATE_ADD(NOW(), INTERVAL 45 DAY), NULL),
(5, 2, NOW(), NULL, 'cancelada', DATE_ADD(NOW(), INTERVAL 15 DAY), NOW());

-- ---------------------------------------------------------
-- PREFERÊNCIAS DE ASSINATURA
-- ---------------------------------------------------------
INSERT INTO preferencia_assinatura (
    id_assinatura, faixa_preco_min, faixa_preco_max, observacoes
) VALUES
(1, 50.00, 110.00, 'Prefere vinhos mais leves e fáceis de harmonizar.'),
(2, 70.00, 150.00, 'Gosta de tintos e espumantes para ocasiões especiais.'),
(3, 100.00, 250.00, 'Busca rótulos premium, maior complexidade e boa guarda.'),
(4, 50.00, 120.00, 'Prefere variedade entre branco e rosé.'),
(5, 80.00, 180.00, 'Interesse em rótulos selecionados e novidades.');

-- ---------------------------------------------------------
-- TIPOS PREFERIDOS NAS ASSINATURAS
-- ---------------------------------------------------------
INSERT INTO preferencia_assinatura_tipo (id_preferencia, tipo_vinho) VALUES
(1, 'branco'),
(1, 'rose'),
(2, 'tinto'),
(2, 'espumante'),
(3, 'tinto'),
(3, 'fortificado'),
(4, 'branco'),
(4, 'rose'),
(5, 'tinto');

-- ---------------------------------------------------------
-- BENEFÍCIOS DE FIDELIDADE
-- ---------------------------------------------------------
INSERT INTO beneficio_fidelidade (
    nome, descricao, tipo_beneficio, pontos_necessarios, ativo
) VALUES
('Desconto 10%', 'Desconto de 10% na próxima compra.', 'desconto', 100, TRUE),
('Brinde Especial', 'Brinde surpresa em pedidos selecionados.', 'brinde', 180, TRUE),
('Frete Grátis', 'Frete grátis para uma compra futura.', 'envio_gratis', 220, TRUE),
('Degustação Exclusiva', 'Acesso a evento especial para clientes fidelizados.', 'acesso_exclusivo', 500, TRUE);

-- ---------------------------------------------------------
-- CARRINHOS
-- ---------------------------------------------------------
INSERT INTO carrinho (id_usuario, status) VALUES
(1, 'ativo'),
(2, 'convertido'),
(3, 'ativo'),
(4, 'abandonado'),
(6, 'ativo'),
(7, 'convertido');

-- ---------------------------------------------------------
-- ITENS DOS CARRINHOS
-- ---------------------------------------------------------
INSERT INTO item_carrinho (id_carrinho, id_vinho, quantidade, preco_unitario) VALUES
(1, 1, 2, 89.90),
(1, 4, 1, 79.90),
(2, 3, 1, 109.90),
(2, 7, 2, 99.90),
(3, 6, 1, 84.90),
(3, 8, 3, 59.90),
(4, 2, 2, 69.90),
(5, 10, 1, 149.90),
(5, 11, 1, 139.90),
(6, 5, 2, 74.90);

-- ---------------------------------------------------------
-- PEDIDOS
-- ---------------------------------------------------------
INSERT INTO pedido (
    id_usuario, id_endereco, numero_pedido, data_pedido,
    valor_subtotal, valor_desconto, valor_frete, valor_total, status_pedido
) VALUES
(1, 1, 'PED20260001', DATE_SUB(NOW(), INTERVAL 15 DAY), 259.70, 10.00, 15.00, 264.70, 'entregue'),
(2, 2, 'PED20260002', DATE_SUB(NOW(), INTERVAL 10 DAY), 309.70, 20.00, 18.00, 307.70, 'enviado'),
(3, 3, 'PED20260003', DATE_SUB(NOW(), INTERVAL 7 DAY), 329.70, 0.00, 20.00, 349.70, 'processando'),
(5, 5, 'PED20260004', DATE_SUB(NOW(), INTERVAL 5 DAY), 149.90, 0.00, 12.00, 161.90, 'cancelado'),
(7, 7, 'PED20260005', DATE_SUB(NOW(), INTERVAL 3 DAY), 224.70, 15.00, 15.00, 224.70, 'entregue'),
(8, 8, 'PED20260006', DATE_SUB(NOW(), INTERVAL 1 DAY), 129.90, 0.00, 14.00, 143.90, 'pendente');

-- ---------------------------------------------------------
-- ITENS DOS PEDIDOS
-- ---------------------------------------------------------
INSERT INTO item_pedido (id_pedido, id_vinho, quantidade, preco_unitario, subtotal) VALUES
(1, 1, 2, 89.90, 179.80),
(1, 4, 1, 79.90, 79.90),

(2, 3, 1, 109.90, 109.90),
(2, 7, 2, 99.90, 199.80),

(3, 10, 1, 149.90, 149.90),
(3, 11, 1, 139.90, 139.90),
(3, 8, 1, 39.90, 39.90),

(4, 10, 1, 149.90, 149.90),

(5, 5, 3, 74.90, 224.70),

(6, 9, 1, 129.90, 129.90);

-- ---------------------------------------------------------
-- PAGAMENTOS
-- ---------------------------------------------------------
INSERT INTO pagamento (
    id_pedido, tipo_pagamento, status_pagamento, valor, parcelas, codigo_transacao, data_pagamento
) VALUES
(1, 'pix', 'aprovado', 264.70, 1, 'PIX-2026-0001', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(2, 'credito', 'aprovado', 307.70, 3, 'CRD-2026-0002', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(3, 'debito', 'processando', 349.70, 1, 'DEB-2026-0003', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(4, 'boleto', 'recusado', 161.90, 1, 'BOL-2026-0004', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(5, 'pix', 'aprovado', 224.70, 1, 'PIX-2026-0005', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(6, 'transferencia', 'pendente', 143.90, 1, 'TRF-2026-0006', NULL);

-- ---------------------------------------------------------
-- AVALIAÇÕES
-- ---------------------------------------------------------
INSERT INTO avaliacao (
    id_usuario, id_vinho, nota, comentario, status_aprovacao, data_avaliacao, data_aprovacao
) VALUES
(1, 1, 5, 'Excelente vinho, muito equilibrado e com ótimo final.', 'aprovado', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
(2, 7, 4, 'Ótimo espumante, muito elegante para celebrações.', 'aprovado', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(3, 10, 5, 'Rótulo premium de altíssimo nível, vale muito a pena.', 'aprovado', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(4, 2, 3, 'Bom para o dia a dia, custo-benefício interessante.', 'pendente', DATE_SUB(NOW(), INTERVAL 14 DAY), NULL),
(5, 9, 4, 'Muito bom com sobremesas, gostei bastante.', 'aprovado', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(7, 5, 5, 'Refrescante e perfeito para dias quentes.', 'aprovado', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ---------------------------------------------------------
-- PONTUAÇÕES DE FIDELIDADE
-- ---------------------------------------------------------
INSERT INTO pontuacao_usuario (
    id_usuario, pontos, tipo_movimentacao, descricao, data_movimentacao, referencia_id
) VALUES
(1, 50, 'compra', 'Pontos gerados pelo pedido PED20260001', DATE_SUB(NOW(), INTERVAL 15 DAY), 1),
(1, 10, 'avaliacao', 'Pontos por avaliação aprovada de produto', DATE_SUB(NOW(), INTERVAL 11 DAY), 1),
(2, 60, 'compra', 'Pontos gerados pelo pedido PED20260002', DATE_SUB(NOW(), INTERVAL 10 DAY), 2),
(3, 70, 'compra', 'Pontos gerados pelo pedido PED20260003', DATE_SUB(NOW(), INTERVAL 7 DAY), 3),
(5, -40, 'resgate', 'Resgate parcial de benefício de fidelidade', DATE_SUB(NOW(), INTERVAL 4 DAY), 2),
(7, 45, 'compra', 'Pontos gerados pelo pedido PED20260005', DATE_SUB(NOW(), INTERVAL 3 DAY), 5),
(8, 30, 'promocao', 'Campanha promocional de boas-vindas', DATE_SUB(NOW(), INTERVAL 1 DAY), NULL),
(9, 100, 'ajuste_admin', 'Ajuste manual feito pela administração', NOW(), NULL);

-- =========================================================
-- CONSULTAS DE VERIFICAÇÃO
-- =========================================================
-- SELECT * FROM usuario;
-- SELECT * FROM vinho;
-- SELECT * FROM pedido;
-- SELECT * FROM pagamento;
-- SELECT * FROM avaliacao;
-- SELECT * FROM assinatura;
-- SELECT * FROM vw_vendas_por_vinho;
-- SELECT * FROM vw_clientes_assinatura_ativa;