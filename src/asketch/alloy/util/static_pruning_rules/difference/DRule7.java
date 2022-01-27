package asketch.alloy.util.static_pruning_rules.difference;

import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.etc.Operators.DOT;
import static asketch.alloy.etc.Operators.STAR;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class DRule7 extends BinaryRule {

  private DRule7(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static DRule7 given(BinaryInfo binaryInfo) {
    return new DRule7(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(rightRel.getOp(), STAR, CARET) && getChild(rightRel, 0)
        .equals(duplicateNodesUnderOps(leftRel, DOT));
  }
}
