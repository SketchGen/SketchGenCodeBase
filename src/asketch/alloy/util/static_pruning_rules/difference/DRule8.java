package asketch.alloy.util.static_pruning_rules.difference;

import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.etc.Operators.STAR;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class DRule8 extends BinaryRule {

  private DRule8(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static DRule8 given(BinaryInfo binaryInfo) {
    return new DRule8(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(leftRel.getOp(), STAR, CARET)
        && opIsOr(rightRel.getOp(), STAR, CARET)
        // Remove the case where *a - ^a
        && (!opIsOr(leftRel.getOp(), STAR) || !opIsOr(rightRel.getOp(), CARET))
        && sameRelations(getChild(leftRel, 0), getChild(rightRel, 0));
  }
}
