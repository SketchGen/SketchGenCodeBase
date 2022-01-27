package asketch.alloy.util.static_pruning_rules.intersect;

import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.etc.Operators.STAR;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class IRule5 extends BinaryRule {

  private IRule5(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static IRule5 given(BinaryInfo binaryInfo) {
    return new IRule5(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(leftRel.getOp(), STAR, CARET) && opIsOr(rightRel.getOp(), STAR, CARET)
        && sameRelations(getChild(leftRel, 0), getChild(rightRel, 0));
  }
}
