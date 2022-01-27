package asketch.alloy.util.static_pruning_rules.rclosure;

import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.etc.Operators.STAR;

import asketch.alloy.util.static_pruning_rules.UnaryInfo;
import asketch.alloy.util.static_pruning_rules.UnaryRule;

public class RRule1 extends UnaryRule {

  private RRule1(UnaryInfo unaryInfo) {
    super(unaryInfo);
  }

  public static RRule1 given(UnaryInfo unaryInfo) {
    return new RRule1(unaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(rel.getOp(), STAR, CARET);
  }
}
