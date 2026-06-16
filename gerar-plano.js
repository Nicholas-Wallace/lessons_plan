const {
  Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
  Header, Footer, AlignmentType, HeadingLevel, BorderStyle, WidthType,
  ShadingType, VerticalAlign, PageNumber, LevelFormat, TabStopType,
  TabStopPosition
} = require('docx');
const fs = require('fs');

const border = { style: BorderStyle.SINGLE, size: 1, color: "CCCCCC" };
const borders = { top: border, bottom: border, left: border, right: border };
const headerBorder = { style: BorderStyle.SINGLE, size: 6, color: "1F4E79" };

function h1(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_1,
    children: [new TextRun({ text, bold: true, size: 32, font: "Arial", color: "1F4E79" })],
    spacing: { before: 320, after: 160 },
    border: { bottom: { style: BorderStyle.SINGLE, size: 4, color: "1F4E79", space: 4 } }
  });
}

function h2(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_2,
    children: [new TextRun({ text, bold: true, size: 26, font: "Arial", color: "2E75B6" })],
    spacing: { before: 240, after: 120 }
  });
}

function h3(text) {
  return new Paragraph({
    children: [new TextRun({ text, bold: true, size: 24, font: "Arial", color: "2F5496" })],
    spacing: { before: 200, after: 80 }
  });
}

function p(text, opts = {}) {
  return new Paragraph({
    children: [new TextRun({ text, size: 22, font: "Arial", ...opts })],
    spacing: { before: 60, after: 60 },
    alignment: AlignmentType.JUSTIFIED
  });
}

function bullet(text, level = 0) {
  return new Paragraph({
    numbering: { reference: "bullets", level },
    children: [new TextRun({ text, size: 22, font: "Arial" })],
    spacing: { before: 40, after: 40 }
  });
}

function numbered(text, level = 0) {
  return new Paragraph({
    numbering: { reference: "numbers", level },
    children: [new TextRun({ text, size: 22, font: "Arial" })],
    spacing: { before: 40, after: 40 }
  });
}

function spacer() {
  return new Paragraph({ children: [new TextRun("")], spacing: { before: 80, after: 80 } });
}

function tableRow(label, value, isHeader = false) {
  const fill = isHeader ? "1F4E79" : "FFFFFF";
  const textColor = isHeader ? "FFFFFF" : "000000";
  return new TableRow({
    children: [
      new TableCell({
        borders,
        width: { size: 3000, type: WidthType.DXA },
        shading: { fill: isHeader ? "1F4E79" : "EBF3FB", type: ShadingType.CLEAR },
        margins: { top: 80, bottom: 80, left: 120, right: 120 },
        children: [new Paragraph({ children: [new TextRun({ text: label, bold: true, size: 22, font: "Arial", color: isHeader ? "FFFFFF" : "1F4E79" })] })]
      }),
      new TableCell({
        borders,
        width: { size: 6360, type: WidthType.DXA },
        shading: { fill: isHeader ? "1F4E79" : "FFFFFF", type: ShadingType.CLEAR },
        margins: { top: 80, bottom: 80, left: 120, right: 120 },
        children: [new Paragraph({ children: [new TextRun({ text: value, size: 22, font: "Arial", color: textColor })] })]
      })
    ]
  });
}

function baremRow(criterio, pts, isHeader = false) {
  return new TableRow({
    children: [
      new TableCell({
        borders,
        width: { size: 6500, type: WidthType.DXA },
        shading: { fill: isHeader ? "1F4E79" : "FFFFFF", type: ShadingType.CLEAR },
        margins: { top: 80, bottom: 80, left: 120, right: 120 },
        children: [new Paragraph({ children: [new TextRun({ text: criterio, bold: isHeader, size: 22, font: "Arial", color: isHeader ? "FFFFFF" : "000000" })] })]
      }),
      new TableCell({
        borders,
        width: { size: 2860, type: WidthType.DXA },
        shading: { fill: isHeader ? "1F4E79" : "F2F2F2", type: ShadingType.CLEAR },
        margins: { top: 80, bottom: 80, left: 120, right: 120 },
        verticalAlign: VerticalAlign.CENTER,
        children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: pts, bold: isHeader, size: 22, font: "Arial", color: isHeader ? "FFFFFF" : "000000" })] })]
      })
    ]
  });
}

