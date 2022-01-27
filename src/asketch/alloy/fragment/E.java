package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.E;

import asketch.alloy.cand.Candidate;
import asketch.alloy.cand.Relation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * This class represents Expression holes.
 */
public class E extends Hole {

  public E(String content, int lineNumber, int begin, int end) {
    super(content, lineNumber, begin, end);
    this.cands = new ArrayList<Candidate>();
  }

  @Override
  public void resetContent() {
    setContent(E);
  }

  public void addRelations(Relation... relations) {
    Collections.addAll(cands, relations);
  }

  public void addRelations(Collection<Relation> relations) {
    cands.addAll(relations);
  }

  public void removeAllCands() {
    cands.clear();
  }
}
