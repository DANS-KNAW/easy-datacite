package nl.knaw.dans.easy.tools.task.dump;

import java.util.LinkedList;
import java.util.List;

import nl.knaw.dans.pf.language.emd.types.BasicRemark;

public class BasicRemarksReader {
    private final List<String> remarks;

    BasicRemarksReader(List<BasicRemark> basicRemarks) {
        remarks = new LinkedList<String>();

        for (BasicRemark r : basicRemarks) {
            remarks.add(r.toString());
        }
    }

    public List<String> getRemarks() {
        return remarks;
    }
}
