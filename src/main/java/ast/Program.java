package ast;

import java.util.List;
import java.util.stream.Collectors;

public record Program(List<SemRule> rules, List<LatexRendering> latexRenderings) implements BaseAst {
    public static String toLatex(Program program) {
        return program.rules.stream().map(SemRule::toLatex)
                .collect(Collectors.joining("\n\n"));
    }
}
