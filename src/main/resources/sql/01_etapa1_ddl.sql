-- ==============================================================================
-- DISCIPLINA: ADS1253 - Programação Orientada a Objetos com Banco de Dados
-- PROFESSOR: Welington Júlio
-- CURSO: Análise e Desenvolvimento de Sistemas (ADS) - PUC Goiás
-- ATIVIDADE: Desafio Prático - Módulo Fornecedores e Produtos
-- ==============================================================================

-- ==============================================================================
-- ETAPA 1: MODELAGEM E DDL (Data Definition Language)
-- ==============================================================================

-- 1. Criação da tabela "fornecedor"
--    - id_fornecedor: identificador único auto-incrementável (SERIAL) e chave primária
--    - nome: razão social ou nome do fornecedor (VARCHAR, obrigatório / NOT NULL)
--    - telefone: contato do fornecedor (VARCHAR, opcional / NULL)
CREATE TABLE IF NOT EXISTS fornecedor (
    id_fornecedor SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20)
);

-- 2. Adição da coluna "id_fornecedor" na tabela "produto" e definição da chave estrangeira
--    Caso a tabela produto já exista dos encontros anteriores, usamos ALTER TABLE:
ALTER TABLE produto 
    ADD COLUMN IF NOT EXISTS id_fornecedor INTEGER;

-- Adiciona a constraint de Chave Estrangeira com a regra ON DELETE RESTRICT
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

-- ==============================================================================
-- JUSTIFICATIVA DA ESCOLHA DO 'ON DELETE RESTRICT' (vs. ON DELETE CASCADE):
-- ==============================================================================
/*
 JUSTIFICATIVA TÉCNICA:
 A opção ON DELETE RESTRICT garante a preservação rigorosa da integridade referencial
 do modelo de negócios. 

 Caso fosse utilizado ON DELETE CASCADE, a exclusão acidental ou não intencional de um 
 fornecedor resultaria na remoção automática e silenciosa de todos os produtos fornecidos 
 por ele. Isso causaria:
 1. Perda irreversível de dados cadastrais de estoque e catálogo da loja;
 2. Inconsistências graves com o histórico de vendas (tabelas 'pedido' e 'pedido_item');
 3. Falta de rastreabilidade contábil e operacional.

 Com o ON DELETE RESTRICT, o PostgreSQL rejeita ativamente a tentativa de exclusão do 
 fornecedor enquanto houver qualquer produto associado a ele, obrigando o usuário/sistema
 a primeiro reatribuir ou gerenciar os produtos órfãos antes da exclusão.
*/
