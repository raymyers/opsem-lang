import ast.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LatexRenderer {

    private final Program program;
    private final Map<String, LatexRendering> renderingMap;

    public LatexRenderer(Program program) {
        this.program = program;
        this.renderingMap = new HashMap<>();
        this.program.latexRenderings().forEach(r ->
                this.renderingMap.put(r.label(), r));

    }
    public static String programToLatex(Program program) {
        return new LatexRenderer(program).toLatex();
    }

    public String toLatex() {
        return program.rules().stream().map(this::semRuleToLatex)
                .collect(Collectors.joining("\n\n"));
    }

    public String condToLatex(Cond cond) {
        String inner = cond.exprs().stream().map(this::exprsToLatex).collect(Collectors.joining(" ,\\, "));
        if (cond.exprs().size() == 1) {
            return inner;
        }
        return "(" + inner + ")";
    }

    public String condLineToLatex(CondLine condLine) {
        if (condLine.post().isPresent()) {
            return condToLatex(condLine.pre()) + " => " + condToLatex(condLine.post().get());
        }
        return condToLatex(condLine.pre());
    }

    public String varToLatex(Expr.Var var) {
        String name = var.name();
        if (this.renderingMap.containsKey(name)) {
            LatexRendering latexRendering = this.renderingMap.get(name);
            if (latexRendering.params().isEmpty()) {
                return latexRendering.rendering();
            }
        }
        return trailingNumberToSubscript(name);
    }

    private static String trailingNumberToSubscript(String name) {
        Pattern endWithDigits = Pattern.compile(".*(\\D)(\\d+)$");

        Matcher matcher = endWithDigits.matcher(name);
        if (matcher.matches()) {
            String base = matcher.group(1);
            String sub = matcher.group(2);
            return base + "_" + sub;
        }
        return name;
    }

    public String exprsWrapToLatex(Expr.ExprsWrap exprsWrap) {
        return "(" + exprsToLatex(exprsWrap.exprs()) + ")";
    }

    public String exprsToLatex(Exprs exprs) {
        return exprs.exprs().stream().map(e -> {
            if (e instanceof Expr.ExprsWrap ew) return exprsWrapToLatex(ew);
            if (e instanceof Expr.Var v) return varToLatex(v);
            return ""; // Shouldn't happen
        }).collect(Collectors.joining("\\,"));
    }

    public String semRuleToLatex(SemRule semRule) {
        String sep = " \\quad ";
        String topLineStr = condLinesToLatex(semRule.topLines(), sep);
        String bottomLineStr = condLinesToLatex(semRule.bottomLines(), sep);
        String inference = "\\inference {" + topLineStr + "}{ " + bottomLineStr + "}[" + semRule.name() + "]";
        return slashBracketWrap(inference);
    }

    private static String slashBracketWrap(String inference) {
        return "\\[\n" + inference + "\n\\]";
    }

    private String condLinesToLatex(List<CondLine> lines, String sep) {
        return lines.stream().map(this::condLineToLatex).collect(Collectors.joining(sep));
    }


}
