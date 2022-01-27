package asketch.alloy.util.static_pruning_rules.intersect;

import static asketch.alloy.etc.Operators.ARROW;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class IRule4 extends BinaryRule {

  private IRule4(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static IRule4 given(BinaryInfo binaryInfo) {
    return new IRule4(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(leftRel.getOp(), ARROW) && opIsOr(rightRel.getOp(), ARROW) && (
        sameRelations(getChild(leftRel, 0), getChild(rightRel, 0)) || sameRelations(
            getChild(leftRel, 1), getChild(rightRel, 1)));
  }
}
