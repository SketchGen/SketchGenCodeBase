package asketch.etc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a blacklist of sigs that ASketch treats specially when generating meta
 * models.  Note that ASketch does not treat those sigs differently during expression generation
 * phase.
 */
public class BlacklistSigs {

  /**
   * Ignore some special signatures if needed
   */
  private Set<String> sigs;

  public BlacklistSigs() {
    this.sigs = new HashSet<String>();
  }

  public void ignore(String... sigs) {
    this.sigs.addAll(Arrays.asList(sigs));
  }

  public boolean isIgnored(String sig) {
    return sigs.contains(sig);
  }
}
