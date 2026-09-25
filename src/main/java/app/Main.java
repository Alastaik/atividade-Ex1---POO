package app;

import java.util.Scanner;

/**
 * Ponto de Entrada Principal da Aplicação
 * Disciplina: ADS1253 - POO com Banco de Dados
 * Professor: Welington Júlio
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Se houver parâmetros ou execução não interativa, roda diretamente o teste integrado
        if (args.length > 0 && args[0].equalsIgnoreCase("--batch")) {
            TesteEtapasCompleto.main(args);
            return;
        }

        System.out.println("================================================================================");
        System.out.println("     SISTEMA DE VENDAS - MÓDULO FORNECEDORES & INTEGRIDADE REFERENCIAL          ");
        System.out.println("     PUC Goiás - Análise e Desenvolvimento de Sistemas                          ");
        System.out.println("================================================================================");
        System.out.println("Escolha uma opção para executar:");
        System.out.println(" 1 - Executar Bateria Completa (Etapas 1, 2, 3 e 4)");
        System.out.println(" 2 - Etapa 2: Teste CRUD FornecedorDAO");
        System.out.println(" 3 - Etapa 3: Teste Consulta INNER JOIN (Produto x Fornecedor)");
        System.out.println(" 4 - Etapa 4: Teste de Integridade Referencial (ON DELETE RESTRICT)");
        System.out.println(" 0 - Sair");
        System.out.print("Opção: ");

        String opcao = "";
        if (scanner.hasNextLine()) {
            opcao = scanner.nextLine().trim();
        }

        switch (opcao) {
            case "2":
                TesteEtapa2FornecedorCRUD.main(args);
                break;
            case "3":
                TesteEtapa3Join.main(args);
                break;
            case "4":
                TesteEtapa4IntegridadeReferencial.main(args);
                break;
            case "0":
                System.out.println("Encerrando aplicação...");
                break;
            case "1":
            default:
                System.out.println("\nExecutando teste integrado de todas as etapas...");
                TesteEtapasCompleto.main(args);
                break;
        }
    }
}
