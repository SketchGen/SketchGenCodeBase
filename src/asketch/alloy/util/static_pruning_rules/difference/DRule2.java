package asketch.alloy.util.static_pruning_rules.difference;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class DRule2 extends BinaryRule {
  private DRule2(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static DRule2 given(BinaryInfo binaryInfo) {
    return new DRule2(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return sameRelations(leftRel, rightRel);
  }
}
