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

    /**
     * Permite registrar mais de uma baixa, repetindo a pergunta após cada
     * tentativa.
     */
    public static void baixaContas() {
        do {
            baixar();
        } while (confirmar("Deseja realizar outra baixa?"));
    }

    /**
     * Localiza a conta, valida o pagamento e só registra a baixa após a
     * confirmação.
     */
    private static void baixar() {
        Conta conta = localizar();
        if (conta == null) {
            return;
        }
        if (conta.getSaldo().signum() == 0) {
            avisar("Esta conta já está quitada.");
            return;
        }
        BigDecimal pagamento;
        while (true) {
            pagamento = valor(detalhes(conta) + "\n\nValor do pagamento:");
            if (pagamento == null) {
                return;
            }
            if (pagamento.compareTo(conta.getSaldo()) <= 0) {
                break;
            }
            avisar("O pagamento não pode superar o saldo devedor.");
        }
        if (confirmar("Saldo atual: " + dinheiro(conta.getSaldo())
                + "\nValor pago: " + dinheiro(pagamento)
                + "\nSaldo final: " + dinheiro(conta.getSaldo().subtract(pagamento))
                + "\n\nConfirma baixa?")) {
            conta.pagar(pagamento);
            avisar("Pagamento registrado.");
        }
    }

    /**
     * Seleciona o relatório, lê o intervalo de datas e exibe o texto produzido
     * por
     */
    public static void relatorios() {
        while (true) {
            String opcao = texto("RELATÓRIOS\n\n1 - Contas a pagar por período\n"
                    + "2 - Contas vencidas por período\n3 - Balanço de despesas por período\n0 - Retornar");
            if (opcao == null || opcao.equals("0")) {
                return;
            }
            if (!opcao.equals("1") && !opcao.equals("2") && !opcao.equals("3")) {
                avisar("Opção inválida.");
                continue;
            }
            LocalDate inicio = data("Data inicial do período:");
            if (inicio == null) {
                continue;
            }
            LocalDate fim;
            while (true) {
                fim = data("Data final do período:");
                if (fim == null || !fim.isBefore(inicio)) {
                    break;
                }
                avisar("A data final deve ser igual ou posterior à inicial.");
            }
            if (fim == null) {
                continue;
            }
            String texto;
            if (opcao.equals("3")) {
                texto = balanco(listarContas(), inicio, fim);
            } else {
                boolean somenteVencidas = false;
                if (opcao.equals("2")) {
                    somenteVencidas = true;
                }
                texto = contasPorPeriodo(listarContas(), inicio,
                        fim, somenteVencidas, LocalDate.now());
            }
            exibir("Relatório", texto);
        }
    }

    /**
     * Procura uma conta pelo documento e devolve o próprio objeto armazenado,
     * quando encontrado.
     *
     * @param documento identificador a procurar
     * @return objeto encontrado, ou null quando não houver correspondência
     */
    private static Conta buscarConta(String documento) {
        if (documento == null) {
            return null;
        }
        for (int i = 0; i < quantidade; i++) {
            if (contas[i].getDocumento().equalsIgnoreCase(documento.trim())) {
                return contas[i];
            }
        }
        return null;
    }

    /**
     * Insere uma conta na primeira posição livre do vetor.
     *
     * @param conta objeto Conta já construído e validado
     * @throws IllegalArgumentException se a conta for null, tiver documento
     * duplicado ou o vetor estiver cheio
     */
    private static void guardarConta(Conta conta) {
        if (conta == null) {
            throw new IllegalArgumentException("Informe uma conta.");
        }
        if (buscarConta(conta.getDocumento()) != null) {
            throw new IllegalArgumentException("Este documento já está cadastrado.");
        }
        if (quantidade == contas.length) {
            throw new IllegalArgumentException("O cadastro está cheio.");
        }
        contas[quantidade] = conta;
        quantidade++;
    }

    /**
     * Remove a conta identificada e reorganiza o vetor para reaproveitar sua
     * posição.
     *
     * @param documento identificador da conta a remover
     * @throws IllegalArgumentException se o documento não estiver no cadastro
     */
    private static void removerConta(String documento) {
        Conta conta = buscarConta(documento);
        if (conta == null) {
            throw new IllegalArgumentException("Documento não encontrado.");
        }
        for (int i = 0; i < quantidade; i++) {
            if (contas[i] == conta) {
                for (int j = i; j < quantidade - 1; j++) {
                    contas[j] = contas[j + 1];
                }
                quantidade--;
                contas[quantidade] = null;
                return;
            }
        }
    }

    /**
     * Entrega um novo vetor com somente as posições ocupadas do cadastro. O
     * vetor retornado é novo, mas aponta para os mesmos objetos Conta.
     * Reordenar a cópia não muda o cadastro.
     *
     * @return novo vetor contendo as referências das contas cadastradas
     */
    private static Conta[] listarContas() {
        Conta[] copia = new Conta[quantidade];
        for (int i = 0; i < quantidade; i++) {
            copia[i] = contas[i];
        }
        return copia;
    }

    /**
     * Formatador compartilhado para mostrar datas como dia/mês/ano. Aqui ele
     * formata datas já existentes; a validação da entrada ocorre em data.
     */
    private static final DateTimeFormatter FORMATO_DATA_RELATORIO = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    /**
     * Converte um número monetário em texto formatado para exibição em reais.
     *
     * @param valor número a apresentar; os chamadores do projeto fornecem
     * valores não nulos
     * @return representação monetária para exibição, como R$ 500,00
     */
    private static String dinheiro(BigDecimal valor) {
        Locale brasil = new Locale("pt", "BR");
        NumberFormat formato = NumberFormat.getCurrencyInstance(brasil);
        return formato.format(valor);
    }

    /**
     * Monta o texto de uma única conta usando seus getters.
     *
     * @param conta objeto não nulo cujos dados serão apresentados
     * @return texto com os dados da conta
     */
    private static String detalhes(Conta conta) {
        String texto = "Documento: " + conta.getDocumento();
        texto = texto + "\nDescrição: " + conta.getDescricao();
        texto = texto + "\nBeneficiário: " + conta.getBeneficiario();
        texto = texto + "\nValor original: " + dinheiro(conta.getValor());
        texto = texto + "\nTotal pago: " + dinheiro(conta.getValorPago());
        texto = texto + "\nSaldo devedor: " + dinheiro(conta.getSaldo());
        texto = texto + "\nVencimento: " + conta.getVencimento().format(FORMATO_DATA_RELATORIO);
        return texto;
    }

    /**
     * Gera tanto contas a pagar quanto contas vencidas, de acordo com a opção
     * recebida.
     *
     * @param contas vetor não nulo, sem posições nulas; no programa, vem de
     * listarContas()
     * @param inicio primeiro vencimento aceito no intervalo
     * @param fim último vencimento aceito no intervalo
     * @param somenteVencidas true aplica o filtro de atraso; false lista todas
     * as contas em aberto do período
     * @param hoje data de referência para identificar atraso; fornecida pelo
     * menu de relatórios
     * @return relatório pronto como String; a exibição é feita por exibir
     * @throws IllegalArgumentException se as datas do intervalo forem nulas ou
     * estiverem invertidas
     */
    private static String contasPorPeriodo(Conta[] contas, LocalDate inicio, LocalDate fim,
            boolean somenteVencidas, LocalDate hoje) {
        validarPeriodo(inicio, fim);

        Conta[] ordenadas = new Conta[contas.length];
        for (int i = 0; i < contas.length; i++) {
            ordenadas[i] = contas[i];
        }

        for (int i = 0; i < ordenadas.length - 1; i++) {
            for (int j = i + 1; j < ordenadas.length; j++) {
                if (ordenadas[i].getVencimento().isAfter(ordenadas[j].getVencimento())) {
                    Conta auxiliar = ordenadas[i];
                    ordenadas[i] = ordenadas[j];
                    ordenadas[j] = auxiliar;
                }
            }
        }

        String titulo = "CONTAS A PAGAR";
        if (somenteVencidas) {
            titulo = "CONTAS VENCIDAS";
        }

        String texto = cabecalho(titulo, inicio, fim);
        texto = texto + "VENCIMENTO | SALDO | DOCUMENTO | DESCRIÇÃO | BENEFICIÁRIO\n\n";
        BigDecimal total = BigDecimal.ZERO;
        int quantidade = 0;

        for (int i = 0; i < ordenadas.length; i++) {
            Conta conta = ordenadas[i];
            boolean mostrar = true;

            if (!noPeriodo(conta.getVencimento(), inicio, fim)) {
                mostrar = false;
            }
            if (conta.getSaldo().signum() <= 0) {
                mostrar = false;
            }
            if (mostrar && somenteVencidas) {
                if (!conta.getVencimento().isBefore(hoje)) {
                    mostrar = false;
                }
            }

            if (mostrar) {
                texto = texto + conta.getVencimento().format(FORMATO_DATA_RELATORIO) + " | ";
                texto = texto + dinheiro(conta.getSaldo()) + " | ";
                texto = texto + conta.getDocumento() + " | ";
                texto = texto + conta.getDescricao() + " | ";
                texto = texto + conta.getBeneficiario() + "\n";
                total = total.add(conta.getSaldo());
                quantidade++;
            }
        }

        if (quantidade == 0) {
            texto = texto + "Nenhuma conta encontrada.\n";
        }
        texto = texto + "\nTotal em aberto: " + dinheiro(total);
        return texto;

    }

    /**
     * Calcula as despesas de cada mês do período e identifica o beneficiário
     * com maior soma no mês.
     *
     * @param contas vetor não nulo com as contas que ainda existem no cadastro
     * @param inicio limite inicial de vencimento, inclusive
     * @param fim limite final de vencimento, inclusive
     * @return texto do balanço mensal com total do período
     * @throws IllegalArgumentException se o intervalo estiver ausente ou
     * invertido
     */
    private static String balanco(Conta[] contas, LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        String texto = cabecalho("BALANÇO DE DESPESAS", inicio, fim);
        texto = texto + "Critério: valor original das contas pelo vencimento.\n";
        texto = texto + "Maior beneficiário: soma das despesas de cada beneficiário no mês.\n\n";
        texto = texto + "MÊS/ANO | TOTAL DE DESPESAS | MAIOR BENEFICIÁRIO\n\n";

        BigDecimal totalPeriodo = BigDecimal.ZERO;
        LocalDate mes = inicio.withDayOfMonth(1);
        DateTimeFormatter formatoMes = DateTimeFormatter.ofPattern("MM/uuuu");

        while (!mes.isAfter(fim)) {
            BigDecimal totalMes = BigDecimal.ZERO;
            BigDecimal maiorValor = BigDecimal.ZERO;
            String maiorBeneficiario = "Sem despesas";

            for (int i = 0; i < contas.length; i++) {
                Conta conta = contas[i];

                if (pertenceAoMes(conta, mes, inicio, fim)) {
                    totalMes = totalMes.add(conta.getValor());
                    BigDecimal totalBeneficiario = somarBeneficiario(
                            contas, conta.getBeneficiario(), mes, inicio, fim);

                    if (totalBeneficiario.compareTo(maiorValor) > 0) {
                        maiorValor = totalBeneficiario;
                        maiorBeneficiario = conta.getBeneficiario();
                    }
                }
            }

            texto = texto + mes.format(formatoMes) + " | ";
            texto = texto + dinheiro(totalMes) + " | " + maiorBeneficiario + "\n";
            totalPeriodo = totalPeriodo.add(totalMes);
            mes = mes.plusMonths(1);
        }

        texto = texto + "\nTotal do período: " + dinheiro(totalPeriodo);
        return texto;
    
}
