
package contas;

// JOptionPane cria as caixas de entrada, os avisos e as confirmações do sistema.
import javax.swing.JOptionPane;
// JTextArea mostra consultas e relatórios; JScrollPane acrescenta a rolagem.
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
// UIManager permite escrever Sim, Não e Cancelar nos botões.
import javax.swing.UIManager;
// BigDecimal calcula valores monetários sem os erros de arredondamento de double.
import java.math.BigDecimal;
// LocalDate representa uma data, permite compará-la e avançar de mês.
import java.time.LocalDate;
// DateTimeFormatter define o formato; ResolverStyle.STRICT exige datas reais.
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
// DateTimeParseException é o erro tratado quando uma data não existe no calendário.
import java.time.format.DateTimeParseException;
// NumberFormat e Locale apresentam os valores no padrão brasileiro, em reais.
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Sistema de Contas a Pagar da XYZ Comércio de Produtos LTDA.
 * Os menus e as operações são métodos desta classe, seguindo a estrutura da main da equipe.
 * Cada conta é um objeto Conta, guardado no vetor abaixo.
 */
public class Main {
    // Vetor de até 100 objetos Conta. Os dados existem apenas durante a execução.
    private static Conta[] contas = new Conta[100];
    // Quantas posições estão ocupadas, começando pelo índice zero do vetor.
    private static int quantidade = 0;
    /**
     * Mostra as opções do sistema. Os textos são os mesmos da versão dos prints.
     * @return opção digitada, ou null se o usuário cancelar
     */
    public static String menuPrincipal() {
        return texto("XYZ COMÉRCIO DE PRODUTOS LTDA.\nSISTEMA DE CONTAS A PAGAR\n\n"
                + "1 - Cadastro de contas\n2 - Baixa de contas\n3 - Relatórios\n0 - Finalizar\n\n"
                + "Os dados são mantidos apenas durante esta execução.");
    }

    /**
     * Começa o programa, chama o menu e escolhe a operação com switch.
     * O do/while repete o menu enquanto a saída não for confirmada.
     * @param args argumentos de execução; não são utilizados neste trabalho
     */
    public static void main(String[] args) {
        // Deixa em português os botões das janelas de confirmação.
        UIManager.put("OptionPane.yesButtonText", "Sim");
        UIManager.put("OptionPane.noButtonText", "Não");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");

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
                    if (!confirmar("Finalizar? Os dados desta execução serão perdidos.")) {
                        opcao = "";
                    }
                    break;
                default:
                    avisar("Opção inválida.");
            }
        } while (!opcao.equals("0"));
    }