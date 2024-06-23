package ast;

import java.util.Optional;

public record CondLine(Cond pre, Optional<Cond> post) implements BaseAst  {
}
