package asketch.alloy.util.static_pruning_rules.transpose;

import static asketch.alloy.etc.Operators.ARROW;
import static asketch.alloy.etc.Operators.DOT;

import asketch.alloy.util.static_pruning_rules.UnaryInfo;
import asketch.alloy.util.static_pruning_rules.UnaryRule;

public class TRule3 extends UnaryRule {

  private TRule3(UnaryInfo unaryInfo) {
    super(unaryInfo);
  }

  public static TRule3 given(UnaryInfo unaryInfo) {
    return new TRule3(unaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(rel.getOp(), DOT)
        && (opIsOr(getChild(rel, 0), ARROW) || opIsOr(getChild(rel, 1), ARROW));
  }
}
