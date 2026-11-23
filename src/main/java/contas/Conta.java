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
 * Guarda os dados de uma conta. Os getters leem os atributos. alterar e pagar validam as mudanças;
 * não existem setters que permitam ignorar essas regras.
 */
public class Conta {
    /**
     * Identificador da conta. private restringe o acesso e final permite atribuir a referência
     * apenas na inicialização ou no construtor. Não existe setDocumento.
     */
    private final String documento;
    /**
     * Texto atual da despesa, validado e atualizado por alterar. getDescricao apenas o consulta.
     */
    private String descricao;
    /**
     * Destinatário do pagamento. alterar atualiza o texto e getBeneficiario permite a leitura.
     */
    private String beneficiario;
    /**
     * Valor original da obrigação. As baixas não reduzem este atributo; o saldo é calculado
     * separadamente.
     */
    private BigDecimal valor;
    /**
     * Soma das baixas já confirmadas. O texto "0.00" cria um decimal exato. pagar soma novos
     * valores a esse acumulado.
     */
    private BigDecimal valorPago = new BigDecimal("0.00");
    /**
     * Data da obrigação. LocalDate não guarda horário e não permite modificar a própria instância.
     */
    private LocalDate vencimento;

   