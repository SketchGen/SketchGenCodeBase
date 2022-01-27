package asketch.alloy.util.static_pruning_rules.union;

import static asketch.alloy.etc.Operators.TILDE;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class URule6 extends BinaryRule {

  private URule6(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static URule6 given(BinaryInfo binaryInfo) {
    return new URule6(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(leftRel.getOp(), TILDE) && opIsOr(rightRel.getOp(), TILDE);
  }
}
