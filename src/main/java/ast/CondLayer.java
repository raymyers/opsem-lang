package ast;

import java.util.List;

public record CondLayer(List<CondLine> condLines) implements BaseAst {
}
