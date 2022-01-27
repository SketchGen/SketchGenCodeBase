package asketch.alloy.util.static_pruning_rules.union;

import static asketch.alloy.util.AlloyUtil.isSuperType;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class URule2 extends BinaryRule {

  private URule2(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static URule2 given(BinaryInfo binaryInfo) {
    return new URule2(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return isSuperType(leftRel, inheritanceMap)
        || isSuperType(rightRel, inheritanceMap);
  }
}
