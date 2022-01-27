package asketch.alloy.util.static_pruning_rules.join;

import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.util.AlloyUtil.isSuperType;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class JRule5 extends BinaryRule {

  private JRule5(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static JRule5 given(BinaryInfo binaryInfo) {
    return new JRule5(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return isSuperType(leftRel, inheritanceMap) && opIsOr(rightRel.getOp(), CARET)
        || isSuperType(rightRel, inheritanceMap) && opIsOr(leftRel.getOp(), CARET);
  }
}
