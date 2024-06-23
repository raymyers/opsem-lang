package ast;

import java.util.List;

public record SemRule(String name, List<CondLayer> layers) implements BaseAst {

}
