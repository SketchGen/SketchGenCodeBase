package asketch.alloy.util.static_pruning_rules.difference;

import static asketch.alloy.etc.Operators.AMP;
import static asketch.alloy.etc.Operators.MINUS;
import static asketch.alloy.etc.Operators.PLUS;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class DRule5 extends BinaryRule {

  private DRule5(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static DRule5 given(BinaryInfo binaryInfo) {
    return new DRule5(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(leftRel.getOp(), PLUS, AMP, MINUS)
        && (sameRelations(rightRel, getChild(leftRel, 0))
        || sameRelations(rightRel, getChild(leftRel, 1)));
  }
}
