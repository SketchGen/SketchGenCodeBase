package asketch.alloy.util.static_pruning_rules.union;

import static asketch.alloy.util.AlloyUtil.createRelation;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class URule8 extends BinaryRule {

  private URule8(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static URule8 given(BinaryInfo binaryInfo) {
    return new URule8(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return !uniqueNodesUnderOps(createRelation(-1, op, leftRel, rightRel, inheritanceMap), op);
  }
}
