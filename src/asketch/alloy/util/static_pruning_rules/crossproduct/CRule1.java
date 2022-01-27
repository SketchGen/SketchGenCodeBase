package asketch.alloy.util.static_pruning_rules.crossproduct;

import static asketch.alloy.etc.Operators.ARROW;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class CRule1 extends BinaryRule {

  private CRule1(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static CRule1 given(BinaryInfo binaryInfo) {
    return new CRule1(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(leftRel.getOp(), ARROW);
  }
}
