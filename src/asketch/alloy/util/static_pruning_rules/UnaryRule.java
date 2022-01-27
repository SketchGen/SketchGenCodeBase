package asketch.alloy.util.static_pruning_rules;

import asketch.alloy.cand.Candidate;
import asketch.alloy.cand.Relation;

public abstract class UnaryRule extends PruningRule {

  protected Candidate op;
  protected Relation rel;

  public UnaryRule(UnaryInfo unaryInfo) {
    this.op = unaryInfo.getOp();
    this.rel = unaryInfo.getRel();
  }
}
