package asketch.alloy.util.static_pruning_rules.closure;

import static asketch.alloy.etc.Operators.ARROW;
import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.etc.Operators.STAR;

import asketch.alloy.util.static_pruning_rules.UnaryInfo;
import asketch.alloy.util.static_pruning_rules.UnaryRule;

public class VRule2 extends UnaryRule {

  private VRule2(UnaryInfo unaryInfo) {
    super(unaryInfo);
  }

  public static VRule2 given(UnaryInfo unaryInfo) {
    return new VRule2(unaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(rel.getOp(), ARROW);
  }
}
