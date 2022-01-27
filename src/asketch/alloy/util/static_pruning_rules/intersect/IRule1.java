package asketch.alloy.util.static_pruning_rules.intersect;

import static asketch.alloy.etc.Operators.AMP;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class IRule1 extends BinaryRule {

  private IRule1(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static IRule1 given(BinaryInfo binaryInfo) {
    return new IRule1(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return leftRel.getOp().equals(AMP);
  }
}
