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
 * Sistema de Contas a Pagar da XYZ Comércio de Produtos LTDA. Os menus e as
 * operações são métodos desta classe, seguindo a estrutura da main da equipe.
 * Cada conta é um objeto Conta, guardado no vetor abaixo.
 */
public class Main {

    // Vetor de até 100 objetos Conta. Os dados existem apenas durante a execução.
    private static Conta[] contas = new Conta[100];
    // Quantas posições estão ocupadas, começando pelo índice zero do vetor.
    private static int quantidade = 0;

    /**
     * Mostra as opções do sistema. Os textos são os mesmos da versão dos
     * prints.
     *
     * @return opção digitada, ou null se o usuário cancelar
     */
    public static String menuPrincipal() {
        return texto("XYZ COMÉRCIO DE PRODUTOS LTDA.\nSISTEMA DE CONTAS A PAGAR\n\n"
                + "1 - Cadastro de contas\n2 - Baixa de contas\n3 - Relatórios\n0 - Finalizar\n\n"
                + "Os dados são mantidos apenas durante esta execução.");
    }

    /**
     * Começa o programa, chama o menu e escolhe a operação com switch. O
     * do/while repete o menu enquanto a saída não for confirmada.
     *
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

    /**
     * Apresenta o submenu de manutenção das contas.
     */
    public static void cadastroContas() {
        while (true) {
            String opcao = texto("CADASTRO DE CONTAS\n\n1 - Inclusão\n2 - Alteração\n"
                    + "3 - Consulta\n4 - Exclusão\n0 - Retornar");
            if (opcao == null || opcao.equals("0")) {
                return;
            }
            switch (opcao) {
                case "1":
                    do {
                        incluir();
                    } while (confirmar("Deseja realizar outra inclusão?"));
                    break;
                case "2":
                    do {
                        alterar();
                    } while (confirmar("Deseja realizar outra alteração?"));
                    break;
                case "3":
                    do {
                        consultar();
                    } while (confirmar("Deseja realizar outra consulta?"));
                    break;
                case "4":
                    do {
                        excluir();
                    } while (confirmar("Deseja realizar outra exclusão?"));
                    break;
                default:
                    avisar("Opção inválida.");
            }
        }
    }

    /**
     * Coordena a inclusão pela interface, desde a leitura do documento até a
     * mensagem de sucesso.
     */
    private static void incluir() {
        String documento;
        while (true) {
            documento = texto("INCLUSÃO\nNúmero do documento:");
            if (documento == null) {
                return;
            }
            if (buscarConta(documento) == null) {
                break;
            }
            avisar("Documento já cadastrado. Informe outro número.");
        }
        Conta nova = lerDados(documento, BigDecimal.ZERO);
        if (nova != null && confirmar(detalhes(nova) + "\n\nConfirma inclusão?")) {
            try {
                guardarConta(nova);
                avisar("Conta incluída.");
            } catch (IllegalArgumentException erro) {
                avisar(erro.getMessage());
            }
        }

    }

    /**
     * Lê os campos e cria uma Conta para a inclusão. Se o usuário cancelar
     * qualquer campo, retorna null e não inclui nada no vetor.
     *
     * @param documento identificador já definido; não é solicitado novamente
     * neste método
     * @param minimo menor valor original permitido para esta operação
     * @return nova Conta com os dados lidos, ou null caso o usuário cancele
     */
    private static Conta lerDados(String documento, BigDecimal minimo) {
        String descricao = texto("Descrição:");
        if (descricao == null) {
            return null;
        }
        String beneficiario = texto("Beneficiário:");
        if (beneficiario == null) {
            return null;
        }
        BigDecimal valor = lerValorMinimo(minimo);
        if (valor == null) {
            return null;
        }
        LocalDate vencimento = data("Data de vencimento:");
        if (vencimento == null) {
            return null;
        }
        return new Conta(documento, descricao, beneficiario, valor, vencimento);
    }

    /**
     * Pede o valor original até receber uma quantia que não seja menor que o
     * total já pago. Cancelar abandona a leitura.
     *
     * @param minimo menor valor permitido
     * @return valor aceito ou null no cancelamento
     */
    private static BigDecimal lerValorMinimo(BigDecimal minimo) {
        while (true) {
            BigDecimal valor = valor("Valor original da conta:");
            if (valor == null) {
                return null;
            }
            if (valor.compareTo(minimo) >= 0) {
                return valor;
            }
            avisar("O valor não pode ser menor que o total já pago: "
                    + dinheiro(minimo));
        }
    }

    /**
     * Centraliza a pergunta pelo documento e a busca da conta.
     *
     * @return conta localizada, ou null em cancelamento ou documento
     * inexistente
     */
    private static Conta localizar() {
        String documento = texto("Número do documento:");
        if (documento == null) {
            return null;
        }
        Conta conta = buscarConta(documento);
        if (conta == null) {
            avisar("Documento não encontrado.");
        }
        return conta;
    }

    /**
     * Localiza a conta, lê os novos dados em variáveis e monta o resumo.
     * Somente depois da confirmação chama conta.alterar. Mantém documento e
     * pagamentos anteriores.
     */
    private static void alterar() {
        Conta conta = localizar();
        if (conta == null) {
            return;
        }
        exibir("Dados atuais", detalhes(conta));

        String descricao = texto("Descrição:");
        if (descricao == null) {
            return;
        }
        String beneficiario = texto("Beneficiário:");
        if (beneficiario == null) {
            return;
        }
        BigDecimal valor = lerValorMinimo(conta.getValorPago());
        if (valor == null) {
            return;
        }
        LocalDate vencimento = data("Data de vencimento:");
        if (vencimento == null) {
            return;
        }

        BigDecimal novoSaldo = valor.subtract(conta.getValorPago());
        String resumo = "Documento: " + conta.getDocumento();
        resumo = resumo + "\nDescrição: " + descricao;
        resumo = resumo + "\nBeneficiário: " + beneficiario;
        resumo = resumo + "\nNovo valor: " + dinheiro(valor);
        resumo = resumo + "\nTotal já pago: " + dinheiro(conta.getValorPago());
        resumo = resumo + "\nNovo saldo: " + dinheiro(novoSaldo);
        resumo = resumo + "\nVencimento: " + vencimento.format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu"));

        if (confirmar(resumo + "\n\nConfirma alteração?")) {
            conta.alterar(descricao, beneficiario, valor, vencimento);
            avisar("Conta alterada. O documento e o total pago foram preservados.");
        }
    }

    /**
     * Mostra os dados de uma conta sem modificar seus atributos.
     */
    private static void consultar() {
        Conta conta = localizar();
        if (conta != null) {
            exibir("Consulta", detalhes(conta));
        }
    }

    /**
     * Coordena a remoção de uma conta pela interface.
     */
    private static void excluir() {
        Conta conta = localizar();
        if (conta != null && confirmar(detalhes(conta) + "\n\nConfirma exclusão?")) {
            removerConta(conta.getDocumento());
            avisar("Conta excluída.");
        }
    
}
