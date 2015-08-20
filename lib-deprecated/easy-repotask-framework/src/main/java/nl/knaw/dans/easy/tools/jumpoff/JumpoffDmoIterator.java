package nl.knaw.dans.easy.tools.jumpoff;

import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.easy.tools.dmo.DmoFilter;
import nl.knaw.dans.easy.tools.dmo.DmoIterator;

public class JumpoffDmoIterator extends DmoIterator<JumpoffDmo> {

    public JumpoffDmoIterator() {
        super(JumpoffDmo.NAMESPACE);
    }

    public JumpoffDmoIterator(DmoFilter<JumpoffDmo> jumpoffDmoFilter) {
        super(JumpoffDmo.NAMESPACE, jumpoffDmoFilter);
    }

}
