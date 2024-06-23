package ast;

import java.util.List;
import java.util.stream.Collectors;

public record SemRule(String name, List<CondLine> topLines, List<CondLine> bottomLines) implements BaseAst {
    public static String toLatex(SemRule semRule) {
        String sep = " \\quad ";
        String topLineStr = semRule.condLinesToLatex(semRule.topLines, sep);
        String bottomLineStr = semRule.condLinesToLatex(semRule.bottomLines, sep);
        String inference = "\\inference {" + topLineStr + "}{ " + bottomLineStr + "}[" + semRule.name + "]";
        return slashBracketWrap(inference);
    }

    private static String slashBracketWrap(String inference) {
        return "\\[\n" + inference + "\n\\]";
    }

    private String condLinesToLatex( List<CondLine> lines, String sep) {
        return lines.stream().map(CondLine::toLatex).collect(Collectors.joining(sep));
    }
}
