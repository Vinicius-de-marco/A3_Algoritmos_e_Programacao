package contas;

/*
 * Valores monetários exatos. add soma, subtract subtrai e compareTo compara. Cada operação devolve
 * um resultado; os números são imutáveis.
 */
import java.math.BigDecimal;
/*
 * Datas sem horário. Permite conferir vencimentos e comparar datas sem fazer cálculos manuais de
 * dias e meses.
 */
import java.time.LocalDate;

/**
 * Guarda os dados de uma conta. Os getters leem os atributos. alterar e pagar
 * validam as mudanças; não existem setters que permitam ignorar essas regras.
 */
public class Conta {

    /**
     * Identificador da conta. private restringe o acesso e final permite
     * atribuir a referência apenas na inicialização ou no construtor. Não
     * existe setDocumento.
     */
    private final String documento;
    /**
     * Texto atual da despesa, validado e atualizado por alterar. getDescricao
     * apenas o consulta.
     */
    private String descricao;
    /**
     * Destinatário do pagamento. alterar atualiza o texto e getBeneficiario
     * permite a leitura.
     */
    private String beneficiario;
    /**
     * Valor original da obrigação. As baixas não reduzem este atributo; o saldo
     * é calculado separadamente.
     */
    private BigDecimal valor;
    /**
     * Soma das baixas já confirmadas. O texto "0.00" cria um decimal exato.
     * pagar soma novos valores a esse acumulado.
     */
    private BigDecimal valorPago = new BigDecimal("0.00");
    /**
     * Data da obrigação. LocalDate não guarda horário e não permite modificar a
     * própria instância.
     */
    private LocalDate vencimento;

    /**
     * Construtor: cria uma conta com documento, descrição, beneficiário, valor
     * e vencimento. Construtor tem o mesmo nome da classe e não declara tipo de
     * retorno.
     *
     * @param documento identificador obrigatório da conta; a verificação de
     * duplicidade ocorre em CadastroContas.incluir
     * @param descricao texto que identifica a despesa
     * @param beneficiario pessoa ou empresa que receberá o pagamento
     * @param valor valor original positivo, com no máximo duas casas decimais
     * significativas
     * @param vencimento data em que a obrigação vence; não pode ser null
     * @throws IllegalArgumentException se faltar um texto ou vencimento, ou se
     * o valor não atender às regras
     */
    public Conta(String documento, String descricao, String beneficiario,
            BigDecimal valor, LocalDate vencimento) {
        this.documento = textoObrigatorio(documento);
        alterar(descricao, beneficiario, valor, vencimento);
    }

    /**
     * Atualiza os dados editáveis de uma conta. Faz o papel de uma alteração
     * controlada de vários atributos, em vez de oferecer setters independentes.
     *
     * @param descricao nova descrição obrigatória
     * @param beneficiario novo beneficiário obrigatório
     * @param valor novo valor original, positivo e não inferior ao total já
     * pago
     * @param vencimento novo vencimento não nulo
     * @throws IllegalArgumentException se qualquer dado recebido violar as
     * regras da conta
     */
    public void alterar(String descricao, String beneficiario,
            BigDecimal valor, LocalDate vencimento) {
        String novaDescricao = textoObrigatorio(descricao);
        String novoBeneficiario = textoObrigatorio(beneficiario);
        validarValor(valor);
        if (valor.compareTo(valorPago) < 0) {
            throw new IllegalArgumentException("O valor não pode ser menor que o total já pago.");
        }
        if (vencimento == null) {
            throw new IllegalArgumentException("Informe a data de vencimento.");
        }

        this.descricao = novaDescricao;
        this.beneficiario = novoBeneficiario;
        this.valor = valor.setScale(2);
        this.vencimento = vencimento;
    }

    /**
     * Registra uma nova baixa parcial ou total. Atualiza o acumulado valorPago,
     * que depois será usado pelo getter getSaldo.
     *
     * @param pagamento quantia desta baixa; não representa o total acumulado
     * @throws IllegalArgumentException se o pagamento for inválido ou
     * ultrapassar o saldo
     */
    public void pagar(BigDecimal pagamento) {
        validarValor(pagamento);
        if (pagamento.compareTo(getSaldo()) > 0) {
            throw new IllegalArgumentException("O pagamento não pode superar o saldo devedor.");
        }
        valorPago = valorPago.add(pagamento).setScale(2);
    }

    /**
     * Função auxiliar privada e estática que valida e prepara um texto. Não
     * precisa ler atributos de uma conta para executar.
     *
     * @param texto texto recebido de um construtor ou método de alteração
     * @return texto sem espaços nas extremidades
     * @throws IllegalArgumentException se o argumento for null, vazio ou
     * contiver apenas espaços
     */
    private static String textoObrigatorio(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha todos os campos de texto.");
        }
        return texto.trim();
    }

    /**
     * Verifica a regra monetária usada tanto pelo valor original quanto pelos
     * pagamentos.
     *
     * @param valor número decimal a validar
     * @throws IllegalArgumentException se o valor não for positivo ou exceder a
     * precisão permitida
     */
    private static void validarValor(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }
        if (valor.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Use no máximo duas casas decimais.");
        }
    }
