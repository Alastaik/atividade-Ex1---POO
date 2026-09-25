-- ==============================================================================
-- DADOS INICIAIS DE TESTE (DML - Data Manipulation Language)
-- ==============================================================================

-- Inserção de fornecedores modelo para testes
INSERT INTO fornecedor (nome, telefone) VALUES 
('Tech Distribuidora Brasil Ltda', '(11) 3456-7890'),
('Mega Eletrônicos Atacadista', '(62) 3222-1100'),
('Logística & Componentes Globais', '(21) 98765-4321')
ON CONFLICT DO NOTHING;

-- Exemplo de associação de produtos aos fornecedores recém-cadastrados
-- Atualiza produtos existentes para apontar para o primeiro fornecedor cadastrado
UPDATE produto 
SET id_fornecedor = (SELECT id_fornecedor FROM fornecedor ORDER BY id_fornecedor LIMIT 1)
WHERE id_fornecedor IS NULL;
