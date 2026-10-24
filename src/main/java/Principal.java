import javax.swing.JOptionPane;

public class Principal {

    public static void main(String[] args) {
        String opcao;

        do {
            opcao = JOptionPane.showInputDialog(
                    "XYZ COMÉRCIO DE PRODUTOS LTDA.\n"
                    + "SISTEMA DE CONTAS A PAGAR\n\n"
                    + "MENU PRINCIPAL\n\n"
                    + "1 - CADASTRO DE CONTAS\n"
                    + "2 - BAIXA DE CONTAS\n"
                    + "3 - RELATÓRIOS\n"
                    + "0 - FINALIZAR\n\n"
                    + "OPÇÃO:");

            if (opcao == null) {
                opcao = "0";
            }

            switch (opcao) {
                case "1":
                    JOptionPane.showMessageDialog(null, "Cadastro de Contas");
                    break;
                case "2":
                    JOptionPane.showMessageDialog(null, "Baixa de Contas");
                    break;
                case "3":
                    JOptionPane.showMessageDialog(null, "Relatórios");
                    break;
                case "0":
                    JOptionPane.showMessageDialog(null, "Sistema finalizado. Até logo!");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida. Digite 0, 1, 2 ou 3.");
            }

        } while (!opcao.equals("0"));
    }
}