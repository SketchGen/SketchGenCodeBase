package asketch.alloy.util.static_pruning_rules.transpose;

import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.etc.Operators.STAR;
import static asketch.alloy.etc.Operators.TILDE;

import asketch.alloy.util.static_pruning_rules.UnaryInfo;
import asketch.alloy.util.static_pruning_rules.UnaryRule;

public class TRule1 extends UnaryRule {

  private TRule1(UnaryInfo unaryInfo) {
    super(unaryInfo);
  }

  public static TRule1 given(UnaryInfo unaryInfo) {
    return new TRule1(unaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(rel.getOp(), STAR, CARET, TILDE);
  }
}
