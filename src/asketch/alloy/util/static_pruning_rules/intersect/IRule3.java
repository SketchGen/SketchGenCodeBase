package asketch.alloy.util.static_pruning_rules.intersect;

import static asketch.alloy.util.AlloyUtil.createRelation;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class IRule3 extends BinaryRule {

  private IRule3(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static IRule3 given(BinaryInfo binaryInfo) {
    return new IRule3(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return !uniqueNodesUnderOps(createRelation(-1, op, leftRel, rightRel, inheritanceMap), op);
  }
}
