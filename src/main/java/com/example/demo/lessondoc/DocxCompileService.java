package com.example.demo.lessondoc;

import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFTable.XWPFBorderType;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

@Service
public class DocxCompileService {

    @Value("${app.docx.storage-dir:./static/compiled}")
    private String storageDir;

    public String getStorageDir() {
        return storageDir;
    }

    public String compile(LessonDocCompileDTO dto) throws IOException {
        String id = UUID.randomUUID().toString();
        Path destination = Paths.get(storageDir);
        Files.createDirectories(destination);

        Path outputFile = destination.resolve(id + ".docx");

        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(outputFile.toFile())) {

            setDocumentLayout(document);
            createHeader(document, dto);
            createFooter(document, dto);

            addTitle(document, safe(dto.titulo));
            addSubtitle(document, safe(dto.titulo));
            addSectionHeading(document, "1. Identificação");
            addIdentificationTable(document, dto);
            addSpacer(document);

            addSectionHeading(document, "1b. Conteúdos Prévios / Pré-requisitos");
            addBullet(document, "Revisão do conteúdo programático anterior aplicado ao tema da aula");
            addBullet(document, "Conceitos básicos relacionados aos objetivos da aula");
            addBullet(document, "Linguagem técnica e vocabulário específico do tema");
            addSpacer(document);

            addSectionHeading(document, "2. Objetivos Específicos");
            addParagraph(document, "Ao final da aula, o aluno deverá ser capaz de:");
            addNumberedList(document, dto.objetivos);
            addSpacer(document);

            addSectionHeading(document, "4. Conteúdo Programático");
            addNumberedContent(document, dto.conteudo);
            addSpacer(document);

            addSectionHeading(document, "5. Metodologia");
            addMethodology(document, dto.metodologia);
            addSpacer(document);

            addSectionHeading(document, "7. Recursos Didáticos");
            addBulletList(document, dto.recursos_didaticos);
            addSpacer(document);

            addSectionHeading(document, "8. Avaliação");
            addEvaluation(document, dto.atividade_avaliativa);
            addSpacer(document);

            addSectionHeading(document, "9. Referências");
            addBibliography(document, dto.bibliografia);
            addSpacer(document);

            document.write(out);
        }

