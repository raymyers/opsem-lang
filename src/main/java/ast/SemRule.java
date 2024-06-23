package ast;

import java.util.List;

public record SemRule(String name, List<CondLine> topLines, List<CondLine> bottomLines) implements BaseAst {

}
