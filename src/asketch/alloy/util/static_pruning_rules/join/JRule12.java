package asketch.alloy.util.static_pruning_rules.join;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

import static asketch.alloy.etc.Operators.TILDE;

public class JRule12 extends BinaryRule {

  private JRule12(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static JRule12 given(BinaryInfo binaryInfo) {
    return new JRule12(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return opIsOr(leftRel, TILDE) && rightRel.getArity() == 1;
  }
}
