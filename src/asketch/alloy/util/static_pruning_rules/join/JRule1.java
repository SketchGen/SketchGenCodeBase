package asketch.alloy.util.static_pruning_rules.join;

import static asketch.alloy.etc.Operators.DOT;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class JRule1 extends BinaryRule {

  private JRule1(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static JRule1 given(BinaryInfo binaryInfo) {
    return new JRule1(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return leftRel.getOp().equals(DOT) && getChild(leftRel, 1).getArity() >= 2;
  }
}
