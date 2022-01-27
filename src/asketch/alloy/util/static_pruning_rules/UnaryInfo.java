package asketch.alloy.util.static_pruning_rules;

import asketch.alloy.cand.Candidate;
import asketch.alloy.cand.Relation;

public class UnaryInfo {

  private Candidate op;
  private Relation rel;

  public UnaryInfo(Candidate op, Relation rel) {
    this.op = op;
    this.rel = rel;
  }

  public Candidate getOp() {
    return op;
  }

  public Relation getRel() {
    return rel;
  }
}
