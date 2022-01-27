package asketch.alloy.util.static_pruning_rules.crossproduct;

import static asketch.alloy.etc.Operators.DOT;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class CRule2 extends BinaryRule {

  private CRule2(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static CRule2 given(BinaryInfo binaryInfo) {
    return new CRule2(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(leftRel.getOp(), DOT) || opIsOr(rightRel.getOp(), DOT);
  }
}