        return "/compiled/" + id + ".docx";
    }

    private void setDocumentLayout(XWPFDocument document) {
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        CTPageSz pageSize = sectPr.addNewPgSz();
        pageSize.setW(BigInteger.valueOf(11906));
        pageSize.setH(BigInteger.valueOf(16838));
        pageSize.setOrient(STPageOrientation.PORTRAIT);

        CTPageMar pageMar = sectPr.addNewPgMar();
        pageMar.setTop(BigInteger.valueOf(1440));
        pageMar.setRight(BigInteger.valueOf(1440));
        pageMar.setBottom(BigInteger.valueOf(1440));
        pageMar.setLeft(BigInteger.valueOf(1440));
    }

    private void createHeader(XWPFDocument document, LessonDocCompileDTO dto) {
        XWPFHeaderFooterPolicy policy = document.createHeaderFooterPolicy();
        XWPFHeader header = policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
        XWPFParagraph paragraph = header.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        paragraph.setBorderBottom(Borders.SINGLE);
        paragraph.setSpacingAfter(120);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Arial");
        run.setFontSize(10);
        run.setColor("2E75B6");
        run.setText("IFRN — Plano de Aula  | " + safe(dto.titulo));
    }

    private void createFooter(XWPFDocument document, LessonDocCompileDTO dto) {
        XWPFHeaderFooterPolicy policy = document.createHeaderFooterPolicy();
        XWPFFooter footer = policy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);
        XWPFParagraph paragraph = footer.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.BOTH);
        paragraph.setBorderTop(Borders.SINGLE);
        paragraph.setSpacingBefore(120);
        paragraph.setPageBreak(false);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Arial");
        run.setFontSize(9);
        run.setColor("888888");
        run.setText("Professor: " + safe(dto.informacoes != null ? dto.informacoes.nome_candidato : "") + " — " + safe(dto.informacoes != null ? dto.informacoes.disciplina : ""));
    }

    private void addTitle(XWPFDocument document, String title) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.setSpacingAfter(80);
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.setFontFamily("Arial");
        run.setFontSize(32);
        run.setColor("1F4E79");
        run.setText("PLANO DE AULA");
    }

    private void addSubtitle(XWPFDocument document, String subtitle) {
        if (subtitle == null || subtitle.isBlank()) {
            return;
        }
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.setSpacingAfter(320);
        XWPFRun run = paragraph.createRun();
        run.setItalic(true);
        run.setFontFamily("Arial");
        run.setFontSize(20);
        run.setColor("2E75B6");
        run.setText(subtitle);
    }

    private void addSectionHeading(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setSpacingBefore(240);
        paragraph.setSpacingAfter(120);
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.setFontFamily("Arial");
        run.setFontSize(18);
        run.setColor("2E75B6");
        run.setText(text);
    }

    private void addParagraph(XWPFDocument document, String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.BOTH);
        paragraph.setSpacingBefore(60);
        paragraph.setSpacingAfter(60);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Arial");
        run.setFontSize(14);
        run.setText(text);
    }

    private void addBullet(XWPFDocument document, String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setSpacingBefore(40);
        paragraph.setSpacingAfter(40);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Arial");
        run.setFontSize(14);
        run.setText("• " + text);
    }

    private void addNumberedList(XWPFDocument document, List<String> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setSpacingBefore(40);
            paragraph.setSpacingAfter(40);
            XWPFRun run = paragraph.createRun();
            run.setFontFamily("Arial");
            run.setFontSize(14);
            run.setText((i + 1) + ". " + safe(items.get(i)));
        }
    }

    private void addNumberedContent(XWPFDocument document, List<LessonDocCompileDTO.ConteudoItem> conteudo) {
        if (conteudo == null || conteudo.isEmpty()) {
            return;
        }
        for (int i = 0; i < conteudo.size(); i++) {
            LessonDocCompileDTO.ConteudoItem item = conteudo.get(i);
            addNumberedParagraph(document, (i + 1) + ". " + safe(item.titulo));
            if (item.subtopicos != null) {
                for (String sub : item.subtopicos) {
                    addBullet(document, safe(sub));
                }
            }
        }
    }

    private void addNumberedParagraph(XWPFDocument document, String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setSpacingBefore(40);
        paragraph.setSpacingAfter(40);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Arial");
        run.setFontSize(14);
        run.setText(text);
    }

    private void addMethodology(XWPFDocument document, List<LessonDocCompileDTO.MetodologiaItem> metodologia) {
        if (metodologia == null || metodologia.isEmpty()) {
            addParagraph(document, "A metodologia será definida conforme o plano de aula.");
            return;
        }
        for (LessonDocCompileDTO.MetodologiaItem item : metodologia) {
            addParagraph(document, safe(item.nome) + ": " + safe(item.descricao));
        }
    }

    private void addBulletList(XWPFDocument document, List<String> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        for (String item : items) {
            addBullet(document, safe(item));
        }
    }

    private void addEvaluation(XWPFDocument document, LessonDocCompileDTO.AtividadeAvaliativa atividade) {
        if (atividade == null) {
            addParagraph(document, "Nenhuma atividade avaliativa definida.");
            return;
        }
        if (atividade.enunciado != null && !atividade.enunciado.isBlank()) {
            addParagraph(document, "Enunciado: " + safe(atividade.enunciado));
        }
        if (atividade.forma_aplicacao != null && !atividade.forma_aplicacao.isBlank()) {
            addParagraph(document, "Forma de aplicação: " + safe(atividade.forma_aplicacao));
        }
        if (atividade.criterios_correcao != null && !atividade.criterios_correcao.isEmpty()) {
            addParagraph(document, "Critérios de correção:");
            addCriteriaTable(document, atividade);
        }
    }

    private void addIdentificationTable(XWPFDocument document, LessonDocCompileDTO dto) {
        XWPFTable table = document.createTable();
        setTableBorders(table);
        setTableWidth(table, 9360);

        addTableRow(table, "Instituição", "IFRN — Instituto Federal de Educação, Ciência e Tecnologia do RN");
        addTableRow(table, "Curso", "Tecnologia em Processos Químicos");
        addTableRow(table, "Nível", "Ensino Superior");
        addTableRow(table, "Disciplina", safe(dto.informacoes != null ? dto.informacoes.disciplina : ""));
        addTableRow(table, "Tema", safe(dto.titulo));
        addTableRow(table, "Professor", safe(dto.informacoes != null ? dto.informacoes.nome_candidato : ""));
        addTableRow(table, "Carga horária", safe(dto.informacoes != null ? dto.informacoes.tempo_aula : ""));
    }

    private void addCriteriaTable(XWPFDocument document, LessonDocCompileDTO.AtividadeAvaliativa atividade) {
        XWPFTable table = document.createTable();
        setTableBorders(table);
        setTableWidth(table, 9360);

        addTableHeader(table, "Critério", "Pontuação");
        for (LessonDocCompileDTO.CriterioCorrecao criterio : atividade.criterios_correcao) {
            addTableRow(table, safe(criterio.criterio), String.valueOf(criterio.peso) + " pts");
        }
    }

    private void addTableHeader(XWPFTable table, String first, String second) {
        XWPFTableRow row = table.getRow(0);
        setCellText(row.getCell(0), first, true, "1F4E79", "FFFFFF");
        XWPFTableCell secondCell = row.addNewTableCell();
        setCellText(secondCell, second, true, "1F4E79", "FFFFFF");
    }

    private void addTableRow(XWPFTable table, String label, String value) {
        XWPFTableRow row = table.createRow();
        if (row.getTableCells().size() == 1) {
            row.createCell();
        }
        setCellText(row.getCell(0), label, false, "EBF3FB", "1F4E79");
        setCellText(row.getCell(1), value, false, "FFFFFF", "000000");
    }

    private void addTableRowWithBlueLabel(XWPFTable table, String label, String value) {
        XWPFTableRow row = table.createRow();
        if (row.getTableCells().size() == 1) {
            row.createCell();
        }
        setCellText(row.getCell(0), label, false, "1F4E79", "FFFFFF");
        setCellText(row.getCell(1), value, false, "FFFFFF", "000000");
    }

    private void addSingleColumnRow(XWPFTable table, String label, String value) {
        XWPFTableRow row = table.createRow();
        XWPFTableCell cell = row.getCell(0);
        if (cell == null) {
            cell = row.createCell();
        }
        cell.removeParagraph(0);
        XWPFParagraph pLabel = cell.addParagraph();
        XWPFRun rLabel = pLabel.createRun();
        rLabel.setFontFamily("Arial");
        rLabel.setFontSize(12);
        rLabel.setBold(true);
        rLabel.setColor("1F4E79");
        rLabel.setText(label);

        XWPFParagraph pValue = cell.addParagraph();
        XWPFRun rValue = pValue.createRun();
        rValue.setFontFamily("Arial");
        rValue.setFontSize(12);
        rValue.setText(value == null ? "" : value);
    }

    private void setCellText(XWPFTableCell cell, String text, boolean bold, String fill, String color) {
        if (cell == null) {
            return;
        }
        cell.removeParagraph(0);
        XWPFParagraph paragraph = cell.addParagraph();
        paragraph.setSpacingBefore(80);
        paragraph.setSpacingAfter(80);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Arial");
        run.setFontSize(12);
        run.setBold(bold);
        run.setColor(color);
        run.setText(safe(text));
        CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
        CTShd shd = tcPr.isSetShd() ? tcPr.getShd() : tcPr.addNewShd();
        shd.setFill(fill);
    }

    private void setTableBorders(XWPFTable table) {
        table.setInsideHBorder(XWPFBorderType.SINGLE, 1, 0, "CCCCCC");
        table.setInsideVBorder(XWPFBorderType.SINGLE, 1, 0, "CCCCCC");
        table.setBottomBorder(XWPFBorderType.SINGLE, 1, 0, "CCCCCC");
        table.setTopBorder(XWPFBorderType.SINGLE, 1, 0, "CCCCCC");
        table.setLeftBorder(XWPFBorderType.SINGLE, 1, 0, "CCCCCC");
        table.setRightBorder(XWPFBorderType.SINGLE, 1, 0, "CCCCCC");
    }

    private void setTableWidth(XWPFTable table, int width) {
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) {
            tblPr = table.getCTTbl().addNewTblPr();
        }
        table.setWidthType(TableWidthType.DXA);
        table.setWidth(width);
    }

    private void addBibliography(XWPFDocument document, List<LessonDocCompileDTO.BibliografiaItem> bibliografia) {
        if (bibliografia == null || bibliografia.isEmpty()) {
            addParagraph(document, "Nenhuma referência cadastrada.");
            return;
        }
        for (LessonDocCompileDTO.BibliografiaItem item : bibliografia) {
            addBullet(document, buildBibliographyItem(item));
        }
    }

    private String buildBibliographyItem(LessonDocCompileDTO.BibliografiaItem item) {
        StringBuilder builder = new StringBuilder();
        builder.append(safe(item.autor)).append(". ");
        builder.append(safe(item.titulo)).append(". ");
        if (item.periodico != null && !item.periodico.isBlank()) {
            builder.append(safe(item.periodico));
            if (item.volume != null && !item.volume.isBlank()) {
                builder.append(", ").append(safe(item.volume));
            }
        } else if (item.editora != null && !item.editora.isBlank()) {
            builder.append(safe(item.editora));
        }
        if (item.ano != null) {
            builder.append(", ").append(item.ano);
        }
        builder.append(".");
        return builder.toString();
    }

    private void addSpacer(XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.createRun().addBreak(BreakType.TEXT_WRAPPING);
        paragraph.setSpacingAfter(80);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
