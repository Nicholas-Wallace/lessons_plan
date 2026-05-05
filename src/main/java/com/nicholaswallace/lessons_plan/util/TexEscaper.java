package com.nicholaswallace.lessons_plan.util;

public final class TexEscaper {

    private TexEscaper() {}

    /**
     * Full escape — use for raw user input that must NOT contain any LaTeX commands.
     * Escapes all LaTeX special characters including backslash and braces.
     */
    public static String escape(String input) {
        if (input == null) return "";
        String s = input;
        s = s.replace("\\", "\\textbackslash{}");
        s = s.replace("&",  "\\&");
        s = s.replace("%",  "\\%");
        s = s.replace("$",  "\\$");
        s = s.replace("#",  "\\#");
        s = s.replace("_",  "\\_");
        s = s.replace("{",  "\\{");
        s = s.replace("}",  "\\}");
        s = s.replace("~",  "\\textasciitilde{}");
        s = s.replace("^",  "\\^{}");
        s = s.replace("\n", "\\\\\n");
        return s;
    }

    /**
     * Text-only escape — use for plain string values inside builder methods
     * (objetivos, subtopicos, recursos, etc.).
     * Does NOT escape backslash or braces, so LaTeX commands embedded by the
     * builders (e.g. \textbf{}, \textit{}) are preserved.
     */
    public static String escapeText(String input) {
        if (input == null) return "";
        String s = input;
        s = s.replace("&",  "\\&");
        s = s.replace("%",  "\\%");
        s = s.replace("$",  "\\$");
        s = s.replace("#",  "\\#");
        s = s.replace("_",  "\\_");
        s = s.replace("~",  "\\textasciitilde{}");
        s = s.replace("^",  "\\^{}");
        return s;
    }
}