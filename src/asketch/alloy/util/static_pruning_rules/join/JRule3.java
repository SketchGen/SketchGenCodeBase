package asketch.alloy.util.static_pruning_rules.join;

import static asketch.alloy.etc.Operators.TILDE;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class JRule3 extends BinaryRule {

  private JRule3(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static JRule3 given(BinaryInfo binaryInfo) {
    return new JRule3(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return leftRel.getArity() == 1 && opIsOr(rightRel.getOp(), TILDE);
  }
}
