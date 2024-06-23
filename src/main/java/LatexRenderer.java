import ast.*;
import org.stringtemplate.v4.ST;

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

    public String simpleCondLineToLatex(CondLine.SimpleCondLine condLine) {
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
        if (exprs.exprs().size() > 1) {
            Expr first = exprs.exprs().get(0);
            int argCount = exprs.exprs().size() - 1;
            if (first instanceof Expr.Var label) {
                LatexRendering latexRendering = this.renderingMap.get(label.name());
                if (latexRendering != null && latexRendering.params().size() == argCount) {
                    ST template = new ST(latexRendering.rendering());
                    for (int i = 0; i < argCount; i++) {
                        Expr arg = exprs.exprs().get(i + 1);
                        String param = latexRendering.params().get(i);
                        template.add(param, exprToLatex(arg));
                    }
                    return template.render();
                } else {
                    // WARN?
                    System.err.println("WARN: Arg count mismatch for label " + label.name());
                }
            }
        }
        return exprs.exprs().stream().map(this::exprToLatex).collect(Collectors.joining("\\,"));
    }

    private String exprToLatex(Expr e) {
        if (e instanceof Expr.ExprsWrap ew) return exprsWrapToLatex(ew);
        if (e instanceof Expr.Var v) return varToLatex(v);
        return "";
    }

    public String semRuleToLatex(SemRule semRule) {
        return slashBracketWrap(condLayersToLatex(semRule.name(), semRule.layers()));
    }

    private String condLayersToLatex(String name, List<CondLayer> layers) {
        String templateStr = "\\inference {<top>}{<bottom>}[<name>]";
        ST template = new ST(templateStr);
        boolean topFull = false;
        boolean bottomFull = false;
        String sep = " & ";

        for (var layer : layers) {
            String renderedLayer = condLayerToLatex(layer, sep);
            if (!topFull) {
                template.add("top", renderedLayer);
                topFull = true;
            } else if (!bottomFull) {
                template.add("bottom", renderedLayer);
                bottomFull = true;
            } else {
                ST innerTemplate = template;
                template = new ST(templateStr);
                template.add("top", innerTemplate.render());
                template.add("bottom", renderedLayer);
            }
        }
        template.add("name", name);

        return template.render();
    }

    private static String slashBracketWrap(String inference) {
        return "\\[\n" + inference + "\n\\]";
    }

    private String condLayerToLatex(CondLayer layer, String sep) {
        return layer.condLines().stream().map(this::condLineToLatex).collect(Collectors.joining(sep));
    }

    private String condLineToLatex(CondLine condLine) {
        if (condLine instanceof CondLine.SimpleCondLine simpleCondLine) {
            return simpleCondLineToLatex(simpleCondLine);
        }
        if (condLine instanceof CondLine.CondBlock condBlock) {
            return condLayersToLatex("", condBlock.layers());
        }
        return "";
    }


}
