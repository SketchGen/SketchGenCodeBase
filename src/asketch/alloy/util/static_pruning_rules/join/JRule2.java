package asketch.alloy.util.static_pruning_rules.join;

import static asketch.alloy.etc.Constants.ONE;
import static asketch.alloy.etc.Constants.SOME;
import static asketch.alloy.etc.Operators.ARROW;

import asketch.alloy.cand.Relation;
import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class JRule2 extends BinaryRule {

  private JRule2(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static JRule2 given(BinaryInfo binaryInfo) {
    return new JRule2(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    if (opIsOr(leftRel.getOp(), ARROW)) {
      Relation leftSubRel = getChild(leftRel, 1);
      if (sameRelations(leftSubRel, rightRel)
          && rightRel.getArity() == 1
          && (rightRel.getCards().get(0).equals(ONE) || rightRel.getCards().get(0).equals(SOME))) {
        return true;
      }
    }
    if (opIsOr(rightRel.getOp(), ARROW)) {
      Relation rightSubRel = getChild(rightRel, 0);
      if (sameRelations(leftRel, rightSubRel)
          && leftRel.getArity() == 1
          && (leftRel.getCards().get(0).equals(ONE) || leftRel.getCards().get(0).equals(SOME))) {
        return true;
      }
    }
    return false;
  }
}
