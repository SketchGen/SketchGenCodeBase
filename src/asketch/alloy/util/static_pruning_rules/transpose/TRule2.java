package asketch.alloy.util.static_pruning_rules.transpose;

import static asketch.alloy.etc.Operators.ARROW;

import asketch.alloy.util.static_pruning_rules.UnaryInfo;
import asketch.alloy.util.static_pruning_rules.UnaryRule;

public class TRule2 extends UnaryRule {

  private TRule2(UnaryInfo unaryInfo) {
    super(unaryInfo);
  }

  public static TRule2 given(UnaryInfo unaryInfo) {
    return new TRule2(unaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(rel.getOp(), ARROW);
  }
}
