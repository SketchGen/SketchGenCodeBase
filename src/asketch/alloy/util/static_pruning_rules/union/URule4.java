package asketch.alloy.util.static_pruning_rules.union;

import static asketch.alloy.etc.Operators.AMP;
import static asketch.alloy.etc.Operators.ARROW;
import static asketch.alloy.etc.Operators.DOT;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class URule4 extends BinaryRule {

  private URule4(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static URule4 given(BinaryInfo binaryInfo) {
    return new URule4(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return (opIsOr(leftRel.getOp(), DOT) && opIsOr(rightRel.getOp(), DOT)
        || opIsOr(leftRel.getOp(), ARROW) && opIsOr(rightRel.getOp(), ARROW)
        || opIsOr(leftRel.getOp(), AMP) && opIsOr(rightRel.getOp(), AMP))
        && (sameRelations(getChild(leftRel, 0), getChild(rightRel, 0))
        || sameRelations(getChild(leftRel, 1), getChild(rightRel, 1)));
  }
}
