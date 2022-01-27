package asketch.alloy.util.static_pruning_rules.union;

import static asketch.alloy.etc.Operators.PLUS;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class URule1 extends BinaryRule {

  private URule1(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static URule1 given(BinaryInfo binaryInfo) {
    return new URule1(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return leftRel.getOp().equals(PLUS);
  }
}
