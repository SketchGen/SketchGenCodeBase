package asketch.alloy.util.static_pruning_rules.union;

import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.etc.Operators.STAR;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class URule5 extends BinaryRule {

  private URule5(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static URule5 given(BinaryInfo binaryInfo) {
    return new URule5(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(leftRel.getOp(), STAR, CARET) && opIsOr(rightRel.getOp(), STAR, CARET)
        && sameRelations(getChild(leftRel, 0), getChild(rightRel, 0));
  }
}
