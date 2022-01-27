package asketch.alloy.util.static_pruning_rules.difference;

import static asketch.alloy.etc.Operators.ARROW;
import static asketch.alloy.etc.Operators.STAR;

import asketch.alloy.etc.Constants;
import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class DRule10 extends BinaryRule {

  private DRule10(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static DRule10 given(BinaryInfo binaryInfo) {
    return new DRule10(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(leftRel.getOp(), ARROW) && opIsOr(rightRel.getOp(), STAR)
        && sameRelations(getChild(leftRel, 0), getChild(leftRel, 1))
        && getChild(leftRel, 0).getArity() == 1
        && getChild(leftRel, 0).getCards().get(0).equals(Constants.ONE);
  }
}
