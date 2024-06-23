import ast.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LatexRenderer {

    public static String toLatex(Program program) {
        return program.rules().stream().map(LatexRenderer::toLatex)
                .collect(Collectors.joining("\n\n"));
    }

    public static String toLatex(Cond cond) {
        String inner = cond.exprs().stream().map(e -> toLatex(e)).collect(Collectors.joining(" ,\\, "));
        if (cond.exprs().size() == 1) {
            return inner;
        }
        return "(" + inner + ")";
    }

    public static String toLatex(CondLine condLine) {
        if (condLine.post().isPresent()) {
            return toLatex(condLine.pre()) + " => " + toLatex(condLine.post().get());
        }
        return toLatex(condLine.pre());
    }

    public static String toLatex(Expr.Var var) {
        Pattern endWithDigits = Pattern.compile(".*(\\D)(\\d+)$");
        Matcher matcher = endWithDigits.matcher(var.name());
        if (matcher.matches()) {
            String base = matcher.group(1);
            String sub = matcher.group(2);
            return base + "_" + sub;
        }
        return var.name();
    }

    public static String toLatex(Expr.ExprsWrap exprsWrap) {
        return "(" + toLatex(exprsWrap.exprs()) + ")";
    }

    public static String toLatex(Exprs exprs) {
        return exprs.exprs().stream().map(e -> {
            if (e instanceof Expr.ExprsWrap ew) return toLatex(ew);
            if (e instanceof Expr.Var v) return toLatex(v);
            return ""; // Shouldn't happen
        }).collect(Collectors.joining("\\,"));
    }

    public static String toLatex(SemRule semRule) {
        String sep = " \\quad ";
        String topLineStr = condLinesToLatex(semRule.topLines(), sep);
        String bottomLineStr = condLinesToLatex(semRule.bottomLines(), sep);
        String inference = "\\inference {" + topLineStr + "}{ " + bottomLineStr + "}[" + semRule.name() + "]";
        return slashBracketWrap(inference);
    }

    private static String slashBracketWrap(String inference) {
        return "\\[\n" + inference + "\n\\]";
    }

    private static String condLinesToLatex(List<CondLine> lines, String sep) {
        return lines.stream().map(LatexRenderer::toLatex).collect(Collectors.joining(sep));
    }
}