const doc = new Document({
  numbering: {
    config: [
      {
        reference: "bullets",
        levels: [{
          level: 0, format: LevelFormat.BULLET, text: "\u2022",
          alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 720, hanging: 360 } } }
        }, {
          level: 1, format: LevelFormat.BULLET, text: "\u25E6",
          alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 1080, hanging: 360 } } }
        }]
      },
      {
        reference: "numbers",
        levels: [{
          level: 0, format: LevelFormat.DECIMAL, text: "%1.",
          alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 720, hanging: 360 } } }
        }, {
          level: 1, format: LevelFormat.DECIMAL, text: "%1.%2",
          alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 1080, hanging: 360 } } }
        }]
      }
    ]
  },
  styles: {
    default: { document: { run: { font: "Arial", size: 22 } } },
    paragraphStyles: [
      { id: "Heading1", name: "Heading 1", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 32, bold: true, font: "Arial", color: "1F4E79" },
        paragraph: { spacing: { before: 320, after: 160 }, outlineLevel: 0 } },
      { id: "Heading2", name: "Heading 2", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 26, bold: true, font: "Arial", color: "2E75B6" },
        paragraph: { spacing: { before: 240, after: 120 }, outlineLevel: 1 } }
    ]
  },
  sections: [{
    properties: {
      page: {
        size: { width: 11906, height: 16838 },
        margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 }
      }
    },
    headers: {
      default: new Header({
        children: [
          new Paragraph({
            children: [
              new TextRun({ text: "IFRN  —  Plano de Aula  |  Destilação Fracionada", size: 18, font: "Arial", color: "2E75B6" })
            ],
            border: { bottom: { style: BorderStyle.SINGLE, size: 4, color: "2E75B6", space: 4 } },
            spacing: { after: 120 }
          })
        ]
      })
    },
    footers: {
      default: new Footer({
        children: [
          new Paragraph({
            children: [
              new TextRun({ text: "Prof. João Silva  —  Operações Unitárias  —  Tecnologia em Processos Químicos", size: 16, font: "Arial", color: "888888" }),
              new TextRun({ text: "\t", size: 16, font: "Arial", color: "888888" })
            ],
            border: { top: { style: BorderStyle.SINGLE, size: 4, color: "CCCCCC", space: 4 } },
            tabStops: [{ type: TabStopType.RIGHT, position: TabStopPosition.MAX }],
            spacing: { before: 120 }
          })
        ]
      })
    },
    children: [
      // TÍTULO
      new Paragraph({
        children: [new TextRun({ text: "PLANO DE AULA", bold: true, size: 40, font: "Arial", color: "1F4E79" })],
        alignment: AlignmentType.CENTER,
        spacing: { before: 0, after: 80 }
      }),
      new Paragraph({
        children: [new TextRun({ text: "Destilação Fracionada", size: 28, font: "Arial", color: "2E75B6", italics: true })],
        alignment: AlignmentType.CENTER,
        spacing: { before: 0, after: 320 }
      }),

      // SEÇÃO 1 — IDENTIFICAÇÃO
      h1("1. Identificação"),
      new Table({
        width: { size: 9360, type: WidthType.DXA },
        columnWidths: [3000, 6360],
        rows: [
          tableRow("Instituição", "IFRN — Instituto Federal de Educação, Ciência e Tecnologia do RN"),
          tableRow("Curso", "Tecnologia em Processos Químicos"),
          tableRow("Nível", "Ensino Superior"),
          tableRow("Disciplina", "Operações Unitárias"),
          tableRow("Tema", "Destilação Fracionada"),
          tableRow("Professor", "João Silva"),
          tableRow("Carga horária", "50 min (mínimo: 40 min)"),
        ]
      }),
      spacer(),

      // SEÇÃO 1b — CONTEÚDOS PRÉVIOS
      h1("1b. Conteúdos Prévios / Pré-requisitos"),
      bullet("Conceito de mistura homogênea e heterogênea"),
      bullet("Ponto de ebulição e volatilidade de substâncias puras"),
      bullet("Equilíbrio líquido-vapor e diagrama de fases"),
      bullet("Conceito de pressão de vapor e Lei de Raoult"),
      bullet("Noções básicas de balanço de massa em sistemas contínuos"),
      spacer(),

      // SEÇÃO 2 — OBJETIVOS
      h1("2. Objetivos Específicos"),
      p("Ao final da aula, o aluno deverá ser capaz de:"),
      numbered("Explicar o princípio de funcionamento da destilação fracionada com base no equilíbrio líquido-vapor de misturas multicomponentes"),
      numbered("Analisar o papel dos pratos ou recheios de uma coluna de destilação na eficiência da separação"),
      numbered("Relacionar as variáveis operacionais — temperatura, pressão e razão de refluxo — ao perfil de separação da coluna"),
      numbered("Avaliar situações de falha operacional em colunas de destilação com base nos conceitos estudados"),
      spacer(),

      // SEÇÃO 4 — CONTEÚDO PROGRAMÁTICO
      h1("4. Conteúdo Programático"),
      numbered("Fundamentos da destilação fracionada"),
      numbered("Princípio de separação por volatilidade diferencial", 1),
      numbered("Equilíbrio líquido-vapor em misturas multicomponentes", 1),
      numbered("Diagrama de temperatura × composição (curvas de bolha e orvalho)", 1),
      numbered("Estrutura e funcionamento da coluna de destilação"),
      numbered("Componentes principais: coluna, refervedor, condensador e acumulador", 1),
      numbered("Pratos (bandejas) e recheios: tipos e função na eficiência de separação", 1),
      numbered("Conceito de estágio de equilíbrio e eficiência de prato", 1),
      numbered("Variáveis operacionais e seu efeito na separação"),
      numbered("Perfil de temperatura ao longo da coluna", 1),
      numbered("Razão de refluxo: definição, refluxo mínimo e total", 1),
      numbered("Influência da pressão operacional na separação", 1),
      numbered("Falhas operacionais e diagnóstico"),
      numbered("Inundação, jato e canalização", 1),
      numbered("Variações de temperatura e contaminação de cortes laterais", 1),
      numbered("Leitura de instrumentação e indicadores de desempenho", 1),
      spacer(),

      // SEÇÃO 5 — METODOLOGIA
      h1("5. Metodologia"),
      p("A aula utilizará como metodologia central a aula expositiva dialogada, combinada com estudo de caso. A aula expositiva dialogada é a estratégia mais adequada para este tema em nível superior porque permite ao professor construir progressivamente os conceitos — partindo do equilíbrio líquido-vapor até chegar ao comportamento real da coluna — ao mesmo tempo em que mobiliza o conhecimento prévio dos alunos por meio de perguntas e provocações ao longo da exposição."),
      spacer(),
      p("O estudo de caso será incorporado organicamente à aula por meio da narrativa de falha operacional apresentada na introdução, que percorrerá toda a sequência didática como fio condutor. Essa combinação é especialmente eficaz para Operações Unitárias porque a disciplina exige que o aluno transite entre teoria e aplicação industrial — e o estudo de caso garante esse trânsito de forma concreta, sem precisar de laboratório ou visita técnica."),
      spacer(),

      // SEÇÃO 6 — PROCEDIMENTOS DIDÁTICOS
      h1("6. Procedimentos Didáticos"),

      h2("Introdução"),
      p("O professor inicia a aula com uma narrativa de experiência profissional. Conta que, no início da carreira como engenheiro trainee em uma refinaria no interior do Rio Grande do Norte, foi designado para acompanhar a operação de uma coluna de destilação fracionada que separava nafta e querosene. Em determinado turno, a equipe percebeu que o querosene saindo pelo prato lateral estava com coloração mais escura que o normal e odor diferente — sinal claro de contaminação com frações mais pesadas."),
      spacer(),
      p("O supervisor virou para ele e perguntou: \"Trainee, o que está acontecendo aqui?\" O professor olhou para os instrumentos, para a coluna de 30 metros, para o fluxo — e não soube responder. Tinha estudado destilação fracionada na faculdade, sabia a teoria, mas na frente daquela coluna real percebeu que havia uma distância enorme entre o livro e o equipamento. Após contar a história, o professor pausa e dirige aos alunos: \"Hoje, ao final desta aula, vocês vão saber exatamente o que estava acontecendo naquela coluna — e por quê.\""),
      spacer(),

      h2("Apresentação dos Objetivos"),
      p("O professor projeta os quatro objetivos da aula e os apresenta de forma contextualizada, conectando cada um ao caso narrado: \"Para entender o que aconteceu naquela coluna, precisamos dominar quatro coisas: como a separação acontece, como a coluna é estruturada, quais variáveis controlam essa separação — e como diagnosticar quando algo sai errado.\" Esse enquadramento não apenas comunica os objetivos, mas mostra aos alunos por que cada um deles importa."),
      spacer(),

      h2("Desenvolvimento — Parte 1: Fundamentos e Estrutura da Coluna"),
      p("O professor conduz a exposição dialogada partindo do princípio básico de separação por volatilidade, retomando o diagrama líquido-vapor e introduzindo o conceito de mistura multicomponente. Apresenta o esquema completo de uma coluna de destilação com seus componentes principais utilizando slides com diagrama esquemático industrial. A cada etapa, formula perguntas aos alunos: \"Por que o produto mais leve sai pelo topo e o mais pesado pelo fundo?\", \"O que acontece com a composição da mistura a cada prato que ela atravessa?\" O professor explica o conceito de estágio de equilíbrio e diferencia colunas de pratos e de recheio, discutindo o impacto de cada tipo na eficiência da separação."),
      spacer(),

      h2("Desenvolvimento — Parte 2: Variáveis Operacionais"),
      p("O professor apresenta as três variáveis operacionais críticas — temperatura, pressão e razão de refluxo — explicando como cada uma afeta o perfil de separação da coluna. A razão de refluxo recebe atenção especial: o professor define refluxo mínimo e total com o auxílio de gráficos, e lança a pergunta: \"O que acontece com a separação se eu reduzir muito o refluxo? E se eu aumentar além do necessário?\" Aqui o professor começa a aproximar a teoria do caso narrado na introdução, sem ainda revelar a causa do problema, para que os alunos comecem a formular hipóteses."),
      spacer(),

      h2("Atividade Avaliativa"),
      p("O professor apresenta no slide um estudo de caso sintético: uma coluna operando com querosene contaminado por frações mais pesadas, com dados de temperatura e razão de refluxo ligeiramente alterados em relação ao projeto. Os alunos, individualmente ou em duplas, respondem a duas perguntas: (1) Com base nos dados, qual variável operacional está fora do padrão e como isso explica a contaminação? (2) Que ajuste operacional você proporia? O professor circula pela sala, observando o raciocínio sem dar respostas. Em seguida, conduz uma correção coletiva rápida, ouvindo os alunos antes de apresentar a solução."),
      spacer(),

      h2("Revisão da Aula"),
      p("O professor retoma os quatro objetivos e percorre brevemente cada um deles. Usa a lousa para construir um esquema sintético conectando os conceitos — do equilíbrio líquido-vapor ao comportamento operacional da coluna. A revisão é conduzida como conversa, com o professor lançando perguntas curtas e os alunos completando as respostas."),
      spacer(),

      h2("Conclusão"),
      p("O professor retoma a história da introdução e revela o desfecho: \"Naquela noite na refinaria, o que estava acontecendo era exatamente o que vocês acabaram de diagnosticar no estudo de caso. A razão de refluxo havia sido reduzida pelo operador do turno anterior para economizar energia — sem perceber que aquela redução deslocou o perfil de temperatura da coluna e contaminou o corte de querosene com frações mais pesadas. Quando finalmente entendi isso, dias depois, com a ajuda do engenheiro sênior, percebi que a resposta estava em tudo que eu tinha estudado na faculdade — mas que só fez sentido de verdade na frente daquela coluna.\""),
      spacer(),
      p("O professor encerra com a reflexão: \"Operações Unitárias não é sobre decorar equações. É sobre desenvolver a capacidade de olhar para um equipamento real, ler o que ele está dizendo e saber o que fazer. É isso que este curso vai construir em vocês.\""),
      spacer(),

      // SEÇÃO 7 — RECURSOS
      h1("7. Recursos Didáticos"),
      bullet("Projetor e slides com diagrama esquemático de coluna de destilação industrial"),
      bullet("Lousa para construção do esquema de revisão"),
      bullet("Slide ou ficha impressa com o estudo de caso da atividade avaliativa"),
      bullet("Gráficos de equilíbrio líquido-vapor e diagrama temperatura × composição"),
      bullet("Gráfico de razão de refluxo × número de estágios (curva McCabe-Thiele simplificada)"),
      spacer(),

      // SEÇÃO 8 — AVALIAÇÃO
      h1("8. Avaliação"),
      p("A avaliação será realizada por meio de um estudo de caso com perguntas dissertativas aplicado durante a própria aula, na etapa de atividade avaliativa. Esse instrumento é o mais adequado para este tema e nível porque destilação fracionada não se verifica com questões de memorização — exige que o aluno aplique os conceitos para interpretar uma situação operacional real e propor soluções fundamentadas."),
      spacer(),
      p("O estudo de caso permite verificar diretamente os objetivos 3 e 4 (relacionar variáveis operacionais ao perfil de separação e avaliar situações de falha), além de mobilizar indiretamente os objetivos 1 e 2, uma vez que o diagnóstico correto pressupõe compreensão do princípio de separação e do papel dos pratos. A correção coletiva ao final permite ao professor identificar lacunas de aprendizagem em tempo real e ajustar as explicações antes da conclusão da aula."),
      spacer(),

      // SEÇÃO 9 — REFERÊNCIAS
      h1("9. Referências"),
      p("GEANKOPLIS, C. J. Transport Processes and Separation Process Principles. 4. ed. Upper Saddle River: Prentice Hall, 2003."),
      spacer(),
      p("McCABE, W. L.; SMITH, J. C.; HARRIOTT, P. Unit Operations of Chemical Engineering. 7. ed. New York: McGraw-Hill, 2005."),
      spacer(),
      p("FOUST, A. S. et al. Princípios das Operações Unitárias. 2. ed. Rio de Janeiro: LTC, 1982."),
      spacer(),
      p("COULSON, J. M.; RICHARDSON, J. F. Chemical Engineering. v. 2: Particle Technology and Separation Processes. 5. ed. Oxford: Butterworth-Heinemann, 2002."),
      spacer(),
      p("PERRY, R. H.; GREEN, D. W. Perry's Chemical Engineers' Handbook. 8. ed. New York: McGraw-Hill, 2008."),
      spacer(),
      p("BIEGLER, L. T.; GROSSMANN, I. E.; WESTERBERG, A. W. Systematic Methods of Chemical Process Design. Upper Saddle River: Prentice Hall, 1997."),
      spacer(),
      p("NPTEL — National Programme on Technology Enhanced Learning. Mass Transfer Operations. Disponível em: https://nptel.ac.in. Acesso: série de videoaulas gratuitas sobre destilação com abordagem de engenharia química."),
      spacer(),

      // OBSERVAÇÕES PARA A BANCA
      h1("Observações para a Banca"),
      p("Esta seção é destinada aos avaliadores. Não é lida durante a execução da aula."),
      spacer(),

      h2("Alinhamento ao Barema"),
      new Table({
        width: { size: 9360, type: WidthType.DXA },
        columnWidths: [6500, 2860],
        rows: [
          baremRow("Critério de Avaliação", "Pontuação", true),
          baremRow("Domínio do conteúdo", "30 pts"),
          baremRow("Clareza e didática", "25 pts"),
          baremRow("Uso de recursos didáticos", "15 pts"),
          baremRow("Adequação ao tempo", "15 pts"),
          baremRow("Postura e comunicação", "15 pts"),
        ]
      }),
      spacer(),

      h3("Domínio do conteúdo (30 pts)"),
      p("Esta é a seção de maior peso e foi tratada com rigor técnico máximo. O conteúdo programático cobre desde os fundamentos termodinâmicos até o diagnóstico de falhas operacionais reais. Os procedimentos evidenciam domínio ao longo de todo o desenvolvimento — os conceitos são construídos progressivamente com linguagem técnica precisa e conexão constante com a aplicação industrial."),
      spacer(),

      h3("Clareza e didática (25 pts)"),
      p("A narrativa de experiência profissional na introdução cumpre função didática central — cria um fio condutor concreto que percorre toda a aula e ancora os conceitos abstratos em uma situação real. Os objetivos são apresentados de forma contextualizada, conectados à história. A progressão do conteúdo segue lógica de complexidade crescente coerente com o nível superior."),
      spacer(),

      h3("Uso de recursos didáticos (15 pts)"),
      p("O plano utiliza slides com diagrama industrial, gráficos de equilíbrio líquido-vapor, construção de esquema na lousa e ficha de estudo de caso. A combinação de recursos visuais projetados com construção dinâmica na lousa demonstra variação intencional de recursos ao longo da aula."),
      spacer(),

      h3("Adequação ao tempo (15 pts)"),
      p("O plano foi projetado para funcionar entre 40 e 50 minutos. As etapas de desenvolvimento podem ser comprimidas sem perda estrutural caso o tempo seja mais curto. A atividade avaliativa e a revisão são as etapas mais flexíveis em duração."),
      spacer(),

      h3("Postura e comunicação (15 pts)"),
      p("A condução da aula é integralmente dirigida aos alunos — não há explicações à banca nem quebra do papel docente em nenhum momento. A narrativa pessoal na introdução e a conclusão com reflexão final evidenciam postura de professor experiente, seguro do conteúdo e comprometido com a aprendizagem."),
      spacer(),

      h2("Nota sobre a Metodologia"),
      p("A aula expositiva dialogada combinada com estudo de caso foi escolhida deliberadamente porque Operações Unitárias exige que o aluno transite entre teoria e prática industrial. A metodologia ativa pura (ABP) seria inadequada para 50 minutos com conteúdo deste nível de complexidade. O estudo de caso inserido dentro da aula expositiva é o ponto de equilíbrio correto para este contexto."),
      spacer(),

      h2("Nota sobre o Tempo Mínimo"),
      p("Caso a aula precise ser encerrada em 40 minutos, recomenda-se comprimir o Desenvolvimento Parte 1 (suprimir a discussão detalhada sobre tipos de recheio) e reduzir o tempo da atividade avaliativa para uma única pergunta. A estrutura narrativa introdução-conclusão deve ser preservada integralmente independentemente do tempo disponível."),
    ]
  }]
});

Packer.toBuffer(doc).then(buffer => {
  fs.writeFileSync('/mnt/user-data/outputs/plano-de-aula-destilacao-fracionada.docx', buffer);
  console.log('OK');
});
