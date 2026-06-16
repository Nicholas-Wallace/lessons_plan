package com.example.demo.lessondoc;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocxCompileServiceTest {

    @Test
    void compileShouldWriteDocxWithExpectedSections() throws Exception {
        LessonDocCompileDTO dto = new LessonDocCompileDTO();
        LessonDocCompileDTO.Informacoes inf = new LessonDocCompileDTO.Informacoes();
        inf.nome_candidato = "Maria Silva";
        inf.numero_inscricao = "12345";
        inf.area_concurso = "Matemática";
        inf.publico_alvo = "Ensino Médio";
        inf.disciplina = "Álgebra";
        inf.tempo_aula = "50 min";
        inf.data_aula = "2026-06-16";

        dto.informacoes = inf;
        dto.titulo = "Plano de Aula 1";
        dto.objetivos = List.of("Desenvolver o raciocínio lógico", "Resolver equações simples");

        LessonDocCompileDTO.ConteudoItem conteudoItem = new LessonDocCompileDTO.ConteudoItem();
        conteudoItem.titulo = "Equações";
        conteudoItem.subtopicos = List.of("Definição", "Exemplos");
        dto.conteudo = List.of(conteudoItem);

        LessonDocCompileDTO.MetodologiaItem metodologiaItem = new LessonDocCompileDTO.MetodologiaItem();
        metodologiaItem.nome = "Exposição";
        metodologiaItem.descricao = "Apresentação dos conceitos no quadro.";
        dto.metodologia = List.of(metodologiaItem);

        dto.recursos_didaticos = List.of("Quadro branco", "Marcadores");

        LessonDocCompileDTO.AtividadeAvaliativa atividade = new LessonDocCompileDTO.AtividadeAvaliativa();
        atividade.enunciado = "Resolver problemas.";
        atividade.forma_aplicacao = "Individual";
        LessonDocCompileDTO.CriterioCorrecao criterio = new LessonDocCompileDTO.CriterioCorrecao();
        criterio.criterio = "Clareza";
        criterio.peso = 50;
        atividade.criterios_correcao = List.of(criterio);
        dto.atividade_avaliativa = atividade;

        LessonDocCompileDTO.BibliografiaItem bibliografiaItem = new LessonDocCompileDTO.BibliografiaItem();
        bibliografiaItem.autor = "Silva";
        bibliografiaItem.titulo = "Álgebra Básica";
        bibliografiaItem.editora = "Editora Exemplo";
        bibliografiaItem.ano = 2025;
        dto.bibliografia = List.of(bibliografiaItem);

        DocxCompileService service = new DocxCompileService();
        Path tempDir = Files.createTempDirectory("docx-test");
        ReflectionTestUtils.setField(service, "storageDir", tempDir.toString());

        String resultPath = service.compile(dto);
        assertTrue(resultPath.endsWith(".docx"));

        Path generated = tempDir.resolve(resultPath.substring(resultPath.lastIndexOf('/') + 1));
        assertTrue(Files.exists(generated));

        try (FileInputStream in = new FileInputStream(generated.toFile());
             XWPFDocument document = new XWPFDocument(in)) {
            StringBuilder text = new StringBuilder();
            document.getParagraphs().forEach(p -> text.append(p.getText()).append("\n"));
            document.getTables().forEach(table -> table.getRows().forEach(row -> row.getTableCells().forEach(cell -> cell.getParagraphs().forEach(p -> text.append(p.getText()).append("\n")))));
            String docText = text.toString();

            assertTrue(docText.contains("Plano de Aula 1"));
            assertTrue(docText.contains("IFRN — Instituto Federal de Educação, Ciência e Tecnologia do RN"));
            assertTrue(docText.contains("1. Desenvolver o raciocínio lógico"));
            assertTrue(docText.contains("Equações"));
            assertTrue(docText.contains("Forma de aplicação: Individual"));
            assertTrue(docText.contains("Silva. Álgebra Básica. Editora Exemplo, 2025."));
        }
    }
}
