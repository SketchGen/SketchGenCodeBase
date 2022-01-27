package asketch.alloy.util.static_pruning_rules.union;

import static asketch.alloy.etc.Operators.AMP;
import static asketch.alloy.etc.Operators.MINUS;
import static asketch.alloy.etc.Operators.PLUS;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class URule3 extends BinaryRule {

  private URule3(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static URule3 given(BinaryInfo binaryInfo) {
    return new URule3(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return (opIsOr(rightRel.getOp(), PLUS, AMP, MINUS)
        && (sameRelations(leftRel, getChild(rightRel, 0)) || sameRelations(leftRel,
        getChild(rightRel, 1))))
        || (opIsOr(leftRel.getOp(), PLUS, AMP, MINUS)
        && (sameRelations(rightRel, getChild(leftRel, 0)) || sameRelations(rightRel,
        getChild(leftRel, 1))));
  }
}
