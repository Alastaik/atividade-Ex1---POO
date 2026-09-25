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

-- TODO 1.3: Adicione um comentário no SQL explicando a escolha do ON DELETE RESTRICT
/*
 JUSTIFICATIVA TÉCNICA DA ESCOLHA DO 'ON DELETE RESTRICT' EM VEZ DE 'CASCADE':

 A regra ON DELETE RESTRICT foi escolhida para assegurar a integridade referencial e 
 a segurança do modelo de negócios. 

 Caso fosse utilizado ON DELETE CASCADE, qualquer exclusão acidental ou intencional 
 de um fornecedor apagaria automaticamente do catálogo todos os produtos associados a ele.
 Isso ocasionaria:
 1. Perda irreversível de dados de estoque, catálogo e preços;
 2. Inconsistências graves com o histórico de vendas já registradas (tabelas 'pedido' e 'pedido_item');
 3. Falta de controle gerencial e operacional.

 Com o ON DELETE RESTRICT, o motor relacional do PostgreSQL recusa categoricamente a 
 exclusão do fornecedor enquanto houver produtos cadastrados com o seu identificador, 
 forçando o usuário ou sistema a reatribuir os produtos a outro fornecedor antes de permitir 
 a exclusão do registro pai.
*/
