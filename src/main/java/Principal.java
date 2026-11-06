
import javax.swing.JOptionPane;

/**
 * Sistema de Contas a Pagar da XYZ Comércio de Produtos LTDA. Responsável pelo
 * gerenciamento de cadastro, baixa e relatórios de contas.
 *
 * @author Marcelo Augusto Schmoeller
 * @author Nome do Integrante 2
 * @author Nome do Integrante 3
 * @author Nome do Integrante 4
 * @author Nome do Integrante 5
 */
public class Principal {

    /**
     * Exibe o menu principal do sistema e direciona o usuário para a
     * funcionalidade escolhida.
     * 
     * @return opcão selecionada pelo usuário.
     */
    public static String menuPrincipal() {

        return JOptionPane.showInputDialog(
                "XYZ COMÉRCIO DE PRODUTOS LTDA.\n"
                + "SISTEMA DE CONTAS A PAGAR\n\n"
                + "MENU PRINCIPAL\n\n"
                + "1 - CADASTRO DE CONTAS\n"
                + "2 - BAIXA DE CONTAS\n"
                + "3 - RELATÓRIOS\n"
                + "0 - FINALIZAR\n\n"
                + "OPÇÃO:");

    }

    /**
     * Realiza o cadastro de contas a pagar no sistema.
     */
    public static void cadastroContas() {
        JOptionPane.showMessageDialog(null, "Cadastro de Contas (em construção)");

    }

    /**
     * Realiza a baixa das contas cadastradas no sistema.
     */
    public static void baixaContas() {
        JOptionPane.showMessageDialog(null, "Baixa de Contas (em construção)");
    }

    /**
     * Apresenta os relatórios referentes às contas do sistema.
     */
    public static void relatorios() {
        JOptionPane.showMessageDialog(null, "Relatórios (em construção)");
    }

    public static void main(String[] args) {

        String opcao;

        do {
            opcao = menuPrincipal();

            if (opcao == null) {
                opcao = "0";
            }

            switch (opcao) {
                case "1":
                    cadastroContas();
                    break;

                case "2":
                    baixaContas();
                    break;

                case "3":
                    relatorios();
                    break;

                case "0":
                    JOptionPane.showMessageDialog(null, "Sistema finalizado. Até logo!");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida! Digite 0, 1, 2 ou 3.");
            }

        } while (!opcao.equals("0"));
    }
}
