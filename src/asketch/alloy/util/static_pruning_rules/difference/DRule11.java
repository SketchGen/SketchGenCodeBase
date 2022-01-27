package asketch.alloy.util.static_pruning_rules.difference;

import static asketch.alloy.etc.Operators.AMP;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class DRule11 extends BinaryRule {

  private DRule11(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static DRule11 given(BinaryInfo binaryInfo) {
    return new DRule11(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(leftRel.getOp(), AMP);
  }
}
