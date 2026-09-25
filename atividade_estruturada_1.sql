-- Tabela fornecedor
CREATE TABLE IF NOT EXISTS fornecedor (
    id_fornecedor SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20)
);

-- Coluna e FK em produto
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

-- ON DELETE RESTRICT: impede a exclusão do fornecedor caso existam produtos vinculados a ele.
