package asketch.alloy.util.static_pruning_rules.join;

import static asketch.alloy.etc.Operators.STAR;
import static asketch.alloy.util.AlloyUtil.isSuperType;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class JRule4 extends BinaryRule {

  private JRule4(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static JRule4 given(BinaryInfo binaryInfo) {
    return new JRule4(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return isSuperType(leftRel, inheritanceMap) && opIsOr(rightRel.getOp(), STAR)
        || isSuperType(rightRel, inheritanceMap) && opIsOr(leftRel.getOp(), STAR);
  }
}
