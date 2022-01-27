package asketch.alloy.util.static_pruning_rules.difference;

import static asketch.alloy.util.AlloyUtil.isSuperType;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class DRule1 extends BinaryRule {

  private DRule1(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static DRule1 given(BinaryInfo binaryInfo) {
    return new DRule1(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return isSuperType(rightRel, inheritanceMap)
        // Remove the case where *a - A->A
        && rightRel.getArity() == 1;
  }
}
