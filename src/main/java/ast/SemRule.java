package ast;

import java.util.List;
import java.util.stream.Collectors;

public record SemRule(String name, List<CondLine> topLines, List<CondLine> bottomLines) implements BaseAst {
    public String toLatex() {
        String sep = " \\quad ";
        String topLineStr = condLinesToLatex(topLines, sep);
        String bottomLineStr = condLinesToLatex(bottomLines, sep);
        String inference = "\\inference {" + topLineStr + "}{ " + bottomLineStr + "}[" + name + "]";
        return slashBracketWrap(inference);
    }

    private static String slashBracketWrap(String inference) {
        return "\\[\n" + inference + "\n\\]";
    }

    private String condLinesToLatex( List<CondLine> lines, String sep) {
        return lines.stream().map(CondLine::toLatex).collect(Collectors.joining(sep));
    }
}
