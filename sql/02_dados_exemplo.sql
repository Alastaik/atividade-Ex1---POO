-- Dados de exemplo
INSERT INTO fornecedor (nome, telefone) VALUES 
('Tech Ltda', '(62) 3456-7890'),
('Mega loja', '(62) 3222-1100'),
('Logística S', '(99) 98765-4321')
ON CONFLICT DO NOTHING;

-- Associar produtos ao primeiro fornecedor
UPDATE produto 
SET id_fornecedor = (SELECT id_fornecedor FROM fornecedor ORDER BY id_fornecedor LIMIT 1)
WHERE id_fornecedor IS NULL;
