package integration;

import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

public class KnnDataLoader {
    public static void loadDate(KnowledgeNodeNetwork knn, String filename, List<KnowledgeNode> knowledgeNodes) {
        knn.resetEmpty();

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename)); //change the local directory for the integration file to run
            String line;
            while ((line = br.readLine()) != null) {
                String[] info = line.split(";\\s+");
                KnowledgeNode kn = new KnowledgeNode(info);
                knowledgeNodes.add(kn);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (KnowledgeNode knowledgeNode : knowledgeNodes) {
            knn.addKnowledgeNode(knowledgeNode);
        }

    }
}
