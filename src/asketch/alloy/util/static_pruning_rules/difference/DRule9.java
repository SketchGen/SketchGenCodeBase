package asketch.alloy.util.static_pruning_rules.difference;

import static asketch.alloy.etc.Operators.TILDE;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class DRule9 extends BinaryRule {

  private DRule9(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static DRule9 given(BinaryInfo binaryInfo) {
    return new DRule9(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(leftRel.getOp(), TILDE) && opIsOr(rightRel.getOp(), TILDE);
  }
}
