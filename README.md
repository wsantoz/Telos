# [Telos] — Gestão e Saúde Financeira

> [Todo objetivo começa com um plano.]

Projeto final **A3** da disciplina de **Algoritmos e Programação** (UNA, 2º/2026).
Uma plataforma em **Java** que ajuda pessoas a organizar o dinheiro e criar o hábito de poupar.

## O problema

Muita gente chega ao fim do mês sem saber para onde o dinheiro foi, e a maioria das
ferramentas só registra gastos, sem orientar o usuário.

## O que o sistema faz

- Registro de receitas e despesas por categoria, com validação de dados
- Orçamento mensal por categoria, com alertas em 80% e 100% do limite
- Metas de economia
- **Score de Saúde Financeira (0 a 1000)**, com classificação e explicação
- Simuladores de financiamento (Price e SAC), de investimento (juros compostos) e "E se?"
- Relatórios do mês

## Status do projeto

- [x] Cadastro, listagem, busca por categoria, remoção e saldo (console)
- [ ] Categorias e orçamento com alertas
- [ ] Persistência em arquivo
- [ ] Score de Saúde Financeira
- [ ] Simuladores
- [ ] Interface web *(desafio extra)*
- [ ] Assistente com IA *(desafio extra)*

## Como executar

Requisito: JDK 17 ou superior.

```bash
mkdir out
javac -d out $(find src -name "*.java")
java -cp out financas.Main
```

## Estrutura

```
src/financas/
├── modelo/     # entidades do domínio (Lancamento, TipoLancamento)
├── servico/    # regras de negócio e cálculos
└── Main.java   # menu de console
```

## Conceitos de programação aplicados

Variáveis e constantes · tipos de dados · entrada e saída · operadores ·
estruturas de decisão e repetição · métodos · listas · tratamento de exceções ·
orientação a objetos · modularização do código

## Equipe

| Nome |
|---|---|
| [Warley] |
| [Kauã | 
| [Marcus] | 
| [João] | 
| [Eduardo] | 
**Professora:** Charlene Cássia
