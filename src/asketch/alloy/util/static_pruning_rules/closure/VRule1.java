package asketch.alloy.util.static_pruning_rules.closure;

import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.etc.Operators.STAR;

import asketch.alloy.util.static_pruning_rules.UnaryInfo;
import asketch.alloy.util.static_pruning_rules.UnaryRule;

public class VRule1 extends UnaryRule {

  private VRule1(UnaryInfo unaryInfo) {
    super(unaryInfo);
  }

  public static VRule1 given(UnaryInfo unaryInfo) {
    return new VRule1(unaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(rel.getOp(), STAR, CARET);
  }
}
