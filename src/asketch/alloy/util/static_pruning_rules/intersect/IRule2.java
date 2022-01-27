package asketch.alloy.util.static_pruning_rules.intersect;

import static asketch.alloy.util.AlloyUtil.isSuperType;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class IRule2 extends BinaryRule {

  private IRule2(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static IRule2 given(BinaryInfo binaryInfo) {
    return new IRule2(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    // Remove the case where (A->A) & *a
    return (isSuperType(leftRel, inheritanceMap) || isSuperType(rightRel, inheritanceMap))
        && pruningTypeMatches(leftRel.getTypes(), rightRel.getTypes());
  }
}
