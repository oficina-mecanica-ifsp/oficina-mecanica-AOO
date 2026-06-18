-- ============================================================
-- SCRIPT DE POPULAÇÃO — Oficina Mecânica IFSP
-- Execute APÓS subir a aplicação (tabelas criadas pelo Hibernate)
-- O usuário admin@oficina.com / admin123 é criado pelo DataInitializer
-- ============================================================

USE mecanica_IFSP;

-- ============================================================
-- USUÁRIOS (senha = BCrypt de "senha123")
-- ============================================================
INSERT INTO usuario (nome, email, senha, tipo) VALUES
  ('Maria Atendente', 'atendente@oficina.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOe3xPiPMtcgJHkJ3M2PtE1W8bNfCJ7zy', 'ATENDENTE'),
  ('João Mecânico',   'mecanico@oficina.com',  '$2a$10$7EqJtq98hPqEX7fNZaFWoOe3xPiPMtcgJHkJ3M2PtE1W8bNfCJ7zy', 'MECANICO'),
  ('Pedro Mecânico2', 'mecanico2@oficina.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOe3xPiPMtcgJHkJ3M2PtE1W8bNfCJ7zy', 'MECANICO')
ON DUPLICATE KEY UPDATE nome = VALUES(nome);

-- ============================================================
-- CLIENTES
-- ============================================================
INSERT INTO cliente (nome, email, telefone, documento) VALUES
  ('Carlos Silva',             'carlos@email.com',        '11999990001', '12345678909'),
  ('Ana Pereira',              'ana@email.com',           '11999990002', '98765432100'),
  ('Roberto Santos',           'roberto@email.com',       '11999990003', '45678912300'),
  ('Transportadora Rápida',    'contato@transportadora.com', '1133334444', '12345678000199'),
  ('Auto Peças Bonfim ME',     'bonfim@autopecas.com',    '1144445555', '98765432000111')
ON DUPLICATE KEY UPDATE nome = VALUES(nome);

-- ============================================================
-- CARROS
-- ============================================================
INSERT INTO carro (placa, modelo, marca, ano, cliente_id) VALUES
  ('ABC1D23', 'Civic',     'Honda',      2020, (SELECT id FROM cliente WHERE documento = '12345678909')),
  ('XYZ9A10', 'Corolla',   'Toyota',     2019, (SELECT id FROM cliente WHERE documento = '98765432100')),
  ('DEF4G56', 'HB20',      'Hyundai',    2021, (SELECT id FROM cliente WHERE documento = '45678912300')),
  ('GHI7J89', 'Fiorino',   'Fiat',       2018, (SELECT id FROM cliente WHERE documento = '12345678000199')),
  ('JKL2M34', 'Gol',       'Volkswagen', 2017, (SELECT id FROM cliente WHERE documento = '12345678909')),
  ('MNO5P67', 'Onix',      'Chevrolet',  2022, (SELECT id FROM cliente WHERE documento = '98765432100'))
ON DUPLICATE KEY UPDATE modelo = VALUES(modelo);

-- ============================================================
-- SERVIÇOS
-- ============================================================
INSERT INTO servico (nome, descricao, preco, duracao_estimada_minutos) VALUES
  ('Troca de Óleo',               'Troca de óleo do motor com filtro',                      80.00,  30),
  ('Alinhamento e Balanceamento', 'Alinhamento computadorizado e balanceamento das 4 rodas', 120.00, 60),
  ('Revisão de Freios',           'Inspeção e substituição de pastilhas e discos',           250.00, 120),
  ('Diagnóstico Eletrônico',      'Leitura de falhas com scanner OBD2',                      60.00,  45),
  ('Troca de Correia Dentada',    'Substituição de correia e tensor',                        380.00, 180),
  ('Limpeza de Bicos Injetores',  'Limpeza ultrassônica dos injetores',                     150.00, 90),
  ('Recarga de Ar Condicionado',  'Recarga de gás R134a com verificação de vazamentos',      180.00, 60)
ON DUPLICATE KEY UPDATE preco = VALUES(preco);

-- ============================================================
-- PRODUTOS / PEÇAS
-- ============================================================
INSERT INTO produto (nome, descricao, preco, tipo, quantidade, quantidade_minima) VALUES
  ('Filtro de Óleo Mann W914',    'Filtro para motores 1.0 a 2.0',              25.90, 'PECA',    20, 5),
  ('Óleo Motor Mobil 5W30 1L',   'Óleo sintético para motores flex',            32.50, 'PRODUTO', 50, 10),
  ('Pastilha de Freio Dianteira', 'Kit com 4 pastilhas TRW para eixo dianteiro', 85.00, 'PECA',   10, 3),
  ('Disco de Freio Dianteiro',   'Disco ventilado ATE para eixo dianteiro',     140.00, 'PECA',    8, 2),
  ('Filtro de Ar',               'Filtro de ar esportivo K&N',                   45.00, 'PECA',   15, 4),
  ('Vela de Ignição NGK',        'Vela iridium para motores flex (unidade)',      18.90, 'PECA',   40, 8),
  ('Fluido de Freio DOT 4',      'Fluido de freio 500ml Bosch',                  22.00, 'PRODUTO', 25, 5),
  ('Correia Dentada Gates',      'Correia com tensor para motores EA111',        120.00, 'PECA',    6, 2),
  ('Gás Refrigerante R134a',     'Gás para ar condicionado automotivo 1kg',       55.00, 'PRODUTO', 12, 3),
  ('Filtro de Combustível',      'Filtro de combustível universal',               35.00, 'PECA',   18, 4)
ON DUPLICATE KEY UPDATE preco = VALUES(preco);

-- ============================================================
-- NOTA: Ordens de Serviço e Pagamentos devem ser criados
-- via API para que as regras de negócio sejam aplicadas
-- corretamente (snapshot, decremento de estoque, etc.)
-- Use a coleção Postman para criar as OS de teste.
-- ============================================================

-- Verificação rápida
SELECT 'Usuários'  AS tabela, COUNT(*) AS total FROM usuario
UNION ALL
SELECT 'Clientes',  COUNT(*) FROM cliente
UNION ALL
SELECT 'Carros',    COUNT(*) FROM carro
UNION ALL
SELECT 'Serviços',  COUNT(*) FROM servico
UNION ALL
SELECT 'Produtos',  COUNT(*) FROM produto;
