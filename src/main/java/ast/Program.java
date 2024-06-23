package ast;

import java.util.List;

public record Program(List<SemRule> rules, List<LatexRendering> latexRenderings) implements BaseAst {
}
