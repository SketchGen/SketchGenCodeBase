package asketch.alloy.util.static_pruning_rules.intersect;

import static asketch.alloy.etc.Operators.TILDE;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class IRule6 extends BinaryRule {

  private IRule6(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static IRule6 given(BinaryInfo binaryInfo) {
    return new IRule6(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(leftRel.getOp(), TILDE) && opIsOr(rightRel.getOp(), TILDE);
  }
}
