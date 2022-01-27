package asketch.alloy.fragment;

import asketch.alloy.cand.Candidate;
import asketch.alloy.cand.Relation;
import asketch.alloy.etc.ColSpan;
import edu.mit.csail.sdg.alloy4.Pos;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class represents an Alloy hole
 */
public class Hole extends AlloyFragment {

  protected ColSpan colSpan;
  protected List<Candidate> cands;
  /**
   * List of basic relations for that hole, including operator holes.
   */
  protected List<Relation> primaryRelations;

  public Hole(String content, int lineNumber, int begin, int end) {
    super(content, lineNumber);
    this.colSpan = new ColSpan(begin, end);
    this.cands = null;
    this.primaryRelations = new ArrayList<>();
  }

  public ColSpan getColSpan() {
    return colSpan;
  }

  public void setColSpan(ColSpan colSpan) {
    this.colSpan = colSpan;
  }

  public List<Candidate> getCands() {
    return cands;
  }

  public List<Relation> getPrimaryRelations() {
    return primaryRelations;
  }

  public void addPrimaryRelations(Relation... relations) {
    Collections.addAll(primaryRelations, relations);
  }

  public void addPrimaryRelations(Collection<Relation> relations) {
    primaryRelations.addAll(relations);
  }

  public void resetContent() {
  }

  /**
   * Find location of the current hole, including row ranges and col ranges. Note that the column
   * and row of Alloy model starts from 1
   *
   * @return Pos in Alloy 4.2
   */
  public Pos findPos() {
    return new Pos(null, colSpan.getBegin() + 1, lineNumber + 1, colSpan.getEnd() + 1,
        lineNumber + 1);
  }
}
