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
        System.out.println(" 1 - Executar Teste Oficial do Roteiro (TesteAtividadeEstruturada1)");
        System.out.println(" 2 - Executar Bateria Integrada Completa (Etapas 0 a 4)");
        System.out.println(" 3 - Etapa 2: Teste Isolado CRUD FornecedorDAO");
        System.out.println(" 4 - Etapa 3: Teste Isolado INNER JOIN (Produto x Fornecedor)");
        System.out.println(" 5 - Etapa 4: Teste Isolado Integridade Referencial (ON DELETE RESTRICT)");
        System.out.println(" 0 - Sair");
        System.out.print("Opção: ");

        String opcao = "";
        if (scanner.hasNextLine()) {
            opcao = scanner.nextLine().trim();
        }

        switch (opcao) {
            case "1":
                TesteAtividadeEstruturada1.main(args);
                break;
            case "2":
                TesteEtapasCompleto.main(args);
                break;
            case "3":
                TesteEtapa2FornecedorCRUD.main(args);
                break;
            case "4":
                TesteEtapa3Join.main(args);
                break;
            case "5":
                TesteEtapa4IntegridadeReferencial.main(args);
                break;
            case "0":
                System.out.println("Encerrando aplicação...");
                break;
            default:
                System.out.println("\nExecutando teste oficial do roteiro...");
                TesteAtividadeEstruturada1.main(args);
                break;
        }
    }
}
