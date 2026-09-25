-- ==============================================================================
-- DISCIPLINA: ADS1253 - Programação Orientada a Objetos com Banco de Dados
-- PROFESSOR: Welington Júlio
-- CURSO: Análise e Desenvolvimento de Sistemas (ADS) - PUC Goiás
-- ATIVIDADE: Encontro 14 - Atividade Estruturada 1 (Ex1)
-- ==============================================================================

-- TODO 1.1: Crie a tabela fornecedor (id_fornecedor SERIAL PRIMARY KEY, nome VARCHAR(100) NOT NULL, telefone VARCHAR(20))
CREATE TABLE IF NOT EXISTS fornecedor (
    id_fornecedor SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20)
);

-- TODO 1.2: Adicione a coluna id_fornecedor em produto e crie a FOREIGN KEY com ON DELETE RESTRICT
ALTER TABLE produto 
    ADD COLUMN IF NOT EXISTS id_fornecedor INTEGER;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_produto_fornecedor'
          AND table_name = 'produto'
    ) THEN
        ALTER TABLE produto
            ADD CONSTRAINT fk_produto_fornecedor
            FOREIGN KEY (id_fornecedor)
            REFERENCES fornecedor(id_fornecedor)
            ON DELETE RESTRICT;
    END IF;
END $$;