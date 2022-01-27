package asketch.alloy.util.static_pruning_rules.join;

import static asketch.alloy.etc.Operators.ARROW;
import static asketch.alloy.etc.Operators.MINUS;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class JRule11 extends BinaryRule {

  private JRule11(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static JRule11 given(BinaryInfo binaryInfo) {
    return new JRule11(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(rightRel.getOp(), ARROW) && rightChildUnderLeftImbalancedOps(
        getChild(rightRel, 0), MINUS, root -> sameRelations(leftRel, getChild(root, 1)))
        || opIsOr(leftRel.getOp(), ARROW) && rightChildUnderLeftImbalancedOps(
        getChild(leftRel, 1), MINUS, root -> sameRelations(getChild(root, 1), rightRel));
  }
}
