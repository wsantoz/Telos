// =====================================================================
// TELOS - GESTÃO FINANCEIRA (versão didática em um único arquivo)
// Programa de console em Java que:
//   1) cadastra receitas e despesas        5) mostra o saldo
//   2) lista e busca lançamentos           6) controla o orçamento por categoria
//   3) remove lançamentos                  7) mostra o relatório de despesas
//   4) simula financiamentos (Price/SAC)
// Leia de cima para baixo: constantes -> dados -> menu -> funções do menu
// -> funções de apoio -> leitura segura do teclado.
// =====================================================================

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    // ================= 1. CONSTANTES (valores fixos) =================
    static final String RECEITA = "RECEITA";
    static final String DESPESA = "DESPESA";
    static final double PERCENTUAL_ATENCAO = 80;   // a partir daqui avisa "atenção"
    static final double PERCENTUAL_ESTOURO = 100;  // a partir daqui avisa "estourado"
    static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    static final Locale BRASIL = Locale.forLanguageTag("pt-BR");

    // ================= 2. MODELOS (o que guardamos) =================
    // Um lançamento é uma receita ou uma despesa.
    static class Lancamento {
        int id;
        String tipo;          // RECEITA ou DESPESA
        double valor;
        String categoria;
        String descricao;
        LocalDate data;
    }

    // Um orçamento é o limite de gasto de uma categoria.
    static class Orcamento {
        String categoria;
        double limite;
    }

    // ================= 3. DADOS DO PROGRAMA =================
    static List<Lancamento> lancamentos = new ArrayList<Lancamento>();
    static List<Orcamento> orcamentos = new ArrayList<Orcamento>();
    static int proximoId = 1;
    static Scanner teclado = new Scanner(System.in);

    // ================= 4. PROGRAMA PRINCIPAL =================
    public static void main(String[] args) {
        int opcao = -1;

        // O menu se repete até o usuário digitar 0.
        while (opcao != 0) {
            mostrarMenu();
            opcao = lerNumeroInteiro("Escolha uma opção: ");

            // Cada opção chama a função responsável por ela.
            if (opcao == 1) {
                cadastrarLancamento();
            } else if (opcao == 2) {
                listarLancamentos();
            } else if (opcao == 3) {
                buscarLancamentosPorCategoria();
            } else if (opcao == 4) {
                removerLancamento();
            } else if (opcao == 5) {
                mostrarSaldo();
            } else if (opcao == 6) {
                definirOrcamento();
            } else if (opcao == 7) {
                mostrarRelatorioPorCategoria();
            } else if (opcao == 8) {
                simularFinanciamento();
            } else if (opcao == 0) {
                System.out.println("Até logo!");
            } else {
                System.out.println("Opção inválida. Tente novamente.");
            }
        }
        teclado.close();
    }

    static void mostrarMenu() {
        System.out.println();
        System.out.println("===== GESTOR FINANCEIRO =====");
        System.out.println("1 - Cadastrar lançamento");
        System.out.println("2 - Listar lançamentos");
        System.out.println("3 - Buscar por categoria");
        System.out.println("4 - Remover lançamento");
        System.out.println("5 - Ver saldo");
        System.out.println("6 - Definir orçamento de uma categoria");
        System.out.println("7 - Relatório de despesas por categoria");
        System.out.println("8 - Simular financiamento (Price/SAC)");
        System.out.println("0 - Sair");
    }

    // ================= 5. FUNÇÕES DO MENU =================

    // Opção 1: lê os dados, confere se estão certos e guarda na lista.
    static void cadastrarLancamento() {
        System.out.println("-- Novo lançamento --");

        // Etapa 1: ler os dados digitados.
        int escolhaTipo = lerNumeroInteiro("Tipo (1 = Receita, 2 = Despesa): ");
        if (escolhaTipo != 1 && escolhaTipo != 2) {
            System.out.println("Tipo inválido.");
            return;
        }
        String tipo = DESPESA;
        if (escolhaTipo == 1) {
            tipo = RECEITA;
        }
        double valor = lerNumeroDecimal("Valor (ex.: 49,90): ");
        System.out.print("Categoria: ");
        String categoria = teclado.nextLine().trim();
        System.out.print("Descrição: ");
        String descricao = teclado.nextLine().trim();
        LocalDate data = lerData("Data (dd/mm/aaaa): ");

        // Etapa 2: conferir se os dados são válidos.
        if (valor <= 0) {
            System.out.println("Não foi possível cadastrar: O valor deve ser maior que zero.");
            return;
        }
        if (categoria.isEmpty()) {
            System.out.println("Não foi possível cadastrar: A categoria é obrigatória.");
            return;
        }
        if (descricao.isEmpty()) {
            System.out.println("Não foi possível cadastrar: A descrição é obrigatória.");
            return;
        }

        // Etapa 3: guardar o novo lançamento na lista.
        Lancamento novo = new Lancamento();
        novo.id = proximoId;
        novo.tipo = tipo;
        novo.valor = valor;
        novo.categoria = categoria;
        novo.descricao = descricao;
        novo.data = data;
        lancamentos.add(novo);
        proximoId = proximoId + 1;
        System.out.println("Cadastrado com sucesso: " + textoDoLancamento(novo));

        // Etapa 4: se for despesa, avisar caso o orçamento da categoria esteja no limite.
        if (tipo.equals(DESPESA)) {
            String alerta = montarAlertaDoOrcamento(categoria);
            if (!alerta.isEmpty()) {
                System.out.println(alerta);
            }
        }
    }

    // Opção 2: mostra todos os lançamentos.
    static void listarLancamentos() {
        if (lancamentos.isEmpty()) {
            System.out.println("Nenhum lançamento cadastrado.");
            return;
        }
        imprimirLancamentos(lancamentos);
    }

    // Opção 3: percorre a lista e separa só os da categoria digitada.
    static void buscarLancamentosPorCategoria() {
        System.out.print("Categoria: ");
        String categoriaBuscada = teclado.nextLine().trim();

        List<Lancamento> encontrados = new ArrayList<Lancamento>();
        for (Lancamento lancamento : lancamentos) {
            if (lancamento.categoria.equalsIgnoreCase(categoriaBuscada)) {
                encontrados.add(lancamento);
            }
        }

        if (encontrados.isEmpty()) {
            System.out.println("Nada encontrado nessa categoria.");
        } else {
            imprimirLancamentos(encontrados);
        }
    }

    // Opção 4: procura o lançamento pelo número (ID) e remove da lista.
    static void removerLancamento() {
        int idParaRemover = lerNumeroInteiro("ID do lançamento a remover: ");

        for (int posicao = 0; posicao < lancamentos.size(); posicao++) {
            if (lancamentos.get(posicao).id == idParaRemover) {
                lancamentos.remove(posicao);
                System.out.println("Lançamento removido.");
                return;
            }
        }
        System.out.println("ID não encontrado.");
    }

    // Opção 5: saldo = receitas - despesas.
    static void mostrarSaldo() {
        double totalReceitas = somarPorTipo(RECEITA);
        double totalDespesas = somarPorTipo(DESPESA);
        double saldo = totalReceitas - totalDespesas;

        System.out.println("Receitas: " + dinheiro(totalReceitas));
        System.out.println("Despesas: " + dinheiro(totalDespesas));
        System.out.println("Saldo:    " + dinheiro(saldo));
    }

    // Opção 6: cria o limite de uma categoria (ou troca o limite, se já existir).
    static void definirOrcamento() {
        System.out.print("Categoria: ");
        String categoria = teclado.nextLine().trim();
        double limite = lerNumeroDecimal("Limite mensal (ex.: 500,00): ");

        if (categoria.isEmpty()) {
            System.out.println("Não foi possível definir: A categoria é obrigatória.");
            return;
        }
        if (limite <= 0) {
            System.out.println("Não foi possível definir: O limite deve ser maior que zero.");
            return;
        }

        Orcamento existente = buscarOrcamentoDaCategoria(categoria);
        if (existente != null) {
            existente.limite = limite;
        } else {
            Orcamento novo = new Orcamento();
            novo.categoria = categoria;
            novo.limite = limite;
            orcamentos.add(novo);
        }
        System.out.println("Orçamento de " + categoria + " definido em " + dinheiro(limite) + ".");
    }

    // Opção 7: mostra quanto foi gasto em cada categoria e a % do total.
    static void mostrarRelatorioPorCategoria() {
        List<String> categorias = listarCategoriasDeDespesa();
        if (categorias.isEmpty()) {
            System.out.println("Não há despesas para o relatório.");
            return;
        }

        double totalDespesas = somarPorTipo(DESPESA);
        System.out.println("-- Despesas por categoria --");

        // Para cada categoria: calcula o gasto, a porcentagem e desenha a barra.
        for (String categoria : categorias) {
            double gasto = somarGastoDaCategoria(categoria);
            double percentual = gasto / totalDespesas * 100;

            // Uma "#" a cada 5% do total.
            String barra = "";
            int quantidadeDeBarras = (int) (percentual / 5);
            for (int i = 0; i < quantidadeDeBarras; i++) {
                barra = barra + "#";
            }
            System.out.println(String.format(BRASIL, "%-15s R$ %9.2f  %5.1f%%  %s",
                    categoria, gasto, percentual, barra));
        }
        System.out.println(String.format(BRASIL, "%-15s R$ %9.2f", "TOTAL", totalDespesas));
    }

    // Opção 8: lê os dados do financiamento e mostra a tabela mês a mês.
    static void simularFinanciamento() {
        int sistema = lerNumeroInteiro("Sistema (1 = Price, 2 = SAC): ");
        if (sistema != 1 && sistema != 2) {
            System.out.println("Sistema inválido.");
            return;
        }
        double valor = lerNumeroDecimal("Valor financiado: ");
        double taxaPercentual = lerNumeroDecimal("Taxa de juros mensal em % (ex.: 1,5): ");
        int meses = lerNumeroInteiro("Prazo em meses: ");

        if (valor <= 0) {
            System.out.println("Não foi possível simular: O valor financiado deve ser maior que zero.");
            return;
        }
        if (taxaPercentual < 0) {
            System.out.println("Não foi possível simular: A taxa não pode ser negativa.");
            return;
        }
        if (meses <= 0) {
            System.out.println("Não foi possível simular: O prazo deve ter pelo menos 1 mês.");
            return;
        }

        boolean ehPrice = (sistema == 1);
        imprimirTabelaDoFinanciamento(ehPrice, valor, taxaPercentual, meses);
    }

    // ================= 6. FUNÇÕES DE APOIO (cálculos e textos) =================

    // Soma o valor de todos os lançamentos de um tipo (RECEITA ou DESPESA).
    static double somarPorTipo(String tipo) {
        double total = 0;
        for (Lancamento lancamento : lancamentos) {
            if (lancamento.tipo.equals(tipo)) {
                total = total + lancamento.valor;
            }
        }
        return total;
    }

    // Soma só as despesas de uma categoria.
    static double somarGastoDaCategoria(String categoria) {
        double total = 0;
        for (Lancamento lancamento : lancamentos) {
            if (lancamento.tipo.equals(DESPESA) && lancamento.categoria.equalsIgnoreCase(categoria)) {
                total = total + lancamento.valor;
            }
        }
        return total;
    }

    // Procura o orçamento de uma categoria; devolve null se não existir.
    static Orcamento buscarOrcamentoDaCategoria(String categoria) {
        for (Orcamento orcamento : orcamentos) {
            if (orcamento.categoria.equalsIgnoreCase(categoria)) {
                return orcamento;
            }
        }
        return null;
    }

    // Compara o gasto com o limite e devolve o aviso (ou texto vazio se estiver tudo bem).
    static String montarAlertaDoOrcamento(String categoria) {
        Orcamento orcamento = buscarOrcamentoDaCategoria(categoria);
        if (orcamento == null) {
            return ""; // essa categoria não tem limite definido
        }

        double gasto = somarGastoDaCategoria(categoria);
        double percentualUsado = gasto / orcamento.limite * 100;

        if (percentualUsado >= PERCENTUAL_ESTOURO) {
            return String.format(BRASIL, "ALERTA: orçamento de %s ESTOURADO (%.0f%% de R$ %.2f).",
                    orcamento.categoria, percentualUsado, orcamento.limite);
        } else if (percentualUsado >= PERCENTUAL_ATENCAO) {
            return String.format(BRASIL, "ATENÇÃO: você já usou %.0f%% do orçamento de %s (R$ %.2f).",
                    percentualUsado, orcamento.categoria, orcamento.limite);
        }
        return ""; // abaixo de 80%: sem aviso
    }

    // Monta a lista de categorias que têm despesa, sem repetir nenhuma.
    static List<String> listarCategoriasDeDespesa() {
        List<String> categorias = new ArrayList<String>();
        for (Lancamento lancamento : lancamentos) {
            if (lancamento.tipo.equals(DESPESA) && !listaTemTexto(categorias, lancamento.categoria)) {
                categorias.add(lancamento.categoria);
            }
        }
        return categorias;
    }

    static boolean listaTemTexto(List<String> lista, String texto) {
        for (String item : lista) {
            if (item.equalsIgnoreCase(texto)) {
                return true;
            }
        }
        return false;
    }

    // Imprime uma linha para cada lançamento da lista.
    static void imprimirLancamentos(List<Lancamento> lista) {
        for (Lancamento lancamento : lista) {
            System.out.println(textoDoLancamento(lancamento));
        }
    }

    // Transforma um lançamento em uma linha de texto.
    static String textoDoLancamento(Lancamento lancamento) {
        String sinal = "-";
        if (lancamento.tipo.equals(RECEITA)) {
            sinal = "+";
        }
        return String.format(BRASIL, "#%d | %s | %-8s | %-15s | %s R$ %.2f | %s",
                lancamento.id, lancamento.data.format(FORMATO_DATA), lancamento.tipo,
                lancamento.categoria, sinal, lancamento.valor, lancamento.descricao);
    }

    // Escreve um valor em reais, por exemplo: R$ 49,90
    static String dinheiro(double valor) {
        return String.format(BRASIL, "R$ %.2f", valor);
    }

    // Parcela fixa da tabela Price.
    static double calcularParcelaPrice(double valor, double taxaMensal, int meses) {
        if (taxaMensal == 0) {
            return valor / meses; // sem juros: só divide
        }
        return valor * taxaMensal / (1 - Math.pow(1 + taxaMensal, -meses));
    }

    // Mostra a tabela mês a mês. Price: parcela fixa. SAC: amortização fixa.
    static void imprimirTabelaDoFinanciamento(boolean ehPrice, double valor, double taxaPercentual, int meses) {
        double taxaMensal = taxaPercentual / 100;
        double parcelaFixaPrice = calcularParcelaPrice(valor, taxaMensal, meses);
        double amortizacaoFixaSac = valor / meses;

        double saldoDevedor = valor;
        double totalPago = 0;

        System.out.println("Mês | Parcela | Juros | Amortização | Saldo devedor");

        // Cada volta do laço é um mês do financiamento.
        for (int mes = 1; mes <= meses; mes++) {
            double juros = saldoDevedor * taxaMensal;
            double amortizacao;
            double parcela;

            if (ehPrice) {
                parcela = parcelaFixaPrice;
                amortizacao = parcela - juros;
            } else {
                amortizacao = amortizacaoFixaSac;
                parcela = amortizacao + juros;
            }

            saldoDevedor = saldoDevedor - amortizacao;
            if (saldoDevedor < 0) {
                saldoDevedor = 0; // evita aparecer -0,00 por arredondamento
            }
            totalPago = totalPago + parcela;

            System.out.println(String.format(BRASIL, "%3d | %.2f | %.2f | %.2f | %.2f",
                    mes, parcela, juros, amortizacao, saldoDevedor));
        }
        System.out.println(String.format(BRASIL, "Total pago: R$ %.2f (juros: R$ %.2f)",
                totalPago, totalPago - valor));
        System.out.println();
    }

    // ================= 7. LEITURA SEGURA DO TECLADO =================
    // Cada função repete a pergunta até o usuário digitar algo válido.

    static int lerNumeroInteiro(String pergunta) {
        while (true) {
            System.out.print(pergunta);
            String texto = teclado.nextLine().trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException erro) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    static double lerNumeroDecimal(String pergunta) {
        while (true) {
            System.out.print(pergunta);
            String texto = teclado.nextLine().trim().replace(",", "."); // aceita vírgula
            try {
                return Double.parseDouble(texto);
            } catch (NumberFormatException erro) {
                System.out.println("Digite um valor numérico válido (ex.: 49,90).");
            }
        }
    }

    static LocalDate lerData(String pergunta) {
        while (true) {
            System.out.print(pergunta);
            String texto = teclado.nextLine().trim();
            try {
                return LocalDate.parse(texto, FORMATO_DATA);
            } catch (DateTimeParseException erro) {
                System.out.println("Data inválida. Use o formato dd/mm/aaaa.");
            }
        }
    }
}
