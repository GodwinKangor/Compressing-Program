import java.util.Comparator;

public class TreeComparator implements Comparator<BinaryTree<CodeTreeElement>> {
    @Override
    public int compare(BinaryTree<CodeTreeElement> tree1, BinaryTree<CodeTreeElement> tree2) {
        return Long.compare(tree1.getData().getFrequency(), tree2.getData().getFrequency());
    }
}
