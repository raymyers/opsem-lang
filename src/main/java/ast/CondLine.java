package ast;

import java.util.List;
import java.util.Optional;

public interface CondLine extends BaseAst  {
    record SimpleCondLine(Cond pre, Optional<Cond> post) implements CondLine  {

    }
    record CondBlock(List<CondLayer> layers) implements CondLine  {

    }

}
