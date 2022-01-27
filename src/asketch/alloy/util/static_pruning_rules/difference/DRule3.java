package asketch.alloy.util.static_pruning_rules.difference;

import static asketch.alloy.etc.Operators.PLUS;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class DRule3 extends BinaryRule {

  private DRule3(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static DRule3 given(BinaryInfo binaryInfo) {
    return new DRule3(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(rightRel.getOp(), PLUS);
  }
}
