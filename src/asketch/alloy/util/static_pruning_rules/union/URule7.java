package asketch.alloy.util.static_pruning_rules.union;

import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.etc.Operators.DOT;
import static asketch.alloy.etc.Operators.STAR;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class URule7 extends BinaryRule {

  private URule7(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static URule7 given(BinaryInfo binaryInfo) {
    return new URule7(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return (opIsOr(leftRel.getOp(), STAR, CARET) && getChild(leftRel, 0)
        .equals(duplicateNodesUnderOps(rightRel, DOT)))
        || (opIsOr(rightRel.getOp(), STAR, CARET) && getChild(rightRel, 0)
        .equals(duplicateNodesUnderOps(leftRel, DOT)));
  }
}
