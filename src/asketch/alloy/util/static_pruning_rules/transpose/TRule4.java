package asketch.alloy.util.static_pruning_rules.transpose;

import static asketch.alloy.etc.Operators.AMP;
import static asketch.alloy.etc.Operators.DOT;
import static asketch.alloy.etc.Operators.MINUS;
import static asketch.alloy.etc.Operators.PLUS;
import static asketch.alloy.etc.Operators.TILDE;

import asketch.alloy.util.static_pruning_rules.UnaryInfo;
import asketch.alloy.util.static_pruning_rules.UnaryRule;

public class TRule4 extends UnaryRule {

  private TRule4(UnaryInfo unaryInfo) {
    super(unaryInfo);
  }

  public static TRule4 given(UnaryInfo unaryInfo) {
    return new TRule4(unaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(rel, PLUS, AMP, MINUS, DOT)
        && (opIsOr(getChild(rel, 0).getOp(), TILDE) || opIsOr(getChild(rel, 1).getOp(), TILDE));
  }
}
