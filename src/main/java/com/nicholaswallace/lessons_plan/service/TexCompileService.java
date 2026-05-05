package com.nicholaswallace.lessons_plan.service;

import com.nicholaswallace.lessons_plan.dto.LessonDocCompileDTO;
import com.nicholaswallace.lessons_plan.dto.LessonDocCompileDTO.*;
import com.nicholaswallace.lessons_plan.util.TexEscaper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TexCompileService {

    @Value("${app.tex.pdflatex-path:pdflatex}")
    private String pdflatexPath;

    @Value("${app.pdf.storage-dir:./static/compiled}")
    private String storageDir;

    @Value("${app.tex.compile-timeout-seconds:30}")
    private long timeoutSeconds;

    // ── Public entry point ────────────────────────────────────────────────────

    public String compile(LessonDocCompileDTO dto) throws IOException, InterruptedException {
        String id = UUID.randomUUID().toString();
        Path workDir = Files.createTempDirectory("tex-" + id);
        try {
            String template = loadTemplate();
            template = fillPlaceholders(template, dto);

            Path texFile = workDir.resolve("lesson.tex");

            // DEBUG — remove after fix
            System.out.println("===== GENERATED TEX =====");
            System.out.println(template);
            System.out.println("=========================");

            Files.writeString(texFile, template, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            runPdfLatex(workDir, texFile.getFileName().toString());

            Path pdf = workDir.resolve("lesson.pdf");
            if (!Files.exists(pdf)) throw new IOException("PDF not produced");

            Path dest = Paths.get(storageDir);
            Files.createDirectories(dest);
            Files.copy(pdf, dest.resolve(id + ".pdf"), StandardCopyOption.REPLACE_EXISTING);

            return "/compiled/" + id + ".pdf";
        } finally {
            cleanup(workDir);
        }
    }

    // ── Template loading ──────────────────────────────────────────────────────

    private String loadTemplate() throws IOException {
        ClassPathResource tpl = new ClassPathResource("tex/lesson_template_01.tex");
        try (InputStream is = tpl.getInputStream()) {
            return new String(is.readAllBytes());
        }
    }

    // ── Master fill ───────────────────────────────────────────────────────────

    private String fillPlaceholders(String tpl, LessonDocCompileDTO dto) {
        // ── Informações laterais
        Informacoes inf = dto.informacoes;
        tpl = tpl.replace("{{informacoes.nome_candidato}}",  esc(inf.nome_candidato));
        tpl = tpl.replace("{{informacoes.numero_inscricao}}", esc(inf.numero_inscricao));
        tpl = tpl.replace("{{informacoes.area_concurso}}",   esc(inf.area_concurso));
        tpl = tpl.replace("{{informacoes.publico_alvo}}",    esc(inf.publico_alvo));
        tpl = tpl.replace("{{informacoes.disciplina}}",      esc(inf.disciplina));
        tpl = tpl.replace("{{informacoes.tempo_aula}}",      esc(inf.tempo_aula));
        tpl = tpl.replace("{{informacoes.data_aula}}",       esc(inf.data_aula));

        // ── Título
        tpl = tpl.replace("{{titulo}}", esc(dto.titulo));

        // ── Blocos gerados a partir de arrays
        tpl = tpl.replace("{{BLOCO_OBJETIVOS}}",   buildObjetivos(dto.objetivos));
        tpl = tpl.replace("{{BLOCO_CONTEUDO}}",    buildConteudo(dto.conteudo));
        tpl = tpl.replace("{{BLOCO_METODOLOGIA}}", buildMetodologia(dto.metodologia));
        tpl = tpl.replace("{{BLOCO_RECURSOS}}",    buildRecursos(dto.recursos_didaticos));

        // ── Atividade avaliativa
        AtividadeAvaliativa av = dto.atividade_avaliativa;
        tpl = tpl.replace("{{atividade_avaliativa.enunciado}}",       esc(av.enunciado));
        tpl = tpl.replace("{{atividade_avaliativa.forma_aplicacao}}", esc(av.forma_aplicacao));
        tpl = tpl.replace("{{BLOCO_CRITERIOS}}",  buildCriterios(av.criterios_correcao));

        // ── Bibliografia
        tpl = tpl.replace("{{BLOCO_BIBLIOGRAFIA}}", buildBibliografia(dto.bibliografia));

        return tpl;
    }

    // ── Block builders ────────────────────────────────────────────────────────

    /**
     * objetivos[] → \item <texto>
     */
    private String buildObjetivos(List<String> objetivos) {
        if (objetivos == null || objetivos.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String o : objetivos) {
            sb.append("  \\item ").append(escText(o)).append("\n");
        }
        return sb.toString();
    }

    /**
     * conteudo[] → \item <titulo>
     *   If subtopicos present:
     *     \begin{enumerate}[label=<N>.\arabic*.]
     *       \item <subtopico>
     *     \end{enumerate}
     */
    private String buildConteudo(List<ConteudoItem> conteudo) {
        if (conteudo == null || conteudo.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < conteudo.size(); i++) {
            ConteudoItem item = conteudo.get(i);
            int topicNumber = i + 1;
            sb.append("  \\item ").append(escText(item.titulo)).append("\n");
            if (item.subtopicos != null && !item.subtopicos.isEmpty()) {
                sb.append("    \\begin{enumerate}[label=").append(topicNumber).append(".\\arabic*.]\n");
                for (String sub : item.subtopicos) {
                    sb.append("      \\item ").append(escText(sub)).append("\n");
                }
                sb.append("    \\end{enumerate}\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * metodologia[] → \textbf{<nome>:} <descricao>
     */
    private String buildMetodologia(List<MetodologiaItem> metodologia) {
        if (metodologia == null || metodologia.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (MetodologiaItem m : metodologia) {
            sb.append("\\textbf{").append(escText(m.nome)).append(":} ")
              .append(escText(m.descricao)).append("\n\n");
        }
        return sb.toString();
    }

    /**
     * recursos_didaticos[] → \item <texto>
     */
    private String buildRecursos(List<String> recursos) {
        if (recursos == null || recursos.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String r : recursos) {
            sb.append("  \\item ").append(escText(r)).append("\n");
        }
        return sb.toString();
    }

    /**
     * criterios_correcao[] → \item <criterio> --- \textbf{<peso>\%}
     */
    private String buildCriterios(List<CriterioCorrecao> criterios) {
        if (criterios == null || criterios.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (CriterioCorrecao c : criterios) {
            sb.append("  \\item ").append(escText(c.criterio))
              .append(" --- \\textbf{").append(c.peso).append("\\%}\n");
        }
        return sb.toString();
    }

    /**
     * bibliografia[] → \item formatted entry.
     *   With "periodico" field  → article format
     *   With "editora"  field   → book format
     */
    private String buildBibliografia(List<BibliografiaItem> bibliografia) {
        if (bibliografia == null || bibliografia.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (BibliografiaItem b : bibliografia) {
            sb.append("  \\item ");
            sb.append(escText(b.autor)).append(". ");
            sb.append("\\textbf{").append(escText(b.titulo)).append("}. ");

            if (b.periodico != null && !b.periodico.isBlank()) {
                // Article: Autor. Título. Periódico, volume, ano.
                sb.append("\\textit{").append(escText(b.periodico)).append("}");
                if (b.volume != null && !b.volume.isBlank()) {
                    sb.append(", ").append(escText(b.volume));
                }
            } else if (b.editora != null && !b.editora.isBlank()) {
                // Book: Autor. Título. Editora, ano.
                sb.append(escText(b.editora));
            }

            if (b.ano != null) {
                sb.append(", ").append(b.ano);
            }
            sb.append(".\n\n");
        }
        return sb.toString();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Full escape — for scalar sidebar/header values where the user input
     * must not contain any LaTeX commands (escapes \ and {} as well).
     */
    private String esc(String value) {
        return TexEscaper.escape(value == null ? "" : value);
    }

    /**
     * Text-only escape — for plain string values inside builder methods.
     * Does NOT escape backslash or braces so that LaTeX commands produced
     * by the builders (e.g. \textbf{}, \item) are preserved.
     */
    private String escText(String value) {
        return TexEscaper.escapeText(value == null ? "" : value);
    }

    private void runPdfLatex(Path workDir, String texFileName) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                pdflatexPath,
                "-interaction=nonstopmode",
                "-halt-on-error",
                "-output-directory", workDir.toString(),
                texFileName
        );
        pb.directory(workDir.toFile());
        pb.redirectErrorStream(true);

        Process p = pb.start();
        boolean finished = p.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        if (!finished) {
            p.destroyForcibly();
            throw new IOException("pdflatex timed out after " + timeoutSeconds + "s");
        }
        int exit = p.exitValue();
        if (exit != 0) {
            String out = new String(p.getInputStream().readAllBytes());
            throw new IOException("pdflatex failed (exit " + exit + "):\n" + out);
        }
    }

    private void cleanup(Path workDir) {
        try {
            Files.walk(workDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try { Files.deleteIfExists(path); } catch (IOException ignored) {}
                    });
        } catch (IOException ignored) {}
    }
}