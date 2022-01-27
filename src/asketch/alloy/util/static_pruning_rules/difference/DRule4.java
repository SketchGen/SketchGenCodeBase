package asketch.alloy.util.static_pruning_rules.difference;

import static asketch.alloy.etc.Operators.AMP;
import static asketch.alloy.etc.Operators.MINUS;
import static asketch.alloy.etc.Operators.PLUS;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class DRule4 extends BinaryRule {

  private DRule4(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static DRule4 given(BinaryInfo binaryInfo) {
    return new DRule4(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(rightRel.getOp(), PLUS, AMP, MINUS)
        && (sameRelations(leftRel, getChild(rightRel, 0))
        || sameRelations(leftRel, getChild(rightRel, 1)));
  }
}
