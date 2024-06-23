package ast;

import java.util.List;
import java.util.stream.Collectors;

public record Program(List<SemRule> rules) implements BaseAst {
    public String toLatex() {
        return rules.stream().map(SemRule::toLatex)
                .collect(Collectors.joining("\n\n"));
    }
}
